package test.activiti.springboot.service.exception;

public class UserRegistrationTaskException extends RuntimeException {
    public UserRegistrationTaskException(String message) {
        super(message);
    }
}
