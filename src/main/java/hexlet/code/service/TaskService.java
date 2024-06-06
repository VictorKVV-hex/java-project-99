package hexlet.code.service;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskDto;
//import hexlet.code.dto.TaskParamsDto;
import hexlet.code.dto.TaskParamsDto;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
//import hexlet.code.specification.TaskSpecification;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;

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

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
