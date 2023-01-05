package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    /*
     * 주문 정보 저장
     * */
    public void save(Order order) {
        em.persist(order);
    }

    /*
     * 주문 단건 조회
     * */
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }


    /*
     * JPA에서는 동적 쿼리를 어떻게 처리할까?
     * (MyBatis는 xml로 동적 쿼리를 처리함)
     * */

    /*
     * 방법 1: !! 실무에선 안 씀 !!
     * */

    /*
     * JPQL 쿼리를 문자로 생성하기는 번거롭고, 실수로 인한 버그가 충분히 발생할 수 있다.
     * */
    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                if (isFirstCondition) {
                    jpql += " where";
                    isFirstCondition = false;
                } else {
                    jpql += " and";
                }
                jpql += " o.name = :name";
            }
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);       // 최대 1000개까지만 조회됨.

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }


    /*
     * 방법 2: 권장 XXX
     * JPA Criteria로 처리
     * */

    /*
    * 김영한님 생각: 실무를 많이 안 해봤거나, 생각이 너무 많은 사람이 한 것 같음.
    *
    * ---> 유지 보수성이 거의 제로에 가까움.
    * 아래 쿼리를 보면 어떤 쿼리가 생성될 지 머리에 떠오르지 않음.
    * JPA 표준 스펙이지만 실무에서는 안 씀.
    * */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건

        return query.getResultList();
    }

    /*
     * 방법 3: Querydsl로 처리
     * 실무에서 쓰는 방법
     * JPA 활용 2편에서 알려줌
     * */
//    public List<Order> findAll(OrderSearch orderSearch) {
//
//    }


    /* 값이 다 있다는 가정 하에 */
    /* 그게 아니라면 위처럼 동적 쿼리를 만들어야 함. */
    /* em.createQuery("select o from Order o join o.member m"
                        + "where o.status = :status"    // status가 null이 아닌 다른 값을 무조건 가지고 있으면 필터 조건으로 쓸 수 있음
                        + "and m.name like :name",      // name이 null이 아닌 다른 값을 무조건 가지고 있으면 필터 조건으로 쓸 수 있음
                        Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000)    // 최대 1000개까지만 조회됨.
                .getResultList();
    */

}

