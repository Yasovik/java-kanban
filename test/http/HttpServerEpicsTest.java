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

public class HttpServerEpicsTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.gson;
    String urlEpics = "http://localhost:8080/epics/";
    String urlSubtasks = "http://localhost:8080/subtasks";
    Epic epic;

    @BeforeEach
    public void setUp() {
        taskServer.start();
        epic = new Epic("Epic", "EpicTest");

    }

    @AfterEach
    public void quit() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Создание и получение епика")
    public void createEpicTest() {
        String body = gson.toJson(epic);
        Response response = given().when().body(body).post(urlEpics);
        response.then().statusCode(SC_CREATED);
        Response response1 = given().when().get(urlEpics);
        response1.then().statusCode(SC_OK);
        response1.then().body("name[0]", equalTo("Epic"));
    }

    @Test
    @DisplayName("Получение епика по ИД")
    public void getEpicByIdTest() {
        String body = gson.toJson(epic);
        Response response = given().when().body(body).post(urlEpics);
        response.then().statusCode(SC_CREATED);
        Response response1 = given().queryParam("id", "1").when().get(urlEpics);
        response1.then().statusCode(SC_OK);
        response1.then().body("name", equalTo("Epic"));
    }

    @Test
    @DisplayName("Получение епика по ИД 404")
    public void getEpicById404Test() {
        Response response1 = given().queryParam("id", "1").when().get(urlEpics);
        response1.then().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Удаление епика по ИД")
    public void deleteEpicTest() {
        String body = gson.toJson(epic);
        Response response = given().when().body(body).post(urlEpics);
        response.then().statusCode(SC_CREATED);
        given().queryParam("id", "1").when().delete(urlEpics).then().statusCode(SC_OK);
        Response response1 = given().queryParam("id", "1").when().get(urlEpics);
        response1.then().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Получение сабтасков епика")
    public void getEpicSubtasksTest() {
        String body1 = gson.toJson(epic);
        given().when().body(body1).post(urlEpics);
        Subtask subtask = new Subtask("1", "1", Status.NEW, 1);
        subtask.setStartTime(LocalDateTime.of(2024, 8, 16, 0, 0));
        subtask.setDuration(Duration.ofHours(14));
        String body = gson.toJson(subtask);
        Response response = given().when().body(body).post(urlSubtasks);
        response.then().statusCode(SC_CREATED);
        Response response1 = given().when().pathParam("tail", "1/subtasks").get(urlEpics + "{tail}");
        response1.then().statusCode(SC_OK);
        response1.then().body("name[0]", equalTo("1"));
    }
}
