package com.mirrors.mirrorsbackend.mvc.post;

import com.mirrors.mirrorsbackend.entities.marketplace_post.CategoryEnum;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PostRequest {
    private String postName;
    private String postPrice;
    private String postDescription;
    private CategoryEnum postCategory;
    private MultipartFile[] postImages;

    public PostRequest(String postName, String postPrice, String postDescription, CategoryEnum postCategory, MultipartFile[] postImages) {
        this.postName = postName;
        this.postPrice = postPrice;
        if (postDescription.isBlank())
            this.postDescription = "";
        else
            this.postDescription = postDescription;
        this.postCategory = postCategory;
        this.postImages = postImages;
    }
}
