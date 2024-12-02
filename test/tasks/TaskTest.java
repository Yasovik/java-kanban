package tasks;

import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    @DisplayName("Экземпляры класса Task равны друг другу, если равен их id")
    public void task1EqualsTask2Test() {
        Task task = new Task("1", "1", Status.NEW);
        Task task1 = new Task("2", "2", Status.NEW);
        task.setId(1);
        task1.setId(1);
        assertEquals(task.getId(), task1.getId(), "Идентификаторы не равны");
    }

    @Test
    @DisplayName("Проверка неизменности задачи (по всем полям) при добавлении задачи в менеджер")
    public void taskUnChangeTest() {
        Task task = new Task("1", "1", Status.NEW);
        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();
        taskManager.addTask(task);
        assertEquals(task.getName(), taskManager.getTaskById(task.getId()).getName());
        assertEquals(task.getDescription(), taskManager.getTaskById(task.getId()).getDescription());
        assertEquals(task.getStatus(), taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    @DisplayName("Проверка на изменение задач сеттерами")
    public void changeValueTest() {
        Task task = new Task("1", "1", Status.NEW);
        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();
        taskManager.addTask(task);
        Task newTask = task;
        newTask.setName("Новое Имя");
        newTask.setDescription("Новый дескриптион");
        newTask.setStatus(Status.IN_PROGRESS);
        assertEquals("Новое Имя", newTask.getName());
        assertEquals("Новый дескриптион", newTask.getDescription());
        assertEquals(Status.IN_PROGRESS, newTask.getStatus());
    }

}