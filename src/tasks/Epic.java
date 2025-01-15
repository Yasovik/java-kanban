package tasks;

import manager.TypeTask;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static status.Status.NEW;
import static utils.Utils.formatter;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskId;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, NEW);
        subTaskId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksInEpic() {
        return subTaskId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    @Override
    public String toString() {
        if (this.startTime == null || this.endTime == null || this.duration == null) {
            return getClass() + "{" + "name='" + this.name + '\'' + "," +
                    " description='" + this.description + '\'' + "," +
                    " id=" + this.id + "," +
                    " status=" + this.status + "," +
                    " startTime=" + "--" +
                    ", duration=" + "--" +
                    ", endTime=" + "--" + '}';
        }

        return getClass() + "{" + "name='" + this.name + '\'' + "," +
                " description='" + this.description + '\'' + "," +
                " id=" + this.id + "," +
                " status=" + this.status + "," +
                " startTime=" + this.startTime.format(formatter) +
                ", duration=" + this.duration.toMinutes() +
                ", endTime=" + this.getEndTime().format(formatter) + '}';
    }

}
