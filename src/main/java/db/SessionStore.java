package db;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import model.User;

public class SessionStore {
    private static final Map<String, User> sessions = new HashMap<>();

    public static void addSession(String key, User user) {
        sessions.put(key, user);
    }

    public static Optional<User> getSession(String key) {
        return Optional.ofNullable(sessions.get(key));
    }
}
