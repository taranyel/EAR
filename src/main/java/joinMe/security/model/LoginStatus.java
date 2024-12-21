package joinMe.security.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginStatus {

    private boolean loggedIn;
    private String username;
    private String errorMessage;
    private boolean success;

    public LoginStatus() {
    }

    public LoginStatus(boolean loggedIn, boolean success, String username, String errorMessage) {
        this.loggedIn = loggedIn;
        this.username = username;
        this.errorMessage = errorMessage;
        this.success = success;
    }
}
