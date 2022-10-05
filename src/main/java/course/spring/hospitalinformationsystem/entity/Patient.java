package course.spring.hospitalinformationsystem.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "patients", uniqueConstraints = {@UniqueConstraint(name = "UC_EGN", columnNames = {"EGN"})})
public class Patient {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min = 2, max = 30)
    @NonNull
    private String firstName;
    @NotNull
    @Size(min = 2, max = 30)
    @NonNull
    private String middleName;
    @NotNull
    @Size(min = 2, max = 30)
    private String lastName;
    @NotNull
    @Size(min = 10, max = 10)
    @NonNull
    private String EGN;
    @NotNull
    @NonNull
    private String address;
    @OneToMany(mappedBy = "patient", fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<HospitalStay> hospitalStaysCollection = new HashSet<>();
    @OneToMany(mappedBy = "patient", fetch = FetchType.EAGER)
    private Set<Test> testsCollection = new HashSet<>();
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();
    @ManyToOne()
    @JoinColumn(name = "ward")
    @ToString.Exclude
    private Ward ward;

    public void setHospitalStaysCollection(List<HospitalStay> hospitalStaysCollection) {
        this.hospitalStaysCollection = hospitalStaysCollection.stream().collect(Collectors.toSet());
    }

    public List<HospitalStay> getHospitalStaysCollection() {
        return hospitalStaysCollection.stream().toList();
    }

    public List<Test> getTestsCollection() {
        return testsCollection.stream().toList();
    }

    public void setTestsCollection(List<Test> testsCollection) {
        this.testsCollection.clear();
        this.testsCollection.addAll(testsCollection);
    }
}
