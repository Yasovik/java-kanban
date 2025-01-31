package httpServer.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import httpServer.HttpTaskServer;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static java.net.HttpURLConnection.*;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetSubTasks(exchange);
                break;
            case "POST":
                handleCreateSubTask(exchange);
                break;
            case "DELETE":
                handleDeleteSubTask(exchange);
                break;
            default:
                sendText(exchange, "Method Not Allowed", HTTP_NOT_FOUND);
        }
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        List<Subtask> tasks = HttpTaskServer.taskManager.getSubtasks();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.split("=")[1]);
            Task tasksA = HttpTaskServer.taskManager.getSubtaskById(id);
            if (tasksA != null && tasks.size() >= id - 1) {
                String jsonResponse = HttpTaskServer.gson.toJson(tasksA);
                sendText(exchange, jsonResponse, HTTP_OK);
            } else sendNotFound(exchange);

        }
        if (query == null && !tasks.isEmpty()) {
            String jsonResponse = HttpTaskServer.gson.toJson(tasks);
            sendText(exchange, jsonResponse, HTTP_OK);
        }
        sendNotFound(exchange);

    }

    private void handleCreateSubTask(HttpExchange exchange) throws IOException {
        try {
            Subtask task = HttpTaskServer.gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Subtask.class);
            if (task.getId() == 0) {
                if (!HttpTaskServer.taskManager.notIntersectTimeCheck(task)) {
                    sendText(exchange, "{\"message\":\"Сабтаски пересекаются по времени\"}", HTTP_NOT_ACCEPTABLE);
                    return;
                }
                HttpTaskServer.taskManager.addSubtask(task);
                sendText(exchange, "{\"message\":\"Сабтаск успешно создан\"}", HTTP_CREATED);
            }
            HttpTaskServer.taskManager.updateSubtask(task);
            sendText(exchange, "{\"message\":\"Сабтаск успешно обновлен\"}", HTTP_CREATED);
        } catch (Exception e) {
            sendInternalServerError(exchange, e.getMessage());
        }
    }

    private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.split("=")[1]);
            HttpTaskServer.taskManager.deleteSubtaskById(id);
            sendText(exchange, "{\"message\":\"Сабтаск удален\"}", HTTP_OK);
        } else {
            sendNotFound(exchange);
        }
    }
}