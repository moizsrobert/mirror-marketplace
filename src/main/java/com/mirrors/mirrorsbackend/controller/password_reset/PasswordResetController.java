package com.mirrors.mirrorsbackend.controller.password_reset;

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
    public void resetPassword(String email) {
        loginService.sendPasswordResetEmail(email);
    }

    @GetMapping("/reset-password")
    public ModelAndView passwordResetPage(@RequestParam(value = "token", required = false) String token) {
        return loginService.confirmToken(token);
    }

    @PostMapping(value = "/reset-password")
    public void passwordReset(PasswordResetRequest passwordResetRequest) {
        loginService.changePassword(passwordResetRequest);
    }
}
