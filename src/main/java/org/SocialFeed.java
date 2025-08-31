package org;

import javafx.application.Application;
import org.graph.Graph;
import org.avl.PostAVLTree;
import org.heap.Trending_Heap;
import org.ui.FeedVisualizer;

import java.util.Arrays;

public class SocialFeed {
    public static void main(String[] args) {
        Graph graph = new Graph();
        PostAVLTree avl = new PostAVLTree();
        Trending_Heap feed = new Trending_Heap();

        // Users
        graph.addUser("Alice");
        graph.addUser("Bob");
        graph.addConnection("Alice", "Bob");

        // Posts
        Post p1 = new Post(1, "First post!", 50, System.currentTimeMillis());
        Post p2 = new Post(2, "Funny meme", 200, System.currentTimeMillis() + 1000);

        avl.addPost(p1);
        avl.addPost(p2);

        feed.addPost(p1);
        feed.addPost(p2);

        System.out.println("\n--- AVL InOrder (by timestamp) ---");
        avl.printInOrder();

        System.out.println("\n--- Trending Posts (Heap by likes) ---");
        feed.printTopK(2);

        // Test Undo/Redo
        feed.updateLikes(p1, 300);
        System.out.println("\nAfter update:");
        feed.printTopK(2);

        feed.undo();
        System.out.println("\nAfter undo:");
        feed.printTopK(2);

        feed.redo();
        System.out.println("\nAfter redo:");
        feed.printTopK(2);

        // 🔥 Launch JavaFX visualizer
        FeedVisualizer.setData(graph, feed, Arrays.asList(p1, p2));
        Application.launch(FeedVisualizer.class, args);

    }
}
