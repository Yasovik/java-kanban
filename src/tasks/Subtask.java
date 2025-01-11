package tasks;

import manager.TypeTask;
import status.Status;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getIdEpic() {
        return epicId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUB_TASK;
    }

}