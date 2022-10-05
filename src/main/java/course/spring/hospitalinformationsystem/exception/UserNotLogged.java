package course.spring.hospitalinformationsystem.exception;

public class UserNotLogged extends RuntimeException{
    public UserNotLogged() {
    }

    public UserNotLogged(String message) {
        super(message);
    }

    public UserNotLogged(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotLogged(Throwable cause) {
        super(cause);
    }

}
