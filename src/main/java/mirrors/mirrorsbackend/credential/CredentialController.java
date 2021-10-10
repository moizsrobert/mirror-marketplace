package mirrors.mirrorsbackend.credential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class CredentialController {

    private final CredentialService credentialService;

    @Autowired
    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @GetMapping
    public List<Credential> getAllCredentials() {
        return credentialService.getAllCredentials();
    }

    @PostMapping
    public void addNewUser(@RequestBody Credential credential) {
        credentialService.addNewCredential(credential);
    }
}
