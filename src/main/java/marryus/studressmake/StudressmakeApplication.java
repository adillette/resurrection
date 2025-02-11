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

	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void checkConnection() {
		try (Connection conn = dataSource.getConnection()) {
			System.out.println("DB 연결 성공!");
			System.out.println("URL: " + conn.getMetaData().getURL());
			System.out.println("Username: " + conn.getMetaData().getUserName());
		} catch (SQLException e) {
			System.out.println("DB 연결 실패!");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(StudressmakeApplication.class, args);
	}
}

