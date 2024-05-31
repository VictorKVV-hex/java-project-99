package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDto {
    private JsonNullable<Integer> index;

    private JsonNullable<Long> assignee_id;

    @NotNull
    @Size(min = 1)
    private JsonNullable<String> title;

    private JsonNullable<String> description;

    @NotNull
    private JsonNullable<String> status;
}
