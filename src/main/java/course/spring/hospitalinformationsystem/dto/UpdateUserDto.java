package course.spring.hospitalinformationsystem.dto;

import course.spring.hospitalinformationsystem.entity.enums.Role;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class UpdateUserDto {

    @NotNull
    @NonNull
    private Long id;
    @NotNull
    @Length(min = 2, max = 30)
    @NonNull
    private String firstName;
    @NotNull
    @Length(min = 2, max = 30)
    @NonNull
    private String lastName;
    @NotNull
    @NonNull
    private String username;
    @NotNull
    @NonNull
    private Role role;
    @NotNull
    @Email
    @NonNull
    private String emailAddress;
}
