package com.miw.gildedrose.api;

import com.miw.gildedrose.exception.GildedRoseException;
import com.miw.gildedrose.model.Item;
import com.miw.gildedrose.model.response.GrResponse;
import com.miw.gildedrose.model.request.ItemBuyRequest;
import com.miw.gildedrose.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping(path = "/view/{itemId}")
    public GrResponse<Item> viewSingleItem(@PathVariable String itemId) throws GildedRoseException {
        Item item = itemService.viewItem(itemId);

        return GrResponse.success(item);
    }

    @GetMapping(path = {"", "/", "/all"})
    public GrResponse<List<Item>> viewAllItems() throws GildedRoseException {
        List<Item> items = itemService.retrieveAllItems();

        return GrResponse.success(items);
    }

    @PostMapping(path = "/buy", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GrResponse<Item> buyItem(@RequestBody ItemBuyRequest request) throws GildedRoseException {
        Item item = itemService.buyItem(request);

        return GrResponse.success(item);
    }

}
