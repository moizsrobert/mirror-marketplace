package com.mirrors.mirrorsbackend.marketplaceuser.login.passwordreset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE PasswordResetToken c SET c.changedAt = ?2 WHERE c.token = ?1")
    int updateChangedAt(String token, LocalDateTime confirmedAt);
}
