package course.spring.hospitalinformationsystem.dto;


import course.spring.hospitalinformationsystem.entity.enums.Diagnosis;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class InputHospitalStayDto {
    @NotNull
    @NonNull
    private String patientEGN;
    @NonNull
    @NotNull
    private Long wardId;
    @NonNull
    @NotNull
    private Diagnosis diagnosisOnAdmission;

}
