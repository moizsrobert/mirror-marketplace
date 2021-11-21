package com.mirrors.mirrorsbackend.mvc.post;

import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@AllArgsConstructor
@RequestMapping("post")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ModelAndView postPage(@RequestParam(value = "id", required = false) String id, Model model) {
        return postService.loadPostPage(id, model);
    }
}
