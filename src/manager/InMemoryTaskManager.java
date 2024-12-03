package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static status.Status.NEW;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private int nextId;
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        nextId = 1;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        for (Epic epic : epics.values()) {
            epic.getSubtasksInEpic().clear();
            updateStatus(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getIdEpic());

        if (epic == null) {
            return;
        }

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasksInEpic().add(subtask.getId());
        updateStatus(epic.getId());
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null) {
                updateStatus(epic.getId());
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        historyManager.remove(id);
        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            for (Integer subtaskId : removedEpic.getSubtasksInEpic()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        historyManager.remove(id);
        Subtask removedSubtask = subtasks.remove(id);
        if (removedSubtask != null) {
            Epic epic = epics.get(removedSubtask.getIdEpic());
            if (epic != null) {
                epic.getSubtasksInEpic().remove((Integer) id);
                updateStatus(epic.getId());
            }
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return new ArrayList<>();
        }

        List<Subtask> subtasksList = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksInEpic()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                subtasksList.add(subtask);
            }
        }
        return subtasksList;
    }

    public void updateStatus(int epicId) {
        List<Subtask> subtasks = getSubtasksByEpicId(epicId);
        if (subtasks.isEmpty()) {
            epics.get(epicId).setStatus(NEW);
            return;
        }

        boolean isNew = true;
        boolean isDone = true;
        for (Subtask subtask : subtasks) {
            Status subtaskStatus = subtask.getStatus();
            if (subtaskStatus != Status.DONE) {
                isDone = false;
            }
            if (subtaskStatus != Status.NEW) {
                isNew = false;
            }
        }

        if (isDone) {
            epics.get(epicId).setStatus(Status.DONE);
        } else if (isNew) {
            epics.get(epicId).setStatus(Status.NEW);
        } else if (!isDone && !isNew) {
            epics.get(epicId).setStatus(Status.IN_PROGRESS);
        }

    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
