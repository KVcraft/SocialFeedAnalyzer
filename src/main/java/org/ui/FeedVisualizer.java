package org.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import org.Post;
import org.graph.Graph;
import org.heap.Trending_Heap;

import java.util.*;

public class FeedVisualizer extends Application {

    private static Trending_Heap feed;
    private static Graph graph;
    private static List<Post> posts = new ArrayList<>();

    public static void setData(Graph g, Trending_Heap f, List<Post> p) {
        graph = g;
        feed = f;
        posts = p;
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // 🔹 Title
        Label title = new Label("📊 Social Feed Visualizer");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.setTop(title);

        // 🔹 Left: Graph (users + connections)
        Pane graphPane = new Pane();
        drawGraph(graphPane);
        root.setLeft(graphPane);

        // 🔹 Center: Posts by users
        VBox postsBox = new VBox(10);
        postsBox.getChildren().add(new Label("📝 User Posts"));
        for (Post p : posts) {
            postsBox.getChildren().add(
                    new Label("User Post " + p.id + ": " + p.content + " (" + p.likes + " likes)")
            );
        }
        root.setCenter(postsBox);

        // 🔹 Right: Trending (Heap)
        VBox trendingBox = new VBox(10);
        trendingBox.getChildren().add(new Label("🔥 Trending Posts"));
        PriorityQueue<Post> copy = new PriorityQueue<>(feed.getHeap());
        while (!copy.isEmpty()) {
            Post p = copy.poll();
            trendingBox.getChildren().add(new Label(
                    "Post " + p.id + " | Likes: " + p.likes + " | " + p.content
            ));
        }
        root.setRight(trendingBox);

        // Scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Social Feed Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Draws users as circles + connections as lines
    private void drawGraph(Pane pane) {
        if (graph == null) return;

        Map<String, Circle> userNodes = new HashMap<>();
        int i = 0;
        for (String user : graph.getUsers()) {
            Circle c = new Circle(50 + i * 120, 100, 30, Color.LIGHTBLUE);
            Text t = new Text(c.getCenterX() - 15, c.getCenterY() + 5, user);
            pane.getChildren().addAll(c, t);
            userNodes.put(user, c);
            i++;
        }

        for (String u : graph.getUsers()) {
            for (String v : graph.getConnections(u)) {
                Circle cu = userNodes.get(u);
                Circle cv = userNodes.get(v);
                if (cu != null && cv != null) {
                    Line line = new Line(
                            cu.getCenterX(), cu.getCenterY(),
                            cv.getCenterX(), cv.getCenterY()
                    );
                    line.setStroke(Color.GRAY);
                    pane.getChildren().add(line);
                }
            }
        }
    }
}
