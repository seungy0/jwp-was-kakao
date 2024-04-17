package domain.auth;

import db.DataBase;
import db.SessionStore;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import model.User;

public class AuthService {

    public boolean isUserLoggedIn(Optional<String> sessionId) {
        return sessionId.isPresent() && SessionStore.getSession(sessionId.get()).isPresent();
    }

    public User login(Map<String, String> form) {
        User user = DataBase.findUserById(form.get("userId"));
        if (user != null && user.getPassword().equals(form.get("password"))) {
            return user;
        }
        return null;
    }

    public String createSession(User user) {
        String uuid = UUID.randomUUID().toString();
        SessionStore.addSession(uuid, user);
        return uuid;
    }
}
