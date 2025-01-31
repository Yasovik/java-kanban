package http.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.net.HttpURLConnection.*;

public abstract class BaseHttpHandler {
    protected void sendText(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendText(h, "{\"error\":\"Resource Not Found\"}", HTTP_NOT_FOUND);
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        sendText(h, "{\"error\":\"Not Acceptable\"}", HTTP_NOT_ACCEPTABLE);
    }

    protected void sendInternalServerError(HttpExchange h, String message) throws IOException {
        sendText(h, "{\"error\":\"Internal Server Error: " + message + "\"}", HTTP_INTERNAL_ERROR);
    }
}
