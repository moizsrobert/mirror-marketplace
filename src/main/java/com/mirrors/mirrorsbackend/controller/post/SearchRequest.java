package com.mirrors.mirrorsbackend.controller.post;

import com.mirrors.mirrorsbackend.model.marketplace_post.CategoryEnum;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SearchRequest {
    private final String title;
    private final CategoryEnum category;

    public SearchRequest(String title, CategoryEnum category) {
        this.title = Objects.requireNonNullElse(title, "");
        this.category = category;
    }
}
