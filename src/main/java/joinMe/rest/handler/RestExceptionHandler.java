package joinMe.rest.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import joinMe.db.exception.NotFoundException;
import joinMe.db.exception.PersistenceException;
import joinMe.db.exception.ValidationException;
import joinMe.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception handlers for REST controllers.
 * <p>
 * The general pattern should be that unless an exception can be handled in a more appropriate place it bubbles up to a
 * REST controller which originally received the request. There, it is caught by this handler, logged and a reasonable
 * error message is returned to the user.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static void logException(Exception e) {
        LOG.warn("Exception caught: {}", e.getMessage());
    }

    private static ErrorInfo errorInfo(HttpServletRequest request, Throwable e) {
        return new ErrorInfo(e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorInfo> persistenceException(HttpServletRequest request, PersistenceException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e.getCause()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorInfo> unsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        logException(e);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorInfo> nullPointer(HttpServletRequest request, NullPointerException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e.getCause()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> resourceNotFound(HttpServletRequest request, NotFoundException e) {
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorInfo> validationError(HttpServletRequest request, ValidationException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorInfo> accessDenied(HttpServletRequest request, AccessDeniedException e) {
        if (SecurityUtils.isAuthenticatedAnonymously()) {
            throw e;
        }
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Map<String, String> errorResponse = new HashMap<>();

        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            errorResponse.put("error", "Invalid input format");
            invalidFormatException.getPath().forEach(reference -> {
                errorResponse.put("field", reference.getFieldName());
                errorResponse.put("message", "Invalid value for field '" + reference.getFieldName());
            });
        } else {
            errorResponse.put("error", "Malformed JSON request");
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        LOG.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
