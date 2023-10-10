package com.example.harrypottermaze;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class FreeformDrawingGame extends Application {

    private Pane drawingPane;
    private Path currentPath;
    private boolean isDrawing = false;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Circle Recognition");

        drawingPane = new Pane();
        drawingPane.setOnMousePressed(this::handleMousePressed);
        drawingPane.setOnMouseDragged(this::handleMouseDragged);
        drawingPane.setOnMouseReleased(this::handleMouseReleased);

        Scene scene = new Scene(drawingPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleMousePressed(MouseEvent event) {
        if (!isDrawing) {
            currentPath = new Path();
            currentPath.setStroke(Color.BLACK);
            currentPath.setStrokeWidth(2);
            currentPath.getElements().add(new MoveTo(event.getX(), event.getY()));
            drawingPane.getChildren().add(currentPath);
            isDrawing = true;
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isDrawing) {
            currentPath.getElements().add(new LineTo(event.getX(), event.getY()));
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (isDrawing) {
            // Check if the drawn path forms a closed circular-like shape
            if (isClosedCircularShape(currentPath)) {
                currentPath.setStroke(Color.GREEN);
                // to continue here, I need to add the battle with Voldemort
            }

            isDrawing = false;
        }
    }

    private boolean isClosedCircularShape(Path path) {

        double startX = ((MoveTo) path.getElements().get(0)).getX();
        double startY = ((MoveTo) path.getElements().get(0)).getY();

        double endX = ((LineTo) path.getElements().get(path.getElements().size() - 1)).getX();
        double endY = ((LineTo) path.getElements().get(path.getElements().size() - 1)).getY();

        double distance = Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));

        return distance < 20;
    }

    public static void main(String[] args) {
        launch(args);
    }
}