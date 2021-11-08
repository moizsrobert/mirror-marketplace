package com.mirrors.mirrorsbackend.marketplaceuser.login;

import com.mirrors.mirrorsbackend.marketplaceuser.login.passwordreset.PasswordResetRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("/api/login");
    }

    @GetMapping("/reset-password")
    public ModelAndView resetPasswordPage() {
        return new ModelAndView("/api/reset-password");
    }

    @PostMapping("/reset-password")
    public ModelAndView resetPassword(String email) {
        return loginService.sendPasswordResetEmail(email);
    }

    @GetMapping("/password-reset")
    public ModelAndView passwordResetPage(@RequestParam("token") String token) {
        return loginService.confirmToken(token);
    }

    @PostMapping(value = "/password-reset")
    public ModelAndView passwordReset(PasswordResetRequest passwordResetRequest) {
        return loginService.changePassword(passwordResetRequest);
    }
}
