package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

    private final MemberRepositoryV3 memberRepository;
//    private final DataSource dataSource;
    //Datasource 대신 트랜잭션 매니저 주입
    private final PlatformTransactionManager transactionManager;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

//        Connection con = dataSource.getConnection();
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try{
            //비즈니스 로직 시작
            Member fromMember = memberRepository.findById(fromId);
            Member toMember = memberRepository.findById(toId);

            memberRepository.update(fromId, fromMember.getMoney()-money);
            /* 예외 케이스를 강제로 만들기 위해 만든 부분 */
            if (toMember.getMemberId().equals("ex")){
                throw new IllegalStateException("이체 중 에러 발생");
            }
            memberRepository.update(toId, toMember.getMoney()+money);
            //비즈니스 로직 끝
            transactionManager.commit(status);
        }catch(Exception e){
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
        /**
        finally{
         finally 는 필요가 없음. 트랜잭션 매니저가 알아서 해줌.
         commit이나 rollback 시 커넥션 알아서 정리해줌.
        }
         */
    }
}
