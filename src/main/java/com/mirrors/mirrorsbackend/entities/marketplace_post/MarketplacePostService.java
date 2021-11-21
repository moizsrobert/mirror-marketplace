package com.mirrors.mirrorsbackend.entities.marketplace_post;

import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.utils.file.ImageStorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MarketplacePostService {

    final MarketplacePostRepository marketplacePostRepository;
    final ImageStorageService imageStorageService;

    public void deleteMarketplacePost(MarketplacePost post) {
        imageStorageService.delete(post.getPostId());
        marketplacePostRepository.delete(post);
    }

    public void deleteMarketplacePostsOfUser(MarketplaceUser user) {
        List<MarketplacePost> postsByUser = marketplacePostRepository.findAllByUser(user);
        for (var post : postsByUser)
            imageStorageService.delete(post.getPostId());
        marketplacePostRepository.deleteAll(postsByUser);
    }
}

