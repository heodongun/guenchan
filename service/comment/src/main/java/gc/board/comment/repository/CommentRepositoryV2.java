package gc.board.comment.repository;

import gc.board.comment.entity.CommentPath;
import gc.board.comment.entity.CommentV2;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepositoryV2 extends CrudRepository<CommentV2, Long> {
    @Query(value = "select c from CommentV2 c where c.commentPath=:path")
    Optional<CommentV2> findByPath(@Param("path") String path);

    @Query(
            nativeQuery = true,
            value = "select path from comment_v2 " +
                    "where article_id= :articleId and path > :pathPrefix and " +
                    "path like :pathPrefix%" +
                    "order by path desc limit 1"
    )
    Optional<String> findDescendantsTopPath(
            @Param("articleId") Long articleId,
            @Param("pathPrefix") String pathPrefix
    );
}
