package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
//import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDto {
    @NotBlank
    @Size(min = 1)
    private JsonNullable<String> name;

    @NotBlank
    @Size(min = 1)
    private JsonNullable<String> slug;
}
