package course.spring.hospitalinformationsystem.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class HospitalStayDto {

    @NonNull
    @NotNull
    private Long id;
    @NonNull
    @NotNull
    private String patient;
    @NonNull
    @NotNull
    private String ward;
    @NonNull
    @NotNull
    private String diagnosisOnAdmission;

    private String diagnosisOnDischarge;
    @NonNull
    @NotNull
    private String admissionDateAndTime;
    private String dischargeDateAndTime;

    private Collection<String> decursusList = new ArrayList<>();

    private List<String> medicationsList = new ArrayList<>();
    private List<String> testsList = new ArrayList<>();

    private String dischargeDisposition;

}
