package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV2 {
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2,  member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    public Member findById(String memberId) throws SQLException{
        String sql = "select * from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            //rs는 내부적으로 커서를 갖고 있음
            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{
                //데이터가 없을 때
                throw new NoSuchElementException("member not found memberId="+memberId);
            }
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con, pstmt, rs);
        }
    }

    /**
     * 커넥션 파라미터로 전달
     */
    public Member findById(Connection con, String memberId) throws SQLException{
        String sql = "select * from member where member_id=?";
        //Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            //con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId="+memberId);
            }
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            // close도 해당 메서드 쓰면 안됨.
            // close(con, pstmt, rs);
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            //커넥션도 닫으면 안됨. 서비스 계층에서 con을 넘겨주고 종료할 것.
//            JdbcUtils.closeConnection(con);
        }
    }

    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";
//        Connection con = null;
        PreparedStatement pstmt = null;

        try{
//            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con, pstmt, null);
        }
    }
    private Connection getConnection(){
        return DBConnectionUtil.getConnection();
    }
    private void close(Connection con, Statement stmt, ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        //이러한 구조는 위에서 Exception이 터져도 catch로 잡으므로
        //아래 코드에는 영향을 주지 않음.
        //pstmt를 바로 close하면, Exception 시 con의 close가 되지 않으므로
        //이와 같이 메서드로 분류해서 각각 try~catch에 넣어줌.
        if (con != null){
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }
}
