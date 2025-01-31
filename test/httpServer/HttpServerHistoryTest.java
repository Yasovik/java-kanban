package httpServer;

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

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class HttpServerHistoryTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.gson;
    String url = "http://localhost:8080/history";
    String urlTasks = "http://localhost:8080/tasks";

    @BeforeEach
    public void setUp() {
        taskServer.start();
        Task task = new Task("2", "2", Status.NEW);
        Task task1 = new Task("3", "3", Status.NEW);
        String body = gson.toJson(task);
        String body1 = gson.toJson(task1);
        Response response = given().when().body(body).post(urlTasks);
        Response response1 = given().when().body(body1).post(urlTasks);
        response.then().statusCode(SC_CREATED);
        response1.then().statusCode(SC_CREATED);
        given().when().queryParam("id", "1").get(urlTasks).then().statusCode(SC_OK);
        given().when().queryParam("id", "2").get(urlTasks).then().statusCode(SC_OK);
    }

    @AfterEach
    public void quit() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Получение истории")
    public void getHistoryTest() {
        Response response = given().when().get(url);
        response.then().log().all();
        response.then().statusCode(SC_OK);
        response.then().body("name[0]", equalTo("2"));
        response.then().body("name[1]", equalTo("3"));
    }
}
