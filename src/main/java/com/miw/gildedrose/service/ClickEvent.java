package com.miw.gildedrose.service;

import com.miw.gildedrose.model.Item;
import com.miw.gildedrose.strategy.ItemPriceStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Event class that provides async functionality for clicking an item.
 *  could be spring ApplicationEvent but for now it is fine with @Async
 *
 * @author ssaqib
 * @since v0.1
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ClickEvent {
    private final ItemClickBucketProvider itemClickBucketProvider;
    private final ItemPriceStrategy itemPriceStrategy;

    /**
     * Asynchronous method so our main flow won't impact with just incrementing the count of clicks
     */
    @Async
    public void itemClicked(Item item) {
        Assert.notNull(item, "Item can't be null on click event!");

        long clickCount = itemClickBucketProvider.incrementItemClick(item.getId());
        log.debug("incrementing item {} click count to: {}", item.getName(), clickCount);

        itemPriceStrategy.apply(item);
    }

}
