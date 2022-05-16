package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    /**
     * 계좌 이체 로직 작성
     */
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney()-money);
        /* 예외 케이스를 강제로 만들기 위해 만든 부분 */
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 에러 발생");
        }
        memberRepository.update(toId, toMember.getMoney()+money);

    }
}
