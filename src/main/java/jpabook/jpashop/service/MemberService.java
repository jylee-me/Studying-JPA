package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)      // JPA의 모든 데이터 변경이나 로직들은 가급적이면 트랜젝션 안에서 실행되야 함. 그래야 Lazy loading이런 것들이 다 됨.
                    // 클래스 레벨에서 쓰면 public 메서드들은 기본적으로 다 트랜젝션 적용이 됨
                    // @Transactional이 스프링이 제공하는 것과 javax가 제공하는 것 2가지가 있는데 스프링이 제공하는 것을 쓰는 것이 스프링에서 쓸 수 있는 것이 많아서 더 좋다.
                    /*@Transactional(readOnly = true)를 하면 JPA가 데이터를 조회하는 곳에서는 성능이 최적화 됨.
                    * 영속성 컨텍스트를 dirty checking(변경된 내용 update)를 안 함
                    * 데이터베이스에 따라서는 읽기 전용 트랜잭션이면 db한테 "읽기 전용이니까 리소스 너무 많이 쓰지 말고 단순히 읽기 모드로 읽어"라고 해주는 드라이버들도 있음*/
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    /*회원 가입*/
    @Transactional      // 읽기가 아닌 쓰기에는 @Transactional(readOnly = false)를 해줌. 기본적으로 @Transactional하면 readOnly가 false임
    public Long join(Member member) {

        validateDuplicateMember(member);        // 중복회원 검증
        memberRepository.save(member);
        return member.getId();
        /* MemberRepository에서 em.persist(member)를 하면 member 객체를 영속성 컨텍스트에 올림.
        (아직까진 DB에 저장 X)
       그때 영속성 컨텍스트는
       키와 밸류에서 키값으로 DB에서 Member 테이블의 pk(기본키)인 id 값을 가짐
       @GeneratedValue를 하면 (데이터베이스마다 다른데 어떤 애들은 시퀀스를 넣기도 하고, 어떤 애들은 임시 테이블을 만들어서 그 테이블로부터 키를 가져오기도 함)
       id 값이 항상 생성이 되는 것이 보장이 됨.
       em.persist만 해서 아직 DB에 저장되는 시점이 아니어도 id 값이 있는 이유
        * */
    }


    /* 중복회원 */
    private void validateDuplicateMember(Member member) {
        // 중복회원이 있으면 exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

        /*if문 안에(!~~~) 쓰는거 웬만하면 하지 말라고 멘토링 시간에 배웠는데 그럼 여기서 if문 안에 true 조건을 넣으려면 어떻게 해야 할까*/

        /*김영한님: 더 간단하게 하는 법: findByName으로 조회한 데이터의 수를 세서 0보다 크면 exception을 날리는 게 최적화에 좋음 */

        /* '멤버A'라는 똑같은 이름을 가진 회원이 '동시에' validateDuplicateMember를 호출하면
        * 위의 로직을 둘 다 통과하고 저장돼서 '멤버A'가 두 명이 된다.
        * --> 실무에서는 최후의 방어를 위해 DB에 member의 name을 unique 제약 조건을 걸어둠 */

    }

    /*회원 전체 조회*/
    public List<Member> findMember(){
        return memberRepository.findAll();
    }

    /*회원 단건 조회*/
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }


    /*
    [필드 주입]
    @Autowired
    private MemberRepository memberRepository;

    - 장점: 편함
    - 단점: 테스트를 하는 경우 등 repository를 바꿔야 할 경우가 있는데 필드 주입을 하면 못 바꿈
     */


    /*
    [setter 주입]

    private MemberRepository memberRepository;

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    - 장점: test 코드를 작성할 때 mock을 직접 주입해줄 수 있음. 필드 주입은 mock을 주입하기가 까다로움.
    - 단점: (치명적) setXXX 메서드를 public으로 열어두기 때문에 애플리케이션이 돌아가는 시점에 누군가가 변경이 가능함
            setter 주입은 생성자 주입과 다르게 주입받는 객체가 변경되 가능성이 있을 때 사용하는데, 그런 경우는 극히 드묾
    */

    /*
    [생성자 주입]

    private MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    이렇게 하면 스프링이 생성자에서 MemberRepository를 인젝션 해줌.
    생성자 주입을 하면 처음 생성할 때 완성이 되어버리기 때문에 중간에 memberRepository를 바꿀 수 없음

    예를 들어 MemberService의 테스트 케이스를 작성할 때,
    public static void main(String[] args) {
        MemberService memberService = new MemberService();
    }
    라는 코드를 작성하면 new MemberService()에 파라미터를 주입해줘야 한다고 빨간 밑줄이 뜸
    --> 생성 시점에 얘는 이러이러한게 필요해, 이거에 의존하고 있어 하고 명확하게 알려줌.
    */

    /*생성자 주입 사용법*/
    /*
    1. 생성자가 딱 하나만 있으면 스프링이 @Autowired 어노테이션이 없어도 자동으로 주입을 해줌.
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    2. 더 이상 변경할 일이 없기 때문에 final로 해놓는 것을 권장함.
        -> 컴파일 시점에 체크를 할 수 있음

    3. Lombok의 @AllArgsConstructor 사용.
    모든 필드에 필요한 생성자를 생성해줌.

    @AllArgsConstructor
    public class MemberService {

    private final MemberRepository memberRepository;
    (생성자 없어도 됨)
    }

    4. @RequiredArgsConstructor 사용
    final이 붙은 필드만 가지고 생성자를 생성해줌.

    @RequiredArgsConstructor
    public class MemberService {

    private final MemberRepository memberRepository;
    */


}
