package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    public File file;
    public FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    public void setUp() throws IOException {
        file = File.createTempFile("kanban", "csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    @Test
    @DisplayName("Сохраняем пустой файл")
    public void saveEmptyFileTest() throws IOException {
        fileBackedTaskManager.save();
        List<String> exp = Files.readAllLines(file.toPath());
        int act = exp.size();
        assertEquals(1, act);
    }

    @Test
    @DisplayName("Загружаем пустой файл")
    public void loadEmptyFileTest() {
        Assertions.assertTrue(FileBackedTaskManager.loadFromFile(file).getSubtasks().isEmpty());
        Assertions.assertTrue(FileBackedTaskManager.loadFromFile(file).getEpics().isEmpty());
        Assertions.assertTrue(FileBackedTaskManager.loadFromFile(file).getTasks().isEmpty());
    }

    @Test
    @DisplayName("Сохряняем таску в файл")
    public void saveTaskTest() {
        Task task = new Task("Task", "TaskTest", Status.NEW);
        fileBackedTaskManager.addTask(task);
        List<Task> taskList = FileBackedTaskManager.loadFromFile(file).getTasks();
        assertEquals(task, taskList.get(0));
    }

    @Test
    @DisplayName("Сохраняем епик в файл")
    public void saveEpicTest() {
        Epic epic = new Epic("Epic", "EpicTest");
        fileBackedTaskManager.addEpic(epic);
        List<Epic> taskList = FileBackedTaskManager.loadFromFile(file).getEpics();
        assertEquals(epic, taskList.get(0));
    }

    @Test
    @DisplayName("Сохраняем сабтаску в файл")
    public void saveSubtaskTest() {
        Epic epic = new Epic("Epic", "EpicTest");
        fileBackedTaskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "SubtaskTest", Status.NEW, epic.getId());
        fileBackedTaskManager.addSubtask(subtask);
        List<Subtask> taskList = FileBackedTaskManager.loadFromFile(file).getSubtasks();
        assertEquals(subtask, taskList.get(0));
    }
}
