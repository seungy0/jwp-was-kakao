package webserver;

import domain.auth.AuthService;
import domain.user.UserService;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.IOUtils;
import webserver.http.HttpHeaders;
import webserver.http.HttpMethods;
import webserver.http.HttpStatus;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final Map<String, MethodRequestHandler> handlers = new HashMap<>();
    private final Socket connection;

    static {
        UserService userService = new UserService();
        AuthService authService = new AuthService();
        handlers.put(HttpMethods.GET, new GetRequestHandler(userService, authService));
        handlers.put(HttpMethods.POST, new PostRequestHandler(userService, authService));
    }

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}",
            connection.getInetAddress(),
            connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String requestLine = readRequestLine(reader);
            Map<String, String> headers = readHeader(reader);
            String body = IOUtils.readData(reader, Integer.parseInt(
                headers.get(HttpHeaders.CONTENT_LENGTH) != null ? headers.get(
                    HttpHeaders.CONTENT_LENGTH) : "0"));
            HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

            HttpResponse response = handlers.get(httpRequest.getMethod())
                .handle(httpRequest)
                .orElse(new HttpResponse(HttpStatus.NOT_FOUND, null, null));

            sendResponse(response, out);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String readRequestLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private Map<String, String> readHeader(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] tokens = line.split(": ");
            headers.put(tokens[0], tokens[1]);
        }

        return headers;
    }

    public void sendResponse(HttpResponse response, OutputStream out)
        throws IOException {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeBytes(
                HttpStatus.HTTP_VERSION + " " + response.getCode() + " " + response.getStatus()
                    + " \r\n");

            response.getHeaders().forEach((key, value) -> {
                try {
                    dos.writeBytes(key + ": " + value + "\r\n");
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            });
            byte[] body = response.getBody();
            if (body != null && body.length != 0) {
                dos.writeBytes("\r\n");
                dos.write(body, 0, body.length);
            }
            dos.flush();
        }
    }
}
