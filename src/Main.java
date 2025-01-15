import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();

        Task task1 = new Task("Архитектура", "Архитектура приложения", Status.NEW);
        Task task2 = new Task("Метод получения", "Получение задачи ", Status.IN_PROGRESS);
        Task task3 = new Task("Метод удаления", "Удалить все задачи", Status.DONE);
        task1.setStartTime(LocalDateTime.of(2024, 10, 10, 10, 10));
        task1.setDuration(Duration.ofHours(10));
        task2.setStartTime(LocalDateTime.of(2024, 10, 10, 10, 10));
        task2.setDuration(Duration.ofHours(10));
        task3.setStartTime(LocalDateTime.of(2024, 10, 10, 10, 10));
        task3.setDuration(Duration.ofHours(10));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);


        Epic epic1 = new Epic("Разработать канбан", "Написать приложения");
        Epic epic2 = new Epic("Разработать калькулятор", "Декомпозировать задачи");
        Epic epic3 = new Epic("Тест доработок", "Протестировать доработки");
        Epic epic4 = new Epic("Пустой эпик", "Без сабтасок");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        taskManager.addEpic(epic4);

        Subtask subtask1 = new Subtask("Продумать логику", "Логика", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Придумать стиль", "Стиль", Status.NEW, epic1.getId());

        Subtask subtask3 = new Subtask("Придумать название", "Имя", Status.DONE, epic2.getId());
        Subtask subtask4 = new Subtask("Увеличить бюджет", "Бюджет", Status.DONE, epic2.getId());

        Subtask subtask5 = new Subtask("Доработать метод", "Метод изменения епиков", Status.NEW, epic3.getId());
        Subtask subtask6 = new Subtask("Изменить приватность", "Модификаторы", Status.DONE, epic3.getId());

        subtask1.setStartTime(LocalDateTime.of(2024, 7, 24, 0, 0));
        subtask1.setDuration(Duration.ofHours(6));
        subtask2.setStartTime(LocalDateTime.of(2024, 8, 24, 0, 0));
        subtask2.setDuration(Duration.ofHours(7));
        subtask3.setStartTime(LocalDateTime.of(2024, 9, 24, 0, 0));
        subtask3.setDuration(Duration.ofHours(8));
        subtask4.setStartTime(LocalDateTime.of(2024, 10, 24, 0, 0));
        subtask4.setDuration(Duration.ofHours(9));
        subtask5.setStartTime(LocalDateTime.of(2024, 11, 24, 0, 0));
        subtask5.setDuration(Duration.ofHours(10));
        subtask6.setStartTime(LocalDateTime.of(2024, 12, 24, 0, 0));
        subtask6.setDuration(Duration.ofHours(11));

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask4.getId());
        taskManager.getSubtaskById(subtask5.getId());
        taskManager.getSubtaskById(subtask6.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        printHistory(taskManager);

        printAllTasks(taskManager);

        final Task task = taskManager.getTaskById(task1.getId());
        task.setStatus(Status.DONE);
        taskManager.updateTask(task1);
        printAllTasks(taskManager);

        System.out.println("CHANGE SUBTASK STATUS");
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        printAllTasks(taskManager);

        System.out.println("DELETE SUBTASK");
        taskManager.deleteSubtaskById(subtask1.getId());
        taskManager.updateEpic(epic1);
        printAllTasks(taskManager);

        System.out.println("DELETE: " + task1.getName());
        taskManager.deleteTask(task1.getId());
        System.out.println("DELETE: " + epic1.getName());
        taskManager.deleteEpic(epic1.getId());
        printAllTasks(taskManager);

        System.out.println("Delete all tasks:");
        taskManager.deleteAll();
        printAllTasks(taskManager);
        printHistory(taskManager);
    }

    public static void printHistory(TaskManager taskManager) {

        System.out.println("История просмотров задач:");
        for (Task t : taskManager.getHistory()) {
            System.out.println(t);
        }
    }

    public static void printAllTasks(TaskManager taskManager) {
        System.out.println("Tasks:");
        for (Task t : taskManager.getTasks()) {
            System.out.println(t.toString());
        }

        System.out.println("Epics:");
        for (Epic e : taskManager.getEpics()) {
            System.out.println(e.toString());
            for (Subtask sub : taskManager.getSubtasksByEpicId(e.getId())) {
                System.out.println("--> " + sub.toString());
            }
        }

        System.out.println("SubTasks:");
        for (Subtask sub : taskManager.getSubtasks()) {
            System.out.println(sub.toString());
        }
    }

}
