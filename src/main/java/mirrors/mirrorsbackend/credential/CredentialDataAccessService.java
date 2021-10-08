package mirrors.mirrorsbackend.credential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CredentialDataAccessService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CredentialDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected List<Credential> selectAllCredentials() {
        String sql = "" +
                "SELECT " +
                "email_address, " +
                "password " +
                "FROM credentials";
        return jdbcTemplate.query(sql, mapCredentialFromDataBase());
    }

    protected int insertCredential(Credential credential) {
        String sql = "" +
                "INSERT INTO credentials (" +
                "email_address, " +
                "password) " +
                "VALUES (?, ?)";
        return jdbcTemplate.update(
                sql,
                credential.getEmailaddress(),
                credential.getPassword()
        );
    }

    private RowMapper<Credential> mapCredentialFromDataBase() {
        return (resultSet, i) -> {
            String emailaddress = resultSet.getString("email_address");
            String password = resultSet.getString("password");
            return new Credential(emailaddress, password);
        };
    }
}
