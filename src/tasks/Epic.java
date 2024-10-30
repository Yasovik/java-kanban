package tasks;

import java.util.ArrayList;

import static status.Status.NEW;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskId;

    public Epic(String name, String description) {
        super(name, description, NEW);
        subTaskId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksInEpic() {
        return subTaskId;
    }

}
