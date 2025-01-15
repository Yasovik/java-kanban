package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static status.Status.NEW;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final TreeSet<Task> tasksStartTime;
    private int nextId;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        nextId = 1;
        tasksStartTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    public List<Task> getPrioritizedTasks() {
        return tasksStartTime.stream().filter(this::notIntersectCheck).toList();
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
    public void deleteAll() {
        deleteAllTasks();
        deleteAllEpics();
        deleteAllSubtasks();
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
        if (task.getStartTime() != null) {
            if (notIntersectCheck(task)) {
                tasksStartTime.add(task);
            }
        }
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
        updateDurationAndStartTimeOfEpic(epics.get(subtask.getIdEpic()));
        if (subtask.getStartTime() != null) {
            if (notIntersectCheck(subtask)) {
                tasksStartTime.add(subtask);
            }
        }
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
            updateDurationAndStartTimeOfEpic(epic);
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
            if (!notIntersectCheck(subtask)) {
                deleteSubtaskById(subtask.getId());
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        tasksStartTime.remove(tasks.get(id));
        historyManager.remove(id);
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        historyManager.remove(id);
        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            for (Integer subtaskId : removedEpic.getSubtasksInEpic()) {
                subtasks.remove(subtaskId);
                //tasksStartTime.remove(subtasks.get(subtaskId));
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        int epicId = subtasks.get(id).getIdEpic();
        tasksStartTime.remove(subtasks.get(id));
        historyManager.remove(id);
        Subtask removedSubtask = subtasks.remove(id);
        updateDurationAndStartTimeOfEpic(epics.get(epicId));
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
        List<Subtask> subtasksList = epic.getSubtasksInEpic().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    private boolean notIntersectCheck(Task task) {
        boolean isNotIntersection = true;
        if (tasksStartTime.isEmpty()) {
            return true;
        }
        for (Task treeTask : tasksStartTime) {
            if (treeTask.getStartTime().isBefore(task.getStartTime()) && treeTask.getEndTime().isAfter(task.getStartTime())
                    || treeTask.getStartTime().isAfter(task.getStartTime()) && treeTask.getEndTime().isBefore(task.getStartTime())) {
                isNotIntersection = false;
                break;
            }
        }
        return isNotIntersection;
    }

    private void updateDurationAndStartTimeOfEpic(Epic epic) {
        if (getSubtasksByEpicId(epic.getId()).isEmpty()) {
            epic.setEndTime(null);
            epic.setStartTime(null);
            epic.setDuration(null);
            return;
        }
        LocalDateTime startTime = getSubtasksByEpicId(epic.getId()).stream().findFirst().orElseThrow().getStartTime();
        LocalDateTime endTime = getSubtasksByEpicId(epic.getId()).stream().findFirst().get().getEndTime();
        Duration duration = getSubtasksByEpicId(epic.getId()).stream().findFirst().get().getDuration();
        int per = 0;
        for (Subtask subtask : getSubtasksByEpicId(epic.getId())) {
            if (startTime.isAfter(subtask.getStartTime())) {
                startTime = subtask.getStartTime();
            }
            if (endTime.isBefore(subtask.getEndTime())) {
                endTime = subtask.getEndTime();
            }
            if (per != 0) duration = duration.plus(subtask.getDuration());
            per++;
        }
        epic.setEndTime(endTime);
        epic.setStartTime(startTime);
        epic.setDuration(duration);
    }

}
