package com.mirrors.mirrorsbackend.marketplaceuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface MarketplaceUserRepository extends JpaRepository<MarketplaceUser, Long> {

    Optional<MarketplaceUser> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE MarketplaceUser u SET u.enabled = TRUE WHERE u.email = ?1")
    int enableMarketplaceUser(String email);
}
