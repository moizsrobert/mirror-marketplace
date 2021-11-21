package com.mirrors.mirrorsbackend.entities.marketplace_post;

import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class MarketplacePost {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String postId = UUID.randomUUID().toString();
    @Column(columnDefinition = "VARCHAR(150)", nullable = false)
    private String postName;
    @Column(columnDefinition = "DOUBLE", nullable = false)
    private Double postPrice;
    @Column(columnDefinition = "VARCHAR(255)")
    private String postDescription;
    @Enumerated(EnumType.STRING)
    private CategoryEnum postCategory;
    private LocalDateTime postCreatedAt;
    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private MarketplaceUser postUser;
}
