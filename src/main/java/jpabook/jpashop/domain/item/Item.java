package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /* 비즈니스 로직 */
    /* 도메인 주도 설계를 할 때 엔티티 자체에서 해결할 수 있는 것들은 엔티티 안에 비즈니스 로직을 넣는게 좋음 -> 객체지향적임
    * stockQuantity라는 재고수량이 Item 엔티티 안에 있음
    * 그러면 이 stockQuantity 데이터를 가지고 있는 데서 Item에서 비즈니스 로직을 처리하는 게 좋음 */

    /*
    * stock 증가
    */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /*
     * stock 감소
     * */
    public void removeStock(int quantity) {
        // 재고가 0보다 적으면 안됨.
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }


}

