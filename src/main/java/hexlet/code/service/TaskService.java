package hexlet.code.service;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskDto;
//import hexlet.code.dto.TaskParamsDto;
import hexlet.code.dto.TaskParamsDto;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
//import hexlet.code.specification.TaskSpecification;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    public List<TaskDto> getAll(TaskParamsDto params) {
        var specification = taskSpecification.build(params);
        var tasks = taskRepository.findAll(specification);
        var result = tasks.stream()
                .map(t -> taskMapper.map(t))
                .toList();
        return result;
    }

    public TaskDto findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача с id " + id + " не найдена"));

        var result = taskMapper.map(task);
        return result;
    }

    public TaskDto create(TaskCreateDto dto) {
        var task = taskMapper.map(dto);
        taskRepository.save(task);
        var result = taskMapper.map(task);
        return result;
    }

    public TaskDto update(TaskUpdateDto dto, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача с id " + id + " не найдена"));
        taskMapper.update(dto, task);
        taskRepository.save(task);
        var result = taskMapper.map(task);
        return result;
    }

/*    public TaskDto update(Long id, TaskUpdateDto taskRequest) {
        Task task = taskRepository.findById(id).orElseThrow();
        Task updated = taskMapper.update(taskRequest, task);
        setAssociations(taskRequest, updated);
        Task saved = taskRepository.save(updated);
        return taskMapper.map(saved);
    }

    private void setAssociations(TaskUpdateDto taskRequest, Task task) {
        TaskStatus taskStatus = null;
        if (taskRequest.getStatus() != null) {
            taskStatus = taskStatusRepository.findBySlug(taskRequest.getStatus().get()).orElseThrow();
        }
        User user = null;
        if (taskRequest.getAssignee_id() != null) {
            user = userRepository.findById(taskRequest.getAssignee_id().get()).orElseThrow();
        }
        List<Label> labels = null;
        if (taskRequest.getTaskLabelIds() != null) {
            labels = labelRepository.findAllById(taskRequest.getTaskLabelIds().get());
        }
        task.setTaskStatus(taskStatus);
        task.setAssignee(user);
        task.setLabels(labels != null ? new HashSet<>(labels) : new HashSet<>());
    }*/

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
