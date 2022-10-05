package course.spring.hospitalinformationsystem.dto;

import course.spring.hospitalinformationsystem.entity.enums.Diagnosis;
import course.spring.hospitalinformationsystem.entity.enums.DischargeDisposition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DischargeHospitalStayDto {
    private Long id;
    private Diagnosis diagnosisOnDischarge;
    private DischargeDisposition dischargeDisposition;

}
