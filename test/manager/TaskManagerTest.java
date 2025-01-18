package manager;

import org.junit.jupiter.api.Assertions;
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

abstract class TaskManagerTest<T extends TaskManager> {
    public abstract T getTaskManager();

    public Task task = new Task("TASK1", "DESK1TASK", Status.NEW);
    public Task task1 = new Task("TASK2", "DESK2TASK", Status.NEW);
    public Epic epic = new Epic("EPIC1", "DESK1EPIC");
    public Epic epic2 = new Epic("EPIC2", "DESK2EPIC");
    public Subtask subtask = new Subtask("3", "3", Status.NEW, epic.getId());
    public Subtask subtask1 = new Subtask("4", "4", Status.NEW, epic.getId());
    public Subtask subtask2 = new Subtask("SUBTASK2", "DESK2SUBTASK", Status.NEW, epic.getId());

    @Test
    @DisplayName("Проверка на получение тасок")
    public void getTasksTest() {
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        T taskManager = getTaskManager();
        taskManager.addTask(task);
        Assertions.assertEquals(taskList, taskManager.getTasks());
    }

    @Test
    @DisplayName("Проверка на получение epic")
    public void getEpicTest() {
        List<Task> epicList = new ArrayList<>();
        epic.setStartTime(LocalDateTime.of(2024, 10, 10, 10, 0));
        epic.setDuration(Duration.ofHours(2));
        epicList.add(epic);
        T taskManager = getTaskManager();
        taskManager.addTask(epic);
        Assertions.assertEquals(epicList, taskManager.getTasks());
    }

    @Test
    @DisplayName("Проверка на получение subTask")
    public void getSubtaskTest() {
        List<Task> subTaskList = new ArrayList<>();
        subtask.setStartTime(LocalDateTime.of(2024, 10, 10, 10, 0));
        subtask.setDuration(Duration.ofHours(2));
        subTaskList.add(subtask);
        T taskManager = getTaskManager();
        taskManager.addTask(subtask);
        Assertions.assertEquals(subTaskList, taskManager.getTasks());
    }

    @Test
    @DisplayName("Проверка на удаление Task")
    public void deleteAllTaskTest() {
        List<Task> taskList = new ArrayList<>();
        subtask.setStartTime(LocalDateTime.of(2024, 10, 10, 10, 0));
        subtask.setDuration(Duration.ofHours(2));
        T taskManager = getTaskManager();
        taskManager.addTask(subtask);
        taskManager.deleteAllTasks();
        Assertions.assertEquals(taskList, taskManager.getTasks());
    }

    @Test
    @DisplayName("Проверка на удаление епиков")
    public void deleteAllEpicTest() {
        List<Task> taskList = new ArrayList<>();
        T taskManager = getTaskManager();
        taskManager.addEpic(epic);
        taskManager.deleteAllEpics();
        Assertions.assertEquals(taskList, taskManager.getEpics());
    }

    @Test
    @DisplayName("Проверка на удаление сабтасков")
    public void deleteAllSubTaskTest() {
        List<Task> taskList = new ArrayList<>();
        T taskManager = getTaskManager();
        taskManager.addSubtask(subtask);
        taskManager.deleteAllSubtasks();
        Assertions.assertEquals(taskList, taskManager.getSubtasks());
    }

    @Test
    @DisplayName("Проверка на получение тасок по ид")
    public void getTaskByIdTest() {
        List<Task> taskList = new ArrayList<>();
        task.setStartTime(LocalDateTime.of(2024, 10, 10, 10, 0));
        task.setDuration(Duration.ofHours(2));
        T taskManager = getTaskManager();
        taskList.add(task);
        taskManager.addTask(task);
        Assertions.assertEquals(taskList.get(0), taskManager.getTaskById(task.getId()));
    }

    @Test
    @DisplayName("Проверка на получение epic по ид")
    public void getEpicByIdTest() {
        List<Task> taskList = new ArrayList<>();
        T taskManager = getTaskManager();
        taskList.add(epic);
        taskManager.addEpic(epic);
        Assertions.assertEquals(taskList.get(0), taskManager.getEpicById(epic.getId()));
    }

    @Test
    @DisplayName("Проверка на удаление всех тасок")
    public void deleteAllTest() {
        T taskManager = getTaskManager();
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.deleteAll();
        Assertions.assertNull(taskManager.getSubtaskById(subtask.getId()));
        Assertions.assertNull(taskManager.getEpicById(epic.getId()));
    }

}
