package course.spring.hospitalinformationsystem.dto;

import lombok.*;

import javax.validation.constraints.NotNull;


@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class PatientDto {
    @NotNull
    @NonNull
    private Long id;
    @NotNull
    @NonNull
    private String firstName;
    @NotNull
    @NonNull
    private String middleName;
    @NotNull
    @NonNull
    private String lastName;
    @NotNull
    @NonNull
    private String EGN;


}
