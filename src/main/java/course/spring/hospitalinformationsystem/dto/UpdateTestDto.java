package course.spring.hospitalinformationsystem.dto;

import course.spring.hospitalinformationsystem.entity.enums.TestType;
import course.spring.hospitalinformationsystem.service.HospitalStayService;
import course.spring.hospitalinformationsystem.service.PatientService;
import course.spring.hospitalinformationsystem.service.TestService;
import course.spring.hospitalinformationsystem.service.UserService;
import lombok.*;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
public class UpdateTestDto {

    @NonNull
    @NotNull
    private Long id;
    @NonNull
    @NotNull
    private Long patientId;
    @NonNull
    @NotNull
    private Long hospitalStayId;
    @NonNull
    @NotNull
    private TestType testType;
    @NonNull
    @NotNull
    private String whatToBeTested;
    private String performedBy;
    private String result;

    private PatientService patientService;
    private HospitalStayService hospitalStayService;

    private TestService testService;
    private UserService userService;


}
