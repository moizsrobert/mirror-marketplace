package com.mirrors.mirrorsbackend.model.saved_post;

import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserService;
import com.mirrors.mirrorsbackend.exception.MarketplaceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SavedPostService {

    final private SavedPostRepository savedPostRepository;
    final private MarketplaceUserService marketplaceUserService;

    public void addToSavedPosts(String postId) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();

        if (Objects.equals(postId, user.getId()))
            throw new MarketplaceException("You cannot save your own post!");

        SavedPost savedPost = new SavedPost(postId, user.getId());
        if (isPostSaved(savedPost))
            throw new MarketplaceException("You cannot save the same post twice!");
        savedPostRepository.save(savedPost);
    }

    public boolean isPostSaved(SavedPost savedPost) {
        return savedPostRepository.existsByIDs(savedPost.getPostId(), savedPost.getUserId());
    }

    public void removeFromSavedPosts(String postId, String userId) {
        savedPostRepository.deleteByIDs(postId, userId);
    }

    public void removeAllByPostID(String postId) {
        savedPostRepository.deleteAllByPostID(postId);
    }

    public void deleteAllSavedPosts() {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        savedPostRepository.deleteAllByUserID(user.getId());
    }

    public List<String> getSavedPostIDs() {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        List<SavedPost> savedPosts = savedPostRepository.findAllByUserID(user.getId());
        List<String> postIDs = new ArrayList<>();
        for (var savedPost : savedPosts) {
            postIDs.add(savedPost.getPostId());
        }
        return postIDs;
    }
}
