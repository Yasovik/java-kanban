import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;
    Task task;
    Task task1;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    public void setUp() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        task = new Task("1", "1", Status.NEW);
        task1 = new Task("2", "3", Status.NEW);
        taskManager.addTask(task);
        epic = new Epic("2", "2");
        taskManager.addEpic(epic);
        subtask = new Subtask("3", "3", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);
    }

    @Test
    @DisplayName("Проверка, что InMemoryTaskManager действительно добавляет задачи типа TASK")
    public void InMemoryTaskManagerAddTaskTest() {
        Task taskActual = taskManager.getTaskById(task.getId());
        assertEquals(task, taskActual);
    }

    @Test
    @DisplayName("Проверка, что InMemoryTaskManager действительно добавляет задачи типа EPIC")
    public void InMemoryTaskManagerAddEpicTest() {
        Epic epicActual = (Epic) taskManager.getEpicById(epic.getId());
        assertEquals(epic, epicActual);
    }

    @Test
    @DisplayName("Проверка, что InMemoryTaskManager действительно добавляет задачи типа SUBTASK")
    public void InMemoryTaskManagerAddSubTaskTest() {
        Subtask subtaskActual = (Subtask) taskManager.getSubtaskById(subtask.getId());
        assertEquals(subtask, subtaskActual);
    }

    @Test
    @DisplayName("Проверка на конфликт между заданным и сгенерированным id внутри менеджера")
    void taskConflictIdTest() {
        task.setId(1);
        taskManager.addTask(task);
        assertNotEquals(task.getId(), task1.getId());
    }
}
