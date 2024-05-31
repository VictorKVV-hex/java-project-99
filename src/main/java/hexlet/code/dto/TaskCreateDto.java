package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TaskCreateDto {
    private int index;

    private long assignee_id;

    @NotNull
    @Size(min = 1)
    private String title;

    private String content;

    @NotNull
    private String status;
}
