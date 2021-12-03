package com.mirrors.mirrorsbackend.model.marketplace_user;

import com.mirrors.mirrorsbackend.controller.profile.CountryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface MarketplaceUserRepository extends JpaRepository<MarketplaceUser, String> {

    Optional<MarketplaceUser> findByEmail(String email);

    @Transactional
    @Query("SELECT u FROM MarketplaceUser u WHERE u.country=?1")
    List<MarketplaceUser> findUsersFromSameCountry(CountryEnum country);

    @Transactional
    @Modifying
    @Query("UPDATE MarketplaceUser u SET u.enabled = TRUE WHERE u.email = ?1")
    void enableMarketplaceUser(String email);

    @Transactional
    @Modifying
    @Query("UPDATE MarketplaceUser u SET u.locked = TRUE WHERE u.id = ?1")
    void lockMarketplaceUser(String userId);

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
            "u.city = ?2 " +
            "WHERE u.id = ?3")
    void changeShippingAddress(CountryEnum country, String city, String id);
}
