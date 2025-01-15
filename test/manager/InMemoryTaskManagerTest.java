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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    protected InMemoryTaskManager taskManager;

    InMemoryTaskManagerTest() {
        taskManager = new InMemoryTaskManager();
    }

    @BeforeEach
    public void setUp() {
        taskManager.addEpic(epic);
        subtask = new Subtask("3", "3", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.of(2024, 7, 18, 0, 0));
        subtask.setDuration(Duration.ofHours(5));
        taskManager.addSubtask(subtask);
        subtask1 = new Subtask("4", "4", Status.NEW, epic.getId());
        subtask1.setStartTime(LocalDateTime.of(2024, 8, 18, 0, 0));
        subtask1.setDuration(Duration.ofHours(5));
        taskManager.addSubtask(subtask1);
    }

    @Test
    @DisplayName("Проверка, что InMemoryTaskManager действительно добавляет задачи типа TASK")
    public void InMemoryTaskManagerAddTaskTest() {
        taskManager.addTask(task);
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

    @Test
    @DisplayName("Проверка что у эпика стату NEW")
    void checkEpicStatusNewTest() {
        subtask.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask);
        subtask1.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        taskManager.updateEpic(epic);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    @DisplayName("Проверка что у эпика статуc DONE")
    void checkEpicStatusDoneTest() {
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateEpic(epic);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    @DisplayName("Проверка что у эпика подзадачи NEW DONE")
    void checkEpicStatusNewDoneTest() {
        subtask.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateEpic(epic);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Проверка что у эпика IN_PROGRESS")
    void checkEpicStatusInProgressTest() {
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateEpic(epic);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Проверка на пересечение и приоритезацию")
    void intersectionAndPrioritizedTest() {
        task.setStartTime(LocalDateTime.of(2024, 10, 10, 10, 0));
        task.setDuration(Duration.ofHours(2));
        task1.setStartTime(LocalDateTime.of(2024, 10, 10, 11, 0));
        task1.setDuration(Duration.ofHours(10));
        taskManager.addTask(task);
        taskManager.addTask(task1);
        taskManager.deleteSubtaskById(subtask.getId());
        taskManager.deleteSubtaskById(subtask1.getId());
        List<Task> notIntersectionTasks = new ArrayList<>();
        notIntersectionTasks.add(task);
        Assertions.assertEquals(notIntersectionTasks, taskManager.getPrioritizedTasks());
    }

    @Override
    public InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}
