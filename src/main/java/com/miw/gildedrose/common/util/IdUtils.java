package com.miw.gildedrose.common.util;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class to generate UUID
 *
 * @author ssaqib
 * @since v0.1
 *
 * Moved doWithSyncMap from service to here
 * @since v0.2
 */
public final class IdUtils {
    private IdUtils() {

    }

    /**
     * Randomly generated uuid as string
     */
    public static String generateNewUUID() {
        return UUID.randomUUID().toString();
    }

    public static String generateRandomUUIDNoDashes() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * just a common map synchronization.
     * Moved from service to common area
     */
    public static <K,V> Map<K, V> doWithSyncMap(Map<K,V> anyMap) {
        return Collections.synchronizedMap(anyMap);
    }

}
