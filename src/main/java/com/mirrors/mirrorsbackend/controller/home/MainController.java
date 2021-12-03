package com.mirrors.mirrorsbackend.controller.home;

import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserInformation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@RestController
@AllArgsConstructor
@RequestMapping("/")
public class MainController {

    private final MainService mainService;

    @GetMapping({"", "/home"})
    public ModelAndView homePage() {
        return new ModelAndView("/home");
    }

    @GetMapping("/profile")
    public MarketplaceUser getUser() {
        return mainService.getCurrentUser();
    }

    @GetMapping("/user_information")
    public MarketplaceUserInformation getInformation(@RequestParam(value = "id", required = false) String userId) {
        return mainService.getUserInformation(userId);
    }

    @PostMapping("/ban")
    public void banUser(String userId) {
        mainService.banUser(userId);
    }
}
