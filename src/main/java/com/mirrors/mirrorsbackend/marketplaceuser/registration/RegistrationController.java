package com.mirrors.mirrorsbackend.marketplaceuser.registration;

import lombok.AllArgsConstructor;
import org.dom4j.rule.Mode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("api/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @GetMapping
    public ModelAndView registerPage() {
        return new ModelAndView("api/registration");
    }

    @PostMapping
    public ModelAndView register(RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping("confirm")
    public ModelAndView confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
