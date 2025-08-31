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

/**
 * Social Graph Visualization with directional edges (arrows)
 */
public class Main extends Application {

    private Graph graph = new Graph();
    private Map<String, Circle> nodeCircles = new HashMap<>();
    private Map<String, Text> nodeLabels = new HashMap<>();
    private List<EdgeLine> edges = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        // Sample users and connections
        graph.addUser("U1");
        graph.addUser("U2");
        graph.addUser("U3");
        graph.addUser("U4");

        graph.addConnection("U1", "U2", false);
        graph.addConnection("U1", "U3", false);
        graph.addConnection("U3", "U4", false);

        Group root = new Group();

        // Layout nodes in a circle
        Map<String, double[]> positions = layoutNodesCircular(graph.getAllUsers(), 250, 250, 150);

        // Draw edges with arrows
        drawEdges(root, positions);

        // Draw nodes and labels
        drawNodes(root, positions);

        Scene scene = new Scene(root, 600, 600, Color.WHITE);
        stage.setTitle("Social Graph Visualization");
        stage.setScene(scene);
        stage.show();
    }

    // Layout nodes in a circle
    private Map<String, double[]> layoutNodesCircular(Set<String> users, int centerX, int centerY, int radius) {
        List<String> userList = new ArrayList<>(users);
        Map<String, double[]> positions = new HashMap<>();

        for (int i = 0; i < userList.size(); i++) {
            double angle = 2 * Math.PI * i / userList.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            positions.put(userList.get(i), new double[]{x, y});
        }
        return positions;
    }

    // Draw edges with arrows
    private void drawEdges(Group root, Map<String, double[]> positions) {
        Set<String> drawn = new HashSet<>();

        for (String user : graph.getAllUsers()) {
            double[] fromPos = positions.get(user);
            for (String friend : graph.getConnections(user)) {
                String edgeKey = user + "-" + friend;
                if (drawn.contains(edgeKey)) continue;

                double[] toPos = positions.get(friend);
                EdgeLine line = new EdgeLine(fromPos[0], fromPos[1], toPos[0], toPos[1], user, friend);
                line.setStroke(Color.GRAY);
                line.setStrokeWidth(1);
                edges.add(line);

                // Add line first
                root.getChildren().add(line);

                // Then arrow on top
                line.addArrow(root);

                drawn.add(edgeKey);
            }
        }
    }

    // Draw nodes and add hover effects
    private void drawNodes(Group root, Map<String, double[]> positions) {
        for (String user : graph.getAllUsers()) {
            double[] pos = positions.get(user);

            Circle circle = new Circle(pos[0], pos[1], 20, Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);

            Text label = new Text(pos[0] - 10, pos[1] + 5, user);

            nodeCircles.put(user, circle);
            nodeLabels.put(user, label);

            // Hover effect
            circle.setOnMouseEntered(e -> highlight(user));
            circle.setOnMouseExited(e -> resetColors());

            root.getChildren().addAll(circle, label);
        }
    }

    // Highlight node and its connections
    private void highlight(String user) {
        resetColors();

        // Highlight hovered node
        nodeCircles.get(user).setFill(Color.ORANGE);

        // Highlight connected nodes and edges
        for (String friend : graph.getAllUsers()) {
            if (graph.getConnections(user).contains(friend) || graph.getConnections(friend).contains(user)) {
                nodeCircles.get(friend).setFill(Color.YELLOW);
                highlightEdge(user, friend);
            }
        }
    }

    // Highlight edges between two users
    private void highlightEdge(String user1, String user2) {
        for (EdgeLine line : edges) {
            if (line.connects(user1, user2)) {
                line.setStroke(Color.RED);
                line.setStrokeWidth(2.5);
                line.updateArrowColor(Color.RED);
            }
        }
    }

    // Reset all colors
    private void resetColors() {
        for (Circle circle : nodeCircles.values()) {
            circle.setFill(Color.LIGHTBLUE);
        }
        for (EdgeLine line : edges) {
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(1);
            line.updateArrowColor(Color.GRAY);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Custom Line with arrowhead and user info
    private static class EdgeLine extends Line {
        String user1, user2;
        Line arrow1, arrow2; // arrow lines

        EdgeLine(double x1, double y1, double x2, double y2, String u1, String u2) {
            super(x1, y1, x2, y2);
            this.user1 = u1;
            this.user2 = u2;
        }

        boolean connects(String a, String b) {
            return (user1.equals(a) && user2.equals(b)) ||
                    (user1.equals(b) && user2.equals(a));
        }

        // Add arrowhead slightly offset from node circle
        public void addArrow(Group root) {
            double radius = 20; // node radius offset
            double ex = getEndX();
            double ey = getEndY();
            double sx = getStartX();
            double sy = getStartY();

            double dx = ex - sx;
            double dy = ey - sy;
            double length = Math.sqrt(dx*dx + dy*dy);
            double ux = dx / length;
            double uy = dy / length;

            // offset end point outside circle
            ex = ex - ux * radius;
            ey = ey - uy * radius;

            double arrowLength = 10;

            double angle = Math.atan2(ey - sy, ex - sx);

            double x1 = ex - arrowLength * Math.cos(angle - Math.PI / 6);
            double y1 = ey - arrowLength * Math.sin(angle - Math.PI / 6);

            double x2 = ex - arrowLength * Math.cos(angle + Math.PI / 6);
            double y2 = ey - arrowLength * Math.sin(angle + Math.PI / 6);

            arrow1 = new Line(ex, ey, x1, y1);
            arrow2 = new Line(ex, ey, x2, y2);

            arrow1.setStroke(Color.GRAY);
            arrow2.setStroke(Color.GRAY);
            arrow1.setStrokeWidth(2);
            arrow2.setStrokeWidth(2);

            root.getChildren().addAll(arrow1, arrow2);
        }

        // Update arrow color when highlighting
        public void updateArrowColor(Color color) {
            if (arrow1 != null) arrow1.setStroke(color);
            if (arrow2 != null) arrow2.setStroke(color);
        }
    }
}
