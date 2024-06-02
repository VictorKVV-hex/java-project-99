package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class LabelUpdateDto {

    @Size(min = 3, max = 1000)
    @NotNull
    private JsonNullable<String> name;
}
