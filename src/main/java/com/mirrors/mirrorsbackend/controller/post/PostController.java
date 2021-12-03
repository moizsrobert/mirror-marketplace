package com.mirrors.mirrorsbackend.controller.post;

import com.mirrors.mirrorsbackend.model.marketplace_post.CategoryEnum;
import com.mirrors.mirrorsbackend.model.marketplace_post.MarketplacePost;
import com.mirrors.mirrorsbackend.model.marketplace_post.MarketplacePostService;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserService;
import com.mirrors.mirrorsbackend.model.saved_post.SavedPost;
import com.mirrors.mirrorsbackend.model.saved_post.SavedPostService;
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
        return marketplacePostService.getPostWithImagesById(postId);
    }

    @GetMapping("/latest")
    public List<PostResponse> getLatestPosts() {
        return marketplacePostService.getLatestPosts();
    }

    @GetMapping("/random_fill")
    public List<PostResponse> getRandomPosts() {
        return marketplacePostService.getRandomPosts();
    }

    @PostMapping("/recommended_fill")
    public List<PostResponse> getRecommendedPosts(CategoryEnum category) {
        return marketplacePostService.getPostsByCategory(category);
    }

    @PostMapping("/search")
    public List<PostResponse> searchPosts(SearchRequest searchRequest) {
        return marketplacePostService.getPostsBySearchRequest(searchRequest);
    }

    @GetMapping("/my-posts")
    public List<Pair<MarketplacePost, List<String>>> getMyPosts() {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        return marketplacePostService.getPostsOfUserWithImages(user);
    }

    @PostMapping("/new-post")
    public void createNewPost(PostRequest postRequest) {
        marketplacePostService.createPost(postRequest);
    }

    @PostMapping("/delete-post")
    public void deletePost(String postId) {
        marketplacePostService.deletePost(postId);
    }

    @PostMapping("/save-post")
    public void savePost(String postId) {
        savedPostService.addToSavedPosts(postId);
    }

    @PostMapping("/remove-saved-post")
    public void removeSavedPost(String postId) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        System.out.println(postId);
        savedPostService.removeFromSavedPosts(postId, user.getId());
    }

    @PostMapping("/is-post-saved")
    public boolean isPostSaved(String postId) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        SavedPost savedPost = new SavedPost(user.getId(), postId);
        return savedPostService.isPostSaved(savedPost);
    }

    @GetMapping("/saved-posts")
    public List<Pair<MarketplacePost, List<String>>> getSavedPosts() {
        List<String> postIds = savedPostService.getSavedPostIDs();
        return marketplacePostService.getPostsWithImagesByIds(postIds);
    }
}
