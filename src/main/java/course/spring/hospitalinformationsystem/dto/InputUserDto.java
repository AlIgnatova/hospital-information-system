package course.spring.hospitalinformationsystem.dto;

import course.spring.hospitalinformationsystem.entity.enums.Role;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class InputUserDto {
    @NotNull
    @Length(min = 2, max = 30)
    @NonNull
    private String firstName;
    @NotNull
    @Length(min = 2, max = 30)
    @NonNull
    private String lastName;
    @NotNull
    @Length(min = 8)
    @Pattern(regexp = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")
    @NonNull
    private String password;
    private Role role;
    private String emailAddress;


}
