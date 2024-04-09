package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOUtils {

    /**
     * @param br BufferedReader는 Request Body를 시작하는 시점이다.
     * @param contentLength contentLength는 Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    public static String readRequestLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    public static Map<String, String> readHeader(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;

        while (!(line = reader.readLine()).isEmpty()) {
            lines.add(line);
        }

        return parseHeaders(lines);
    }

    private static Map<String, String> parseHeaders(List<String> lines) {
        Map<String, String> headers = new HashMap<>();

        for (String line : lines) {
            String[] tokens = line.split(": ");
            headers.put(tokens[0], tokens[1]);
        }

        return headers;
    }
}
