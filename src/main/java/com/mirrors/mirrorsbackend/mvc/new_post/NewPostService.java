package com.mirrors.mirrorsbackend.mvc.new_post;

import com.mirrors.mirrorsbackend.entities.marketplace_post.CategoryEnum;
import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePost;
import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePostRepository;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.utils.file.ImageStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@AllArgsConstructor
public class NewPostService {

    final MarketplacePostRepository marketplacePostRepository;
    final ImageStorage imageStorage;

    public ModelAndView createNewPost(NewPostRequest postRequest, MarketplaceUser user) {
        if (!postRequest.getPostName().matches("[^,:;=?@#|'<>^]*.{4,128}$"))
            throw new IllegalStateException("Invalid post name!");

        MarketplacePost post = new MarketplacePost();
        short numberOfImages;
        if (postRequest.getPostImages()[0].isEmpty())
            numberOfImages = 0;
        else
            numberOfImages = (short) postRequest.getPostImages().length;
        double postPrice;

        if (numberOfImages > 8)
            throw new IllegalStateException("You cannot upload more than 8 images!");

        try {
            postPrice = Double.parseDouble(postRequest.getPostPrice().replace(",", "").replace("$", ""));
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            throw new IllegalStateException("Invalid price!");
        }

        if (postPrice < 0)
            throw new IllegalStateException("Invalid price!");

        if (postRequest.getPostDescription().length() > 255)
            throw new IllegalStateException("Description has to be less than 256 characters!");

        if (Arrays.stream(CategoryEnum.values()).noneMatch((s) -> s.name().equals(postRequest.getPostCategory().name())))
            throw new IllegalStateException("Category doesn't exist!");

        for (short i = 0; i < numberOfImages; i++) {
            MultipartFile image = postRequest.getPostImages()[i];
            imageStorage.save(post.getPostId(), image, i);
        }

        post.setPostName(postRequest.getPostName());
        post.setPostPrice(postPrice);
        post.setPostDescription(postRequest.getPostDescription());
        post.setPostCategory(postRequest.getPostCategory());
        post.setPostCreatedAt(LocalDateTime.now());
        post.setPostUser(user);
        marketplacePostRepository.save(post);

        return new ModelAndView("redirect:post?id=" + post.getPostId());
    }

}
