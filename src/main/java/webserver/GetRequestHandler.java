package webserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

import webserver.http.HttpHeaders;
import webserver.http.HttpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetRequestHandler implements MethodRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetRequestHandler.class);

    @Override
    public Optional<HttpResponse> handler(HttpRequest httpRequest) throws IOException {
        // TODO: 요청 URL에 따라 적절한 처리를 하는 부분
        Optional<HttpResponse> response = responseResources(httpRequest.getPath());
        if (response.isPresent()) {
            return response;
        }
        return responseGetApi(httpRequest);
    }

    private static Optional<HttpResponse> responseGetApi(HttpRequest httpRequest) {
        // TODO: GET 방식의 요청 URL에 따라 적절한 처리를 하는 부분
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
