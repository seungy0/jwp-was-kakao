package webserver;

import java.io.IOException;
import java.util.Optional;

public interface MethodRequestHandler {

    Optional<HttpResponse> handle(HttpRequest httpRequest) throws IOException;
}
