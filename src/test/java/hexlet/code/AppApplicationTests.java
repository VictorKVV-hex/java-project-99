package hexlet.code;

import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

}


