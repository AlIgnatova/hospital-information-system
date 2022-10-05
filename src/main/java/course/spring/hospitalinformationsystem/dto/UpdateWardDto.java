package course.spring.hospitalinformationsystem.dto;

import course.spring.hospitalinformationsystem.entity.enums.WardType;
import course.spring.hospitalinformationsystem.service.UserService;
import course.spring.hospitalinformationsystem.service.WardService;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class UpdateWardDto {

    @NonNull
    @NotNull
    private Long id;
    @NonNull
    @NotNull
    private WardType wardType;
    @NotNull
    @NonNull
    @PositiveOrZero
    private int bedCapacity;
    @NotNull
    @NonNull
    private List<String> staff = new ArrayList<>();

    private UserService userService;
    private WardService wardService;

}
