package hexlet.code;

import hexlet.code.repository.UserRepository;
import hexlet.code.model.User;
import java.util.HashMap;

import net.datafaker.Faker;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
import org.instancio.Instancio;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AppApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private Faker faker;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ObjectMapper om;

	@Test
	void contextLoads() {
	}

	// Технически имя тестового метода не важно
	// Лучше использовать шаблон testНазваниеМетодаКонтроллера для основного сценария
	@Test
	public void testIndex() throws Exception {
		mockMvc.perform(get("/users"))
				.andExpect(status().isOk());
	}
	private User generateUser() {
		return Instancio.of(User.class)
				.ignore(Select.field(User::getId))
				.supply(Select.field(User::getFirstName), () -> "Mike")
				.supply(Select.field(User::getLastName), () -> "Mikin")
				.supply(Select.field(User::getEmail), () -> "post@mail.ru")
				.supply(Select.field(User::getPassword), () -> "12345")
				.create();
	}

	@Test
	public void testViewUser() throws Exception {
		User user = generateUser();
		userRepository.save(user);
		mockMvc.perform(get("/users/" + user.getId()))
//        mockMvc.perform(get("/tasks/{id}", task.getId()))
				.andExpect(status().isOk())
				.andExpect(result -> {
					var body = result.getResponse().getContentAsString();
					assertThatJson(body).node("id").isEqualTo(user.getId());
					assertThatJson(body).node("email").isEqualTo(user.getEmail());
				});
	}

	@Test
	public void testUpdate() throws Exception {
		var user = Instancio.of(User.class)
				.ignore(Select.field(User::getId))
				.supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
//				.supply(Select.field(User::getEmail), () -> "post@mail.ru")
				.create();
		userRepository.save(user);

		var data = new HashMap<>();
		data.put("firstName", "Mike");

		var request = put("/users/" + user.getId())
				.contentType(MediaType.APPLICATION_JSON)
				// ObjectMapper конвертирует Map в JSON
				.content(om.writeValueAsString(data));

		mockMvc.perform(request)
				.andExpect(status().isOk());

		user = userRepository.findById(user.getId()).get();
		assertThat(user.getFirstName()).isEqualTo(("Mike"));
	}
}


