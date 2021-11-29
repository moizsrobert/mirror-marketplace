package com.mirrors.mirrorsbackend.mvc.home;

import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserInformation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final HomeService homeService;

    @GetMapping({"", "/home"})
    public ModelAndView homePage() {
        return new ModelAndView("/home");
    }

    @GetMapping("/profile")
    public MarketplaceUser getUser() {
        return homeService.getCurrentUser();
    }

    @GetMapping("/user_information")
    public MarketplaceUserInformation getInformation(@RequestParam(value = "id", required = false) String userId) {
        return homeService.getUserInformation(userId);
    }

    @GetMapping("/ban")
    public void banUser(@RequestParam(value = "id", required = false) String userId) {
        homeService.banUser(userId);
    }
}
