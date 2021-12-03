package com.mirrors.mirrorsbackend.controller.post;

import com.mirrors.mirrorsbackend.model.marketplace_post.MarketplacePost;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUser;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponse {
    private final String id;
    private final String title;
    private final String description;
    private final String country;
    private final Double price;
    private final List<String> imagePaths;

    public PostResponse(MarketplacePost post, MarketplaceUser user, List<String> imagePaths) {
        this.id = post.getPostId();
        this.title = post.getPostName();
        this.description = post.getPostDescription();
        this.country = user.getCountry().getDisplayName();
        this.price = post.getPostPrice();
        this.imagePaths = imagePaths;
    }
}
