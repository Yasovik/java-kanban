package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    private void linkLast(Task task) {
        Node node = new Node(task, null, tail);

        if (tail == null) {
            head = node;
        } else {
            tail.setNext(node);
            node.setPrev(tail);
        }

        tail = node;
        history.put(task.getId(), node);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node currentNode = head;

        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node != null) {
            Node nextNode = node.getNext();
            Node prevNode = node.getPrev();

            if (node == head) {
                head = nextNode;
            }
            if (node == tail) {
                tail = prevNode;
            }
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            }

            node.setNext(null);
            node.setPrev(null);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }

    @Override
    public void remove(int id) {
        if (history.isEmpty()) {
            return;
        }

        Node nodeToRemove = history.remove(id);
        if (nodeToRemove == null) {
            return;
        }

        removeNode(nodeToRemove);
    }
}
