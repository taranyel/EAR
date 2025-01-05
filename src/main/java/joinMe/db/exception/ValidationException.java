package joinMe.db.exception;

/**
 * Signifies that invalid data have been provided to the application.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
