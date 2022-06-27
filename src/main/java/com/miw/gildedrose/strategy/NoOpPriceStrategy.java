package com.miw.gildedrose.strategy;

import com.miw.gildedrose.model.Item;

/**
 * A No operation i.e. keeps same price of the item
 *
 * @author ssaqib
 * @since v0.1
 */
public class NoOpPriceStrategy implements ItemPriceStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer apply(Item item) {
        if (item == null) {
            return null;
        }

        // no op is to return same price i.e. no change in price
        return item.getPrice();
    }
}
