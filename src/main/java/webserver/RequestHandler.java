package webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.IOUtils;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}",
            connection.getInetAddress(),
            connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String requestLine = IOUtils.readRequestLine(reader);
            Map<String, String> headers = IOUtils.readHeader(reader);
            String body = IOUtils.readData(reader, Integer.parseInt(
                headers.get("Content-Length") != null ? headers.get("Content-Length") : "0"));
            HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

            if (httpRequest.isMethod("GET") && responseResources(httpRequest.getPath(), out)) {
                return;
            }

            if (httpRequest.isMethod("GET")) {
                GetRequestHandler.getHandler(httpRequest, out);
                return;
            }
            if (httpRequest.isMethod("POST")) {
                PostRequestHandler.handler(httpRequest, out);
            }

            System.out.println("Request : " + requestLine);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private boolean responseResources(String path, OutputStream out) throws IOException {
        File file = new File(
            "./src/main/resources" + (path.endsWith(".html") || path.endsWith("favicon.ico")
                ? "/templates" : "/static")
                + path);

        if (file.exists()) {
            byte[] body = Files.readAllBytes(file.toPath());
            String contentType = Files.probeContentType(file.toPath());
            HttpResponse httpResponse = new HttpResponse("200", "OK",
                Map.of("Content-Type", contentType != null ? contentType : "null"),
                body);
            httpResponse.sendResponse(out);
            return true;
        }
        return false;
    }
}
