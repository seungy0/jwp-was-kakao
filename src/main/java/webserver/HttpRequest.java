package webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> header;
    private final Map<String, String> query;
    private final String body;
    private final Map<String, String> form;

    public HttpRequest(String requestLine, Map<String, String> header, String body) {
        String[] tokens = requestLine.isEmpty() ? null : requestLine.split(" ");
        if (tokens == null || tokens.length < 2) {
            throw new IllegalArgumentException("Invalid request line");
        }
        this.method = tokens[0];
        String[] pathToken = tokens[1].split("\\?");
        this.path = pathToken[0];
        this.header = header;
        this.query = pathToken.length > 1 ? parseQueryString(pathToken[1]) : new HashMap<>();
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
        return query;
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
            String[] keyValue = token.split("=");
            map.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
        }
        return map;
    }

    public String getMethod() {
        return method;
    }
}
