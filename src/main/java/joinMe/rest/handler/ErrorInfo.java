package joinMe.rest.handler;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains information about an error and can be send to client as JSON to let them know what went wrong.
 */
@Setter
@Getter
public class ErrorInfo {

    private String message;

    private String requestUri;

    public ErrorInfo(String message, String requestUri) {
        this.message = message;
        this.requestUri = requestUri;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" + requestUri + ", message = " + message + "}";
    }
}
