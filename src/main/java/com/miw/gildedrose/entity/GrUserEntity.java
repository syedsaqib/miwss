package com.miw.gildedrose.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * GR user entity, with unique index on userName
 *
 * @author ssaqib
 * @since v0.1
 */
@Entity
@Table(name = "users",
        indexes = {
            @Index(name = "idx_users_username", unique = true, columnList = "userName")
        })
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class GrUserEntity extends BaseDomainEntity {

    @Column(name ="username")
    private String userName;

    @Column
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column
    private String role; // CUSTOMER, ADMIN, SUPER_ADMIN etc

}
