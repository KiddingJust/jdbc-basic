package hello.jdbc.repository.ex;

//RuntimeException 상속받았으므로, 언체크 예외
public class MyDbException extends RuntimeException {
    public MyDbException() {
    }

    public MyDbException(String message) {
        super(message);
    }
    //원인 가져오는 부분
    public MyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }
}
