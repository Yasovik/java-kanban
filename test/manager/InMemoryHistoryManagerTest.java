package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    Task task1 = new Task("task1", "descriptionTask1", Status.NEW);
    Task task2 = new Task("task2", "descriptionTask2", Status.IN_PROGRESS);
    Task task3 = new Task("task3", "descriptionTask3", Status.DONE);
    Task updatedTask = new Task("task1", "description2", Status.DONE);
    Epic epic = new Epic("Epic", "EpicTest");
    Subtask subtask = new Subtask("Subtask", "SubtaskTest", Status.NEW, epic.getId());

    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @BeforeEach
    public void setUp() {
        task1.setStartTime(LocalDateTime.of(2024, 7, 16, 0, 0));
        task1.setDuration(Duration.ofHours(14));
        task2.setStartTime(LocalDateTime.of(2024, 8, 16, 0, 0));
        task2.setDuration(Duration.ofHours(14));
        task3.setStartTime(LocalDateTime.of(2024, 9, 16, 0, 0));
        task3.setDuration(Duration.ofHours(14));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
    }

    @Test
    @DisplayName("задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных")
    void shouldKeepPreviousVersionTaskInHistory() {
        historyManager.add(task1);
        taskManager.updateTask(updatedTask);
        historyManager.add(updatedTask);
        assertEquals(2, historyManager.getHistory().size(), "История должна содержать исходную и обновлённую задачу");
        assertEquals("descriptionTask1", historyManager.getHistory().get(0).getDescription(), "Первая версия задачи должна быть сохранена");
        assertEquals("description2", historyManager.getHistory().get(1).getDescription(), "Обновлённая версия должна быть добавлена в историю");
    }

    @Test
    @DisplayName("Проверка, что встроенный связный список версий, добавляет корректно")
    void addInHistoryTest() {
        taskManager.getTaskById(task1.getId());
        int result = taskManager.getHistory().size();
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Проверка, что встроенный связный список версий, удаляет корректно")
    void deleteInHistoryTest() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.deleteTask(task1.getId());
        int result = taskManager.getHistory().size();
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Проверка, что встроенный связный список версий, хранит только уникальные записи}")
    void distinctValueInHistoryTest() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        int result = taskManager.getHistory().size();
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Проверка на дублирование задач")
    public void duplicateTaskTest() {
        List<Task> historyTask = new ArrayList<>();
        task1.setStartTime(LocalDateTime.of(2024, 5, 16, 0, 0));
        task1.setDuration(Duration.ofHours(14));
        historyTask.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        Assertions.assertEquals(historyTask, historyManager.getHistory());
    }

    @Test
    @DisplayName("Проверка на удаления из начала")
    public void removeFromStartTest() {
        List<Task> historyTask = new ArrayList<>();
        historyTask.add(task2);
        historyTask.add(task3);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.deleteTask(task1.getId());
        Assertions.assertEquals(historyTask, taskManager.getHistory());
    }

    @Test
    @DisplayName("Проверка на удаление из середины")
    public void removeFromMidTest() {
        List<Task> historyTask = new ArrayList<>();
        historyTask.add(task1);
        historyTask.add(task3);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.deleteTask(task2.getId());
        Assertions.assertEquals(historyTask, taskManager.getHistory());
    }

    @Test
    @DisplayName("Проверка на удаление с конца")
    public void removeFromEndTest() {
        List<Task> historyTask = new ArrayList<>();
        historyTask.add(task1);
        historyTask.add(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.deleteTask(task3.getId());
        Assertions.assertEquals(historyTask, taskManager.getHistory());
    }

}
