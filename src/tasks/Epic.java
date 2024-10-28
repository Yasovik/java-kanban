package tasks;

import manager.TaskManager;
import status.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskId;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTaskId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksInEpic() {
        return subTaskId;
    }

    public void updateStatus(TaskManager taskManager) {
        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(this.getId());
        if (subtasks.isEmpty()) {
            this.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
                break;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            this.setStatus(Status.DONE);
        } else if (anyInProgress) {
            this.setStatus(Status.IN_PROGRESS);
        } else {
            this.setStatus(Status.NEW);
        }
    }

}
