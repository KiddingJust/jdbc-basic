package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 트랜잭션 - @Transactional AOP
 */
@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    //주입은 PlatformTransactionManager
    public MemberServiceV3_3(MemberRepositoryV3 memberRepository){
        this.memberRepository = memberRepository;
    }

    //메서드 호출 시 트랜잭션을 걸어주는 것. 애노테이션 한줄로 끝.
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
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
