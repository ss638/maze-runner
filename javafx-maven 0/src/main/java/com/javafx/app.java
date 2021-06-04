package com.javafx;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

class node {
    public int x, y;

    node() {
    }

    node(int a, int b) {
        x = a;
        y = b;
    }

    void set(int a, int b) {
        x = a;
        y = b;
    }
}

public class app extends Application {
    public int Size = 10;// Effective map size, used for Prim algorithm to generate map
    public static final int Range = 60;// cell side length
    public static int goldInHand = 0;// cell side length
    public static int goldOnMaze = 5;// cell side length

    public static int difficulty = 5;// cell side length
    public static int stamina = 12;// cell side length
    public static int trap = 5;// cell side length
    public static int apple = 10 - trap;// cell side length

    public int VSize = (Size + 2) * Range;// Actual map size
    public int maze[][] = new int[VSize][VSize];// map
    public int vis[][] = new int[VSize][VSize];// Visited path
    public node f[][] = new node[VSize][VSize];
    public int[][] dir = {{-Range, 0}, {Range, 0}, {0, -Range}, {0, Range}};// moving direction
    public CreateMap c2 = new CreateMap(Size, Size);
    public CreateMap c = new CreateMap(Size, Size);

    Rectangle rec = new Rectangle(Range, Range, Range, Range);
    private int recX = Range, recY = Range;
    private boolean autoPath = false;// Whether to enable automatic solution
    public static int currentx = 1;
    public static int currenty = 1;
    public static Pane pane = new Pane();

    public static Stage statisticsStage = new Stage();
    public static Stage staminaOverStage = new Stage();
    public static Stage goldOverStage = new Stage();
    public static Stage wonStage = new Stage();

    public void start(Stage stage) throws Exception {

        CreateMap();
        Pane pane = Init();// Generate maze platform
        Scene scene = new Scene(pane, VSize, VSize);

        scene.setOnKeyPressed(k -> {
            System.out.println(stamina);
            if (stamina == 0) {
                Text lastText = new Text(20, 30, "Stamina is Over - You lost");
                Pane lastPane = new Pane();
                lastPane.getChildren().addAll(lastText);
                Scene lastScene = new Scene(lastPane);
                staminaOverStage.setScene(lastScene);
                staminaOverStage.setTitle("Game over");
                staminaOverStage.show();
                staminaOverStage.requestFocus();
                staminaOverStage.setWidth(300);
                staminaOverStage.setHeight(100);
                wonStage.close();
                goldOverStage.close();
                stage.close();
            }
            Pane pane2 = new Pane();
            KeyCode code = k.getCode();
            int tx = recX, ty = recY;
            Text text1 = new Text();
            Text text2 = new Text();
            Text text3 = new Text();
            Text text4 = new Text();
            if (currentx < Size && currenty < Size) {
                if (mapString[currentx][currenty].equals("G")) {
                    text1 = new Text(10, 20, "Gold In Hand " + ++goldInHand);
                    text2 = new Text(10, 40, "Gold On Maze " + --goldOnMaze);
                    text3 = new Text(10, 60, "Stamina " + stamina);
                    text4 = new Text(10, 80, "Apples Remaining " + apple);
                } else if (mapString[currentx][currenty].equals("T")) {
                    if (goldInHand == 0) {
                        goldOverStage.close();
                        Text lastText = new Text(20, 20, "You stepped on a Trap and you have Zero gold left");
                        Pane newPane = new Pane();
                        newPane.getChildren().addAll(lastText);
                        Scene goldOverScene = new Scene(newPane);
                        goldOverStage.setScene(goldOverScene);
                        goldOverStage.setTitle("Game over");
                        goldOverStage.show();
                        goldOverStage.requestFocus();
                        goldOverStage.setWidth(400);
                        goldOverStage.setHeight(100);
                        stage.close();
                        statisticsStage.close();
                        wonStage.close();
                        staminaOverStage.close();
                    } else {
                        text1 = new Text(10, 20, "Gold In Hand " + --goldInHand);
                        text2 = new Text(10, 40, "Gold On Maze " + goldOnMaze);
                        text3 = new Text(10, 60, "Stamina " + stamina);
                        text4 = new Text(10, 80, "Apples Remaining " + apple);
                    }
                } else if (mapString[currentx][currenty].equals("A")) {
                    text1 = new Text(10, 20, "Gold In Hand " + goldInHand);
                    text2 = new Text(10, 40, "Gold On Maze " + goldOnMaze);
                    stamina = stamina + 3;
                    text3 = new Text(10, 60, "Stamina " + stamina);
                    text4 = new Text(10, 80, "Apples Remaining " + --apple);
                } else {
                    text1 = new Text(10, 20, "Gold In Hand " + goldInHand);
                    text2 = new Text(10, 40, "Gold On Maze " + goldOnMaze);
                    text4 = new Text(10, 80, "Apples Remaining " + apple);

                }
                if (mapString[currentx][currenty].equals("G") ||
                        mapString[currentx][currenty].equals("A")) {
                    mapString[currentx][currenty] = "N";
                    Rectangle r = new Rectangle(tx, ty, Range, Range);
                    r.setStroke(Color.BLACK);
                    r.setFill(Color.WHITE);
                    pane.getChildren().add(r);
                }
            }
            if (code.equals(KeyCode.LEFT) && autoPath == false) { // Left button pressed
                tx -= Range;
            } else if (code.equals(KeyCode.RIGHT) && autoPath == false) {// Right clicked
                tx += Range;
            } else if (code.equals(KeyCode.UP) && autoPath == false) {// Pressed the up arrow key
                ty -= Range;
            } else if (code.equals(KeyCode.DOWN) && autoPath == false) {// Pressed down arrow key
                ty += Range;
            }
            if (inside(tx, ty) && maze[tx][ty] == 1 && autoPath == false) {
                text1 = new Text(10, 20, "Gold In Hand " + goldInHand);
                text2 = new Text(10, 40, "Gold On Maze " + goldOnMaze);
                text3 = new Text(10, 60, "Stamina " + --stamina);
                text4 = new Text(10, 80, "Apples Remaining " + apple);
                // System.out.println(recX+" "+recY+" "+tx + " " + ty);
                if (code.equals(KeyCode.LEFT) && autoPath == false) { // Left button pressed
                    currentx = currentx - 1;
                } else if (code.equals(KeyCode.RIGHT) && autoPath == false) {// Right clicked
                    currentx = currentx + 1;
                } else if (code.equals(KeyCode.UP) && autoPath == false) {// Pressed the up arrow key
                    currenty = currenty - 1;
                } else if (code.equals(KeyCode.DOWN) && autoPath == false) {// Pressed down arrow key
                    currenty = currenty + 1;
                }
                if (tx == VSize - Range - Range && ty == VSize - Range * 2) {
                    Text lastText = new Text(20, 40, "You Reached Destination with " + goldInHand + "golds. You won");
                    Pane lastPane = new Pane();
                    lastPane.getChildren().addAll(lastText);
                    Scene lastScene = new Scene(lastPane);
                    wonStage.setScene(lastScene);
                    wonStage.setTitle("Game over");
                    wonStage.show();
                    wonStage.requestFocus();
                    wonStage.setWidth(400);
                    wonStage.setHeight(200);
                    statisticsStage.close();
                    staminaOverStage.close();
                    stage.close();
                } else {
                    move(tx, ty);
                    recX = tx;
                    recY = ty;
                }

            }
            pane2.getChildren().removeAll();
            pane2.getChildren().addAll(text1, text2, text3, text4);
            Scene scene2 = new Scene(pane2);
            statisticsStage.setScene(scene2);
            stage.requestFocus();

        });
        statisticsStage.setX(0);
        statisticsStage.setY(0);
        statisticsStage.setWidth(200);
        statisticsStage.setHeight(200);
        statisticsStage.setTitle("Data");
        Text text1 = new Text(10, 20, "Gold In Hand " + goldInHand);
        Text text2 = new Text(10, 40, "Gold On Maze " + goldOnMaze);
        Text text3 = new Text(10, 60, "Stamina " + stamina);
        Text text4 = new Text(10, 80, "Apples Remaining " + stamina);
        Pane pane2 = new Pane();
        pane2.getChildren().addAll(text1, text2, text3, text4);
        Scene scene2 = new Scene(pane2);
        statisticsStage.setScene(scene2);
        statisticsStage.show();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Maze ");
        stage.show();
    }

    public void move(int tx, int ty) {
        SequentialTransition link = new SequentialTransition();// Animation list
        link.setNode(rec);
        TranslateTransition tt = new TranslateTransition();
        tt.setFromX(recX - Range);
        tt.setToX(tx - Range);
        tt.setFromY(recY - Range);
        tt.setToY(ty - Range);
        // System.out.println(recX+" "+recY+" "+tx+" "+ty);
        link.getChildren().add(tt);
        link.play();
    }

    public String[][] mapString;// Array to store the maze

    public void CreateMap() {
        mapString = new String[Size][Size];
        c.Init();// Generate maze

        int appleCount = apple;
        int goldCount = goldOnMaze;
        int trapCount = trap;
        for (int i = 0; i < Size; i++) // Set all grids as walls
            for (int j = 0; j < Size; j++)
                mapString[i][j] = "N";
        while (appleCount > 0 || goldCount > 0 || trapCount > 0) {
            int i = (int) ((Math.random() * 1000) % Size);
            int j = (int) ((Math.random() * 1000) % Size);
            if (mapString[i][j].equals("N")
                    && i != 1
                    && j != 1
                    && i != 0
                    && j != 0
                    && (i != Size - 2 || j != Size - 2)
            ) {
                int num = new Double(Math.random() * 10000).intValue() % 3;
                if (num == 0) {
                    if (appleCount > 0) {
                        mapString[i][j] = "A";// 0 is the wall 1 is the road
                        appleCount--;
                    }
                } else if (num == 1) {
                    if (goldCount > 0) {
                        mapString[i][j] = "G";// 0 is the wall 1 is the road
                        goldCount--;
                    }
                } else {
                    if (trapCount > 0) {
                        mapString[i][j] = "T";// 0 is the wall 1 is the road
                        trapCount--;
                    }
                }
            }
        }
        for (int i = 0; i < VSize; i += Range) {
            for (int j = 0; j < VSize; j += Range) {
                maze[i][j] = c.map[i / Range][j / Range];
            }
        } // Maze mapping
    }

    public Pane Init() throws FileNotFoundException {

        // create a image
        Image apple = new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQNGts_8ETTCfLB6i-lBv2i-EZcofz1zhDJ0g&usqp=CAU");
        Image trap = new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNVCR6fRfRLlg9ta4zicMkEzkzaHjLASkK4A&usqp=CAU");
        Image gold = new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRGcY4I4sZkwOS_ync05SOK0XtChhJvlh2zvA&usqp=CAU");

        // create ImagePattern
        for (int i = 0, k = 0; i < VSize; i += Range, k++) {
            for (int j = 0, l = 0; j < VSize; j += Range, l++) {
                Rectangle r = new Rectangle(i, j, Range, Range);
                if (maze[i][j] == 0) {
                    r.setFill(Color.PINK);
                } else if (maze[i][j] == 1) {
                    if (i == 0 || j == 0 ||
                            i >= (VSize - Range)
                            || j >= (VSize - Range)
                    ) {
                        r.setStroke(Color.WHITE);

                    } else {
                        r.setStroke(Color.BLACK);
                        r.setFill(Color.WHITE);
//                        r.setFill(image_pattern);
                        if (k < Size && l < Size) {
                            if (mapString[k][l].equals("A")) {
                                ImagePattern mineImagePattern = new ImagePattern(apple);
                                r.setFill(mineImagePattern);
                            } else if (mapString[k][l].equals("T")) {
                                ImagePattern mineImagePattern = new ImagePattern(trap);
                                r.setFill(mineImagePattern);
                            } else if (mapString[k][l].equals("G")) {
                                ImagePattern mineImagePattern = new ImagePattern(gold);
                                r.setFill(mineImagePattern);
                            }
                        }
                    }
//                    stack.getChildren().addAll(r, text);
                }
                if (i == VSize - Range - Range && j == VSize - Range * 2) {
                    r.setFill(Color.RED);
                }
                pane.getChildren().add(r);
            }
        }
//

        Image human = new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRgYBuwmv8jftbgYUqWI_Y16hBMhe75E_LZ4w&usqp=CAU");
        ImagePattern humanPattern = new ImagePattern(human);
        rec.setFill(humanPattern);
        pane.getChildren().add(rec);// Show target block
        return pane;
    }

    boolean inside(int fx, int fy) {
        return (fx >= Range && fx <= VSize - Range - 2 && fy >= Range && fy <= VSize - Range - 2);
    }

    public static void main(String[] args) {
        Application.launch();
    }
}