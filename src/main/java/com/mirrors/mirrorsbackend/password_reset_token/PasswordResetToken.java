package com.mirrors.mirrorsbackend.password_reset_token;

import com.mirrors.mirrorsbackend.marketplace_user.MarketplaceUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class PasswordResetToken {

    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String token = UUID.randomUUID().toString();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime changedAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private MarketplaceUser marketplaceUser;

    public PasswordResetToken(
            LocalDateTime now,
            LocalDateTime plusMinutes,
            MarketplaceUser marketplaceUser) {
        this.createdAt = now;
        this.expiresAt = plusMinutes;
        this.marketplaceUser = marketplaceUser;
    }
}
