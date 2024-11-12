package tasks;

import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EpicTest {

    InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();

    @Test
    @DisplayName("Проверка, что наследники класса Task равны друг другу, если равен их id")
    public void epicEqualsEpic1Test() {
        Epic epic = new Epic("1", "1");
        Epic epic1 = new Epic("2", "2");
        epic.setId(1);
        epic1.setId(1);
        assertEquals(epic.getId(), epic1.getId(), "Идентификаторы не равны");
        assertEquals(epic, epic1, "Задачи не равны");
    }

    @Test
    @DisplayName("Проверка, что объект Epic нельзя добавить в самого себя в виде подзадачи")
    public void epicAddInEpicTest() {
        Epic epic = new Epic("epic", "desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Name", "desc", Status.NEW, epic.getId());
        subtask.setId(epic.getId());
        taskManager.addSubtask(subtask);
        assertFalse(epic.getSubtasksInEpic().isEmpty(), "Эпик не может добавить себя в качестве подзадачи");
    }
}
