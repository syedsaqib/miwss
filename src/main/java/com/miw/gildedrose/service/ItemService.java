package com.miw.gildedrose.service;

import com.miw.gildedrose.common.util.SecurityContextUtils;
import com.miw.gildedrose.entity.ItemEntity;
import com.miw.gildedrose.exception.ApiObjectNotFoundException;
import com.miw.gildedrose.exception.GildedRoseException;
import com.miw.gildedrose.exception.GrBadRequestException;
import com.miw.gildedrose.exception.GrErrorCode;
import com.miw.gildedrose.model.Item;
import com.miw.gildedrose.model.request.ItemBuyRequest;
import com.miw.gildedrose.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Main item service
 *
 * @author ssaqib
 * @since v0.1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ClickEvent clickEvent;

    @Transactional
    public Item viewItem(String itemId) throws GildedRoseException {
        Item item = retrieveItem(itemId);
        if (item != null) {
            clickEvent.itemClicked(item);
        }

        return item;
    }

    @Transactional
    public List<Item> retrieveAllItems() throws GildedRoseException {
        Iterable<ItemEntity> entities = itemRepository.findAll();

        List<Item> items = StreamSupport.stream(entities.spliterator(), false)
                            .map(this::mapEntity)
                            .collect(Collectors.toList());

        log.debug("--> GET all items size = {}", items.size());
        return items;
    }

    /**
     * Retrieves an item given itemId.
     * Different from view Item as view is only called from controller (UI / rest) to increment clicks
     * @param itemId the uuid of item
     * @return the {@link Item}
     * @throws GildedRoseException
     */
    @Transactional
    public Item retrieveItem(String itemId) throws GildedRoseException {
        try {

            Optional<ItemEntity> itemEntity = itemRepository.findById(itemId);

            Item item = mapEntity(itemEntity.orElseThrow(ApiObjectNotFoundException::new));
            log.debug("retrieved item = {}", item);

            return item;
        } catch (ApiObjectNotFoundException afe) {
            throw afe;
        } catch (Exception e) {
            log.error("--> Database Error: {}", e.getMessage(), e);
            throw GildedRoseException.of(GrErrorCode.DATABASE_ERROR);
        }
    }

    /**
     * Buy an item by logged in user who has role CUSTOMER
     */
    @Transactional
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public Item buyItem(ItemBuyRequest request) throws GildedRoseException {
        log.debug("--> buy item request: {}, by user: {}", request, SecurityContextUtils.getLoggedInUsername());

        ItemEntity itemEntity = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> GrBadRequestException.of(GrErrorCode.OBJECT_NOT_FOUND));

        Integer qtyOnHand = itemEntity.getQuantity();
        if (qtyOnHand < request.getQuantity())       {
            throw GrBadRequestException.of(GrErrorCode.INSUFFICIENT_QUANTITY);
        }

        Integer newQty = qtyOnHand >= 1 ? (qtyOnHand-1) : 0;
        itemEntity.setQuantity(newQty);
        itemRepository.save(itemEntity);

        // TODO: not part of exercise project to create an order but we can by having user in context and item + quantity
        // so Order class having GrUser (customer), Item and quantity

        return mapEntity(itemEntity);
    }

    private Item mapEntity(ItemEntity entity) {
        return entity == null ? null
                : Item.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .price(entity.getPrice())
                    .quantity(entity.getQuantity())
                    .build();
    }

    private ItemEntity mapModel(Item item) {
        return item == null ? null
                : ItemEntity.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .description(item.getDescription())
                    .build();

    }

}
