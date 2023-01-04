package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/*테스트 요구사항
 * 1. 회원가입 성공
 * 2. 회원가입 시 이름 중복되면 예외 발생*/

@RunWith(SpringRunner.class)        // Junit 실행할 때 스프링이랑 같이 실행할래
@SpringBootTest                     // 스프링부트 컨테이너 안에서 테스트 할래. -> 이게 없으면 @Autowired 다 실패
@Transactional                      // 테스트 끝나면 RollBack 할래
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;


    @Test
    public void join() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("서울", "강남구", "123456"));

        //when
        Long savedId = memberService.join(member);

        //then
        assertEquals(member, memberRepository.findOne(savedId));
    }


    /*중복 회원 예외 테스트 깔끔하게 하는 법*/
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("김1");

        Member member2 = new Member();
        member2.setName("김1");

        //when
        memberService.join(member1);
        memberService.join(member2);

        //then
        fail("테스트 실패. 예외가 발생해야 한다.");
    }


    @Test
    public void 중복_회원_예외_테스트_실패() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("김1");

        Member member2 = new Member();
        member2.setName("김1");

        //when
        memberService.join(member1);
        memberService.join(member2);        // 예외가 발생해야 함.

        //then
        fail("테스트 실패. 예외가 발생해야 한다.");
        /* fail: junit이 제공하는 메서드
        * then까지 오면 안 되고 memberService.join(member2);에서 예외가 발생해서 밖으로 나가야 됨
        * 여기까지 코드가 오면 테스트 실패라고 알려주는 것.
        * */
    }

    @Test
    public void 중복_회원_예외_테스트_성공() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("김1");

        Member member2 = new Member();
        member2.setName("김1");

        //when
        memberService.join(member1);
        try {
            memberService.join(member2);
        } catch (IllegalStateException e) {
            return;
        }

        //then
        fail("테스트 실패. 예외가 발생해야 한다.");
    }

    /*테스트 케이스를 위한 설정
    테스트는 케이스 격리된 환경에서 실행하고, 끝나면 데이터를 초기화하는 것이 좋다. 그런 면에서 메모리
    DB를 사용하는 것이 가장 이상적이다.
    추가로 테스트 케이스를 위한 스프링 환경과, 일반적으로 애플리케이션을 실행하는 환경은 보통 다르므로
    설정 파일을 다르게 사용하자.
    다음과 같이 간단하게 테스트용 설정 파일을 추가하면 된다.
    
    PDF 42쪽 참고
    */
}