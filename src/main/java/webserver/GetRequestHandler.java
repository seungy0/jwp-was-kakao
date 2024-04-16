package webserver;

import db.DataBase;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import utils.DynamicHtmlRenderer;
import webserver.http.HttpHeaders;
import webserver.http.HttpStatus;

public class GetRequestHandler implements MethodRequestHandler {

    @Override
    public Optional<HttpResponse> handle(HttpRequest httpRequest) throws IOException {
        Optional<HttpResponse> response = responseResources(httpRequest.getPath());
        if (response.isPresent()) {
            return response;
        }
        return responseGetApi(httpRequest);
    }

    private static Optional<HttpResponse> responseGetApi(HttpRequest httpRequest)
        throws IOException {
        if (httpRequest.getPath().equals("/user/list")) {
            String renderedUserList = DynamicHtmlRenderer.renderUserList("user/list",
                DataBase.findAll());
            return Optional.of(new HttpResponse(HttpStatus.OK,
                Map.of(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8"),
                renderedUserList.getBytes()));
        }
        return Optional.empty();
    }

    private static Optional<HttpResponse> responseResources(String path) throws IOException {
        if (path.equals("/")) {
            path = "/index.html";
        }
        File file = new File(
            "./src/main/resources" + (path.endsWith(".html") || path.endsWith("favicon.ico")
                ? "/templates" : "/static")
                + path);

        if (file.exists()) {
            byte[] body = Files.readAllBytes(file.toPath());
            String contentType = Files.probeContentType(file.toPath());
            HttpResponse httpResponse = new HttpResponse(HttpStatus.OK,
                Map.of(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "null"),
                body);
            return Optional.of(httpResponse);
        }
        return Optional.empty();
    }
}
