package hexlet.code.service;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskDto;
//import hexlet.code.dto.TaskParamsDto;
import hexlet.code.dto.TaskParamsDto;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
//import hexlet.code.model.Label;
import hexlet.code.model.Label;
//import hexlet.code.model.Task;
//import hexlet.code.model.TaskStatus;
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

//import java.util.HashSet;
//import java.util.HashSet;
import java.util.List;
import java.util.Set;
//import java.util.Objects;

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

/*    public TaskDto create(TaskCreateDto dto) {
        var task = taskMapper.map(dto);
        taskRepository.save(task);
        var result = taskMapper.map(task);
        return result;
    }*/

/*    public TaskDto create(TaskCreateDto taskData) {
        var task = taskMapper.map(taskData);

        var assigneeId = taskData.getAssignee_id();

//       if (assigneeId != null) {
//         if(!Objects.isNull(assigneeId)){
        if (assigneeId != 0L) {
            var assignee = userRepository.findById(assigneeId).orElse(null);
            task.setAssignee(assignee);
        }

        var statusSlug = taskData.getStatus();
        var taskStatus = taskStatusRepository.findBySlug(statusSlug).orElse(null);
        task.setTaskStatus(taskStatus);


        var taskLabelIds = taskData.getTaskLabelIds();
//        if (taskLabelIds != null) {
        var labels = new HashSet<>(labelRepository.findByIdIn(taskLabelIds).orElse(new HashSet<>()));
        task.setLabels(labels);
//        }

        taskRepository.save(task);
        var taskDTO = taskMapper.map(task);
        return taskDTO;
    }*/

    public TaskDto create(TaskCreateDto dto) {
        var task = taskMapper.map(dto);

        User assignee = null;
        if (dto.getAssignee_id() != 0L) {
            assignee = userRepository.findById(dto.getAssignee_id()).orElse(null);
        }
        task.setAssignee(assignee);

        TaskStatus taskStatus = null;
        if (dto.getStatus() != null) {
            taskStatus = taskStatusRepository.findBySlug(dto.getStatus()).orElse(null);
        }
        task.setTaskStatus(taskStatus);

        Set<Label> labelSet = null;
        if (dto.getTaskLabelIds() != null) {
            labelSet = labelRepository.findByIdIn((dto.getTaskLabelIds())).orElse(null);
        }
        task.setLabels(labelSet);

        taskRepository.save(task);
        return taskMapper.map(task);
    }

/*    public TaskDto update(TaskUpdateDto dto, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача с id " + id + " не найдена"));
        taskMapper.update(dto, task);
        setAssociations(dto, task);
        taskRepository.save(task);
        var result = taskMapper.map(task);
        return result;
    }

    private void setAssociations(TaskUpdateDto taskRequest, Task task) {

*//*        if (taskRequest.getDescription() != null) {
            task.setDescription(taskRequest.getDescription().get());
        }
        TaskStatus taskStatus = null;
        if (taskRequest.getStatus() != null) {
            taskStatus = taskStatusRepository.findBySlug(taskRequest.getStatus().get()).orElseThrow();
        }*//*
        // Почему маппер не меняет User, не понятно. Но так - работет!
        User user = null;
        if (taskRequest.getAssignee_id() != null) {
            user = userRepository.findById(taskRequest.getAssignee_id().get()).orElseThrow();
        }
*//*        List<Label> labels = null;
        if (taskRequest.getTaskLabelIds() != null) {
            labels = labelRepository.findAllById(taskRequest.getTaskLabelIds().get());
        }
        task.setTaskStatus(taskStatus);*//*
        task.setAssignee(user);
//        task.setLabels(labels != null ? new HashSet<>(labels) : new HashSet<>());
    }*/

    public TaskDto update(TaskUpdateDto data, Long taskId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + taskId + " not found."));

        taskMapper.update(data, task);

        var assigneeId = data.getAssignee_id();
        if (assigneeId != null) {
            var assignee = assigneeId.get() == null ? null
                    : userRepository.findById(assigneeId.get()).orElseThrow();
            task.setAssignee(assignee);
        }

        TaskStatus taskStatus = null;
        if (data.getStatus() != null) {
            taskStatus = taskStatusRepository.findBySlug(data.getStatus().get()).orElse(null);
            task.setTaskStatus(taskStatus);
        }

        Set<Label> labelSet = null;
        if (data.getTaskLabelIds() != null) {
            labelSet = labelRepository.findByIdIn((data.getTaskLabelIds()).get()).orElse(null);
            task.setLabels(labelSet);
        }


        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
