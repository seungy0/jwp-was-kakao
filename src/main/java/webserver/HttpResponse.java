package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpStatus;

public class HttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private final String code;
    private final String status;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers, byte[] body) {
        this.code = httpStatus.code;
        this.status = httpStatus.status;
        this.headers = headers;
        this.body = body;
    }

    public void sendResponse(OutputStream out)
        throws IOException {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeBytes(HttpStatus.HTTP_VERSION + " " + code + " " + status + " \r\n");

            headers.forEach((key, value) -> {
                try {
                    dos.writeBytes(key + ": " + value + "\r\n");
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            });
            if (body != null && body.length != 0) {
                dos.writeBytes("\r\n");
                dos.write(body, 0, body.length);
            }
            dos.flush();
        }
    }
}
