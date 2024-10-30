import manager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Архитектура", "Архитектура приложения", Status.NEW);
        Task task2 = new Task("Метод получения", "Получение задачи ", Status.IN_PROGRESS);
        Task task3 = new Task("Метод удаления", "Удалить все задачи", Status.DONE);

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

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
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
        taskManager.deleteAllTasks();
        printAllTasks(taskManager);
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
