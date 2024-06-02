package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelCreateDto {
    @Size(min = 3, max = 1000)
    @NotNull
    private String name;
}
