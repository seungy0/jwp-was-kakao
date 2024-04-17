package domain.user;

import db.DataBase;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Collection<User> getAllUsers() {
        return DataBase.findAll();
    }

    public void createUser(Map<String, String> querys) {
        DataBase.addUser(
            new User(querys.get("userId"), querys.get("password"), querys.get("name"),
                URLDecoder.decode(querys.get("email"), StandardCharsets.UTF_8)));
        logger.debug("User Create : {}", querys.get("userId"));
    }

    public Optional<User> login(Map<String, String> form) {
        User user = DataBase.findUserById(form.get("userId"));
        if (user != null && user.getPassword().equals(form.get("password"))) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
