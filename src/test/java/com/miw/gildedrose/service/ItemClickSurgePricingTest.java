package com.miw.gildedrose.service;

import com.miw.gildedrose.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * end-to-end surge pricing test by clicking N times and check new surged price
 * with real bootstrap data in data.sql
 *
 * @author ssaqib
 */
@SpringBootTest
public class ItemClickSurgePricingTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemClickBucketProvider itemClickBucketProvider;

    @Value("${gildedRose.item.clicks}")
    private Integer clickThreshold;

    @Value("${gildedRose.pricing.increase-percent}")
    private Integer surgePercent;

    private String TEST_ITEM_ID;
    private Integer INIT_PRICE = 0;

    @BeforeEach
    public void init() {
        List<Item> allItems = itemService.retrieveAllItems();

        TEST_ITEM_ID = allItems.get(0).getId();
        INIT_PRICE = allItems.get(0).getPrice();
    }


    /**
     * Test to view item 10 times and then see if price is updated 10% or not?
     */
    @Test
    public void testSurgePrice() {
        Item item = itemService.viewItem(TEST_ITEM_ID);
        Integer expectedSurgedPrice = calculateSurgedPrice(INIT_PRICE);

        assertEquals(INIT_PRICE, item.getPrice());
        // just delay so async task completes
        sleep(10);

        Integer clickCount = itemClickBucketProvider.retrieveItemClicks(TEST_ITEM_ID);
        assertEquals(1, clickCount);

        for(int i=0; i<clickThreshold; i++) {
            // keep viewing clickThreshold times
            itemService.viewItem(TEST_ITEM_ID);
        }

        // into a little sleep as for loop on @Async might needed time to threads to be finished
        sleep(100);

        // check new price
        Item surgedItem = itemService.retrieveItem(TEST_ITEM_ID);

        assertTrue(surgedItem.getPrice() > INIT_PRICE);

        assertEquals(expectedSurgedPrice, surgedItem.getPrice());
    }

    private Integer calculateSurgedPrice(Integer price) {
        return (int) Math.round(price * (1 + surgePercent/100.0));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignored
        }
    }

}
