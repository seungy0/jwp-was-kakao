package webserver;

import db.DataBase;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpHeaders;
import webserver.http.HttpStatus;

public class PostRequestHandler implements MethodRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetRequestHandler.class);

    @Override
    public Optional<HttpResponse> handler(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getPath().equals("/user/create")) {
            createUser(httpRequest.getForm());
            HttpResponse httpResponse = new HttpResponse(HttpStatus.REDIRECT,
                Map.of(HttpHeaders.LOCATION, "/index.html"), null);
            return Optional.of(httpResponse);
        }

        return Optional.empty();
    }

    private static void createUser(Map<String, String> querys) {
        DataBase.addUser(
            new User(querys.get("userId"), querys.get("password"), querys.get("name"),
                querys.get("email")));
        logger.debug("User Create : {}", querys.get("userId"));
    }
}
