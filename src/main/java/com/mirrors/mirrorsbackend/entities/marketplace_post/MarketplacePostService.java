package com.mirrors.mirrorsbackend.entities.marketplace_post;

import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserRole;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserService;
import com.mirrors.mirrorsbackend.mvc.post.PostRequest;
import com.mirrors.mirrorsbackend.mvc.profile.CountryEnum;
import com.mirrors.mirrorsbackend.utils.file.ImageStorage;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MarketplacePostService {

    private final MarketplacePostRepository marketplacePostRepository;
    private final MarketplaceUserService marketplaceUserService;
    private final ImageStorage imageStorage;

    public MarketplacePost getMarketplacePostById(String postId) {
        return marketplacePostRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("Post not found!"));
    }

    public List<MarketplacePost> getMarketplacePostsOfUser(MarketplaceUser user) {
        return marketplacePostRepository.findAllByUserID(user.getId());
    }

    public Pair<MarketplacePost, List<String>> getMarketplacePostWithImages(String postId) {
        MarketplacePost post = getMarketplacePostById(postId);
        List<String> paths = imageStorage.loadPaths(postId);
        return Pair.of(post, paths);
    }

    public List<Pair<MarketplacePost, List<String>>> getMarketplacePostsOfUserWithImages(MarketplaceUser user) {
        List<Pair<MarketplacePost, List<String>>> postListWithImages = new ArrayList<>();
        List<MarketplacePost> posts = marketplacePostRepository.findAllByUserID(user.getId());
        for (var post : posts) {
            List<String> paths = imageStorage.loadPaths(post.getPostId());
            postListWithImages.add(Pair.of(post, paths));
        }

        return postListWithImages;
    }

    public void createMarketplacePost(PostRequest postRequest) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();

        if (getMarketplacePostsOfUser(user).toArray().length >= 5)
            throw new IllegalStateException("You cannot have more than 5 active posts");

        if (user.getCountry() == CountryEnum.NOTHING_SELECTED ||
            user.getCity() == null)
            throw new IllegalStateException("You must set your address before making a post!");

        if (!postRequest.getPostName().matches("[^,:;=?@#|'<>^]*.{4,128}$"))
            throw new IllegalStateException("Invalid title!");

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
            postPrice = Double.parseDouble(postRequest.getPostPrice());
            postPrice = Double.parseDouble(String.format("%.2f", postPrice).replace(",", "."));
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            throw new IllegalStateException("Invalid price!");
        }

        if (postPrice < 0)
            throw new IllegalStateException("Invalid price!");

        if (postRequest.getPostCategory() == CategoryEnum.NOTHING_SELECTED)
            throw new IllegalStateException("Select a category!");

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
        post.setUserId(user.getId());
        marketplacePostRepository.save(post);
    }

    public void incrementMarketplacePostViews(String postId) {
        if (marketplacePostRepository.findById(postId).isPresent())
            marketplacePostRepository.incrementViews(postId);
        else throw new IllegalStateException("Post not found!");
    }

    public void deleteMarketplacePost(String postId) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        if (postId == null)
            throw new IllegalStateException("Post not found!");
        Optional<MarketplacePost> optionalPost = marketplacePostRepository.findById(postId);
        if (optionalPost.isPresent()) {
            MarketplacePost post = optionalPost.get();
            if (post.getUserId().equals(user.getId()) || user.getMarketplaceUserRole() == MarketplaceUserRole.ADMIN) {
                imageStorage.delete(postId);
                marketplacePostRepository.delete(post);
            } else
                throw new IllegalStateException("No rights to delete post.");
        } else
            throw new IllegalStateException("Post not found!");
    }

    public void deleteMarketplacePostsOfUser(MarketplaceUser user) {
        List<MarketplacePost> postsByUser = marketplacePostRepository.findAllByUserID(user.getId());
        for (var post : postsByUser)
            imageStorage.delete(post.getPostId());
        marketplacePostRepository.deleteAll(postsByUser);
    }
}

