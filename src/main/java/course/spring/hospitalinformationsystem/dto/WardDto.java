package course.spring.hospitalinformationsystem.dto;

import lombok.*;
import javax.validation.constraints.NotNull;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class WardDto {

    @NotNull
    @NonNull
    private Long id;
    @NotNull
    @NonNull
    private String wardType;
    @NotNull
    @NonNull
    private int bedAvailability;
    private List<String> staff;
    private List<String> patients;




}
