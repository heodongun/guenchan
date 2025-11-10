package gc.board.comment.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentPathTest {
    @Test
    void createChildCommentTest() {
        //00000 <-생성
        createChildCommentTest(CommentPath.create(""),null,"00000");

        //00000
        //     00000 <- 생성
        createChildCommentTest(CommentPath.create("00000"),null,"0000000000");

        //00000
        //00001 <- 생성
        createChildCommentTest(CommentPath.create(""),"00000","00001");

        //0000z
        //     abcdz
        //          zzzzz
        //     abce0 <- 생성
        createChildCommentTest(CommentPath.create("0000z"),"0000zabcdzzzzzzzzzzz","0000zabce0");

    }

    void createChildCommentTest(CommentPath commentPath,String decendantsTopPath,String expectedChildPath) {
        CommentPath childCommentPath = commentPath.createChildCommentPath(decendantsTopPath);
        Assertions.assertThat(childCommentPath.getPath()).isEqualTo(expectedChildPath);
    }

    @Test
    void createChildCommentPathIfMaxDepthTest(){
        Assertions.assertThatThrownBy(()->{
            CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createChildCommentPathIfChunkOverflowTest(){
        //given
        CommentPath commentPath = CommentPath.create("");

        //when, then
        Assertions.assertThatThrownBy(()->{
            commentPath.createChildCommentPath("zzzzz".repeat(5));
        }).isInstanceOf(IllegalArgumentException.class);
    }
}