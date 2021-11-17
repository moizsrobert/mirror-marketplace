package com.mirrors.mirrorsbackend.mvc.newpost;

import com.mirrors.mirrorsbackend.marketplacepost.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class NewPostRequest {
    private String postName;
    private String postPrice;
    private String postDescription;
    private CategoryEnum postCategory;
    private MultipartFile[] postImages;
}
