package com.miw.gildedrose.strategy;

import com.miw.gildedrose.model.Item;

/**
 * Strategy to apply on item's price
 *
 * @author ssaqib
 * @since v0.1
 */
public interface ItemPriceStrategy {

    /**
     * Strategy to apply on item's price
     * @param item the item on whose price needs to be updated
     * @return new price based on concrete implementation of strategy
     */
    Integer apply(Item item);
}
