package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 */
@Slf4j
public class MemberServiceV4 {

    private final MemberRepository memberRepository;

    //주입은 PlatformTransactionManager
    public MemberServiceV4(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    //메서드 호출 시 트랜잭션을 걸어주는 것. 애노테이션 한줄로 끝.
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) {
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
