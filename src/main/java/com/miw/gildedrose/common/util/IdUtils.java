package com.miw.gildedrose.common.util;

import java.util.UUID;

/**
 * Utility class to generate UUID
 *
 * @author ssaqib
 * @since v0.1
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

}
