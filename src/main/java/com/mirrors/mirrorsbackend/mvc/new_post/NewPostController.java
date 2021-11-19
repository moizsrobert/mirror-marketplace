package com.mirrors.mirrorsbackend.mvc.new_post;

import com.mirrors.mirrorsbackend.marketplace_user.MarketplaceUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@AllArgsConstructor
@RequestMapping("/new-post")
public class NewPostController {

    final NewPostService newPostService;

    @GetMapping
    public ModelAndView postPage() {
        return new ModelAndView("new-post");
    }

    @PostMapping
    public ModelAndView createNewPost(NewPostRequest postRequest, @AuthenticationPrincipal MarketplaceUser user) {
        return newPostService.createNewPost(postRequest, user);
    }
}
