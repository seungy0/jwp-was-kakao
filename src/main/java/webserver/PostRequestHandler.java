package webserver;

import domain.auth.AuthService;
import domain.user.UserService;
import java.util.Map;
import java.util.Optional;
import model.User;
import webserver.http.HttpHeaders;
import webserver.http.HttpStatus;

public class PostRequestHandler implements MethodRequestHandler {

    private final UserService userService;
    private final AuthService authService;

    public PostRequestHandler(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }


    @Override
    public Optional<HttpResponse> handle(HttpRequest httpRequest) {
        if (httpRequest.getPath().equals("/user/create")) {
            userService.createUser(httpRequest.getForm());
            HttpResponse httpResponse = new HttpResponse(HttpStatus.REDIRECT,
                Map.of(HttpHeaders.LOCATION, "/index.html"), null);
            return Optional.of(httpResponse);
        }

        if (httpRequest.getPath().equals("/user/login")) {
            return userLogin(httpRequest);
        }

        return Optional.empty();
    }

    private Optional<HttpResponse> userLogin(HttpRequest httpRequest) {
        Optional<User> user = userService.login(httpRequest.getForm());
        if (user.isPresent()) {
            String sessionId = authService.createSession(user.get());
            return Optional.of(createSessionAndRedirect(sessionId));
        }
        return Optional.of(new HttpResponse(HttpStatus.REDIRECT,
            Map.of(HttpHeaders.LOCATION, "/user/login_failed.html"), null));
    }

    private HttpResponse createSessionAndRedirect(String sessionId) {
        return new HttpResponse(HttpStatus.REDIRECT,
            Map.of(HttpHeaders.LOCATION, "/index.html",
                HttpHeaders.SET_COOKIE, "JSESSIONID=" + sessionId + "; Path=/"),
            null);
    }
}
