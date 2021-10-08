package mirrors.mirrorsbackend.credential;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Credential {

    private final String emailaddress;
    private String password;

    public Credential(@JsonProperty("emailaddress") String emailaddress,
                      @JsonProperty("password") String password) {
        this.emailaddress = emailaddress;
        this.password = password;
    }

    public String getEmailaddress() {
        return this.emailaddress;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "emailaddress=" + emailaddress +
                ", password='" + password + '\'' +
                '}';
    }
}
