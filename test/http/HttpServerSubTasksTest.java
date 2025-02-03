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
import tasks.Epic;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class HttpServerSubTasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.gson;
    String urlEpics = "http://localhost:8080/epics";
    String urlSubtasks = "http://localhost:8080/subtasks";
    Epic epic;

    @BeforeEach
    public void setUp() {
        taskServer.start();
        epic = new Epic("Epic", "EpicTest");
        given().when().body(gson.toJson(epic)).post(urlEpics).then().statusCode(SC_CREATED);
    }

    @AfterEach
    public void quit() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Создание сабтаски 201 получение сабтаски 200")
    public void createAndGetSubtaskTest() {
        Subtask subtask = new Subtask("1", "1", Status.NEW, 1);
        subtask.setStartTime(LocalDateTime.of(2024, 8, 16, 0, 0));
        subtask.setDuration(Duration.ofHours(14));
        String body = gson.toJson(subtask);
        Response response = given().when().body(body).post(urlSubtasks);
        response.then().statusCode(SC_CREATED);
        Response response1 = given().when().get(urlSubtasks);
        response1.then().statusCode(SC_OK);
        response1.then().body("name[0]", equalTo("1"));
    }

    @Test
    @DisplayName("Получение сабтаски по ИД")
    public void GetSubtaskByIdTest() {
        Subtask subtask = new Subtask("1", "1", Status.NEW, 1);
        subtask.setStartTime(LocalDateTime.of(2024, 8, 16, 0, 0));
        subtask.setDuration(Duration.ofHours(14));
        String body = gson.toJson(subtask);
        Response response = given().when().body(body).post(urlSubtasks);
        response.then().statusCode(SC_CREATED);
        Response response1 = given().queryParam("id", "2").when().get(urlSubtasks);
        response1.then().statusCode(SC_OK);
        response1.then().body("name[0]", equalTo("1"));
    }

    @Test
    @DisplayName("Получение сабтаски по ИД 404")
    public void GetSubtaskById404Test() {
        Response response1 = given().queryParam("id", "2").when().get(urlSubtasks);
        response1.then().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Создание сабтаски если они пересекаются 406")
    public void createSubtasks406Test() {
        Subtask subtask = new Subtask("1", "1", Status.NEW, 1);
        subtask.setStartTime(LocalDateTime.of(2024, 8, 16, 12, 0));
        subtask.setDuration(Duration.ofHours(14));
        Subtask subtask1 = new Subtask("1", "1", Status.NEW, 1);
        subtask1.setStartTime(LocalDateTime.of(2024, 8, 16, 13, 0));
        subtask1.setDuration(Duration.ofHours(14));
        String body = gson.toJson(subtask);
        String body1 = gson.toJson(subtask1);
        given().when().body(body).post(urlSubtasks).then().statusCode(SC_CREATED);
        Response response1 = given().when().body(body1).post(urlSubtasks);
        response1.then().statusCode(SC_NOT_ACCEPTABLE);
    }

    @Test
    @DisplayName("Обновление сабтаси 201")
    public void updateSubtasks201Test() {
        Subtask subtask = new Subtask("1", "1", Status.NEW, 1);
        subtask.setStartTime(LocalDateTime.of(2024, 8, 16, 12, 0));
        subtask.setDuration(Duration.ofHours(14));
        String body = gson.toJson(subtask);
        given().when().body(body).post(urlSubtasks).then().statusCode(SC_CREATED);
        subtask.setName("NEW");
        subtask.setId(2);
        String body1 = gson.toJson(subtask);
        given().when().body(body1).post(urlSubtasks).then().statusCode(SC_CREATED);
        Response response1 = given().when().get(urlSubtasks);
        response1.then().statusCode(SC_OK);
        response1.then().body("name[0]", equalTo("NEW"));
    }

    @Test
    @DisplayName("Удаление сабтаски")
    public void deleteSubtasksTest() {
        Subtask subtask = new Subtask("1", "1", Status.NEW, 1);
        subtask.setStartTime(LocalDateTime.of(2024, 8, 16, 12, 0));
        subtask.setDuration(Duration.ofHours(14));
        String body = gson.toJson(subtask);
        given().when().body(body).post(urlSubtasks).then().statusCode(SC_CREATED);
        Response response = given().queryParam("id", "2").when().delete(urlSubtasks);
        response.then().statusCode(SC_OK);
        given().when().delete(urlSubtasks).then().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Создание сабтаски 500")
    public void createSubtask500Test() {
        Subtask subtask = new Subtask("1", "1", Status.NEW, 1);
        String body = gson.toJson(subtask);
        Response response = given().when().body(body).post(urlSubtasks);
        response.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
