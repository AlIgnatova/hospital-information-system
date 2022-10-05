package course.spring.hospitalinformationsystem.dto;
import lombok.*;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class CompleteTestDto {
    @NonNull
    @NotNull
    private Long id;
    @NonNull
    @NotNull
    private String result;

}
