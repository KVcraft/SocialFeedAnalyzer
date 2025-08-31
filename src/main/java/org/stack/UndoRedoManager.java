package org.stack;

import org.Post;
import java.util.Stack;

public class UndoRedoManager {
    public enum Type { ADD, REMOVE, UPDATE }

    public static class Operation {
        public Type type;
        public Post post;
        public int oldLikes, newLikes;

        public Operation(Type type, Post post) {
            this.type = type; this.post = post;
        }

        public Operation(Type type, Post post, int oldLikes, int newLikes) {
            this.type = type; this.post = post;
            this.oldLikes = oldLikes; this.newLikes = newLikes;
        }
    }

    private Stack<Operation> undoStack = new Stack<>();
    private Stack<Operation> redoStack = new Stack<>();

    public void addOperation(Operation op) {
        undoStack.push(op);
        redoStack.clear();
    }

    public Operation undo() {
        if (undoStack.isEmpty()) return null;
        Operation op = undoStack.pop();
        redoStack.push(op);
        return op;
    }

    public Operation redo() {
        if (redoStack.isEmpty()) return null;
        Operation op = redoStack.pop();
        undoStack.push(op);
        return op;
    }
}
