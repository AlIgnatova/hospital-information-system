package course.spring.hospitalinformationsystem.entity;

import course.spring.hospitalinformationsystem.entity.enums.TestType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Entity(name = "tests")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Test {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @NotNull
    @ManyToOne
    private Patient patient;
    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "hospitalstay", nullable = false)
    @ToString.Exclude
    private HospitalStay hospitalStay;
    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    private TestType testType;
    @NotNull
    @NonNull
    private String whatToBeTested;
    @ManyToOne
    @JoinColumn(name = "performedBy_id")
    private User performedBy;
    private boolean isCompleted = false;
    private String result;
    private LocalDateTime created;
    private LocalDateTime modified;
}
