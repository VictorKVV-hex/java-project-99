package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private LabelRepository labelRepository;

    @Mapping(source = "title", target = "name")
    @Mapping(source = "status", target = "taskStatus")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "toEntity")
    @Mapping(source = "assignee_id", target = "assignee.id")
    public abstract Task map(TaskCreateDto dto);

    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assignee_id")
    @Mapping(source = "labels", target = "taskLabelIds")
    public abstract TaskDto map(Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "status", target = "taskStatus")
//    @Mapping(source = "description", target = "description")
    @Mapping(source = "assignee_id", target = "assignee")
//    @Mapping(source = "assignee_id", target = "assignee.id")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "toEntity")
    public abstract void update(TaskUpdateDto dto, @MappingTarget Task model);

    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assignee_id")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "labels", target = "taskLabelIds")
    public abstract TaskCreateDto mapToCreateDto(Task model);


    public TaskStatus toTaskStatus(String statusSlug) {
        return statusRepository.findBySlug(statusSlug)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with slug " + statusSlug + " not found"));
    }

    public Set<Long> toDto(Set<Label> labels) {
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }

    @Named("toEntity")
    public Set<Label> toEntity(Set<Long> labelIds) {
        if (labelIds == null) {
            return null;
        }
        return labelIds.stream()
                .map(labelId -> labelRepository.findById(labelId)
                        .orElseThrow())
                .collect(Collectors.toSet());
    }
}
