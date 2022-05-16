package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckTest {

    @Test
    void checked_catch(){
        Service service = new Service();
        // 예외 처리, message=ex  가 출력됨.
        service.callCatch();
    }
    @Test
    void checked_throw(){
        Service service = new Service();
        // service에서도 예외를 던지므로, 여기서라도 처리가 필요함.
//        service.callThrow();
        Assertions.assertThatThrownBy(()->service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }

    /**
     * Exception을 상속 받은 예외는, 체크 예외가 된다.
     */
    static class MyCheckedException extends Exception {
        //무엇 때문에 예외가 발생했는지 알 수 있도록 하기 위함.
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * checked 예외는 잡아서 던지거나 처리해주는 것이 반드시 필요함.
     */
    static class Service {
        Repository repository = new Repository();
        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch(){
            try {
                repository.call();
            } catch (MyCheckedException e) {
                //예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }
        /**
         * 체크 예외를 밖으로 던지는 코드
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }
    static class Repository {
        //처리하거나 밖으로 던지지 않으면 빨간 줄이 생김.
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}
