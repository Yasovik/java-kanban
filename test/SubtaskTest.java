import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {
    InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();

    @Test
    @DisplayName("Проверка, что наследники класса Task равны друг другу, если равен их id")
    public void subtaskEqualsSubtask1Test() {
        Subtask subtask = new Subtask("1", "1", Status.NEW, 1);
        Subtask subtask1 = new Subtask("2", "1", Status.NEW, 1);
        subtask.setId(1);
        subtask1.setId(1);
        assertEquals(subtask.getId(), subtask1.getId(), "Идентификаторы не равны");
        assertEquals(subtask, subtask1, "Задачи не равны");
    }

    @Test
    @DisplayName("Проверка на то, что объект Subtask нельзя сделать своим же эпиком")
    public void subtaskNotEpicTest() {
        Epic epic = new Epic("1", "2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("1", "2", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);
        subtask.setId(subtask.getId());
        assertNotEquals(subtask.getId(), subtask.getIdEpic(), "Подзадача не может быть собственным эпиком");
    }
}
