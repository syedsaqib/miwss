package com.miw.gildedrose.entity;

import com.miw.gildedrose.common.util.IdUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

/**
 * Base database entity object for all entities in system
 *
 * @author ssaqib
 * @since v0.1
 */
@Data
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public class BaseDomainEntity {

    @EqualsAndHashCode.Include
    @Id
    @Column(updatable = false)
    private String id;

    /**
     * assign a unique uuid to id only when inserting (not updating)
     */
    @PrePersist
    public void onPreInsert() {
        if (id == null || id.isBlank()) {
            id = IdUtils.generateNewUUID();
        }
    }
}
