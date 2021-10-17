package com.mirrors.mirrorsbackend.home;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public ModelAndView redirectToHomePage() {
        return homePage();
    }

    @GetMapping("/home")
    public ModelAndView homePage() {
        return new ModelAndView("/home");
    }
}
