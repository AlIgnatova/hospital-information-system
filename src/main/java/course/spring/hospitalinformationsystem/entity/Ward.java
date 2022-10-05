package course.spring.hospitalinformationsystem.entity;

import course.spring.hospitalinformationsystem.entity.enums.WardType;
import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "wards", uniqueConstraints = {@UniqueConstraint(name = "UC_WARD_TYPE", columnNames = {"WARDTYPE"})})
public class Ward {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    private WardType wardType;
    @NotNull
    @NonNull
    @PositiveOrZero
    private int bedCapacity;
    private int bedOccupancy;
    private int bedAvailability;
    @NotNull
    @NonNull
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ward_users",
            joinColumns = @JoinColumn(name = "WARD_ID", referencedColumnName = "ID",
                    foreignKey = @ForeignKey(name = "FK_WARD")),
            inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID",
                    foreignKey = @ForeignKey(name = "FK_USER")))
    private Set<User> staff;
    @OneToMany(mappedBy = "ward", fetch = FetchType.EAGER)
    private Set<Patient> patients = new HashSet<>();
    private LocalDateTime created;
    private LocalDateTime modified;


    public List<Patient> getPatients() {
        return patients.stream().toList();
    }

    public List<User> getStaff() {
        return staff.stream().toList();
    }

    public void setStaff(Set<User> staff) {
        this.staff.clear();
        this.staff.addAll(staff);
    }

    public void setPatients(List<Patient> patients) {
        this.patients.clear();
        this.patients.addAll(patients);
    }
}
