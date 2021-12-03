package com.mirrors.mirrorsbackend.model.marketplace_post;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
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
    @Column(columnDefinition = "VARCHAR(512)")
    private String postDescription;
    @Enumerated(EnumType.STRING)
    private CategoryEnum postCategory;
    private LocalDateTime postCreatedAt;
    @Column(columnDefinition = "VARCHAR(36)", nullable = false)
    private String userId;
}
