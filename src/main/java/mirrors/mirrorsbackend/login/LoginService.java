package mirrors.mirrorsbackend.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginService {

    private final LoginDataAccessService loginDataAccessService;

    @Autowired
    public LoginService(LoginDataAccessService loginDataAccessService) {
        this.loginDataAccessService = loginDataAccessService;
    }

    protected List<Login> getAllLogins() {
        return loginDataAccessService.selectAllLogins();
    }

    protected void addNewLogin(Login login) {
        //TODO: Meg kell nézni, hogy nem foglalt-e már az email
        loginDataAccessService.insertLogin(login);
    }




}
