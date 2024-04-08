package webserver;

import db.DataBase;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetRequestHandler.class);

    public static void getHandler(HttpRequest httpRequest, OutputStream out) throws IOException {
        if (httpRequest.getPath().equals("/user/create")) {
            createUser(httpRequest.getQuery());
            return;
        }

        // api handler
        HttpResponse.sendResponse(out, "404 Not Found".getBytes(), "text/html;charset=utf-8");
        DataBase.findAll().stream()
            .map(User::toString)
            .forEach(logger::info);
    }

    private static void createUser(Map<String, String> querys) {
        DataBase.addUser(
            new User(querys.get("userId"), querys.get("password"), querys.get("name"),
                querys.get("email")));
    }
}
