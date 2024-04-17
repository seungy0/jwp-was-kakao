package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import utils.IOUtils;
import webserver.http.HttpHeaders;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> header;
    private final Map<String, String> queryParam;
    private final String body;
    private final Map<String, String> form;
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String requestLine = readRequestLine(reader);
        Map<String, String> headers = readHeader(reader);
        String body = IOUtils.readData(reader, Integer.parseInt(
            headers.get(HttpHeaders.CONTENT_LENGTH) != null ? headers.get(
                HttpHeaders.CONTENT_LENGTH) : "0"));
        return new HttpRequest(requestLine, headers, body);
    }

    private HttpRequest(String requestLine, Map<String, String> header, String body) {
        String[] tokens = requestLine.isEmpty() ? null : requestLine.split(" ");
        validateRequestLine(tokens);
        this.method = tokens[METHOD_INDEX];
        String[] pathToken = tokens[PATH_INDEX].split("\\?");
        this.path = pathToken[0];
        this.header = header;
        this.queryParam = pathToken.length > 1 ? parseQueryString(pathToken[1]) : new HashMap<>();
        this.body = body;
        this.form = parseForm();
    }

    public boolean isMethod(String method) {
        return this.method.equals(method);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getForm() {
        return form;
    }

    public Map<String, String> getQuery() {
        return queryParam;
    }

    public String getMethod() {
        return method;
    }

    public Optional<String> getSessionId() {
        return Optional.ofNullable(header.get("Cookie"))
            .flatMap(cookie -> Arrays.stream(cookie.split(";"))
                .map(String::trim)
                .filter(c -> c.startsWith("JSESSIONID"))
                .map(c -> c.split("=")[1])
                .findFirst());

    }

    private Map<String, String> parseQueryString(String query) {
        return parseKeyValuePairs(query, "&");
    }

    private Map<String, String> parseForm() {
        return body == null ? new HashMap<>() : parseKeyValuePairs(body, "&");
    }

    private Map<String, String> parseKeyValuePairs(String input, String delimiter) {
        String[] tokens = input.split(delimiter);
        Map<String, String> map = new HashMap<>();
        for (String token : tokens) {
            List<String> keyValue = Arrays.stream(token.split("=", 2))
                .map(String::trim)
                .collect(Collectors.toList());

            Optional.of(keyValue)
                .filter(k -> k.size() > 1)
                .ifPresent(k -> map.put(k.get(0), k.get(1)));
        }
        return map;
    }

    private static void validateRequestLine(String[] tokens) {
        if (tokens == null || tokens.length < 2) {
            throw new IllegalArgumentException("Invalid request line");
        }
    }

    private static String readRequestLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private static Map<String, String> readHeader(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] tokens = line.split(": ");
            headers.put(tokens[0], tokens[1]);
        }

        return headers;
    }
}
