package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;
    private MemberRepository memberRepository;
    private ItemRepository itemRepository;

    /*
     * 주문
     * */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        /* 생성 메서드를 누구는 createOrderItem()로 쓰고,
        누구는 OrderItem orderItem = new OrderItem();로 만들 수 있음
        --> 한 가지 스타일로 하는 게 좋음. 유지보수에 좋음.
        * */

        /*
        [new OrderItem() 못 하게 막는 법]:

        OrderItem.java에
        1. 기본 생성자를 protected로 만들면 됨.
        ---> protected OrderItem() { }

        2. 럼복을 사용하면 됨
        ---> @NoArgsConstructor(access = AccessLevel.PROTECTED)

        ==> 이렇게 하면 new OrderItem(); 했을 때 빨간색 밑줄이 뜨면서, '직접 생성하면 안 되고 다른 스타일로 생성해야 하는구나. 보니까 생성 메서드가 있네.' 이렇게 생각할 수 있게 됨.
        */

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);       // 이 예제에서는 주문 상품을 하나만 선택할 수 있도록 단순화 했음.

        // 주문 저장
        orderRepository.save(order);

        return order.getId();
    } 
    
    /* cascade 설정을 해놔서 orderRepository.save(order); 하면 orderItem이랑 delivery까지 자동으로 persist 된다. */

    /* cascade의 범위: 한 life Cycle안에 있을 때.
    이 예제에서는 orderItem과 delivery는 order에서만 쓰인다. --> cascade 설정 ok.
    만약 다른 곳에서도 orderItem과 delivery를 참조하면 cascade 설정 걸면 안 됨.
    다른 곳에서 delivery를 쓰는데 cascade를 걸어 놓으면, order에서 delivery를 건드렸을 때 다른 곳에서 쓰이는 delivery도 바뀜
    ---> 별도의 repository를 만들어서 써야 한다.

    언제 어디서 persist 해야 하는지 정확하게 이해 되지 않으면 안 쓰는게 낫다.
    나중에 완벽하게 다 파악한 후 리팩토링을 하자.
    * */


    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        order.cancel();
    }

    // 주문 검색
//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
//    }
    
    
    /* 
    * PDF의 55쪽 도메인 모델 패턴 잘 읽어보기
    * */
}
