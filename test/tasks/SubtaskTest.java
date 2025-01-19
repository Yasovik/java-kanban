package tasks;

import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    @DisplayName("Проверка на то, что объект Subtask нельзя сделать своим же эпиком")
    public void subtaskNotEpicTest() {
        Epic epic = new Epic("1", "2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("1", "2", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.of(2024, 7, 18, 0, 0));
        subtask.setDuration(Duration.ofHours(4));
        taskManager.addSubtask(subtask);
        subtask.setId(subtask.getId());
        assertNotEquals(subtask.getId(), subtask.getIdEpic(), "Подзадача не может быть собственным эпиком");
    }

    @Test
    @DisplayName("Проверка: Удаляемые подзадачи не должны хранить внутри себя старые id.")
    void deleteSubTaskNoContainsOldIdTest() {
        Epic epic = new Epic("epic1", "description1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("subtask1", "description1", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.of(2024, 7, 24, 0, 0));
        subtask.setDuration(Duration.ofHours(6));
        taskManager.addSubtask(subtask);
        epic.getSubtasksInEpic().add(subtask.getIdEpic());
        taskManager.deleteSubtaskById(subtask.getId());
        assertFalse(epic.getSubtasksInEpic().contains(subtask.getId()));
        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }
}
