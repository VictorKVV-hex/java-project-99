package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private ModelGenerator modelGenerator;
    private JwtRequestPostProcessor token;
    private User testUser;
    private Task testTask;
    private TaskStatus testStatus;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getTestUser()).create();
        userRepository.save(testUser);

        testStatus = Instancio.of(modelGenerator.getTestStatus()).create();
        taskStatusRepository.save(testStatus);

        testLabel = Instancio.of(modelGenerator.getTestLabel()).create();
        labelRepository.save(testLabel);

        testTask = Instancio.of(modelGenerator.getTestTask()).create();
        testTask.setTaskStatus(testStatus);
        testTask.setAssignee(testUser);
        testTask.setLabels(Set.of(testLabel));
        taskRepository.save(testTask);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        //
        var request = get("/api/tasks/" + testTask.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()));
    }

    @Test
    public void testCreate() throws Exception {
        var newTask = Instancio.of(modelGenerator.getTestTask()).create();
        newTask.setTaskStatus(testStatus);
        newTask.setAssignee(testUser);
        newTask.setLabels(Set.of(testLabel));
        var dto = taskMapper.mapToCreateDto(newTask);

        var request = post("/api/tasks").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findById(testTask.getId()).orElse(null);
        assertNotNull(task);
        assertThat(task.getIndex()).isEqualTo(testTask.getIndex());
        assertThat(task.getAssignee().getId()).isEqualTo(testTask.getAssignee().getId());
        assertThat(task.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(testTask.getTaskStatus().getSlug());
        assertThat(task.getName()).isEqualTo(testTask.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        //taskRepository.save(testTask);

        var data = new TaskUpdateDto();
        data.setTitle(JsonNullable.of("title1"));
        data.setDescription(JsonNullable.of("description1"));

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        testTask = taskRepository.findById(testTask.getId()).orElse(null);
        assertNotNull(testTask);
        assertThat(testTask.getDescription()).isEqualTo(data.getDescription().get());
        assertThat(testTask.getName()).isEqualTo(data.getTitle().get());
    }

    @Test
    public void testDestroy() throws Exception {
        //
        var request = delete("/api/tasks/" + testTask.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }

/*    @Test
    public void testUpdate1() throws Exception {
*//*        TestUtils.saveTask(mockMvc, testTask);
        var task = TestUtils.getTaskByName(mockMvc, testTask.getName());
        var data = new HashMap<String, String>();
        var name = "New Task Name";
        data.put("title", name);*//*

        var data = new TaskUpdateDto();
        data.setTitle(JsonNullable.of("New Task Name"));

        var request = put("/api/tasks/" + testTask.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("title").isEqualTo(data.getTitle()),
//                v -> v.node("status").isEqualTo(testTask.getTaskStatus()),
                v -> v.node("taskLabelIds").isEqualTo(testTask.getLabels()) // у тебя падает вот тут
        );
*//*        var request = put("/api/tasks/{id}", task.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("title").isEqualTo(data.get("title")),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus()),
                v -> v.node("taskLabelIds").isEqualTo(testTask.getLabels()) // у тебя падает вот тут
        );

        var actualTask = TestUtils.getTaskByName(mockMvc, name);

        assertEquals(name, actualTask.getName());
        assertEquals(testTask.getDescription(), actualTask.getDescription());
        assertEquals(testTask.getTaskStatus(), actualTask.getTaskStatus());
        assertEquals(testTask.getLabels(), actualTask.getLabels());*//*
    }*/
}
