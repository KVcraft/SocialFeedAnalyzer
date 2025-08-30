package org.heap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class Post {
    int id;
    String content;
    int likes;

    Post(int id, String content, int likes) {
        this.id = id;
        this.content = content;
        this.likes = likes;
    }

    @Override
    public String toString() {
        return content + " (" + likes + ")";
    }
}

public class Trending_Heap extends Application {
    private PriorityQueue<Post> heap = new PriorityQueue<>((a, b) -> b.likes - a.likes);

    @Override
    public void start(Stage stage) {
        // Sample posts
        heap.add(new Post(1, "First post!", 50));
        heap.add(new Post(2, "Funny meme", 200));
        heap.add(new Post(3, "Serious rant", 120));
        heap.add(new Post(4, "Breaking news", 90));
        heap.add(new Post(5, "Tech update", 75));

        // Convert heap to list (array-representation of heap)
        List<Post> heapArray = new ArrayList<>(heap);

        Pane root = new Pane();

        // Recursively draw heap as binary tree
        drawHeap(root, heapArray, 0, 250, 50, 120);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Trending Heap Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    private void drawHeap(Pane root, List<Post> heapArray, int index, double x, double y, double hGap) {
        if (index >= heapArray.size()) return;

        // Draw current node
        Rectangle rect = new Rectangle(x - 40, y - 20, 80, 40);
        rect.setFill(Color.LIGHTBLUE);
        rect.setStroke(Color.BLACK);

        Text text = new Text(x - 35, y + 5, heapArray.get(index).toString());

        root.getChildren().addAll(rect, text);

        // Left child index
        int left = 2 * index + 1;
        // Right child index
        int right = 2 * index + 2;

        // Draw edges + children
        if (left < heapArray.size()) {
            double childX = x - hGap;
            double childY = y + 100;
            root.getChildren().add(new Line(x, y + 20, childX, childY - 20));
            drawHeap(root, heapArray, left, childX, childY, hGap / 2);
        }

        if (right < heapArray.size()) {
            double childX = x + hGap;
            double childY = y + 100;
            root.getChildren().add(new Line(x, y + 20, childX, childY - 20));
            drawHeap(root, heapArray, right, childX, childY, hGap / 2);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
