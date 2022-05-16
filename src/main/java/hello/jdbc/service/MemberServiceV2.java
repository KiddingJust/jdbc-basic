package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;
    /**
     * 계좌 이체 로직 작성
     */
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        /* 여기서 커넥션 생성 */
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false); //트랜잭션 시작. 기본값은 true. 트랜잭션을 시작하려면 자동 커밋 모드를 꺼야함.
            //비즈니스 로직
            Member fromMember = memberRepository.findById(fromId);
            Member toMember = memberRepository.findById(toId);

            memberRepository.update(con, fromId, fromMember.getMoney()-money);
            /* 예외 케이스를 강제로 만들기 위해 만든 부분 */
            if (toMember.getMemberId().equals("ex")){
                throw new IllegalStateException("이체 중 에러 발생");
            }
            memberRepository.update(con, toId, toMember.getMoney()+money);

            con.commit();   //성공 시 커밋
        }catch(Exception e){
            con.rollback(); //실패 시 롤백
            throw new IllegalStateException(e);
        }finally{
            if(con != null){
                try{
                    //커넥션 풀 고려하여 true로 변경.
                    // 커넥션 풀이므로 con이 종료되는 게 아니라 반납되는 개념이므로,
                    // 풀에 돌려주기 전에 기본값으로 변경하는 것
                    con.setAutoCommit(true);    //커넥션 풀 고려하여 true로 변경. 커넥션 풀이므로 con이 종료되는 게 아니라 반납되는 개념이므로, 풀에 돌려주기 전에 기본값으로 변경하는 것
                    con.close();
                }catch(Exception e){
                    log.info("error", e);
                }
            }
        }
    }
}
