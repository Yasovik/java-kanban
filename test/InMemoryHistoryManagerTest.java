import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    Task task1 = new Task("task1", "descriptionTask1", Status.NEW);
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
}
