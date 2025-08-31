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
    private Map<String, Circle> nodeCircles = new HashMap<>();
    private Map<String, Text> nodeLabels = new HashMap<>();
    private List<Line> edges = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        // Sample data
        graph.addUser("U1");
        graph.addUser("U2");
        graph.addUser("U3");
        graph.addUser("U4");

        graph.addConnection("U1", "U2");
        graph.addConnection("U1", "U3");
        graph.addConnection("U3", "U4"); // added extra edge to test

        Group root = new Group();

        // Layout: place nodes in a circle
        int radius = 150;
        int centerX = 250;
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
                edges.add(line);
                root.getChildren().add(line);
            }
        }

        // Draw nodes and labels
        for (String user : users) {
            double[] pos = positions.get(user);

            Circle circle = new Circle(pos[0], pos[1], 20, Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);

            Text label = new Text(pos[0] - 10, pos[1] + 5, user);

            nodeCircles.put(user, circle);
            nodeLabels.put(user, label);

            // Hover effect: highlight connected nodes and edges
            circle.setOnMouseEntered(e -> highlight(user));
            circle.setOnMouseExited(e -> resetColors());

            root.getChildren().addAll(circle, label);
        }

        Scene scene = new Scene(root, 600, 600, Color.WHITE);
        stage.setTitle("Social Graph Visualization");
        stage.setScene(scene);
        stage.show();
    }

    private void highlight(String user) {
        resetColors();

        // highlight the hovered node
        nodeCircles.get(user).setFill(Color.ORANGE);

        // highlight its connections (both directions)
        for (String friend : graph.getAllUsers()) {
            if (graph.getConnections(user).contains(friend) || graph.getConnections(friend).contains(user)) {
                nodeCircles.get(friend).setFill(Color.YELLOW);
                highlightEdge(user, friend);
            }
        }
    }

    private void highlightEdge(String user1, String user2) {
        // highlight edges connecting user1 <-> user2
        for (Line line : edges) {
            double x1 = nodeCircles.get(user1).getCenterX();
            double y1 = nodeCircles.get(user1).getCenterY();
            double x2 = nodeCircles.get(user2).getCenterX();
            double y2 = nodeCircles.get(user2).getCenterY();

            if ((line.getStartX() == x1 && line.getStartY() == y1 &&
                    line.getEndX() == x2 && line.getEndY() == y2) ||
                    (line.getStartX() == x2 && line.getStartY() == y2 &&
                            line.getEndX() == x1 && line.getEndY() == y1)) {
                line.setStroke(Color.RED);
                line.setStrokeWidth(2.5);
            }
        }
    }

    private void resetColors() {
        for (Circle circle : nodeCircles.values()) {
            circle.setFill(Color.LIGHTBLUE);
        }
        for (Line line : edges) {
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
