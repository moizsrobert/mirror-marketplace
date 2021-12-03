package com.mirrors.mirrorsbackend.model.marketplace_post;

import com.mirrors.mirrorsbackend.controller.post.PostResponse;
import com.mirrors.mirrorsbackend.controller.post.SearchRequest;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserRole;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserService;
import com.mirrors.mirrorsbackend.model.saved_post.SavedPostService;
import com.mirrors.mirrorsbackend.exception.MarketplaceException;
import com.mirrors.mirrorsbackend.controller.post.PostRequest;
import com.mirrors.mirrorsbackend.controller.profile.CountryEnum;
import com.mirrors.mirrorsbackend.util.file.ImageStorage;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class MarketplacePostService {

    private final MarketplacePostRepository marketplacePostRepository;
    private final MarketplaceUserService marketplaceUserService;
    private final SavedPostService savedPostService;
    private final ImageStorage imageStorage;

    private final int LATEST_POSTS_TO_FIND = 3;
    private final int RECOMMENDED_POSTS_TO_FIND = 3;
    private final int RANDOM_POSTS_TO_FIND = 15;

    public MarketplacePost getPostById(String postId) {
        return marketplacePostRepository.findById(postId)
                .orElseThrow(() -> new MarketplaceException("Post not found!"));
    }

    public Pair<MarketplacePost, List<String>> getPostWithImagesById(String postId) {
        MarketplacePost post = getPostById(postId);
        List<String> paths = imageStorage.loadPaths(postId);
        return Pair.of(post, paths);
    }

    public List<Pair<MarketplacePost, List<String>>> getPostsWithImagesByIds(List<String> postIds) {
        List<Pair<MarketplacePost, List<String>>> posts = new ArrayList<>();
        for (var postId : postIds)
            posts.add(getPostWithImagesById(postId));
        return posts;
    }

    public List<MarketplacePost> getPostsOfUser(MarketplaceUser user) {
        return marketplacePostRepository.findAllByUserID(user.getId());
    }

    public List<Pair<MarketplacePost, List<String>>> getPostsOfUserWithImages(MarketplaceUser user) {
        List<Pair<MarketplacePost, List<String>>> postListWithImages = new ArrayList<>();
        List<MarketplacePost> posts = marketplacePostRepository.findAllByUserID(user.getId());
        for (var post : posts) {
            List<String> paths = imageStorage.loadPaths(post.getPostId());
            postListWithImages.add(Pair.of(post, paths));
        }
        return postListWithImages;
    }

    public List<PostResponse> getLatestPosts() {
        List<PostResponse> postResponses = new ArrayList<>();
        List<MarketplacePost> posts = marketplacePostRepository.findByLatest(LATEST_POSTS_TO_FIND);
        for (var post : posts) {
            List<String> paths = imageStorage.loadPaths(post.getPostId());
            MarketplaceUser postUser = marketplaceUserService.getUserById(post.getUserId());
            postResponses.add(new PostResponse(post, postUser, paths));
        }
        return postResponses;
    }

    public List<PostResponse> getRandomPosts() {
        List<PostResponse> postResponses = new ArrayList<>();

        CountryEnum country = marketplaceUserService.getUserFromContext().getCountry();
        List<MarketplaceUser> usersFromSameCountry = marketplaceUserService.getUsersFromCountry(country);
        List<String> userIdsFromSameCountry = new ArrayList<>();
        for (var u : usersFromSameCountry)
            userIdsFromSameCountry.add(u.getId());

        List<MarketplacePost> randomLocalPosts = marketplacePostRepository.findByRandomFromUsers(
                RANDOM_POSTS_TO_FIND,
                userIdsFromSameCountry
        );
        List<MarketplacePost> posts = new ArrayList<>(randomLocalPosts);

        if (randomLocalPosts.size() < 15) {
            List<MarketplacePost> randomNonLocalPosts = marketplacePostRepository.findByRandomNotFromUsers(
                    RANDOM_POSTS_TO_FIND - randomLocalPosts.size(),
                    userIdsFromSameCountry
            );
            posts.addAll(randomNonLocalPosts);
        }

        for (var post : posts) {
            List<String> paths = imageStorage.loadPaths(post.getPostId());
            MarketplaceUser postUser = marketplaceUserService.getUserById(post.getUserId());
            postResponses.add(new PostResponse(post, postUser, paths));
        }
        return postResponses;
    }

    public List<PostResponse> getPostsBySearchRequest(SearchRequest searchRequest) {
        List<PostResponse> posts = new ArrayList<>();
        List<MarketplacePost> queryPosts;

        if (searchRequest.getTitle().isBlank()) {
            if (searchRequest.getCategory() == CategoryEnum.NOTHING_SELECTED || searchRequest.getCategory() == null)
                throw new MarketplaceException("Invalid search attempt!");
            else
                queryPosts = marketplacePostRepository.findAllByCategory(searchRequest.getCategory());
        } else {
            if (searchRequest.getTitle().length() < 3)
                throw new MarketplaceException("You must enter atleast 3 characters!");
            if (searchRequest.getCategory() == CategoryEnum.NOTHING_SELECTED || searchRequest.getCategory() == null)
                queryPosts = marketplacePostRepository.findByTitleIgnoreCase(searchRequest.getTitle());
            else
                queryPosts = marketplacePostRepository.findByTitleAndCategoryIgnoreCase(searchRequest.getTitle(), searchRequest.getCategory());
        }

        for (var post : queryPosts) {
            var paths = imageStorage.loadPaths(post.getPostId());
            var user = marketplaceUserService.getUserById(post.getUserId());
            posts.add(new PostResponse(post, user, paths));
        }

        String countryName = marketplaceUserService.getUserFromContext().getCountry().getDisplayName();
        List<PostResponse> sortedPosts = new ArrayList<>();

        posts.forEach((p) -> {
            if (Objects.equals(p.getCountry(), countryName))
                sortedPosts.add(p);
        });

        posts.forEach((p) -> {
            if (!Objects.equals(p.getCountry(), countryName))
                sortedPosts.add(p);
        });

        return sortedPosts;
    }

    public List<PostResponse> getPostsByCategory(CategoryEnum category) {
        if (category == null || category.equals(CategoryEnum.NOTHING_SELECTED))
            throw new MarketplaceException("Couldn't load recommended items!");

        List<PostResponse> posts = new ArrayList<>();

        var loadedPosts = marketplacePostRepository.findRandomByCategory(RECOMMENDED_POSTS_TO_FIND, category.name());
        for (var post : loadedPosts) {
            var paths = imageStorage.loadPaths(post.getPostId());
            var user = marketplaceUserService.getUserById(post.getUserId());
            posts.add(new PostResponse(post, user, paths));
        }

        return posts;
    }

    public void createPost(PostRequest postRequest) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();

        if (getPostsOfUser(user).toArray().length >= 5)
            throw new MarketplaceException("You cannot have more than 5 active posts");

        if (user.getCountry() == CountryEnum.NOTHING_SELECTED ||
                user.getCity() == null)
            throw new MarketplaceException("You must set your address before making a post!");

        if (!postRequest.getPostName().matches("[^,:;=?@#|'<>^]*.{4,128}$"))
            throw new MarketplaceException("Invalid title!");

        MarketplacePost post = new MarketplacePost();
        short numberOfImages;
        if (postRequest.getPostImages()[0].isEmpty())
            numberOfImages = 0;
        else
            numberOfImages = (short) postRequest.getPostImages().length;
        double postPrice;

        if (numberOfImages > 8)
            throw new MarketplaceException("You cannot upload more than 8 images!");

        try {
            postPrice = Double.parseDouble(postRequest.getPostPrice());
            postPrice = Double.parseDouble(String.format("%.2f", postPrice).replace(",", "."));
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            throw new MarketplaceException("Invalid price!");
        }

        if (postPrice < 0)
            throw new MarketplaceException("Invalid price!");

        if (postRequest.getPostCategory() == CategoryEnum.NOTHING_SELECTED)
            throw new MarketplaceException("Select a category!");

        if (postRequest.getPostDescription().length() > 512)
            throw new MarketplaceException("Description is too long! (max. 512 characters)");

        if (Arrays.stream(CategoryEnum.values()).noneMatch((s) -> s.name().equals(postRequest.getPostCategory().name())))
            throw new MarketplaceException("Category doesn't exist!");

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

    public void deletePost(String postId) {
        MarketplaceUser user = marketplaceUserService.getUserFromContext();
        if (postId == null)
            throw new MarketplaceException("Post not found!");
        Optional<MarketplacePost> optionalPost = marketplacePostRepository.findById(postId);
        if (optionalPost.isPresent()) {
            MarketplacePost post = optionalPost.get();
            if (post.getUserId().equals(user.getId()) || user.getMarketplaceUserRole() == MarketplaceUserRole.ADMIN) {
                imageStorage.delete(postId);
                savedPostService.removeAllByPostID(postId);
                marketplacePostRepository.delete(post);
            } else
                throw new MarketplaceException("No rights to delete post.");
        } else
            throw new MarketplaceException("Post not found!");
    }

    public void deletePostsOfUser(MarketplaceUser user) {
        List<MarketplacePost> postsByUser = marketplacePostRepository.findAllByUserID(user.getId());
        for (var post : postsByUser) {
            imageStorage.delete(post.getPostId());
            savedPostService.removeAllByPostID(post.getPostId());
        }
        marketplacePostRepository.deleteAll(postsByUser);
    }
}

