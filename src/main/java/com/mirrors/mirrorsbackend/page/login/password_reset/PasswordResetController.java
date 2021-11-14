package com.mirrors.mirrorsbackend.page.login.password_reset;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PasswordResetController {

    private final PasswordResetService loginService;

    @GetMapping("/forgot")
    public ModelAndView resetPasswordPage() {
        return new ModelAndView("/api/forgot");
    }

    @PostMapping("/forgot")
    public ModelAndView resetPassword(String email) {
        return loginService.sendPasswordResetEmail(email);
    }

    @GetMapping("/reset-password")
    public ModelAndView passwordResetPage(@RequestParam(value = "token", required = false) String token) {
        return loginService.confirmToken(token);
    }

    @PostMapping(value = "/reset-password")
    public ModelAndView passwordReset(PasswordResetRequest passwordResetRequest) {
        return loginService.changePassword(passwordResetRequest);
    }
}
