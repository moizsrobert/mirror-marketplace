package com.mirrors.mirrorsbackend.page.login;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class LoginController {
    @GetMapping(value = "/login")
    public ModelAndView loginPage() {
        return new ModelAndView("/api/login");
    }
}
