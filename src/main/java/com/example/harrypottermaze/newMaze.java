package com.example.harrypottermaze;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;


public class newMaze extends Application {

    ////// BOOLEAN //////
    private boolean gameEnded = false;
    private Boolean bool = false;
    private boolean collectedWand = false;

    ////// CALLBACKS //////
    private gameOverScreen.GameOverCallback gameOverCallback;
    private winScreen.WinCallback winCallback;
    private gameOverScreen.GameOverExitCallback gameOverExitCallback;

    private winScreen.WinExitCallback winExitCallback;

    ////// STRING, TEXT //////
    private String selectedHouse;
    private Text timerText;

    ////// INTEGERS ///////
    private static final int ROWS = 12;
    private static final int COLUMNS = 23;
    private static final int CELL_SIZE = 45;
    private static final int MINUTES = 1;
    private static final int SECONDS = 0;
    private int playerRow;
    private int playerCol;
    private int youRow;
    private int youCol;
    private int volRow;
    private int volCol;
    private int tvRow;
    private int tvCol;
    private int remainingMinutes;
    private int remainingSeconds;
    private int[][] maze;

    int numPotionsCollected = 0;
    int numWandCollected = 0;
    int numTimeCollected = 0;
    int numHearts = 3;
    int numWand = 0;

    ////// DOUBLE //////
    private double startX;
    private double startY;

    ////// CLASSES ///////
    private FreeformDrawingGame drawingGame;
    private TranscriberDemo transcriberDemo;

    ////// TIMELINE ///////
    private Timeline timeline;
    private Timeline voldemortTimeline;

    /////// RECTANGLE //////
    private Rectangle player;
    private Rectangle targetYou;
    private Rectangle voldemort;
    private Rectangle targetVold;

    /////// GRIDPANE ///////
    private GridPane mazeGrid;
    private GridPane legendItems;
    private GridPane life;

    /////// IMAGES //////
    Image lifeHeart = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/heart.png"));
    Image wand = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/wand.png"));
    Image potion = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/potion.png"));
    Image timeTurner = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/timeTurner.png"));
    Image ribbonItems = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/ribbonItems.png"));
    Image ribbonLife = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/ribbonLife.png"));
    Image start = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/Start.png"));
    Image finish = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/Finish.png"));
    Image voldemortTarget = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/targetVol.png"));
    Image you = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/targetYou.png"));
    Image steps = new Image (getClass().getResourceAsStream("/com/example/harrypottermaze/footstep.png"));
    Image steps2 = new Image (getClass().getResourceAsStream("/com/example/harrypottermaze/footstep2.png"));


    /////// BACKGROUND ///////
    Image generalbackground = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/sfondo.jpg"));
    BackgroundImage backgroundImageObject = new BackgroundImage(generalbackground, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
    Background newBackground = new Background(backgroundImageObject);

    ////// RANDOM //////
    Random rand = new Random();

    ////// CONSTRUCTOR //////
    public newMaze(String selectedHouse) {

        this.selectedHouse = selectedHouse;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        // Initialize maze and player positions
        initializeMaze();
        playerRow = 1;
        playerCol = 1;
        youRow = 1;
        youCol = 0;
        volRow = 1;
        volCol = 19;
        tvRow = 1;
        tvCol = 20;

        // Initialize UI elements
        mazeGrid = new GridPane();
        legendItems = new GridPane();
        life = new GridPane();
        drawMaze();
        player = drawPlayer();
        targetYou = drawTargetYou();
        voldemort = drawVoldemort(true);
        targetVold = drawTargetVol();
        moveVoldemort(primaryStage);
        createLifeLegend();

        // Create image views for start, finish, and items ribbon
        ImageView startView = new ImageView(start); /////START//////
        startView.setFitHeight(80);
        startView.setFitWidth(80);
        startView.setTranslateX(20);
        startView.setTranslateY(-70);

        ImageView finishView = new ImageView(finish); /////FINISH//////
        finishView.setFitHeight(80);
        finishView.setFitWidth(80);
        finishView.setTranslateX(1170);
        finishView.setTranslateY(-20);

        ImageView ribbonItemsView = new ImageView(ribbonItems); /////RIBBON//////
        ribbonItemsView.setFitHeight(100);
        ribbonItemsView.setFitWidth(100);
        ribbonItemsView.setTranslateY(-150);

        // Create the timer display
        remainingMinutes = MINUTES;
        remainingSeconds = SECONDS;
        timerText = new Text(getFormattedTime());
        timerText.setFont(Font.font("Zapfino", 24));
        timerText.setFill(Color.BLACK);

        // Counting of time
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    if (remainingSeconds > 0) {
                        remainingSeconds--;
                    } else {
                        if (remainingMinutes > 0) {
                            remainingMinutes--;
                            remainingSeconds = 59;
                        } else {
                            if (!gameEnded) {
                                gameOverWindow(true, primaryStage);
                            }
                            timeline.stop(); //STOP TIMELINE
                            gameEnded = true;
                        }
                    }
                    timerText.setText(getFormattedTime());
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeStart();

        // Create the timer display at the top
        BorderPane timePane = new BorderPane();
        timePane.setTop(timerText);
        timePane.setAlignment(timerText, Pos.CENTER);

        // Create VBOx to hold the ribbonItemsView, startView, timePane, lives
        VBox grid = new VBox();
        grid.getChildren().addAll(mazeGrid, legendItems);
        grid.getChildren().add(0, ribbonItemsView);
        grid.getChildren().add(0, startView);
        grid.getChildren().add(0, finishView);
        grid.getChildren().add(0, life);
        grid.getChildren().add(0, timePane);
        grid.setBackground(newBackground);
        Scene scene = new Scene(grid, COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);
        primaryStage.setTitle("Harry Potter's Maze");

        // Handle RESUME callback
        com.example.harrypottermaze.pauseScreen.PauseCallback pauseCallback = resumeClicked -> {
            if (resumeClicked) {
                timeStart(); // Restart the timer
                drawVoldemort(true);
                voldemortTimeline.play();
            }
        };

        // Handle EXIT callbacks

        ///// PAUSE SCREEN /////
        com.example.harrypottermaze.pauseScreen.PauseCallback pauseCallback2 = exitClicked -> {
            if (exitClicked) {
                timeStop(); // Restart the timer
                drawVoldemort(false);
                voldemortTimeline.stop();


            }
        };

        ////// GAME OVER SCREEN /////
        gameOverExitCallback = exitClicked -> {
            if (exitClicked) {
                timeStop(); // Restart the timer
                drawVoldemort(false);
                voldemortTimeline.stop();


            }
        };

        ///// WIN SCREEN /////
        winExitCallback = exitClicked -> {
            if (exitClicked) {
                timeStop(); // Restart the timer
                drawVoldemort(false);
                voldemortTimeline.stop();


            }
        };

        // Handle RESTART callback from game over and win screens

        ///// GAME OVER /////
        gameOverCallback = restartClicked -> {
            if (restartClicked) {
                timeStop(); // Restart the timer
                drawVoldemort(false);
                voldemortTimeline.stop();


            }
        };

        ///// WIN /////
        winCallback = restartClicked -> {
            if (restartClicked) {
                timeStop(); // Restart the timer
                drawVoldemort(false);
                voldemortTimeline.stop();


            }
        };

        //Controls the player movement event and control the pause menu
        scene.setOnKeyPressed(event -> {
            movePlayer(event.getCode(), primaryStage);

            if (event.getCode() == KeyCode.SPACE) {
                timeStop();
                drawVoldemort(false);
                voldemortTimeline.stop();
                Stage pauseStage = new Stage();
                pauseScreen pause = new pauseScreen(primaryStage, pauseCallback);
                pause.start(pauseStage);

            }
        });

        // We set the height of the stage
        primaryStage.setHeight(800);
        primaryStage.setWidth(800);

        // Fix alignment etc
        mazeGrid.setAlignment(Pos.CENTER);
        mazeGrid.setTranslateY(-330);
        legendItems.setAlignment(Pos.CENTER_LEFT);
        life.setAlignment(Pos.CENTER_LEFT);
        ribbonItemsView.setTranslateY(130);
        ribbonItemsView.setTranslateX(20);
        legendItems.setTranslateX(20);
        legendItems.setTranslateY(-430);
        life.setTranslateX(20);
        life.setTranslateY(250);

        // We display the scene in the stage
        primaryStage.setScene(scene);

        // We show the stage
        primaryStage.show();
    }

    // check resume status
    public void checkResumeStatus(boolean resumeButtonClicked) {
        if (resumeButtonClicked) {
            timeStart();
        }
    }

    ////////// INITIALIZE MAZE //////////
    private void initializeMaze() {

        maze = new int[][]{
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {0,0,0,1,1,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,1,1,0,0,0,0,1,0,0,1,0,1,1,0,1,0,0,0,1,1},
                {1,1,1,1,1,0,0,0,0,1,0,0,3,0,1,0,0,1,0,0,0,0,1},
                {2,2,2,2,1,0,0,0,0,1,0,0,1,0,1,0,0,1,0,0,0,0,1},
                {2,2,2,2,1,1,1,1,0,1,1,0,1,1,1,0,0,1,0,0,0,0,1},
                {2,2,2,2,1,0,0,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,1},
                {2,2,2,2,1,0,0,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,1},
                {2,2,2,2,1,0,0,0,0,0,0,0,0,1,1,0,0,1,1,1,1,1,1},
                {2,2,2,2,1,0,0,0,0,0,0,0,0,1,1,0,0,1,2,2,2,2,2},
                {2,2,2,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2},
        };
    }

    /////// DRAW MAZE /////////
    private void drawMaze() throws IOException {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);

                if (maze[row][col] == 1) {
                    Color cellColor = getColorForHouse(selectedHouse);
                    cell.setFill(cellColor); //Wall
                } else if (maze[row][col] == 0){
                    cell.setFill(Color.TRANSPARENT); //Empty
                    positionateItems(cell, row, col);
                }else if(maze[row][col] == 2)
                    cell.setFill(Color.TRANSPARENT); //Empty
                else if(maze[row][col] == 3){
                    cell.setFill(Color.SADDLEBROWN);
                    managingDoorOpen(cell);
                }


                mazeGrid.add(cell, col, row);
            }

        }
    }

    ////// DRAW PLAYER ///////
    public Rectangle drawPlayer() {
        Rectangle player = new Rectangle(CELL_SIZE, CELL_SIZE);
        player.setFill((new ImagePattern(steps, 0, 0, 1, 1, true)));
        mazeGrid.add(player, playerCol, playerRow);
        checkCollision(player);

        return player;
    }

    /////// DRAW TARGET (PLAYER)///////
    public Rectangle drawTargetYou() {
        Rectangle targetYou = new Rectangle(CELL_SIZE, CELL_SIZE);
        targetYou.setFill((new ImagePattern(you, 0, 0, 1, 1, true)));
        mazeGrid.add(targetYou, youCol, youRow);

        return targetYou;
    }

    ////////// DRAW VOLDEMORT ///////////
    public Rectangle drawVoldemort(Boolean bool) {
        if (bool) {
            Rectangle voldemort = new Rectangle(CELL_SIZE, CELL_SIZE);
            voldemort.setFill((new ImagePattern(steps2, 0, 0, 1, 1, true)));
            mazeGrid.add(voldemort, volCol, volRow);
            voldemortfight(voldemort);

            return voldemort;
        }else
            return null;

    }

    public Rectangle drawTargetVol() {
        Rectangle targetVol = new Rectangle(CELL_SIZE, CELL_SIZE);
        targetVol.setFill((new ImagePattern(voldemortTarget, 0, 0, 1, 1, true)));
        mazeGrid.add(targetVol, tvCol, tvRow);

        return targetVol;
    }

    //////// REMOVE FLAG ////////
    public void removeFlag(Rectangle flag) {
        if (flag != null) {
            mazeGrid.getChildren().remove(flag);
        }
    }

    //Manage the movement of the player with the WASD or arrows also manage the movement of the target
    private void movePlayer(KeyCode keyCode, Stage primaryStage) {
        int newRow = playerRow;
        int newCol = playerCol;

        switch (keyCode) {
            case UP:
            case W:
                newRow = playerRow - 1;

                break;
            case DOWN:
            case S:
                newRow = playerRow + 1;

                break;
            case LEFT:
            case A:
                newCol = playerCol - 1;

                break;
            case RIGHT:
            case D:
                newCol = playerCol + 1;

                break;
            default:
                break;
        }

        // Controls if the movement is valid
        if (isValidMove(newRow, newCol)) {
            mazeGrid.getChildren().removeAll(player, targetYou);
            playerRow = newRow;
            playerCol = newCol;
            player = drawPlayer();
            youRow = playerRow;
            youCol = playerCol - 1;
            targetYou = drawTargetYou();

            if(playerRow > 0 && playerRow < 3 && playerCol == 22){
                winWindow(primaryStage);
            }

        }


    }

    /////// CALCULATE DIRECTION //////
    private int calculateDirectionToPlayer() {
        int rowDifference = playerRow - volRow;
        int colDifference = playerCol - volCol;

        if (rowDifference > 0) {
            return 1;
        } else if (rowDifference < 0) {
            return 0;
        } else if (colDifference > 0) {
            return 3;
        } else if (colDifference < 0) {
            return 2;
        }

        return -1;
    }


    ///////// MOVE VOLDEMORT ///////////
    private void moveVoldemort(Stage primaryStage) {

            KeyFrame keyFrame = new KeyFrame(Duration.seconds(7), event -> {
                int direction = calculateDirectionToPlayer();
                int newRow = volRow;
                int newCol = volCol;


                switch (direction) {
                    case 0: // UP
                        newRow = volRow - 1;
                        break;
                    case 1: // DOWN
                        newRow = volRow + 1;
                        break;
                    case 2: // LEFT
                        newCol = volCol - 1;
                        break;
                    case 3: // RIGHT
                        newCol = volCol + 1;
                        break;
                }

                if (isValidMove(newRow, newCol)) {

                    mazeGrid.getChildren().removeAll(voldemort, targetVold);
                    volRow = newRow;
                    volCol = newCol;
                    voldemort = drawVoldemort(true);
                    tvRow = volRow;
                    tvCol = volCol + 1;
                    targetVold = drawTargetVol();

                    int playerRow = GridPane.getRowIndex(player);
                    int playerCol = GridPane.getColumnIndex(player);
                    int voldRow = GridPane.getRowIndex(voldemort);
                    int voldCol = GridPane.getColumnIndex(voldemort);


                    if(playerRow == voldRow && playerCol == voldCol){

                        numHearts--;

                        System.out.println("Voldemort touched!");
                        System.out.println( "VoldNumHearts : numHearts");
                        updateLifeLegend(numHearts);

                        if (numHearts == 0) {

                            gameOverWindow(false, primaryStage);
                        }
                    }

                }

            });

            // Set timeline for Voldemort
             voldemortTimeline = new Timeline(keyFrame);
             voldemortTimeline.setCycleCount(Timeline.INDEFINITE);
             voldemortTimeline.play();

    }


    ////// POSITIONATE ITEMS //////
    private void positionateItems(Rectangle cell, int row, int col){
        int num = rand.nextInt(100);


        //randomly putting wand but after a certain position in the maze
        if (num >= 30 && num <= 100 && row >= 5 && col >= 8 && col < 12) {
            numWand++;
            if (numWand > 1)
                cell.setFill(Color.TRANSPARENT);
            else{
                cell.setFill((new ImagePattern(wand, 0, 0, 1, 1, true)));
                if(row == playerRow && col == playerCol)
                    cell.setFill(Color.TRANSPARENT);
            }

        }


        //randomly putting potions but after a certain position in the maze
        else if (num >= 1 && num < 3 && row > 0 && col > 12) {
            cell.setFill((new ImagePattern(potion, 0, 0, 1, 1, true)));

        }

        //randomly putting timeTurner but after a certain position in the maze
        else if (num < 30 && num >= 27 && row > 0 && row <= 5 && col < 20 && col > 12) {
            cell.setFill((new ImagePattern(timeTurner, 0, 0, 1, 1, true)));

        }


    }

    //Create the legend of life hearts
    private void createLifeLegend() {
        VBox legend = new VBox();

        ImageView ribbonView = new ImageView(ribbonLife);
        ribbonView.setFitHeight(100);
        ribbonView.setFitWidth(100);
        ribbonView.setTranslateY(10);
        legend.getChildren().addAll(ribbonView);

        Pane itemPane = new Pane();

        VBox.setMargin(legend, new Insets(5, 0, 0, 0));

        for (int col = 0; col < numHearts; col++) {

                ImageView heartView = new ImageView(lifeHeart);
                heartView.setFitHeight(30);
                heartView.setFitWidth(30);
                heartView.setLayoutX(col * 35); // Item in horizontal
                itemPane.getChildren().add(heartView);

        }

        legend.getChildren().add(itemPane);

        life.getChildren().addAll(legend);
    }

    ///////// UPDATE LIFE LEGEND //////////
    private void updateLifeLegend(int numHearts) {
        life.getChildren().clear(); // Clear lives

        VBox legend = new VBox();

        ImageView ribbonView = new ImageView(ribbonLife);
        ribbonView.setFitHeight(100);
        ribbonView.setFitWidth(100);
        ribbonView.setTranslateY(10);
        legend.getChildren().addAll(ribbonView);

        Pane itemPane = new Pane();

        VBox.setMargin(legend, new Insets(5, 0, 0, 0));

        for (int col = 0; col < numHearts; col++) {
            ImageView heartView = new ImageView(lifeHeart);
            heartView.setFitHeight(30);
            heartView.setFitWidth(30);
            heartView.setLayoutX(col * 35);
            itemPane.getChildren().add(heartView);
        }

        legend.getChildren().add(itemPane);
        life.getChildren().addAll(legend);
    }

    //Check collision between the player and the items
    private void checkCollision(Rectangle player) {
        int playerRow = GridPane.getRowIndex(player);
        int playerCol = GridPane.getColumnIndex(player);

        Rectangle cell = getNodeByRowColumnIndex(playerRow, playerCol, mazeGrid);

        if (cell != null) {
            if (cell.getFill() instanceof ImagePattern) {
                ImagePattern pattern = (ImagePattern) cell.getFill();
                if (pattern.getImage().equals(potion)) {
                    cell.setFill(Color.TRANSPARENT);
                    numPotionsCollected++;
                    for (int col = 0; col < numPotionsCollected; col++) {
                        ImageView potionView = new ImageView(potion);
                        potionView.setFitHeight(30);
                        potionView.setFitWidth(30);
                        legendItems.add(potionView, col, 3);
                        makeDraggable(potionView);
                    }

                }
                else if(pattern.getImage().equals(timeTurner)){
                    cell.setFill(Color.TRANSPARENT);
                    numTimeCollected++;

                    for (int col = 0; col < numTimeCollected; col++) {
                        ImageView timeView = new ImageView(timeTurner);
                        timeView.setFitWidth(30);
                        timeView.setFitHeight(30);
                        legendItems.add(timeView, col, 2);
                        makeDraggable(timeView);
                    }

                }else if(pattern.getImage().equals(wand)) {
                    cell.setFill(Color.TRANSPARENT);
                    numWandCollected++;
                    if (numWandCollected == 1) {
                        ImageView wandView = new ImageView(wand);
                        wandView.setFitHeight(30);
                        wandView.setFitWidth(30);
                        legendItems.add(wandView, 0, 1);
                        makeDraggable(wandView);
                    }
                }


            }
        }

    }

    //When you collect items in the legend you can drag and drop them on the player to use them
    private void makeDraggable(ImageView node){

        node.setOnMousePressed(e -> {
            startX = e.getSceneX() - node.getTranslateX();
            startY = e.getSceneY() - node.getTranslateY();
            node.toFront();
        });

        node.setOnMouseDragged(e -> {
            node.setTranslateX(e.getSceneX() - startX);
            node.setTranslateY(e.getSceneY() - startY);
        });

        node.setOnMouseReleased(e -> {

            node.setVisible(false);
            Image imageItem = node.getImage();
            if(imageItem == potion && numHearts < 3){
                    numHearts++;
                    System.out.println( "PotionNumHearts : numHearts");
                    updateLifeLegend(numHearts);


            }else if(imageItem == timeTurner){
                if(remainingSeconds < 30)
                    remainingSeconds = remainingSeconds + 30;
                else{
                    int remain = 0;
                    remain = 60 - remainingSeconds;
                    int last = 0;
                    last = 30 - remain;
                    remainingMinutes++;
                    remainingSeconds = last;

                }


            }else if(imageItem == wand){
                collectedWand = true;
            }

        });

    }

    //During the checkCollision is used to control if there is an item inside the cell where there is the player
    private Rectangle getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Rectangle result = null;
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);

            if (rowIndex != null && colIndex != null && rowIndex == row && colIndex == column) {
                result = (Rectangle)node;
                break;
            }
        }

        return result;
    }

    // remove voldemort from the grid
    void removeVoldemort() {
        // Stop the Voldemort timeline
        voldemortTimeline.stop();
        voldemortTarget = null;
        targetVold.setFill(Color.TRANSPARENT);
        targetVold = null;

        // Remove the flag
        Rectangle flag = getNodeByRowColumnIndex(tvRow, tvCol, mazeGrid);
        removeFlag(flag);

        // Remove Voldemort from the maze
        mazeGrid.getChildren().removeAll(voldemort, targetVold);
        voldemort = null;
        System.out.println("removing Voldemort");
    }


    //check the valid movement of the player
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLUMNS && maze[row][col] == 0;
    }

    // Get formatted time of the remaining time
    private String getFormattedTime() {
        return String.format("%02d:%02d", remainingMinutes, remainingSeconds);
    }

    //Managing the game over menu
    private void gameOverWindow(boolean timeIsUp, Stage primaryStage){

        //open the gameOverScreen
        gameOverScreen GameOverScreen = new gameOverScreen(gameOverCallback, gameOverExitCallback, timeIsUp, primaryStage, selectedHouse);
        try {
            GameOverScreen.start(new Stage());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    //Managing the win menu
    private void winWindow(Stage primaryStage){
        if(bool) {
            winScreen winScreen = new winScreen(winCallback, winExitCallback, primaryStage, selectedHouse);
            try {
                winScreen.start(new Stage()); //open the winScreen
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }


    }

   //managing the open of the door with the formula, when you're in front of the door and touch it with the mouse a window open to let you say the formula "Open the door"
   public void managingDoorOpen(Rectangle cell) throws IOException {
       System.out.println("in managingDoorOpen");
       // Event handler when the mouse enters a specific cell (Rectangle)

       cell.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
           // Check if the player is at the right location and conditions are met
           if (playerRow == 4 && playerCol == 11 && maze[4][12] == 3 && collectedWand) {
               System.out.println("Player is at the right location and has collected the wand");

               // Try to create a TranscriberDemo instance (for voice recognition)
               try {
                   transcriberDemo = new TranscriberDemo();
               } catch (IOException e) {
                   // Handle any IOException by rethrowing it as a runtime exception
                   throw new RuntimeException(e);
               }

               // Create a new root grid pane and prepare UI elements for a new stage
               GridPane root = new GridPane();
               Image recordIcon = new Image(getClass().getResourceAsStream("/com/example/harrypottermaze/record.png"));

               // Create an ImageView for the vocal icon
               ImageView iconView = new ImageView(recordIcon);
               iconView.setTranslateX(130);
               iconView.setTranslateY(80);
               iconView.setFitHeight(50);
               iconView.setPreserveRatio(true);

               // Configure the layout and stage for the door open screen
               root.setVgap(0);
               root.setHgap(0);
               root.setAlignment(Pos.CENTER);
               Stage doorStage = new Stage();
               Scene doorScene = new Scene(root, 500, 500);
               doorStage.setTitle("OPEN THE DOOR");
               root.setBackground(newBackground);

               // Create text elements for instructions
               Text textLine1 = new Text("Click the vocal icon and say:");
               Text textLine2 = new Text("'Open the door'");
               textLine1.setTranslateY(-100);
               textLine2.setTranslateY(-30);
               textLine2.setTranslateX(50);
               textLine1.setFont(Font.font("Zapfino", 18));
               textLine2.setFont(Font.font("Zapfino", 18));
               textLine1.setStyle("fx-font-weight: bold; -fx-text-fill: #302c2c;");
               textLine2.setStyle("fx-font-weight: bold; -fx-text-fill: #302c2c;");

               // Apply a visual effect (glow) to the text
               final Effect glow = new Glow(1.0);
               textLine1.setEffect(glow);
               textLine2.setEffect(glow);

               // Add text elements and vocal icon to the root pane
               root.getChildren().addAll(textLine1, textLine2, iconView);

               // Add a mouse click event handler for the vocal icon
               iconView.setOnMouseClicked(e -> {
                   // Attempt to recognize the voice command
                   boolean magicPhraseRecognized = TranscriberDemo.recognizeOpenMap();
                   if (magicPhraseRecognized) {
                       // If the voice command is recognized, open the door and close the doorStage
                       maze[4][12] = 0;
                       Rectangle cell2 = getNodeByRowColumnIndex(4, 12, mazeGrid);
                       cell2.setFill(Color.TRANSPARENT);
                       doorStage.close();
                   } else {
                       // If the voice command is not recognized, display a message
                       System.out.println("Sorry, cannot open the door");
                   }
               });

               // Set the scene and show the doorStage
               doorStage.setScene(doorScene);
               doorStage.show();
           }
       });
    }

    //managing the voldemort fight, when you touch voldemort with the mouse you can fight drawing a circle, if voldemort touches you you lost hearts
    private void voldemortfight(Rectangle cell){
        cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(maze[4][12] == 0 && collectedWand == true){
                if (drawingGame == null) {
                    drawingGame = new FreeformDrawingGame(this);
                    drawingGame.startDrawingGame();
                    bool = true;
                }
                collectedWand = false;
            }

        });
    }


    ///// START TIME //////
    public void timeStart(){
        timeline.play();
    }

    ///// STOP TIME //////
    public void timeStop(){
        timeline.stop();
    }

    //Change color maze based on the selected house
    private Color getColorForHouse(String house) {
        if ("Gryffindor".equals(house)) {
            Color gryff = Color.rgb(141, 19, 36);
            return gryff;
        } else if ("Slytherin".equals(house)) {
            Color sylth = Color.rgb(32, 95, 70);
            return sylth;
        } else if ("Hufflepuff".equals(house)) {
            Color huff = Color.rgb(224, 159, 45);
            return huff;
        } else {
            return Color.BLACK;
        }
    }

    public static void main(String[] args) {
        launch(args); // Launch the app
    }

}
