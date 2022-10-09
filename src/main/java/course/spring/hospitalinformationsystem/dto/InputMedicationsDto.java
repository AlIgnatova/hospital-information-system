package course.spring.hospitalinformationsystem.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class InputMedicationsDto {

    private List<String> medicationList;
}
