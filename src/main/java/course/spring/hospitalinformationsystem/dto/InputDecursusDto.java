package course.spring.hospitalinformationsystem.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class InputDecursusDto {
    @Size(min = 5)
    private String text;
}
