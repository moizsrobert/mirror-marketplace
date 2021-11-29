package com.mirrors.mirrorsbackend.entities.saved_post;

import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePost;
import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePostService;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SavedPostService {

    final private SavedPostRepository savedPostRepository;
    final private MarketplaceUserService marketplaceUserService;
    final private MarketplacePostService marketplacePostService;

    public void addToSavedPosts(String postId) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        MarketplacePost post = marketplacePostService.getMarketplacePostById(postId);

        if (Objects.equals(user.getId(), post.getUserId()))
            throw new IllegalStateException("You cannot save your own post!");

        SavedPost savedPost = new SavedPost(user.getId(), post.getPostId());
        if (isPostSaved(savedPost))
            throw new IllegalStateException("You cannot save the same post twice!");
        savedPostRepository.save(savedPost);
    }

    public boolean isPostSaved(SavedPost savedPost) {
        return savedPostRepository.findOne(Example.of(savedPost)).isPresent();
    }

    public void removeFromSavedPosts(String postId) {
        SavedPost savedPost = new SavedPost(
                marketplacePostService.getMarketplacePostById(postId).getPostId(),
                marketplaceUserService.getUserFromContext().getId()
        );
        savedPostRepository.delete(savedPost);
    }

    public void deleteAllSavedPosts() {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        savedPostRepository.deleteAllByUserID(user.getId());
    }

    public List<MarketplacePost> getSavedPosts() {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        List<SavedPost> savedPosts = savedPostRepository.findAllByUserID(user.getId());
        List<MarketplacePost> posts = new ArrayList<>();
        for (var savedPost : savedPosts) {
            String postId = savedPost.getPostId();
            MarketplacePost post = marketplacePostService.getMarketplacePostById(postId);
            posts.add(post);
        }
        return posts;
    }
}
