package com.miw.gildedrose.repository;

import com.miw.gildedrose.entity.GrUserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for GR Users
 *
 * @author ssaqib
 * @since v0.1
 */
@Repository
public interface GrUserRepository extends PagingAndSortingRepository<GrUserEntity, String> {

    Optional<GrUserEntity> findByUserName(String userName);

    Optional<GrUserEntity> findByUserNameAndPassword(String userName, String password);
}
