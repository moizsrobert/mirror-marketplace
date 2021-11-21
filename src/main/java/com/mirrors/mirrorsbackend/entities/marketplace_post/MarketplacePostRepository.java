package com.mirrors.mirrorsbackend.entities.marketplace_post;

import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface MarketplacePostRepository extends JpaRepository<MarketplacePost, String> {

    @Transactional
    @Query("SELECT p FROM MarketplacePost p WHERE p.postUser=?1")
    List<MarketplacePost> findAllByUser(MarketplaceUser user);

}
