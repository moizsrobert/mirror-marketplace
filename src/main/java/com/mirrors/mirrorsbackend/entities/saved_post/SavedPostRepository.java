package com.mirrors.mirrorsbackend.entities.saved_post;

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
    @Modifying
    @Query("DELETE FROM SavedPost sp WHERE sp.userId = ?1")
    List<SavedPost> deleteAllByUserID(String userId);
}
