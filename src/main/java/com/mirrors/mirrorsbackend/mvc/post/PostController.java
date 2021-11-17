package com.mirrors.mirrorsbackend.mvc.post;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@AllArgsConstructor
@RequestMapping("post")
public class PostController {
    @GetMapping
    public ModelAndView postPage(@RequestParam(value = "token", required = false) String token) {
        return new ModelAndView("post?token=" + token);
    }
}
