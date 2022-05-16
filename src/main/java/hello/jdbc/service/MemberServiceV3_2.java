package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

    private final MemberRepositoryV3 memberRepository;
    //트랜잭션 템플릿 사용할 것
//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;

    //주입은 PlatformTransactionManager
    public MemberServiceV3_2(MemberRepositoryV3 memberRepository, PlatformTransactionManager transactionManager) {
        this.memberRepository = memberRepository;
        this.txTemplate = new TransactionTemplate(transactionManager);
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        //bizLogic을 내부에 넣어주기만 하면 됨. 내부에서 try catch를 한번은 잡아주어야 함.
        //아래 코드 안에서 트랜잭션을 시작 -> 비즈니스 로직 수행 후 -> commit, rollback 수행됨.
        //템플릿 콜백 패턴을 생각하면 됨.
        txTemplate.executeWithoutResult((status) -> {
            try{
                bizLogic(fromId, toId, money);
            }catch(Exception e){
                throw new IllegalStateException(e);
            }
        });

    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        //비즈니스 로직 시작
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney()-money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney()+money);

    }
    private void validation(Member toMember){
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 에러 발생");
        }
    }
}
