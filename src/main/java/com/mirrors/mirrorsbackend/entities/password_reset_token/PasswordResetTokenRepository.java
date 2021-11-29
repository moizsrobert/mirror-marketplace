package com.mirrors.mirrorsbackend.entities.password_reset_token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    @Transactional
    @Modifying
    @Query("UPDATE PasswordResetToken c SET c.changedAt = ?2 WHERE c.token = ?1")
    void updateChangedAt(String token, LocalDateTime confirmedAt);
}
