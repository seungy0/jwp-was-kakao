package webserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> query;
    private final String body;

    public HttpRequest(List<String> lines) {
        String[] tokens = lines.isEmpty() ? null : lines.get(0).split(" ");
        if (tokens == null || tokens.length < 2) {
            throw new IllegalArgumentException("Invalid request line");
        }
        this.method = tokens[0];
        String[] pathToken = tokens[1].split("\\?");
        this.path = pathToken[0];
        this.query = pathToken.length > 1 ? parseQueryString(pathToken[1]) : new HashMap<>();
        this.body = parseBody(lines);
    }

    public boolean isMethod(String method) {
        return this.method.equals(method);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    private Map<String, String> parseQueryString(String query) {
        String[] tokens = query.split("&");
        Map<String, String> queryMap = new HashMap<>();
        for (String token : tokens) {
            String[] keyValue = token.split("=");
            queryMap.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
        }
        return queryMap;
    }

    private String parseBody(List<String> lines) {
        int emptyLineIndex = lines.indexOf("");
        return emptyLineIndex == -1 ? null
            : String.join("\n", lines.subList(emptyLineIndex + 1, lines.size()));
    }
}
