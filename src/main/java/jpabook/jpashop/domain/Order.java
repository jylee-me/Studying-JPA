package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @JsonIgnore
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//

    /* 주문 생성하는 것은 복잡함. Order만 생성하는 것이 아니라, OrderItem도 있어야 하고 Delivery도 있어야 하고 여러 연관 관계가 얽혀있음.
    * 이런 복잡한 생성은 별도의 생성 메서드가 있으면 좋다.
    * */

    /* 가변인자
    * OrderItem... orderItems에서 '...'을 가변인자라고 한다.
    * */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);         // 처음 상태를 ORDER로 강제해놓음
        order.setOrderDate(LocalDateTime.now());    // 주문 상태는 현재 시각으로 설정
        return order;
    }
    
    /* 이렇게 생성 메서드를 작성하는 게 중요한 이유:
    * 앞으로 뭔가 생성하는 시점을 변경해야 하면 이 메서드만 바꾸면 됨. <<- 중요 포인트!
    *   -->> 근데 뭔 말임??
    * */

    //==비즈니스 로직==//
    /** 주문 취소 */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();         // 한 사람이 여러개의 아이템을 주문할 수 있고 그 아이템 각각 취소룰 해주는 메서드가 필요함.
        }
    }


    //==조회 로직==//
    /*
     * 주문 상품 전체 가격 조회
     * */
/*    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }*/

    // 람다식으로 이렇게 바꿀 수 있음
    public int getTotalPrice() {
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }



}
















