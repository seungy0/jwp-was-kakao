package webserver.http;

public enum HttpStatus {
	OK("200", "OK"),
	CREATED("201", "Created"),
	NO_CONTENT("204", "No Content"),
	REDIRECT("302", "Found"),
	BAD_REQUEST("400", "Bad Request"),
	NOT_FOUND("404", "Not Found"),
	INTERNAL_SERVER_ERROR("500", "Internal Server Error"),
	SERVICE_UNAVAILABLE("503", "Service Unavailable"),
	NULL("null", "null");

	public final String code;
	public final String status;

	HttpStatus(String code, String status) {
		this.code = code;
		this.status = status;
	}

	public static final String HTTP_VERSION = "HTTP/1.1";
}