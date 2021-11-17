package com.mirrors.mirrorsbackend.marketplaceuser;

import com.mirrors.mirrorsbackend.mvc.settings.CountryEnum;
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
    void enableMarketplaceUser(String email);

    @Transactional
    @Modifying
    @Query("UPDATE MarketplaceUser u SET u.password = ?1 WHERE u.id = ?2")
    void changePassword(String newPassword, String id);

    @Transactional
    @Modifying
    @Query("UPDATE MarketplaceUser u SET " +
            "u.displayName = ?1, " +
            "u.firstName = ?2, " +
            "u.lastName = ?3 " +
            "WHERE u.id = ?4")
    void changePersonalInfo(String displayName, String firstName, String lastName, String id);

    @Transactional
    @Modifying
    @Query("UPDATE MarketplaceUser u SET " +
            "u.phoneNumber = ?1 " +
            "WHERE u.id = ?2")
    void changePhoneNumber(String phoneNumber, String id);

    @Transactional
    @Modifying
    @Query("UPDATE MarketplaceUser u SET " +
            "u.country = ?1, " +
            "u.city = ?2, " +
            "u.streetAddress = ?3, " +
            "u.zipCode = ?4 " +
            "WHERE u.id = ?5")
    void changeShippingAddress(CountryEnum country, String city, String streetAddress, String zipCode, String id);
}
