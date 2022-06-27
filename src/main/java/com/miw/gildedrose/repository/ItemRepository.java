package com.miw.gildedrose.repository;


import com.miw.gildedrose.entity.ItemEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Item(s)
 *
 * @author ssaqib
 * @since v0.1
 */
@Repository
public interface ItemRepository extends PagingAndSortingRepository<ItemEntity, String> {

}
