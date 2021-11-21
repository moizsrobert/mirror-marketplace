package com.mirrors.mirrorsbackend.mvc.post;

import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePost;
import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePostRepository;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.utils.file.ImageStorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PostService {

    private final MarketplacePostRepository marketplacePostRepository;
    private final ImageStorageService imageStorageService;

    public ModelAndView loadPostPage(String id, Model model) {
        Optional<MarketplacePost> optionalPost = marketplacePostRepository.findById(id);
        if (optionalPost.isPresent()) {
            MarketplacePost post = optionalPost.get();
            model.addAttribute("post", post);
            MarketplaceUser seller = post.getPostUser();
            model.addAttribute("seller", seller);
            List<File> images = imageStorageService.load(post.getPostId());
            if (!images.isEmpty()) {
                List<String> imageNames = new ArrayList<>();
                for (var f : images)
                    imageNames.add(f.getName());
                model.addAttribute("images", imageNames);
            }
            return new ModelAndView("/post");
        } else
            return new ModelAndView("redirect:/api/not-found");
    }
}
