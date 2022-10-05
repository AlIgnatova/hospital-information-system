package course.spring.hospitalinformationsystem.dto;

import course.spring.hospitalinformationsystem.entity.enums.TestType;
import course.spring.hospitalinformationsystem.service.HospitalStayService;
import course.spring.hospitalinformationsystem.service.PatientService;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
public class InputTestDto {

    @NonNull
    @NotNull
    private Long hospitalStayId;
    @NonNull
    @NotNull
    private TestType testType;
    @NonNull
    @NotNull
    private String whatToBeTested;

    private PatientService patientService;
    private HospitalStayService hospitalStayService;


}
