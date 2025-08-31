package org.avl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainAVL extends Application {

    private PostAVLTree avlTree = new PostAVLTree();
    private TextArea outputArea = new TextArea();

    @Override
    public void start(Stage primaryStage){

        //Input fields
        TextField idField = new TextField();
        idField.setPromptText("Post ID");

        TextField contentField = new TextField();
        idField.setPromptText("Content");

        TextField authorField = new TextField();
        idField.setPromptText("Author");

        TextField likesField = new TextField();
        idField.setPromptText("Likes");

        Button insertBtn = new Button("Insert Post");
        Button showBtn = new Button("Show Posts");

        //Layout
        HBox inputBox = new HBox(10, idField, contentField, authorField, likesField, insertBtn, showBtn);
        VBox root = new VBox(10, inputBox, outputArea);
        root.setStyle("-fx-padding: 15; -fx-background-color: #f4f4f4;");

        //Insert post
        insertBtn.setOnAction(e -> {
            try {
                int postId = Integer.parseInt(idField.getText());
                String content = contentField.getText();
                String author = authorField.getText();
                int likes = Integer.parseInt(likesField.getText());
                long timestamp = System.currentTimeMillis();

                Post newPost = new Post(postId, content, author, timestamp, likes);

                avlTree.insertPostWrapper(newPost);

                showAlert("Success", "Post inserted successfully!");
                idField.clear();
                contentField.clear();
                authorField.clear();
                likesField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Post ID and Likes must be numbers!");
            }
        });

        //Show posts
        showBtn.setOnAction(e -> {
            outputArea.clear();
            outputArea.appendText("Posts in chronological order:\n\n");
            avlTree.printInOrderWrapper(outputArea);
        });

        Scene scene = new Scene(root, 800, 400);
        primaryStage.setTitle("AVL Post Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
