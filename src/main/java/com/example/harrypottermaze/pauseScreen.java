package com.example.harrypottermaze;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.security.auth.callback.Callback;
import java.io.IOException;

public class pauseScreen extends Application {

    private PauseCallback callback;
    private String msgToShow = "Game: PAUSED";
    private Stage gameStage; // Reference to the main game stage
    private boolean resumeButtonClicked = false;

    // constructor
    public pauseScreen(Stage gameStage, PauseCallback callback) {
        this.gameStage = gameStage;
        this.callback = callback;
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage pauseStage) {

        // title of the stage
        pauseStage.setTitle("Game: PAUSED");

        // BorderPane to hold the content
        BorderPane root = new BorderPane();

        // Background of the game
        Image backgroundImage = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/sfondo.jpg"));
        BackgroundImage backgroundImageObject = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImageObject);
        root.setBackground(background);

        // Load the "ribbon 3" image from resources
        Image pauseImage = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/ribbon.png"));

        // Create an ImageView to display the image with a specified width and height
        ImageView pauseImageView = new ImageView(pauseImage);
        pauseImageView.setFitWidth(800);
        pauseImageView.setFitHeight(400);
        pauseImageView.setPreserveRatio(true);
        pauseImageView.setTranslateY(-30);
        root.setAlignment(pauseImageView, Pos.CENTER);

        // Create a Text node for the message ("Time is up" or "Lives are up")
        Text messageText = new Text(msgToShow);
        messageText.setFont(Font.font("Zampfino", 20));
        messageText.setTranslateY(-130);

        // VBOx for the messageText
        VBox messageBox = new VBox();
        messageBox.setAlignment(Pos.CENTER);
        messageBox.getChildren().add(messageText);

        // Create an HBox for the buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 0, 0, 0));

        // Create the "Resume" button
        Button resumeButton = new Button("Resume");
        resumeButton.getStyleClass().add("main-button");

        // handle the click of resume button
        resumeButton.setOnAction(event -> {
            callback.onResumeClicked(true);
            resumeButtonClicked = true;
            gameStage.show(); // Show the game stage (newMaze)
            pauseStage.close(); // Close the pause screen
        });

        // Create the "Exit" button
        Button exitButton = new Button("Exit");
        exitButton.getStyleClass().add("main-button");

        // handle the click of the exit button
        exitButton.setOnAction(event -> {
            pauseStage.close(); // Close the pause screen
            gameStage.close();
            homeScreen hScreen = new homeScreen(); // Go back to the home page
            try {
                hScreen.start(new Stage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Add buttons to the button box
        buttonBox.getChildren().addAll(resumeButton, exitButton);
        buttonBox.setTranslateY(-80);

        // Set the image at the top of the BorderPane
        root.setTop(pauseImageView);

        // Set the message box at the center of the BorderPane
        root.setCenter(messageBox);

        // Set the button box at the center of the BorderPane
        root.setBottom(buttonBox);

        // Create the scene with the BorderPane as the root node
        Scene pauseScene = new Scene(root, 500, 500);

        // Set the scene for the game over stage
        pauseStage.setScene(pauseScene);

        pauseStage.setTitle("PAUSE");
        pauseStage.show();

        // Load the CSS file
        pauseScene.getStylesheets().add
                (gameOverScreen.class.getResource("mycss.css").toExternalForm());

        // Handle the space bar event to resume the game // unused
        pauseScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                gameStage.show(); // Show the game stage
                pauseStage.close(); // Close the pause screen
            }
        });
    }

    // Method to display the pause screen
    public void showPauseScreen() {
        Stage pauseStage = new Stage();
        start(pauseStage);
    }

    public boolean isResumeButtonClicked() {
        return resumeButtonClicked;
    }

    public interface PauseCallback {
        void onResumeClicked(boolean resumeClicked);
    }
}
