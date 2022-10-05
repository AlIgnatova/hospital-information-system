package course.spring.hospitalinformationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull
    @NonNull
    private Long id;
    @NotNull
    @NonNull
    private String firstName;
    @NotNull
    @NonNull
    private String lastName;
    @NotNull
    @NonNull
    private String username;
    @NotNull
    @NonNull
    private String role;
    @NotNull
    @NonNull
    private String emailAddress;

}
