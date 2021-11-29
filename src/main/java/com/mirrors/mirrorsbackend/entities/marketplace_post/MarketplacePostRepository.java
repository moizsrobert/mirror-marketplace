package com.mirrors.mirrorsbackend.entities.marketplace_post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface MarketplacePostRepository extends JpaRepository<MarketplacePost, String> {

    @Transactional
    @Query("SELECT p FROM MarketplacePost p WHERE p.userId=?1")
    List<MarketplacePost> findAllByUserID(String userId);

    @Transactional
    @Modifying
    @Query("UPDATE MarketplacePost p SET p.postViews = p.postViews + 1 WHERE p.postId = ?1")
    void incrementViews(String postId);

}
