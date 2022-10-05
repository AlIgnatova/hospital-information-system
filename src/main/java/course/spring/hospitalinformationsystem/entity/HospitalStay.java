package course.spring.hospitalinformationsystem.entity;


import course.spring.hospitalinformationsystem.entity.enums.Diagnosis;
import course.spring.hospitalinformationsystem.entity.enums.DischargeDisposition;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;

import static course.spring.hospitalinformationsystem.dto.Converter.getDateAndTime;

@Entity(name = "hospitalstays")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HospitalStay {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @NotNull
    @ManyToOne
    @ToString.Exclude
    private Patient patient;
    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ward", nullable = false)
    private Ward ward;
    private boolean isStayActive = true;
    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    private Diagnosis diagnosisOnAdmission;
    @Enumerated(EnumType.STRING)
    private Diagnosis diagnosisOnDischarge;
    private LocalDateTime admissionDateAndTime;
    private LocalDateTime dischargeDateAndTime;
    private LocalDateTime modified;
    @OneToMany(mappedBy = "hospitalStay",fetch = FetchType.EAGER)
    private Set<Decursus> decursusList = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> medicationsList = new HashSet<>();
    @OneToMany(mappedBy = "hospitalStay",fetch = FetchType.EAGER)
    private Set<Test> testsList = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private DischargeDisposition dischargeDisposition;

    public List<Decursus> getDecursusList() {
        List<Decursus> list = decursusList.stream().sorted(Comparator.comparingLong(d -> d.getId())).toList();
        return list;
    }

    public String DischargeSummary() {
        StringBuilder sb;
        String dischargeDisp;
        String dischargeTime;
        String dischargeDiagn;
        String recommendations = "";
        StringBuilder tests = new StringBuilder("");
        if (!decursusList.isEmpty()) {
            recommendations = getDecursusList().get(decursusList.size() - 1).getText();
        } else {
            recommendations = "Provided";
        }
        if (this.isStayActive) {
            sb = new StringBuilder("Interim epicrisis").append(System.lineSeparator());
            dischargeDisp = "Patient still not discharged";
            dischargeTime = "Patient still not discharged";
            dischargeDiagn = "Patient still not discharged";
            recommendations = "Provided";

        } else {
            sb = new StringBuilder("Discharge summary").append(System.lineSeparator());
            dischargeDisp = dischargeDisposition.getDischargeDisposition();
            dischargeTime = getDateAndTime(dischargeDateAndTime);
            dischargeDiagn = String.valueOf(diagnosisOnDischarge);
        }

        List<Test> testsList = this.getTestsList().stream()
                .filter(Test::isCompleted).toList();
        if(!testsList.isEmpty()) {

            testsList.stream().forEach(t -> {
                tests.append(String.format("Test id: '%d', tested: '%s', result: '%s', performed by MD '%s %s'.",
                        t.getId(), t.getWhatToBeTested(), t.getResult(), t.getPerformedBy().getFirstName(),
                        t.getPerformedBy().getLastName())).append(System.lineSeparator());
            });
        }

        sb.append(patient.getFirstName() + " " + patient.getMiddleName() + " " + patient.getLastName() + ", " + patient.getEGN()).append(System.lineSeparator());
        sb.append("Admitted on: " + getDateAndTime(admissionDateAndTime) + " with diagnosis: " + diagnosisOnAdmission).append(System.lineSeparator());
        sb.append("Discharged on: " + dischargeTime + " with diagnosis: " + dischargeDiagn).append(System.lineSeparator());
        sb.append("Performed tests: ").append(tests).append(System.lineSeparator());
        sb.append("Performed treatment: ").append(medicationsList.toString()).append(System.lineSeparator());
        sb.append("Discharge dispositon: " + dischargeDisp).append(System.lineSeparator());
        sb.append("Recommendations for home treatment: " + recommendations).append(System.lineSeparator());

        return sb.toString();
    }

}
