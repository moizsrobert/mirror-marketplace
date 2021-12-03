package com.mirrors.mirrorsbackend.model.marketplace_post;

import org.springframework.data.jpa.repository.JpaRepository;
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
    @Query("SELECT p FROM MarketplacePost p WHERE p.postName LIKE CONCAT('%',?1,'%')")
    List<MarketplacePost> findByTitleIgnoreCase(String title);

    @Transactional
    @Query("SELECT p FROM MarketplacePost p WHERE p.postCategory=?1")
    List<MarketplacePost> findAllByCategory(CategoryEnum category);

    @Transactional
    @Query(nativeQuery = true, value = "SELECT * FROM marketplace.marketplace_post WHERE post_category=?2 ORDER BY RAND() LIMIT ?1")
    List<MarketplacePost> findRandomByCategory(int limit, String categoryName);

    @Transactional
    @Query("SELECT p FROM MarketplacePost p WHERE p.postName LIKE CONCAT('%',?1,'%') AND p.postCategory=?2")
    List<MarketplacePost> findByTitleAndCategoryIgnoreCase(String title, CategoryEnum category);

    @Transactional
    @Query(nativeQuery = true, value = "SELECT * FROM marketplace.marketplace_post ORDER BY post_created_at DESC LIMIT ?1")
    List<MarketplacePost> findByLatest(int limit);

    @Transactional
    @Query(nativeQuery = true, value = "SELECT * FROM marketplace.marketplace_post WHERE user_id IN ?2 ORDER BY RAND() LIMIT ?1")
    List<MarketplacePost> findByRandomFromUsers(int limit, List<String> usersIdsFromSameCountry);

    @Transactional
    @Query(nativeQuery = true, value = "SELECT * FROM marketplace.marketplace_post WHERE user_id NOT IN ?2 ORDER BY RAND() LIMIT ?1")
    List<MarketplacePost> findByRandomNotFromUsers(int limit, List<String> usersIdsFromSameCountry);
}
