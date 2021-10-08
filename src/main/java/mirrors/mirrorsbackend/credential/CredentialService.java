package mirrors.mirrorsbackend.credential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CredentialService {

    private final CredentialDataAccessService credentialDataAccessService;

    @Autowired
    public CredentialService(CredentialDataAccessService credentialDataAccessService) {
        this.credentialDataAccessService = credentialDataAccessService;
    }

    protected List<Credential> getAllCredentials() {
        return credentialDataAccessService.selectAllCredentials();
    }

    protected void addNewCredential(Credential credential) {
        //TODO: Meg kell nézni, hogy nem foglalt-e már az email
        credentialDataAccessService.insertCredential(credential);
    }




}
