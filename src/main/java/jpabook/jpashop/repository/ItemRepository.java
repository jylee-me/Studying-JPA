package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    /*
    * 아이템 등록
    * */
    public void save(Item item) {
        if (item.getId() == null) {     // item은 persist 하기 전까진 id 값이 없음. 새 아이템이면 null임. null이 아니면 디비에 저장된 값이 있다는 거.
            em.persist(item);       // 아이템 신규 등록
        } else {
            em.merge(item);     // update 같은 거
        }
    }

    /*
    * 아이템 단건 조회
    * */
    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    /*
    * 아이템 전체 조회
    * */
    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
