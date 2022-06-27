package com.miw.gildedrose.service;

import com.miw.gildedrose.common.util.IdUtils;
import com.miw.gildedrose.entity.ItemEntity;
import com.miw.gildedrose.model.Item;
import com.miw.gildedrose.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemServiceTest {
    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private ClickEvent clickEvent;

    @Autowired
    private ItemService fixture;

    private String TEST_UUID;
    private static final int TOTAL_ITEMS = 5;

    @BeforeEach
    public void init() {
        List<ItemEntity> entities = buildTestItems(TOTAL_ITEMS);
        when(itemRepository.findAll()).thenReturn(entities);

        TEST_UUID = entities.get(0).getId();
        when(itemRepository.findById(TEST_UUID)).thenReturn(Optional.of(entities.get(0)));
    }

    @Test
    public void testFindAll() {
        List<Item> items = fixture.retrieveAllItems();

        assertNotNull(items);
        assertEquals(TOTAL_ITEMS, items.size());

        Item first = items.get(0);
        assertNotNull(first);
        assertEquals(TEST_UUID, first.getId());
    }

    @Test
    public void testRetrieveItem() {
        Item item = fixture.retrieveItem(TEST_UUID);

        assertNotNull(item);
        assertNotNull(item.getId());
        assertNotNull(item.getPrice());
        assertEquals(TEST_UUID, item.getId());
    }

    private List<ItemEntity> buildTestItems(int count) {
        List<ItemEntity> items = new ArrayList<>(count);
        for(int i=0; i<count; i++) {
            items.add(
                ItemEntity.builder()
                    .id(IdUtils.generateNewUUID())
                    .name("item " + i)
                    .description("item desc " + i)
                    .price(10 + 10*i)
                    .build()
            );
        }

        return items;
    }
}
