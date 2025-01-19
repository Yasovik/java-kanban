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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static utils.Utils.formatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,startTime,duration,endTime,epic\n");
            for (Task task : getTasks()) {
                fileWriter.write(toString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(toString(epic) + "\n");
            }

            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(toString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать данные в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.read();
        return fileBackedTaskManager;
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
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

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        sb.append(task.getStartTime()).append(",");
        sb.append(task.getDuration()).append(",");
        sb.append(task.getEndTime()).append(",");
        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getIdEpic());
        } else {
            sb.append("");
        }
        return sb.toString();
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        String type = parts[1];
        String name = parts[2];
        String status = parts[3];
        String desc = parts[4];
        String startTime = parts[5];
        Duration duration = Duration.parse(parts[6]);
        String endTime = parts[7];
        TypeTask typeTask = TypeTask.valueOf(type);
        Task task = null;
        if (typeTask == TypeTask.TASK) {
            task = new Task(name, desc, Status.valueOf(status));
            LocalDateTime start = LocalDateTime.parse(startTime.formatted(formatter));
            task.setStartTime(start);
            task.setDuration(Duration.ofMinutes(duration.toMinutes()));
        }
        if (typeTask == TypeTask.EPIC) {
            task = new Epic(name, desc);
            if (!Objects.equals(startTime, "Время начала не определено")) {
                task.setStartTime(LocalDateTime.parse(startTime.formatted(formatter)));
            }
            if (!Objects.equals(duration, "Продолжительность не определена")) {
                task.setDuration(Duration.ofMinutes(duration.toMinutes()));
            }
            if (!Objects.equals(endTime, "Время окончания не определено")) {
                task.setEndTime(LocalDateTime.parse(startTime.formatted(formatter)));
            }
        }
        if (typeTask == TypeTask.SUB_TASK) {
            String epicId = parts[8];
            task = new Subtask(name, desc, Status.valueOf(status), Integer.parseInt(epicId));
            task.setStartTime(LocalDateTime.parse(startTime.formatted(formatter)));
            task.setDuration(Duration.ofMinutes(duration.toMinutes()));
        }
        return task;
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
}
