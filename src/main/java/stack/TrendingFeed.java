package stack;

import java.util.PriorityQueue;

public class TrendingFeed {
    private PriorityQueue<Post> heap = new PriorityQueue<>(
            (a, b) -> b.likes - a.likes
    );

    private UndoRedoManager history = new UndoRedoManager();

    public void addPost(Post post) {
        heap.add(post);
        history.addOperation(new Operation(Operation.Type.ADD, post));
    }

    public void removePost(Post post) {
        if (heap.remove(post)) {
            history.addOperation(new Operation(Operation.Type.REMOVE, post));
        }
    }

    public void updateLikes(Post post, int newLikes) {
        int oldLikes = post.likes;
        heap.remove(post);
        post.likes = newLikes;
        heap.add(post);
        history.addOperation(new Operation(Operation.Type.UPDATE, post, oldLikes, newLikes));
    }

    public void undo() {
        Operation op = history.undo();
        if (op == null) {
            System.out.println("Nothing to undo.");
            return;
        }

        switch (op.type) {
            case ADD -> heap.remove(op.post);
            case REMOVE -> heap.add(op.post);
            case UPDATE -> {
                heap.remove(op.post);
                op.post.likes = op.oldLikes;
                heap.add(op.post);
            }
        }
        System.out.println("Undo: " + op.type);
    }

    public void redo() {
        Operation op = history.redo();
        if (op == null) {
            System.out.println("Nothing to redo.");
            return;
        }

        switch (op.type) {
            case ADD -> heap.add(op.post);
            case REMOVE -> heap.remove(op.post);
            case UPDATE -> {
                heap.remove(op.post);
                op.post.likes = op.newLikes;
                heap.add(op.post);
            }
        }
        System.out.println("Redo: " + op.type);
    }

    public void printTopK(int k) {
        PriorityQueue<Post> copy = new PriorityQueue<>(heap);
        int count = 0;
        while (!copy.isEmpty() && count < k) {
            System.out.println(copy.poll());
            count++;
        }
    }

    public static void main(String[] args) {
        TrendingFeed feed = new TrendingFeed();

        Post p1 = new Post(1, "First post!", 50);
        Post p2 = new Post(2, "Funny meme", 200);

        feed.addPost(p1);
        feed.addPost(p2);

        System.out.println("\nTop posts:");
        feed.printTopK(2);

        feed.updateLikes(p1, 300);
        System.out.println("\nAfter update:");
        feed.printTopK(2);

        // Undo update
        feed.undo();
        System.out.println("\nAfter undo:");
        feed.printTopK(2);

        // Redo update
        feed.redo();
        System.out.println("\nAfter redo:");
        feed.printTopK(2);
    }
}