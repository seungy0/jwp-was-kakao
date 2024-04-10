package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private final String status;
    private final String msg;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(String status, String msg, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.msg = msg;
        this.headers = headers;
        this.body = body;
    }

    public void sendResponse(OutputStream out)
        throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("HTTP/1.1 " + status + " " + msg + " \r\n");

        headers.forEach((key, value) -> {
            try {
                dos.writeBytes(key + ": " + value + "\r\n");
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });
        if (body.length != 0) {
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
        }
        dos.flush();
    }
}
