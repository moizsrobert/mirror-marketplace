package com.mirrors.mirrorsbackend.login;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class LoginController {

    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("/api/login");
    }

}
