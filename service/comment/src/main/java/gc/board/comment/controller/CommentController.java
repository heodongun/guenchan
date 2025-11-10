package gc.board.comment.controller;

import gc.board.comment.service.CommentService;
import gc.board.comment.service.request.CommentCreateRequest;
import gc.board.comment.service.response.CommentPageResponse;
import gc.board.comment.service.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  //읽기
  @GetMapping("/v1/comments/{commentId}")
  public CommentResponse read(@PathVariable Long commentId) {
    return commentService.read(commentId);
  }

  //생성
  @PostMapping("/v1/comments")
  public CommentResponse create(
          @RequestBody CommentCreateRequest request
  ) {
    return commentService.create(request);
  }

  //삭제
  @DeleteMapping("/v1/comments/{commentId}")
  public void delete(@PathVariable Long commentId) {
    commentService.delete(commentId);
  }

  //페이지 번호방식의 api 호출
  @GetMapping("/v1/comments")
  public CommentPageResponse readAll(
          @RequestParam("articleId") Long articleId,
          @RequestParam("page") Long page,
          @RequestParam("pageSize") Long pageSize
  ){
    return commentService.readAll(articleId, page, pageSize);
  }


  //무한스크롤방식의 api 호출
  @GetMapping("/v1/comments/infinite-scroll")
  public List<CommentResponse> readAll(
          @RequestParam("articleId") Long articleId,
          @RequestParam(value = "lastParentCommentId", required = false) Long lastParentCommentId,
          @RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
          @RequestParam("pageSize") Long pageSize
  ) {
    return commentService.readAll(
            articleId, lastParentCommentId, lastCommentId, pageSize
    );
  }
}
