package heap;

import model.Post;
import java.util.*;

/**
 * Keeps only Top-K posts by likes using a min-heap of size K.
 * This is memory-efficient compared to storing/sorting all posts every time.
 */
public class TrendingHeap {
    private final int capacity;
    private final PriorityQueue<Post> minHeap; // smallest likes at root

    public TrendingHeap(int capacity) {
        this.capacity = Math.max(1, capacity);
        this.minHeap = new PriorityQueue<>(Comparator.comparingInt(Post::getLikes)); // ascending
    }

    public void add(Post post) {
        if (minHeap.size() < capacity) {
            minHeap.offer(post);
        } else if (post.getLikes() > Objects.requireNonNull(minHeap.peek()).getLikes()) {
            minHeap.poll();
            minHeap.offer(post);
        }
    }

    /** Return posts sorted DESC by likes */
    public List<Post> topK() {
        List<Post> list = new ArrayList<>(minHeap);
        list.sort((a, b) -> Integer.compare(b.getLikes(), a.getLikes()));
        return list;
    }

    public void clear() { minHeap.clear(); }
}
