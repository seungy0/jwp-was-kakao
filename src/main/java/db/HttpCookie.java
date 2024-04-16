package db;

import java.util.HashMap;
import java.util.Map;

/**
 * Cookie 정보를 Map 형태로 관리한다.
 */
public class HttpCookie {
    private static final Map<String, String> cookies = new HashMap<>();

    public static void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public static String getCookieString() {
        StringBuilder sb = new StringBuilder();
        for (String key : cookies.keySet()) {
            sb.append(key).append("=").append(cookies.get(key)).append(";");
        }
        return sb.toString();
    }
}
