package webserver;

import db.DataBase;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetRequestHandler.class);

    public static void handler(HttpRequest httpRequest, OutputStream out) throws IOException {
        if (httpRequest.getPath().equals("/user/create")) {
            createUser(httpRequest.getForm());
            HttpResponse httpResponse = new HttpResponse("302", "Found",
                Map.of("Location", "/index.html"), null);
            httpResponse.sendResponse(out);
            return;
        }

        HttpResponse httpResponse = new HttpResponse("404", "Not Found", null, null);
        httpResponse.sendResponse(out);
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