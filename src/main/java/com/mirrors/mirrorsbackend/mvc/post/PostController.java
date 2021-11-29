package com.mirrors.mirrorsbackend.mvc.post;

import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePost;
import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePostService;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserService;
import com.mirrors.mirrorsbackend.entities.saved_post.SavedPost;
import com.mirrors.mirrorsbackend.entities.saved_post.SavedPostService;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final MarketplaceUserService marketplaceUserService;
    private final MarketplacePostService marketplacePostService;
    private final SavedPostService savedPostService;

    @GetMapping("/view")
    public Pair<MarketplacePost, List<String>> viewPost(@RequestParam(value = "id", required = false) String postId) {
        marketplacePostService.incrementMarketplacePostViews(postId);
        return marketplacePostService.getMarketplacePostWithImages(postId);
    }

    @GetMapping("/my-posts")
    public List<Pair<MarketplacePost, List<String>>> getPosts() {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        return marketplacePostService.getMarketplacePostsOfUserWithImages(user);
    }

    @PostMapping("/new-post")
    public void createNewPost(PostRequest postRequest) {
        marketplacePostService.createMarketplacePost(postRequest);
    }

    @PostMapping("/delete-post")
    public void deletePost(String postId) {
        marketplacePostService.deleteMarketplacePost(postId);
    }

    @PostMapping("/save-post")
    public void savePost(String postId) {
        savedPostService.addToSavedPosts(postId);
    }

    @PostMapping("/is-post-saved")
    public boolean isPostSaved(String postId) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        SavedPost savedPost = new SavedPost(user.getId(), postId);
        return savedPostService.isPostSaved(savedPost);
    }

    @GetMapping("/saved-posts")
    public List<MarketplacePost> getSavedPosts() {
        return savedPostService.getSavedPosts();
    }
}
