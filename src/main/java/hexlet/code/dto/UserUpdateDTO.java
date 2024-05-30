package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {
    private JsonNullable<String> firstName;
    private JsonNullable<String> lastName;
    @NotNull
    @Email
    private JsonNullable<String> email;
    @NotNull(message = "Пароль не менее 3 символов")
    @Size(min = 3)
    private JsonNullable<String> passwordDigest;
}
