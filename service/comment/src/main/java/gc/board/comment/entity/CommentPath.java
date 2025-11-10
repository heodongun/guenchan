package gc.board.comment.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class CommentPath {
    private String path;

    // Corrected: includes full A–Z (with Y), plus a–z in order
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int DEPTH_CHUNK_SIZE = 5;
    private static final int MAX_DEPTH=5;

    private static final String MIN_CHUNK=String.valueOf(CHARSET.charAt(0)).repeat(DEPTH_CHUNK_SIZE);
    private static final String MAX_CHUNK=String.valueOf(CHARSET.charAt(CHARSET.length()-1)).repeat(DEPTH_CHUNK_SIZE);

    public static CommentPath create(String path){
        if(isDepthOverflowed(path)){
            throw new IllegalArgumentException("depth overflowed");
        }
        CommentPath commentPath = new CommentPath();
        commentPath.path = path;
        return commentPath;
    }

    private static boolean isDepthOverflowed(String path){
        return calDepth(path)>MAX_DEPTH;
    }

    private static int calDepth(String path){
        return path.length()/DEPTH_CHUNK_SIZE;
    }

    public int getDepth(){
        return calDepth(path);
    }

    public boolean isRoot(){
        return calDepth(path)==1;
    }

    public String getParentPath(){
        return path.substring(0, path.length()-DEPTH_CHUNK_SIZE);
    }

    //자식 댓글의 path 만들기
    public CommentPath createChildCommentPath(String decendantsTopPath){
        if(decendantsTopPath==null){
            return CommentPath.create(path+MIN_CHUNK);
        }
        String childrenTopPath = findChildrenTopPath(decendantsTopPath);
        return CommentPath.create(increse(childrenTopPath));
    }

    private String findChildrenTopPath(String decendantsTopPath){
        return decendantsTopPath.substring(0, (getDepth()+1)*DEPTH_CHUNK_SIZE);
    }

    private String increse(String path){
        String lastChunk = path.substring(path.length()-DEPTH_CHUNK_SIZE);
        if(isChunkOverflowed(lastChunk)){
            throw new IllegalArgumentException("chunk overflowed");
        }
        int charsetLength = CHARSET.length(); //62
        int value=0; //10진수로 바뀌는 값
        for(char ch:lastChunk.toCharArray()){
            value=value*charsetLength+CHARSET.indexOf(ch);
        }
        value=value+1;

        String result = ""; //62진수 문자열이 저장되는 값

        for(int i=0; i<lastChunk.length(); i++){
            result=CHARSET.charAt(value%charsetLength)+result;
            value=value/charsetLength;
        }
    return path.substring(0, path.length()-DEPTH_CHUNK_SIZE)+result;
    }

    private boolean isChunkOverflowed(String lastChunk) {
        return MAX_CHUNK.equals(lastChunk);
    }


}
