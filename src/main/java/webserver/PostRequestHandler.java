package webserver;

import db.DataBase;
import db.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpHeaders;
import webserver.http.HttpStatus;

public class PostRequestHandler implements MethodRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetRequestHandler.class);

    @Override
    public Optional<HttpResponse> handle(HttpRequest httpRequest) {
        if (httpRequest.getPath().equals("/user/create")) {
            createUser(httpRequest.getForm());
            HttpResponse httpResponse = new HttpResponse(HttpStatus.REDIRECT,
                Map.of(HttpHeaders.LOCATION, "/index.html"), null);
            return Optional.of(httpResponse);
        }

        if (httpRequest.getPath().equals("/user/login")) {
            Map<String, String> form = httpRequest.getForm();
            User user = DataBase.findUserById(form.get("userId"));
            return Optional.of(login(user, form));
        }

        return Optional.empty();
    }

    private static HttpResponse login(User user, Map<String, String> form) {
        if (user != null && user.getPassword().equals(form.get("password"))) {
            HttpCookie.addCookie("JSESSIONID", UUID.randomUUID().toString());
            return new HttpResponse(HttpStatus.REDIRECT,
                Map.of(HttpHeaders.LOCATION, "/index.html",
                    HttpHeaders.SET_COOKIE, HttpCookie.getCookieString() + "; Path=/"),
                null);
        }
        return new HttpResponse(HttpStatus.REDIRECT,
            Map.of(HttpHeaders.LOCATION, "/user/login_failed.html"), null);
    }

    private static void createUser(Map<String, String> querys) {
        DataBase.addUser(
            new User(querys.get("userId"), querys.get("password"), querys.get("name"),
                URLDecoder.decode(querys.get("email"), StandardCharsets.UTF_8)));
        logger.debug("User Create : {}", querys.get("userId"));
    }
}
