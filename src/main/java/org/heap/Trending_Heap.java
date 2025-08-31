package org.heap;

import org.Post;
import org.stack.UndoRedoManager;
import java.util.PriorityQueue;

public class Trending_Heap {
    private PriorityQueue<Post> heap = new PriorityQueue<>((a, b) -> b.likes - a.likes);
    private UndoRedoManager history = new UndoRedoManager();

    public void addPost(Post post) {
        heap.add(post);
        history.addOperation(new UndoRedoManager.Operation(UndoRedoManager.Type.ADD, post));
    }

    public void removePost(Post post) {
        if (heap.remove(post)) {
            history.addOperation(new UndoRedoManager.Operation(UndoRedoManager.Type.REMOVE, post));
        }
    }

    public void updateLikes(Post post, int newLikes) {
        int oldLikes = post.likes;
        heap.remove(post);
        post.likes = newLikes;
        heap.add(post);
        history.addOperation(new UndoRedoManager.Operation(UndoRedoManager.Type.UPDATE, post, oldLikes, newLikes));
    }

    public PriorityQueue<Post> getHeap() {
        return new PriorityQueue<>(heap); // return a copy to avoid modification
    }


    public void undo() {
        UndoRedoManager.Operation op = history.undo();
        if (op == null) return;
        switch (op.type) {
            case ADD -> heap.remove(op.post);
            case REMOVE -> heap.add(op.post);
            case UPDATE -> { heap.remove(op.post); op.post.likes = op.oldLikes; heap.add(op.post); }
        }
    }

    public void redo() {
        UndoRedoManager.Operation op = history.redo();
        if (op == null) return;
        switch (op.type) {
            case ADD -> heap.add(op.post);
            case REMOVE -> heap.remove(op.post);
            case UPDATE -> { heap.remove(op.post); op.post.likes = op.newLikes; heap.add(op.post); }
        }
    }

    public void printTopK(int k) {
        PriorityQueue<Post> copy = new PriorityQueue<>(heap);
        for (int i = 0; i < k && !copy.isEmpty(); i++) {
            System.out.println(copy.poll());
        }
    }
}

