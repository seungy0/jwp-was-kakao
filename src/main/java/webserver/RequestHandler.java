package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.http.HttpHeaders;
import webserver.http.HttpMethods;
import webserver.http.HttpStatus;
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

            String requestLine = readRequestLine(reader);
            Map<String, String> headers = readHeader(reader);
            String body = IOUtils.readData(reader, Integer.parseInt(
                headers.get(HttpHeaders.CONTENT_LENGTH) != null ? headers.get(HttpHeaders.CONTENT_LENGTH) : "0"));
            HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

            HttpResponse http404Response = new HttpResponse(HttpStatus.NOT_FOUND, null, null);

            Map<String, MethodRequestHandler> handlers = new HashMap<>();
            handlers.put(HttpMethods.GET, new GetRequestHandler());
            handlers.put(HttpMethods.POST, new PostRequestHandler());

            handlers.get(httpRequest.getMethod())
                .handler(httpRequest)
                .orElse(http404Response)
                .sendResponse(out);

            System.out.println("Request : " + requestLine);
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

        while (!(line = reader.readLine()).isEmpty()) {
            String[] tokens = line.split(": ");
            headers.put(tokens[0], tokens[1]);
        }

        return headers;
    }
}
