package com.mirrors.mirrorsbackend.model.saved_post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {
    @Transactional
    @Query("SELECT sp FROM SavedPost sp WHERE sp.userId = ?1")
    List<SavedPost> findAllByUserID(String userId);

    @Transactional
    @Query("SELECT CASE WHEN COUNT(sp) > 0 THEN TRUE ELSE FALSE END FROM SavedPost sp WHERE sp.postId = ?1 AND sp.userId = ?2")
    boolean existsByIDs(String postId, String userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM SavedPost sp WHERE sp.userId = ?1")
    void deleteAllByUserID(String userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM SavedPost sp WHERE sp.postId = ?1")
    void deleteAllByPostID(String postId);

    @Transactional
    @Modifying
    @Query("DELETE FROM SavedPost sp WHERE sp.postId = ?1 AND sp.userId = ?2")
    void deleteByIDs(String postId, String userId);
}
