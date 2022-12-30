package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/* 내가 전에 배운 JpaRepository를 상속하지 않고
EntityManager로 영속성 관리를 하고 JpaRepository가 제공하는 기본 메서드를 사용하지 않는 이유는 뭘까
*/

@Repository     // 컴포넌트 스캔을 해서 스프링 빈으로 등록함
@RequiredArgsConstructor
public class MemberRepository {
    
    private final EntityManager em;


    /*Repository도 생성자 주입을 할 수 있다.*/
    // --> 근데 리파지토리에 생성자 주입을 왜 하지??????
    // -----> @PersistenceContext가 EntityManager를 주입하는 거였음..
    // --> @RequiredArgsConstructor를 쓰면 코드에 일관성을 줄 수 있고 코드의 길이도 줄일 수 있음

    /*
    EntityManager는 @PersistenceContext로 주입을 해야 하는데
    스프링 데이터 JPA가 @Autowired로도 주입할 수 있도록 지원을 해줌

    그래서 아래의 코드를
    @PersistenceContext     //JPA를 사용하기 때문에 JPA가 제공하는 표준 어노테이션인 PersistenceContext를 사용
    private EntityManager em;   // 스프링이 EntityManager를 만들어서 주입해줌

    public MemberRepository(EntityManager em) {
        this.em = em;
    }

    이렇게 바꿀 수 있다.
    @Autowired
    private EntityManager em;

    public MemberRepository(EntityManager em) {
        this.em = em;
    }

    그럼 클레스 레벨에 @RequiredArgsConstructor를 붙이고 EntityManager 필드를 final로 선언할 수 있음.
    */


    // 회원 저장
    public void save(Member member) {
        em.persist(member);             // persist(member)로 영속성 컨텍스트에 member 객체를 넣음. -> 나중에 트랜젝션이 커밋 되는 시점에 DB에 insert 쿼리가 날라감
    }

    // Member의 id로 데이터 단 건 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // Member의 모든 데이터 조회
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)     // (Jpal, 반환타입)
                .getResultList();
    }

    // 이름으로 검색하기
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
