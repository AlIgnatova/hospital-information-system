package course.spring.hospitalinformationsystem.utils;

import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorHandlingUtils {

    public static void handleValidationErrors(Errors errors){
        if(errors.hasErrors()){
            List<String> errorMessages = new ArrayList<>();
            List<String> fieldMessages = errors.getFieldErrors().stream()
                    .map(err -> String.format("%s for: '%s' = '%s'",
                            err.getDefaultMessage(), err.getField(), err.getRejectedValue())).toList();
            List<String> globalErrorMessages = errors.getGlobalErrors().stream()
                    .map(err -> String.format("%s", err.getDefaultMessage())).toList();
            errorMessages.addAll(fieldMessages);
            errorMessages.addAll(globalErrorMessages);
            throw new InvalidEntityDataException("Invalid user data ", errorMessages);
        }
    }
}
