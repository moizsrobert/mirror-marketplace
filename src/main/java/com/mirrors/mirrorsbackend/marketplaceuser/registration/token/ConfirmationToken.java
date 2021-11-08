package com.mirrors.mirrorsbackend.marketplaceuser.registration.token;

import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
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
public class ConfirmationToken {

    @Id
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String token = UUID.randomUUID().toString();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "user_id")
    private MarketplaceUser marketplaceUser;

    public ConfirmationToken(
            LocalDateTime now,
            LocalDateTime plusMinutes,
            MarketplaceUser marketplaceUser) {
        this.createdAt = now;
        this.expiresAt = plusMinutes;
        this.marketplaceUser = marketplaceUser;
    }
}
