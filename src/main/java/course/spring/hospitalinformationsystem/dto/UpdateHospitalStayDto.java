package course.spring.hospitalinformationsystem.dto;


import course.spring.hospitalinformationsystem.entity.enums.Diagnosis;
import course.spring.hospitalinformationsystem.entity.enums.DischargeDisposition;
import course.spring.hospitalinformationsystem.service.HospitalStayService;
import course.spring.hospitalinformationsystem.service.PatientService;
import course.spring.hospitalinformationsystem.service.WardService;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
public class UpdateHospitalStayDto {
    @NonNull
    @NotNull
    private Long id;
    @NotNull
    @NonNull
    private String patientEGN;
    @NonNull
    @NotNull
    private Long wardId;
    @NonNull
    @NotNull
    private Diagnosis diagnosisOnAdmission;
    private Diagnosis diagnosisOnDischarge;
    private List<String> decursusList = new ArrayList<>();
    private List<String> medicationsList = new ArrayList<>();
    private List<String> testsList = new ArrayList<>();
    private DischargeDisposition dischargeDisposition;

    private HospitalStayService hospitalStayService;
    private PatientService patientService;
    private WardService wardService;


}
