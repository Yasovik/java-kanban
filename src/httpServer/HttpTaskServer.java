package httpServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import httpServer.handler.*;
import manager.Managers;
import manager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public static TaskManager taskManager;
    public static Gson gson;
    private HttpServer server;

    public HttpTaskServer(TaskManager manager) {
        taskManager = manager;
        gson = new GsonBuilder().registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.start();
        Task task = new Task("1", "1", Status.NEW);
        task.setStartTime(LocalDateTime.of(2024, 6, 16, 0, 0));
        task.setDuration(Duration.ofHours(14));
        System.out.println(gson.toJson(task));
        Epic epic = new Epic("3", "4");
        epic.setStartTime(LocalDateTime.of(2024, 7, 16, 0, 0));
        epic.setDuration(Duration.ofHours(2));
        Subtask subtask = new Subtask("2", "34", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.of(2024, 6, 16, 0, 0));
        subtask.setDuration(Duration.ofHours(14));
        System.out.println(gson.toJson(epic));
        System.out.println(gson.toJson(subtask));
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            System.out.println("Сервер запущен на порту " + PORT);

            server.createContext("/tasks", new TaskHandler());
            server.createContext("/subtasks", new SubtasksHandler());
            server.createContext("/epics", new EpicsHandler());
            server.createContext("/history", new HistoryHandler());
            server.createContext("/prioritized", new PrioritizedHandler());

            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Сервер остановлен.");
        }
    }
}
