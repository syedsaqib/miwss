package com.miw.gildedrose.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.map.PassiveExpiringMap;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A provider for item clicks per hour
 *
 * @author ssaqib
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemClickBucketProvider {
    // default expire after 60 minutes i.e. 1 hour
    @Value("${gildedRose.item.expireAfterMinutes:60}")
    private long expireAfterMinutes;

    // have a guava expiring cache that expires every x HOUR
    // this would implement kind of token bucket per hour
    private Map<String, Integer> itemClickBucket;

    @PostConstruct
    public void init() {
        long expireMillis = TimeUnit.MINUTES.toMillis(expireAfterMinutes);
        log.debug("----> item click bucket is set to expire after: {} millis", expireMillis);

        // common collections 4 has a map whose individual keys expire after given millis
        itemClickBucket = new PassiveExpiringMap<>(expireMillis);

        /*
         * Note note note: after some testing realized that PassiveExpiringMap has an issue that it increments expiring time
         * on new put. ideally we wanted to keep the same expiring time after first put so this would give an issue
         * if 10th click is for instance at 59th minute, then the bucket expiry would go to 1:59 hour instead of 1 hour
         */
    }

    public Integer retrieveItemClicks(String itemId) {
        Integer clickCount = doWithSyncMap(itemClickBucket).get(itemId);
        return clickCount == null ? 0 : clickCount;
    }

    public Integer incrementItemClick(String itemId) {
        // put 1 as new value and if exist then sum(old + 1) and put that new value
        return doWithSyncMap(itemClickBucket).merge(itemId, 1, Integer::sum);
    }

    /**
     * Removes the item from bucket.
     * called when threshold is reached
     */
    public void resetItemClicks(String itemId) {
        doWithSyncMap(itemClickBucket).remove(itemId);
    }

    private Map<String, Integer> doWithSyncMap(Map<String,Integer> bucket) {
        return Collections.synchronizedMap(bucket);
    }

}
