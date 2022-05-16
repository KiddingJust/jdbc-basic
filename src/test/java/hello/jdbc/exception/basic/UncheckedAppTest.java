package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
public class UncheckedAppTest {
    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }
    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("ex", e);
        }
    }
    static class Controller {
        Service service = new Service();
        //이제 해당 메서드에서 throws로 예외를 던져줄 필요가 없음.
        public void request() {
            service.logic();
        }
    }
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();
        //이제 해당 메서드에서 throws로 예외를 던져줄 필요가 없음.
        public void logic() {
            repository.call();
            networkClient.call();
        }
    }
    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패"); }
    }
    static class Repository {
        //리포지토리에서 예외를 잡아서, 예외가 생기면 RuntimeSQLException을 발생시킬 것.
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }
        private void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }
    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }
    static class RuntimeSQLException extends RuntimeException {
        //Throwable cause를 인자로 주면 예외가 왜 발생했는지, 이전 예외를 함께 넣을 수 있다.
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
