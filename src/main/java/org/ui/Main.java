package org.ui;

import graph.Graph;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Post;
import stack.Action;
import stack.ActionStack;

import java.util.*;

public class Main extends Application {

    // Data
    private final Graph graph = new Graph();
    private final ActionStack actions = new ActionStack();

    // Graph UI
    private final Map<String, Circle> nodeCircles = new HashMap<>();
    private final Map<String, Text> nodeLabels  = new HashMap<>();
    private final List<EdgeLine> edges = new ArrayList<>();
    private String selectedUser = null;

    // Post UI
    private final ObservableList<String> userFeedItems = FXCollections.observableArrayList();
    private final ListView<String> userFeedList = new ListView<>(userFeedItems);

    private final ObservableList<String> trendingItems = FXCollections.observableArrayList();
    private final ListView<String> trendingList = new ListView<>(trendingItems);

    @Override
    public void start(Stage stage) {
        // Seed sample data
        seedSample();

        // Root layout: split graph (left) and tabs (right)
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(8));

        // Left: Graph canvas
        Group graphGroup = new Group();
        drawGraph(graphGroup);

        // Right: Controls + Tabs
        VBox right = new VBox(10);
        right.setPadding(new Insets(8));
        right.getChildren().addAll(buildControls(), buildTabs());

        // Place in a SplitPane so you can resize
        SplitPane split = new SplitPane();
        split.getItems().addAll(new StackPane(graphGroup), right);
        split.setDividerPositions(0.55);

        root.setCenter(split);

        Scene scene = new Scene(root, 1000, 650, Color.WHITE);
        stage.setTitle("Memory-Efficient Social Feed Analyzer");
        stage.setScene(scene);
        stage.show();

        // Initial populate of lists
        refreshTrending();
    }

    private void seedSample() {
        graph.addUser("U1"); graph.addUser("U2"); graph.addUser("U3"); graph.addUser("U4");
        graph.addConnection("U1", "U2", true);
        graph.addConnection("U1", "U3", false);
        graph.addConnection("U3", "U4", false);

        graph.addPost("U1", new Post(1, "Hello world!", "U1", System.currentTimeMillis()-40000, 5));
        graph.addPost("U3", new Post(2, "JavaFX is fun", "U3", System.currentTimeMillis()-30000, 9));
        graph.addPost("U2", new Post(3, "Graphs + AVL ✔", "U2", System.currentTimeMillis()-20000, 4));
        graph.addPost("U4", new Post(4, "Heap keeps top-K", "U4", System.currentTimeMillis()-10000, 15));
    }

    // ---------- Controls (Add User, Follow, New Post, Undo) ----------
    private Pane buildControls() {
        TextField userField = new TextField(); userField.setPromptText("New User ID (e.g., U5)");
        Button addUserBtn = new Button("Add User");
        addUserBtn.setOnAction(e -> {
            String id = userField.getText().trim();
            if (!id.isEmpty()) {
                graph.addUser(id);
                actions.push(Action.addUser(id));
                redrawGraph();
                userField.clear();
            }
        });

        TextField followA = new TextField(); followA.setPromptText("Follower (U…)");
        TextField followB = new TextField(); followB.setPromptText("Followee (U…)");

        CheckBox bidi = new CheckBox("Bidirectional");
        Button followBtn = new Button("Follow");
        followBtn.setOnAction(e -> {
            String a = followA.getText().trim(), b = followB.getText().trim();
            if (!a.isEmpty() && !b.isEmpty() && !a.equals(b)) {
                graph.addConnection(a, b, bidi.isSelected());
                actions.push(Action.addFollow(a, b));
                redrawGraph();
            }
        });

        TextField postUser = new TextField(); postUser.setPromptText("User for post (U…)");
        TextField postText = new TextField(); postText.setPromptText("Post content…");
        Button postBtn = new Button("Create Post");
        postBtn.setOnAction(e -> {
            String u = postUser.getText().trim();
            String c = postText.getText().trim();
            if (!u.isEmpty() && !c.isEmpty()) {
                Post p = new Post(new Random().nextInt(100000), c, u, System.currentTimeMillis(), new Random().nextInt(50));
                graph.addPost(u, p);
                actions.push(Action.addPost(u, p));
                if (Objects.equals(selectedUser, u)) refreshUserFeed(u);
                refreshTrending();
                postText.clear();
            }
        });

        Button undoBtn = new Button("Undo");
        undoBtn.setOnAction(e -> {
            if (actions.undo(graph)) {
                redrawGraph();
                if (selectedUser != null) refreshUserFeed(selectedUser);
                refreshTrending();
            }
        });

        GridPane gp = new GridPane();
        gp.setHgap(8); gp.setVgap(6);

        gp.add(new Label("Users & Follows"), 0, 0, 3, 1);

        gp.add(userField, 0, 1, 2, 1);
        gp.add(addUserBtn, 2, 1);

        gp.add(followA, 0, 2);
        gp.add(followB, 1, 2);
        gp.add(bidi,     2, 2);
        gp.add(followBtn,2, 3);

        gp.add(new Label("Posts"), 0, 4, 3, 1);
        gp.add(postUser, 0, 5);
        gp.add(postText, 1, 5);
        gp.add(postBtn,  2, 5);

        gp.add(undoBtn, 2, 6);

        gp.setPadding(new Insets(8));
        gp.setStyle("-fx-background-color:#f7f7fb; -fx-border-color:#e1e1f0; -fx-border-radius:8; -fx-background-radius:8;");
        return gp;
    }

    // ---------- Tabs (User Feed & Trending) ----------
    private TabPane buildTabs() {
        TabPane tabs = new TabPane();

        // User Feed (AVL)
        VBox userFeedBox = new VBox(8);
        Label userFeedTitle = new Label("Click a user node to see their chronological feed (AVL).");
        userFeedList.setPrefHeight(260);
        userFeedBox.getChildren().addAll(userFeedTitle, userFeedList);
        Tab t1 = new Tab("User Feed (AVL)", userFeedBox);
        t1.setClosable(false);

        // Trending (Heap Top-K)
        VBox trendingBox = new VBox(8);
        Label trTitle = new Label("Top trending posts (capacity-limited heap).");
        trendingList.setCellFactory(list -> new ColorCell());
        trendingList.setPrefHeight(260);
        trendingBox.getChildren().addAll(trTitle, trendingList);
        Tab t2 = new Tab("Trending (Heap)", trendingBox);
        t2.setClosable(false);

        tabs.getTabs().addAll(t1, t2);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return tabs;
    }

    // Colored list cells for trending
    private static class ColorCell extends ListCell<String> {
        @Override protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setText(null); setBackground(null); }
            else {
                setText(item);
                setBackground(new Background(new BackgroundFill(Color.hsb((getIndex()*30)%360, 0.25, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

    // ---------- Graph drawing (keeps your working arrow logic) ----------
    private void drawGraph(Group root) {
        root.getChildren().clear();
        nodeCircles.clear(); nodeLabels.clear(); edges.clear();

        Map<String, double[]> positions = layoutNodesCircular(graph.getAllUsers(), 300, 300, 220);

        // edges first
        drawEdges(root, positions);
        // nodes on top
        drawNodes(root, positions);
    }

    private void redrawGraph() {
        // Find the Group inside SplitPane -> StackPane
        // Simpler: rebuild the entire left side (Group) by replacing its children
        // Here we assume first item in SplitPane is StackPane(Group)
        // (Because we created it that way above.)
        // Grab root from current scene:
        SplitPane split = (SplitPane)((BorderPane) userFeedList.getScene().getRoot()).getCenter();
        StackPane leftStack = (StackPane) split.getItems().get(0);
        Group g = (Group) leftStack.getChildren().get(0);
        drawGraph(g);
    }

    private Map<String, double[]> layoutNodesCircular(Set<String> users, int centerX, int centerY, int radius) {
        List<String> list = new ArrayList<>(users);
        Collections.sort(list);
        Map<String, double[]> pos = new HashMap<>();
        int n = list.size();
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / Math.max(n,1);
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            pos.put(list.get(i), new double[]{x, y});
        }
        return pos;
    }

    private void drawEdges(Group root, Map<String, double[]> positions) {
        Set<String> drawn = new HashSet<>();
        for (String u : graph.getAllUsers()) {
            double[] from = positions.get(u);
            for (String v : graph.getConnections(u)) {
                String key = u + "->" + v;
                if (drawn.contains(key)) continue;

                double[] to = positions.get(v);
                EdgeLine line = new EdgeLine(from[0], from[1], to[0], to[1], u, v);
                line.setStroke(Color.GRAY); line.setStrokeWidth(1);
                edges.add(line);

                root.getChildren().add(line);
                line.addArrow(root); // direction u -> v

                drawn.add(key);
            }
        }
    }

    private void drawNodes(Group root, Map<String, double[]> positions) {
        for (String user : graph.getAllUsers()) {
            double[] p = positions.get(user);

            Circle circle = new Circle(p[0], p[1], 20, Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);

            Text label = new Text(p[0] - 10, p[1] + 5, user);

            nodeCircles.put(user, circle);
            nodeLabels.put(user, label);

            // Hover: highlight ONLY outgoing edges (novelty: direction-aware)
            circle.setOnMouseEntered(e -> highlightOutgoing(user));
            circle.setOnMouseExited(e -> resetColors());

            // Click: select user & show AVL feed
            circle.setOnMouseClicked(e -> {
                selectedUser = user;
                refreshUserFeed(user);
            });

            root.getChildren().addAll(circle, label);
        }
    }

    private void highlightOutgoing(String user) {
        resetColors();
        nodeCircles.get(user).setFill(Color.ORANGE);
        for (String v : graph.getConnections(user)) {
            nodeCircles.get(v).setFill(Color.YELLOW);
            highlightEdge(user, v);
        }
    }

    private void highlightEdge(String u, String v) {
        for (EdgeLine line : edges) {
            if (line.connects(u, v)) {
                line.setStroke(Color.RED);
                line.setStrokeWidth(2.5);
                line.updateArrowColor(Color.RED);
            }
        }
    }

    private void resetColors() {
        for (Circle c : nodeCircles.values()) c.setFill(Color.LIGHTBLUE);
        for (EdgeLine line : edges) {
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(1);
            line.updateArrowColor(Color.GRAY);
        }
    }

    private void refreshUserFeed(String user) {
        userFeedItems.clear();
        for (Post p : graph.getUserPostsChronological(user)) {
            userFeedItems.add(p.toString());
        }
    }

    private void refreshTrending() {
        trendingItems.clear();
        int rank = 1;
        for (Post p : graph.getTrendingTopK()) {
            trendingItems.add("#" + rank++ + " " + p.toString());
        }
    }

    public static void main(String[] args) { launch(args); }

    // ----- Edge with arrowhead -----
    private static class EdgeLine extends Line {
        final String u, v;
        Line arrow1, arrow2;

        EdgeLine(double x1, double y1, double x2, double y2, String u, String v) {
            super(x1, y1, x2, y2);
            this.u = u; this.v = v;
        }
        boolean connects(String a, String b) { return u.equals(a) && v.equals(b); }

        void addArrow(Group root) {
            double radius = 20;
            double sx = getStartX(), sy = getStartY();
            double ex = getEndX(),   ey = getEndY();

            double dx = ex - sx, dy = ey - sy, len = Math.sqrt(dx*dx + dy*dy);
            if (len == 0) return;
            double ux = dx/len, uy = dy/len;
            ex -= ux * radius; ey -= uy * radius;

            double L = 10, ang = Math.atan2(ey - sy, ex - sx);
            double x1 = ex - L * Math.cos(ang - Math.PI/6);
            double y1 = ey - L * Math.sin(ang - Math.PI/6);
            double x2 = ex - L * Math.cos(ang + Math.PI/6);
            double y2 = ey - L * Math.sin(ang + Math.PI/6);

            arrow1 = new Line(ex, ey, x1, y1);
            arrow2 = new Line(ex, ey, x2, y2);
            arrow1.setStroke(Color.GRAY); arrow2.setStroke(Color.GRAY);
            arrow1.setStrokeWidth(2);     arrow2.setStrokeWidth(2);
            root.getChildren().addAll(arrow1, arrow2);
        }

        void updateArrowColor(Color c) {
            if (arrow1 != null) arrow1.setStroke(c);
            if (arrow2 != null) arrow2.setStroke(c);
        }
    }
}
