package com.mirrors.mirrorsbackend.registration.token;

import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConfirmationToken {

    @SequenceGenerator(
            name = "confirmation_token_sequence",
            sequenceName = "confirmation_token_sequence",
            allocationSize = 1)
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "confirmation_token_sequence")
    private Long id;

    @Column(nullable = false)
    private String token;

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
            String token,
            LocalDateTime now,
            LocalDateTime plusMinutes,
            MarketplaceUser marketplaceUser) {
        this.token = token;
        this.createdAt = now;
        this.expiresAt = plusMinutes;
        this.marketplaceUser = marketplaceUser;
    }
}
