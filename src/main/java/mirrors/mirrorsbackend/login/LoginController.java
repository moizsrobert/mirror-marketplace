package mirrors.mirrorsbackend.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping
    public List<Login> getAllUsers() {
        return loginService.getAllLogins();
    }

    @PostMapping
    public void addNewUser(@RequestBody Login login) {
        loginService.addNewLogin(login);
    }
}
