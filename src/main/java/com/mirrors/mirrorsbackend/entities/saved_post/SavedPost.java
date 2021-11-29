package com.mirrors.mirrorsbackend.entities.saved_post;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@ToString
@Entity
public class SavedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(36)")
    private String postId;

    @Column(columnDefinition = "VARCHAR(36)")
    private String userId;

    public SavedPost(String postId, String userId) {
        this.postId = postId;
        this.userId = userId;
    }
}
