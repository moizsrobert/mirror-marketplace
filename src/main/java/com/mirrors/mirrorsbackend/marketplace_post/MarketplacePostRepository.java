package com.mirrors.mirrorsbackend.marketplace_post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface MarketplacePostRepository extends JpaRepository<MarketplacePost, Long> {

    Optional<MarketplacePost> findByPostId(String id);



}
