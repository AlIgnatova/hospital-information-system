package course.spring.hospitalinformationsystem.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class TestDto {

    @NotNull
    @NonNull
    private Long id;
    @NotNull
    @NonNull
    private String patient;
    @NotNull
    @NonNull
    private Long hospitalStayId;
    @NotNull
    @NonNull
    private String testType;
    @NotNull
    @NonNull
    private String whatToBeTested;
    private String performedBy;
    private String result;


}
