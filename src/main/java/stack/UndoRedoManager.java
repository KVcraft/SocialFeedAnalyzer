package stack;

import java.util.Stack;

public class UndoRedoManager {
    private Stack<Operation> undoStack = new Stack<>();
    private Stack<Operation> redoStack = new Stack<>();

    public void addOperation(Operation op) {
        undoStack.push(op);
        redoStack.clear(); // once you make a new action, redo history resets
    }

    public Operation undo() {
        if (!undoStack.isEmpty()) {
            Operation op = undoStack.pop();
            redoStack.push(op);
            return op;
        }
        return null;
    }

    public Operation redo() {
        if (!redoStack.isEmpty()) {
            Operation op = redoStack.pop();
            undoStack.push(op);
            return op;
        }
        return null;
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
}