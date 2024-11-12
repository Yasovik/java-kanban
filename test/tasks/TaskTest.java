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
        assertEquals(task, task1, "Задачи не равны");
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

}