package vitua.kotler.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
public class KotlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KotlerApplication.class, args);
	}

}
