package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * Spring Sql Exception Translator 추가
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository{

    //JdbcTemplate추가
    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?, ?)";
        //커넥션 받아오고, 예외 반환하는 부분까지 모두 처리해줌. 커넥션 닫기나 동기화 등등도
        template.update(sql, member.getMemberId(), member.getMoney());
        return member;
        /** 여기부터는 이제 삭제
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
//            throw new MyDbException(e);
            //translator를 통한 exception 처리
            DataAccessException ex = exTranslator.translate("save", sql, e);
            throw ex;
        } finally {
            close(con, pstmt, null);
        }
         */
    }

    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        //커넥션 받아오고, 예외 반환하는 부분까지 모두 처리해줌.
        //반환되는 부분은 Mapper 형식으로 받아주어야 함.
        Member member = template.queryForObject(sql, memberRowMapper(), memberId);
        return member;
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }

    public void update(String memberId, int money){
        String sql = "update member set money=? where member_id=?";
        template.update(sql, money, memberId);
    }
    public void delete(String memberId){
        String sql = "delete from member where member_id=?";
        template.update(sql, memberId);
    }
}
