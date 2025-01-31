package httpServer.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import httpServer.HttpTaskServer;
import tasks.Task;

import java.io.IOException;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Task> history = HttpTaskServer.taskManager.getPrioritizedTasks();
            String jsonResponse = HttpTaskServer.gson.toJson(history);
            sendText(exchange, jsonResponse, HTTP_OK);
        } else {
            sendText(exchange, "Method Not Allowed", HTTP_NOT_FOUND);
        }
    }
}