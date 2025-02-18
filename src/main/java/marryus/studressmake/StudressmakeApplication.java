package marryus.studressmake;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class StudressmakeApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudressmakeApplication.class, args);
	}
}

