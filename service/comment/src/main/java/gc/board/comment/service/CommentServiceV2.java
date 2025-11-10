package gc.board.comment.service;

import gc.board.comment.entity.CommentPath;
import gc.board.comment.entity.CommentV2;
import gc.board.comment.repository.CommentRepositoryV2;
import gc.board.comment.service.request.CommentCreateRequestV2;
import gc.board.comment.service.response.CommentResponse;
import gc.board.common.snowflake.Snowflake;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Service;

import static java.util.function.Predicate.not;


@Service
@RequiredArgsConstructor
public class CommentServiceV2 {
    private final Snowflake snowflake=new Snowflake();
    private final CommentRepositoryV2 commentRepository;
    private final EntityManagerFactoryInfo entityManagerFactoryInfo;

    @Transactional
    public CommentResponse create(CommentCreateRequestV2 request) {
        CommentV2 parent=getCommentResponse(request);
        CommentPath parentCommentPath=parent==null?CommentPath.create(""):
                parent.getCommentPath();
        CommentV2 comment = commentRepository.save(
                CommentV2.create(
                        snowflake.nextId(),
                        request.getContent(),
                        request.getArticleId(),
                        request.getWriterId(),
                        parentCommentPath.createChildCommentPath(
                                commentRepository.findDescendantsTopPath(
                                        request.getArticleId(),parentCommentPath.getPath()
                                ).orElse(null)
                        )
                )
        );
        return CommentResponse.from(comment);
    }

    public CommentResponse read(Long commentId) {
        return CommentResponse.from(
                commentRepository.findById(commentId).orElseThrow()
        );
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
                .filter(not(CommentV2::getDeleted))
        .ifPresent(comment->{
            if(hasChildren(comment)){
                comment.delete();
            }else{
                delete(comment);
            }
        });
    }

    private boolean hasChildren(CommentV2 comment) {
        return commentRepository.findDescendantsTopPath(
                comment.getArticleId(),
                comment.getCommentPath().getPath()
        ).isPresent();
    }

    private void delete(CommentV2 comment) {
        commentRepository.delete(comment);
        if(!comment.isRoot()){
            commentRepository.findByPath(comment.getCommentPath().getParentPath())
                    .filter(CommentV2::getDeleted)
                    .filter(not(this::hasChildren))
                    .ifPresent(this::delete);
        }
    }

    private CommentV2 getCommentResponse(CommentCreateRequestV2 request) {
        String parentPath= request.getParentPath();
        if(parentPath==null){
            return null;
        }
        return commentRepository.findByPath(parentPath)
                .filter(not(CommentV2::getDeleted))
                .orElseThrow();
    }
}
