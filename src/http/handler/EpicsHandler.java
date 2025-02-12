package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static java.net.HttpURLConnection.*;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetEpics(exchange);
                break;
            case "POST":
                handleCreateEpic(exchange);
                break;
            case "DELETE":
                handleDeleteEpic(exchange);
                break;
            default:
                sendText(exchange, "Method Not Allowed", HTTP_NOT_FOUND);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        List<Epic> tasks = HttpTaskServer.taskManager.getEpics();
        String path = exchange.getRequestURI().getPath();
        if (path.endsWith("subtasks")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);
            List<Subtask> task = HttpTaskServer.taskManager.getSubtasksByEpicId(id);
            if (task.isEmpty()) {
                sendText(exchange, "{\"message\":\"Сабтасков нет\"}", HTTP_NOT_ACCEPTABLE);
            }
            String jsonResponse = HttpTaskServer.gson.toJson(task);
            sendText(exchange, jsonResponse, HTTP_OK);
        }
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.split("=")[1]);
            Epic task = (Epic) HttpTaskServer.taskManager.getEpicById(id);
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

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        try {
            Epic task = HttpTaskServer.gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Epic.class);
            if (task.getId() == 0) {
                if (!HttpTaskServer.taskManager.notIntersectTimeCheck(task)) {
                    sendText(exchange, "{\"message\":\"Эпики пересекаются по времени\"}", HTTP_NOT_ACCEPTABLE);
                    return;
                }
                HttpTaskServer.taskManager.addEpic(task);
                sendText(exchange, "{\"message\":\"Эпик успешно создан\"}", HTTP_CREATED);
            }
            HttpTaskServer.taskManager.updateEpic(task);
            sendText(exchange, "{\"message\":\"Эпик успешно обновлен\"}", HTTP_CREATED);
        } catch (Exception e) {
            sendInternalServerError(exchange, e.getMessage());
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.split("=")[1]);
            HttpTaskServer.taskManager.deleteEpic(id);
            sendText(exchange, "{\"message\":\"Эпик удален\"}", HTTP_OK);
        } else {
            sendNotFound(exchange);
        }
    }
}