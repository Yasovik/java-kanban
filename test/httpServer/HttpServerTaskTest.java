package httpServer;

import com.google.gson.Gson;
import io.restassured.response.Response;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import status.Status;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class HttpServerTaskTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.gson;
    String url = "http://localhost:8080/tasks";

    @BeforeEach
    public void setUp() {
        taskServer.start();
    }

    @AfterEach
    public void quit() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Создание таски 200 ОК")
    public void createTaskTest() {
        Task task = new Task("1", "2", Status.NEW);
        String body = gson.toJson(task);
        Response response = given().when().body(body).log().all().post(url);
        response.then().statusCode(SC_CREATED);
    }

    @Test
    @DisplayName("Получение тасок 200 ОК и сравнение полей")
    public void getTaskTest() {
        Task task = new Task("2", "2", Status.NEW);
        String body = gson.toJson(task);
        Response response = given().when().body(body).post(url);
        response.then().statusCode(SC_CREATED);
        Response response1 = given().when().get(url);
        response1.then().log().all().statusCode(SC_OK);
        Assertions.assertEquals(task.getName(), response1.then().extract().path("name[0]"));
        Assertions.assertEquals(task.getDescription(), response1.then().extract().path("description[0]"));
        Assertions.assertEquals(task.getStatus().toString(), response1.then().extract().path("status[0]"));
    }

    @Test
    @DisplayName("Получение тасок по ИД 200 ОК и сравнение полей")
    public void getTaskByIdTest() {
        Task task = new Task("2", "2", Status.NEW);
        String body = gson.toJson(task);
        Response response = given().when().body(body).post(url);
        response.then().statusCode(SC_CREATED);
        Response response1 = given().queryParam("id", "1").when().get(url);
        response1.then().statusCode(SC_OK);
        Assertions.assertEquals(task.getName(), response1.then().extract().path("name[0]"));
        Assertions.assertEquals(task.getDescription(), response1.then().extract().path("description[0]"));
        Assertions.assertEquals(task.getStatus().toString(), response1.then().extract().path("status"));
    }

    @Test
    @DisplayName("Получение тасок по ИД если их нет 404")
    public void getTaskByIdNotFoundTest() {
        Response response1 = given().queryParam("id", "1").when().get(url);
        response1.then().statusCode(SC_NOT_FOUND);
        response1.then().body("error", containsString("Resource Not Found"));
    }

    @Test
    @DisplayName("Обновление таски 200 ОК")
    public void updateTaskTest() {
        Task task = new Task("1", "2", Status.NEW);
        String body = gson.toJson(task);
        Response response = given().when().body(body).post(url);
        response.then().statusCode(SC_CREATED);
        task.setName("NEW");
        task.setId(1);
        String body1 = gson.toJson(task);
        Response response1 = given().when().body(body1).post(url);
        response1.then().statusCode(SC_CREATED);
        Response response2 = given().queryParam("id", "1").when().get(url);
        Assertions.assertEquals("NEW", response2.then().extract().path("name"));
    }

    @Test
    @DisplayName("406 если таски пересекаются")
    public void createTask406Test() throws InterruptedException {
        Task task = new Task("1", "2", Status.NEW);
        task.setStartTime(LocalDateTime.of(2024, 8, 16, 12, 0));
        task.setDuration(Duration.ofHours(14));
        Task task1 = new Task("1", "2", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2024, 8, 16, 13, 0));
        task1.setDuration(Duration.ofHours(14));
        String body = gson.toJson(task);
        String body1 = gson.toJson(task1);
        Response response = given().when().body(body).post(url);
        response.then().statusCode(SC_CREATED);
        Response response1 = given().when().body(body1).post(url);
        response1.then().statusCode(SC_NOT_ACCEPTABLE);
    }

    @Test
    @DisplayName("Удаления таски по ИД")
    public void deleteTaskTest() throws InterruptedException {
        Task task = new Task("1", "2", Status.NEW);
        String body = gson.toJson(task);
        given().when().body(body).post(url);
        Response response1 = given().when().queryParam("id", "1").body(body).delete(url);
        response1.then().statusCode(SC_OK);
        given().when().get(url).then().statusCode(SC_NOT_FOUND);
    }
}