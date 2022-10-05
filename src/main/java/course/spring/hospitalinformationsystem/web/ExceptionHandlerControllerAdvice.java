package course.spring.hospitalinformationsystem.web;

import course.spring.hospitalinformationsystem.dto.ErrorResponse;
import course.spring.hospitalinformationsystem.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNonExistingEntityException(NonExistingEntityException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInvalidEntityDataException(InvalidEntityDataException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                        ex.getConstraintViolations()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(MethodNotAllowedException ex){
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnautorizedException(UnautorizedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInsufficientPrivilegiesException(InsufficientPrivilegiesException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserNotLoggedInException(UserNotLogged ex){
        return ResponseEntity.status(HttpStatus.PROXY_AUTHENTICATION_REQUIRED)
                .body(new ErrorResponse(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value(), ex.getMessage()));
    }

}
