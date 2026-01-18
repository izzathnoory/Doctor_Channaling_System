import java.util.ArrayList;
import java.util.List;

public class WaitingQueue<T> {
    private List<T> items;
    private int maxSize;

    public WaitingQueue() {
        this.items = new ArrayList<>();
        this.maxSize = -1; // unlimited
    }

    public WaitingQueue(int maxSize) {
        this.items = new ArrayList<>();
        this.maxSize = maxSize;
    }

    // Enqueue - add element to rear of queue
    public boolean enqueue(T item) {
        if (maxSize > 0 && items.size() >= maxSize) {
            return false; // Queue full
        }
        items.add(item);
        return true;
    }

    // Dequeue - remove element from front of queue
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        return items.remove(0);
    }

    // Peek at front element without removing
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return items.get(0);
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Get current size
    public int size() {
        return items.size();
    }

    // Clear all elements
    public void clear() {
        items.clear();
    }

    // Display all elements (front to rear)
    public void displayQueue() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return;
        }
        System.out.println("--- Queue Contents (Front to Rear) ---");
        for (T item : items) {
            System.out.println(item);
        }
    }

    // Get all items as list
    public List<T> toList() {
        return new ArrayList<>(items);
    }

    // Get item at specific index
    public T get(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    // Remove specific item
    public boolean remove(T item) {
        return items.remove(item);
    }

    // Check if queue contains item
    public boolean contains(T item) {
        return items.contains(item);
    }
}
