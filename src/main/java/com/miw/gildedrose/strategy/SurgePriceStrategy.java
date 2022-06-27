package com.miw.gildedrose.strategy;

import com.miw.gildedrose.model.Item;
import com.miw.gildedrose.repository.ItemRepository;
import com.miw.gildedrose.service.ItemClickBucketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

/**
 * A surge price strategy on item
 *
 * @author ssaqib
 */
@RequiredArgsConstructor
@Slf4j
public class SurgePriceStrategy implements ItemPriceStrategy {

    private final ItemClickBucketProvider itemClickProvider;
    private final ItemRepository itemRepository;

    private final Object lock = new Object();

    @Value("${gildedRose.item.clicks:1000}")
    private Integer clickThreshold;

    @Value("${gildedRose.pricing.increase-percent:0}")
    private int priceIncrementPercent;

    @Transactional
    @Override
    public Integer apply(Item item) {
        Integer surgePrice = item != null ? item.getPrice() : null;

        if (item != null && itemClickProvider.retrieveItemClicks(item.getId()) >= clickThreshold) {
            itemClickProvider.resetItemClicks(item.getId());

            surgePrice = (int) Math.round(item.getPrice() * (1 + priceIncrementPercent/100.0));
            log.debug("---> item: [{}] click threshold is >= {} so increasing price to new price = {} ",
                    item.getName(), clickThreshold, surgePrice);

            updateItemPrice(item, surgePrice);
        }

        return surgePrice;
    }

    private boolean updateItemPrice(Item item, Integer surgePrice) {
        // only update in db when price is changed
        try {
            if (surgePrice != null && !surgePrice.equals(item.getPrice())) {
                synchronized (lock) {
                    itemRepository.findById(item.getId())
                            .ifPresent(dbEntity -> {
                                // check again due to async update on clicks
                                if (!surgePrice.equals(dbEntity.getPrice())) {
                                    dbEntity.setPrice(surgePrice);
                                    itemRepository.save(dbEntity);
                                    log.debug("----> updated item: {} price to new price: {}", item.getName(), surgePrice);
                                }
                            });
                }
                return true;
            }
        } catch (Exception e) {
            log.error("---> Error while updating item: {}'s surge price = {}", item, surgePrice, e);
        }

        return false;
    }
}
