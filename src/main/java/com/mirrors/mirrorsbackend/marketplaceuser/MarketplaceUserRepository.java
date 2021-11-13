package com.mirrors.mirrorsbackend.marketplaceuser;

import com.mirrors.mirrorsbackend.page.settings.CountryEnum;
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

    @Transactional
    @Modifying
    @Query("UPDATE MarketplaceUser u SET u.password = ?2 WHERE u.email = ?1")
    int changePassword(String email, String newPassword);

    @Transactional
    @Modifying
    @Query("UPDATE MarketplaceUser u SET " +
            "u.displayName = ?1, " +
            "u.firstName = ?2, " +
            "u.lastName = ?3, " +
            "u.phoneNumber = ?4, " +
            "u.country = ?5, " +
            "u.city = ?6, " +
            "u.streetAddress = ?7, " +
            "u.zipCode = ?8 " +
            "WHERE u.id = ?9")
    int changeSettings(String displayName,
                       String firstName,
                       String lastName,
                       String phoneNumber,
                       CountryEnum country,
                       String city,
                       String streetAddress,
                       String zipCode,
                       String id);
}
