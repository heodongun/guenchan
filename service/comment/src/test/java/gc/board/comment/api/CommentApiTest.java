package gc.board.comment.api;

import gc.board.comment.service.response.CommentPageResponse;
import gc.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiTest {
  RestClient restClient = RestClient.create("http://localhost:9001");

  @Test
  void createTest() {
    CommentResponse response1 = createComment(
            new CommentCreateRequest(1L, "comment1", null, 1L)
    );
    CommentResponse response2 = createComment(
            new CommentCreateRequest(1L, "comment2", response1.getCommentId(), 1L)
    );
    CommentResponse response3 = createComment(
            new CommentCreateRequest(1L, "comment3", response1.getCommentId(), 1L)
    );

    System.out.println("commentId=%s".formatted(response1.getCommentId()));
    System.out.println("\tcommentId=%s".formatted(response2.getCommentId()));
    System.out.println("\tcommentId=%s".formatted(response3.getCommentId()));
  }

  private CommentResponse createComment(CommentCreateRequest request) {
    return restClient.post()
            .uri("/v1/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse.class);
  }

//  commentId=235996940716695552
//    commentId=235996941253566464
//    commentId=235996941316481024


  @Test
  void readTest() {
    CommentResponse response = restClient.get()
            .uri("/v1/comments/{commentId}", 235996940716695552L)
            .retrieve()
            .body(CommentResponse.class);

    System.out.println("response = " + response);
  }

  //  commentId=235996940716695552
//    commentId=235996941253566464
//    commentId=235996941316481024
  @Test
  void deleteTest() {
    restClient.delete()
            .uri("/v1/comments/{commentId}", 235996941316481024L)
            .retrieve();
  }

  @Getter
  @AllArgsConstructor
  public static class CommentCreateRequest {
    private Long articleId;
    private String content;
    private Long parentCommentId;
    private Long writerId;
  }


  @Test
  void readAll() {
    CommentPageResponse response = restClient.get()
            .uri("/v1/comments?articleId=1&page=1&pageSize=10")
            .retrieve()
            .body(CommentPageResponse.class);

    System.out.println("response.getCommentCount() = " + response.getCommentCount());

    for(CommentResponse comment : response.getComments()) {
      if(!comment.getCommentId().equals(comment.getParentCommentId()))
        System.out.print("\t");
      System.out.println("commentId = " + comment.getCommentId());
    }
  }

  /**
   * response.getCommentCount() = 101
   * commentId = 236000923775180800
   * 	commentId = 236000923808735238
   * commentId = 236000923775180801
   * 	commentId = 236000923808735234
   * commentId = 236000923775180802
   * 	commentId = 236000923808735258
   * commentId = 236000923775180803
   * 	commentId = 236000923808735233
   * commentId = 236000923775180804
   * 	commentId = 236000923808735251
   */


  @Test
  void readAllInfiniteScroll() {
    //무한스크롤방식의 첫번째페이지 (response1)
    List<CommentResponse> response1 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });

    System.out.println("first page");
    for(CommentResponse comment : response1) {
      if(!comment.getCommentId().equals(comment.getParentCommentId()))
        System.out.print("\t");
      System.out.println("commentId = " + comment.getCommentId());
    }

    Long lastParentCommentId = response1.getLast().getParentCommentId();
    Long lastCommentId = response1.getLast().getCommentId();

    //무한스크롤방식의 두번째페이지 (response2)
    List<CommentResponse> response2 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5" +
                    "&lastParentCommentId=%s&lastCommentId=%s"
                            .formatted(lastParentCommentId, lastCommentId)
            )
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });

    System.out.println("second page");
    for(CommentResponse comment : response2) {
      if(!comment.getCommentId().equals(comment.getParentCommentId()))
        System.out.print("\t");
      System.out.println("commentId = " + comment.getCommentId());
    }
  }
}
