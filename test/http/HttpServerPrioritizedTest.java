package http;

import com.google.gson.Gson;
import io.restassured.response.Response;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class HttpServerPrioritizedTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.gson;
    String url = "http://localhost:8080/prioritized";
    String urlTasks = "http://localhost:8080/tasks";

    @BeforeEach
    public void setUp() {
        taskServer.start();
        Task task = new Task("2", "2", Status.NEW);
        Task task1 = new Task("3", "3", Status.NEW);
        task.setStartTime(LocalDateTime.of(2025, 10, 10, 10, 0));
        task.setDuration(Duration.ofHours(2));
        task1.setStartTime(LocalDateTime.of(2024, 10, 10, 12, 0));
        task1.setDuration(Duration.ofHours(10));
        String body = gson.toJson(task);
        String body1 = gson.toJson(task1);
        Response response = given().when().body(body).post(urlTasks);
        Response response1 = given().when().body(body1).post(urlTasks);
        response.then().statusCode(SC_CREATED);
        response1.then().statusCode(SC_CREATED);
    }

    @AfterEach
    public void quit() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Приоритезация тасков")
    public void getPrioritizedTest() {
        Response response = given().when().get(url);
        response.then().statusCode(SC_OK);
        response.then().body("name[0]", equalTo("3"));
    }
}
