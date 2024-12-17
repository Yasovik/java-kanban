package manager;

import exception.ManagerSaveException;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private String taskToString(Task task) {
        return task.getId() + "," + TypeTask.TASK + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + ",";
    }

    private String epicToString(Epic epic) {
        return epic.getId() + "," + TypeTask.EPIC + "," + epic.getName() + "," + epic.getStatus() + "," + epic.getDescription() + ",";
    }

    private String subtaskToString(Subtask subtask) {
        return subtask.getId() + "," + TypeTask.SUB_TASK + "," + subtask.getName() + "," + subtask.getStatus() + ","
                + subtask.getDescription() + "," + subtask.getIdEpic();
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        TypeTask typeTask = TypeTask.valueOf(parts[1]);
        Task task = null;

        if (typeTask == TypeTask.TASK) {
            task = new Task(parts[2], parts[4], Status.valueOf(parts[3]));
        } else if (typeTask == TypeTask.EPIC) {
            task = new Epic(parts[2], parts[4]);
        } else if (typeTask == TypeTask.SUB_TASK) {
            task = new Subtask(parts[2], parts[4], Status.valueOf(parts[3]), Integer.parseInt(parts[5]));
        }
        return task;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                fileWriter.write(taskToString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(epicToString(epic) + "\n");
            }

            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(subtaskToString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать данные в файл");
        }
    }

    private void read() throws ManagerSaveException {
        try {
            List<String> lines = Files.readAllLines(file.toPath());

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                Task task = fromString(line);
                if (task != null) {
                    if (task.getType() == TypeTask.EPIC) {
                        addEpic((Epic) task);
                    } else if (task.getType() == TypeTask.SUB_TASK) {
                        addSubtask((Subtask) task);
                    } else if (task.getType() == TypeTask.TASK) {
                        addTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("При чтении файла произошла ошибка.");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.read();
        return fileBackedTaskManager;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void updateStatus(int epicId) {
        super.updateStatus(epicId);
        save();
    }
}
