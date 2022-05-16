package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DBConnectionUtilTest {

    @Test
    void connection(){
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }

    @Test
    void dataSourceConnectionPool() throws SQLException {
        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(URL);
        datasource.setUsername(USERNAME);
        datasource.setPassword(PASSWORD);
        datasource.setMaximumPoolSize(10);
        datasource.setPoolName("MyPool");

        useDataSource(datasource);

    }

    private void useDataSource(DataSource dataSource)throws SQLException {
        //커넥션 가져와서 체크.
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
    }
}