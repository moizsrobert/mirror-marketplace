package com.mirrors.mirrorsbackend.mvc.login;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@AllArgsConstructor
public class LoginController {
    @GetMapping(value = "/api/login")
    public ModelAndView loginPage() {
        return new ModelAndView("/api/login");
    }
}
