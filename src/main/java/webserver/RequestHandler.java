package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            List<String> line = readRequest(reader);
            HttpRequest httpRequest = new HttpRequest(line);

            if (httpRequest.isMethod("GET") && responseResources(httpRequest.getPath(), out)) {
                return;
            }

            if (httpRequest.isMethod("GET")) {
                GetRequestHandler.getHandler(httpRequest, out);
                return;
            }

            System.out.println("Request : " + line);
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
            sendResponse(out, body, contentType);
            return true;
        }
        return false;
    }

    private static List<String> readRequest(BufferedReader reader) throws IOException {
        List<String> line = new ArrayList<>();
        String readLine = reader.readLine();
        while (readLine != null && !readLine.isEmpty()) {
            line.add(readLine);
            readLine = reader.readLine();
        }
        return line;
    }

    private void sendResponse(OutputStream out, byte[] body, String contentType)
        throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("HTTP/1.1 " + (body.length == 0 ? "404 Not Found" : "200 OK") + " \r\n");
        dos.writeBytes("Content-Type: " + contentType + "\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
