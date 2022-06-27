package com.miw.gildedrose.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class to buy item
 *
 * @author ssaqib
 */
@Data
@NoArgsConstructor
public class ItemBuyRequest {
    private String userId;
    private String itemId;
    private Integer quantity;
}
