package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static java.net.HttpURLConnection.*;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetTasks(exchange);
                break;
            case "POST":
                handleCreateTask(exchange);
                break;
            case "DELETE":
                handleDeleteTask(exchange);
                break;
            default:
                sendText(exchange, "Method Not Allowed", HTTP_NOT_FOUND);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        List<Task> tasks = HttpTaskServer.taskManager.getTasks();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.split("=")[1]);
            Task task = HttpTaskServer.taskManager.getTaskById(id);
            if (task != null && tasks.size() >= id) {
                String jsonResponse = HttpTaskServer.gson.toJson(task);
                sendText(exchange, jsonResponse, HTTP_OK);
            } else sendNotFound(exchange);

        }
        if (query == null && !tasks.isEmpty()) {
            String jsonResponse = HttpTaskServer.gson.toJson(tasks);
            sendText(exchange, jsonResponse, HTTP_OK);
        }
        sendNotFound(exchange);

    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        try {
            Task task = HttpTaskServer.gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Task.class);
            if (task.getId() == 0) {
                if (!HttpTaskServer.taskManager.notIntersectTimeCheck(task)) {
                    sendText(exchange, "{\"message\":\"Таски пересекаются по времени\"}", HTTP_NOT_ACCEPTABLE);
                    return;
                }
                HttpTaskServer.taskManager.addTask(task);
                sendText(exchange, "{\"message\":\"Таска успешно создана\"}", HTTP_CREATED);
            }
            HttpTaskServer.taskManager.updateTask(task);
            sendText(exchange, "{\"message\":\"Таска успешно обновлена\"}", HTTP_CREATED);
        } catch (Exception e) {
            sendInternalServerError(exchange, e.getMessage());
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.split("=")[1]);
            HttpTaskServer.taskManager.deleteTask(id);
            sendText(exchange, "{\"message\":\"Tаска удалена\"}", HTTP_OK);
        } else {
            sendNotFound(exchange);
        }
    }
}