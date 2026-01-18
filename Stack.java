import java.util.ArrayList;
import java.util.List;

public class Stack<T> {
    private List<T> items;
    private int maxSize;

    public Stack() {
        this.items = new ArrayList<>();
        this.maxSize = -1; // unlimited
    }

    public Stack(int maxSize) {
        this.items = new ArrayList<>();
        this.maxSize = maxSize;
    }

    // Push element to top of stack
    public boolean push(T item) {
        if (maxSize > 0 && items.size() >= maxSize) {
            return false; // Stack full
        }
        items.add(item);
        return true;
    }

    // Pop element from top of stack
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        return items.remove(items.size() - 1);
    }

    // Peek at top element without removing
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return items.get(items.size() - 1);
    }

    // Check if stack is empty
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

    // Display all elements (from bottom to top)
    public void display() {
        if (isEmpty()) {
            System.out.println("Stack is empty");
            return;
        }
        System.out.println("--- Stack Contents (Bottom to Top) ---");
        for (T item : items) {
            System.out.println(item);
        }
    }

    // Display all elements (from top to bottom) - for history
    public void displayHistory() {
        if (isEmpty()) {
            System.out.println("No history available");
            return;
        }
        System.out.println("--- History (Most Recent First) ---");
        for (int i = items.size() - 1; i >= 0; i--) {
            System.out.println(items.get(i));
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
}
