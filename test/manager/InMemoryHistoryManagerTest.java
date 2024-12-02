package manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    Task task1 = new Task("task1", "descriptionTask1", Status.NEW);
    Task task2 = new Task("task2", "descriptionTask2", Status.IN_PROGRESS);
    Task updatedTask = new Task("task1", "description2", Status.DONE);
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    @DisplayName("задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных")
    void shouldKeepPreviousVersionTaskInHistory() {
        taskManager.addTask(task1);
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
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        int result = taskManager.getHistory().size();
        assertEquals(1, result);

    }

    @Test
    @DisplayName("Проверка, что встроенный связный список версий, удаляет корректно")
    void deleteInHistoryTest() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.deleteTask(task1.getId());
        int result = taskManager.getHistory().size();
        assertEquals(1, result);

    }

    @Test
    @DisplayName("Проверка, что встроенный связный список версий, хранит только уникальные записи}")
    void distinctValueInHistoryTest() {
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        int result = taskManager.getHistory().size();
        assertEquals(1, result);

    }

}
