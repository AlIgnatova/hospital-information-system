package course.spring.hospitalinformationsystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    @NonNull
    private int statusCode;
    @NonNull
    private String errorMessage;
    private List<String> constraintViolations = List.of();

}
