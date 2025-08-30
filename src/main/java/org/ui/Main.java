package org.ui;

import graph.Graph;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    private Graph graph = new Graph();

    @Override
    public void start(Stage stage) {
        // Sample data
        graph.addUser("U1");
        graph.addUser("U2");
        graph.addUser("U3");
        graph.addUser("U4");

        graph.addConnection("U1", "U2");
        graph.addConnection("U1", "U3");

        Group root = new Group();

        // Layout: place nodes in a circle
        int radius = 150;   // circle radius
        int centerX = 250;  // canvas center
        int centerY = 250;

        List<String> users = new ArrayList<>(graph.getAllUsers());
        Map<String, double[]> positions = new HashMap<>();

        for (int i = 0; i < users.size(); i++) {
            double angle = 2 * Math.PI * i / users.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            positions.put(users.get(i), new double[]{x, y});
        }

        // Draw edges first
        for (String user : users) {
            double[] fromPos = positions.get(user);
            for (String friend : graph.getConnections(user)) {
                double[] toPos = positions.get(friend);

                Line line = new Line(fromPos[0], fromPos[1], toPos[0], toPos[1]);
                line.setStroke(Color.GRAY);
                root.getChildren().add(line);
            }
        }

        // Draw nodes and labels
        for (String user : users) {
            double[] pos = positions.get(user);

            Circle circle = new Circle(pos[0], pos[1], 20, Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);

            Text label = new Text(pos[0] - 10, pos[1] + 5, user);

            root.getChildren().addAll(circle, label);
        }

        Scene scene = new Scene(root, 600, 600, Color.WHITE);
        stage.setTitle("Social Graph Visualization");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
