package mirrors.mirrorsbackend.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginDataAccessService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LoginDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected List<Login> selectAllLogins() {
        String sql = "" +
                "SELECT " +
                "email_address, " +
                "password, " +
                "FROM credentials";
        return jdbcTemplate.query(sql, mapLoginFromDataBase());
    }

    protected int insertLogin(Login login) {
        String sql = "" +
                "INSERT INTO credentials (" +
                "email_address, " +
                "password, " +
                "VALUES (?, ?)";
        return jdbcTemplate.update(
                sql,
                login.getEmailaddress(),
                login.getPassword()
        );
    }

    private RowMapper<Login> mapLoginFromDataBase() {
        return (resultSet, i) -> {
            String emailaddress = resultSet.getString("emailaddress");
            String password = resultSet.getString("password");
            return new Login(emailaddress, password);
        };
    }
}
