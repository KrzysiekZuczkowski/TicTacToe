package com.game.tictactoe;

import javafx.application.Application;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TicTacToe extends Application {


    private static final String[] GAME_FIGURES = new String[]{"x", "o"};
    private static final String X_WIN = GAME_FIGURES[0] + GAME_FIGURES[0] + GAME_FIGURES[0];
    private static final String O_WIN = GAME_FIGURES[1] + GAME_FIGURES[1] + GAME_FIGURES[1];


    private String playerFigure;
    private boolean playerTurn = true;
    private boolean gameOver = false;
    private Label totalLabel = new Label();
    private final Image buttonBack = new Image("file:src/main/resources/ticTacToe.png");
    private final Image buttonX = new Image("file:src/main/resources/buttonX.png");
    private final Image buttonO = new Image("file:src/main/resources/buttonO.png");
    private final Background buttBack = getBackground(buttonBack);
    private final Background buttX = getBackground(buttonX);
    private final Background buttO = getBackground(buttonO);

    public Background getBackground(Image image) {
        BackgroundSize backgroundSize = new BackgroundSize(
                100,
                100,
                true,
                true,
                true,
                false
        );
        BackgroundImage buttonImage = new BackgroundImage(
                image,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        return new Background(buttonImage);
    }

    public void ticTacToeGame(GridPane grid, Button button, int rowCol) {
        playerMove(button);
        String gameFlow = getGameFlow(grid);
        checkGameFlow(gameFlow, rowCol);
        if (!playerTurn && !gameOver) {
            computerMove((Button) grid.getChildren().get(getIndex(gameFlow)));
            checkGameFlow(getGameFlow(grid), rowCol);
        }
    }

    public void playerMove(Button button) {
        playerTurn = false;
        button.setText(getPlayerFigure());
        button.setFont(new Font(0));
        if(playerFigure.equals(GAME_FIGURES[0]))
            button.setBackground(buttX);
        else
            button.setBackground(buttO);
        button.setOnAction((e) -> {
        });
    }

    public String getPlayerFigure() {
        return playerFigure;
    }

    public void setPlayerFigure(String playerFigure) {
        this.playerFigure = playerFigure;
    }

    public String getGameFlow(GridPane grid) {
        return getButtonList(grid).stream()
                .map(Button::getText)
                .collect(Collectors.joining("", "", ""));
    }

    public List<Button> getButtonList(GridPane grid) {
        return grid.getChildren().stream()
                .filter(n -> n instanceof Button)
                .map(n -> (Button) n)
                .filter(button -> !button.getText().equals("New Game"))
                .collect(toList());
    }

    public void  checkGameFlow(String gameFlow, int rowColNumber) {
        int numberOfXAndO = getNumberOfXAndO(gameFlow);
        boolean needToCheck = numberOfXAndO > 4;
        String playerPlaysFigures = getPlayerFigure().equals(GAME_FIGURES[0]) ? X_WIN : O_WIN;
        String computerPlaysFigures = getPlayerFigure().equals(GAME_FIGURES[0]) ? O_WIN : X_WIN;

        if(needToCheck) {
            int i = 0, k = 2 * rowColNumber;
            StringBuilder horizontal;
            StringBuilder vertical;
            StringBuilder backslash = new StringBuilder();
            StringBuilder slash = new StringBuilder();
            while (i < rowColNumber && !gameOver) {
                horizontal = new StringBuilder();
                vertical = new StringBuilder();
                int n = 2 * (i + i);
                backslash.append(gameFlow.charAt(n));
                slash.append(gameFlow.charAt(n + k));
                for (int j = 0; j < rowColNumber; j++) {
                    horizontal.append(gameFlow.charAt(i * rowColNumber + j));
                    vertical.append(gameFlow.charAt(i + j * rowColNumber));
                }
                decideGame(playerPlaysFigures, computerPlaysFigures, horizontal, vertical);
                i++;
                k = k - 2 * rowColNumber;
            }
            decideGame(playerPlaysFigures, computerPlaysFigures, backslash, slash);
            if (numberOfXAndO == 9 && !gameOver) {
                gameOver = true;
                setTotalLabel("Draw!!!");
            }
        }
    }

    private void decideGame(String playerPlaysFigures, String computerPlaysFigures,
                            StringBuilder fromTheGrid1, StringBuilder fromTheGrid2) {

        if (fromTheGrid1.toString().equals(playerPlaysFigures) ||
                fromTheGrid2.toString().equals(playerPlaysFigures)) {
            gameOver = true;
            setTotalLabel("You win!!!");
        } else if (fromTheGrid1.toString().equals(computerPlaysFigures) ||
                fromTheGrid2.toString().equals(computerPlaysFigures)) {
            gameOver = true;
            setTotalLabel("Computer win!!!");
        }
    }

    public int getNumberOfXAndO(String gameFlow) {
        return IntStream.range(0, gameFlow.length())
                .filter(n -> gameFlow.charAt(n) != 'T')
                .map(n -> 1)
                .sum();
    }

    public void setTotalLabel(String message) {
        totalLabel.setText(message);
        totalLabel.setFont(new Font("Arial", 35));
        totalLabel.setTextFill(Color.rgb(210, 0, 0));
    }

    public void computerMove(Button button) {
        playerTurn = true;
        button.setText(getPlayerFigure().equals(GAME_FIGURES[0]) ? GAME_FIGURES[1] : GAME_FIGURES[0]);
        button.setFont(new Font(0));
        button.setBackground(getPlayerFigure().equals(GAME_FIGURES[0]) ? buttO: buttX);
        button.setOnAction((e) -> {
        });
    }

    public int getIndex(String gameFlow) {
        List<Integer> list = getIndexListOfUnusedFields(gameFlow);
        List<Integer> bestIndex = getShuffleList(new ArrayList<>(Arrays.asList(0, 2, 6, 8)));

        for(Integer i : bestIndex)
            if(list.contains(i))
                return i;

        if(list.size() > 1) {
            Random random = new Random();
            int index = random.nextInt(list.size());
            return list.get(index);
        }
        return list.get(0);
    }

    public List<Integer> getIndexListOfUnusedFields(String gameFlow) {
        return IntStream.range(0, gameFlow.length())
                .filter(n -> gameFlow.charAt(n) == 'T')
                .boxed()
                .collect(toList());
    }

    private List<Integer> getShuffleList(List<Integer> list) {
        List<Integer> shuffledList = new ArrayList<>();
        Random random = new Random();

        shuffledList.add(4);

        while(list.size() - 1 > 0) {
            int i = random.nextInt(list.size());
            shuffledList.add(list.get(i));
            list.remove(list.get(i));
        }

        shuffledList.add(list.get(0));
        return shuffledList;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        setPlayerFigure(GAME_FIGURES[0]);

        GridPane grid = new GridPane();
        int rowCol = 3;
        double sceneWidth = 900, sceneHeight = 600;

        for (int i = 0; i < rowCol; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(50);
            grid.getRowConstraints().add(row);
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(50);
            grid.getColumnConstraints().add(column);
            for (int j = 0; j < rowCol; j++) {
                Button button = new Button("T");
                button.setFont(new Font(0));
                button.setMaxSize(sceneWidth / rowCol, sceneHeight / rowCol);
                GridPane.setRowIndex(button, i);
                GridPane.setColumnIndex(button, j);
                button.setBackground(buttBack);
                BorderStroke borderStroke = new BorderStroke(
                        Color.BLACK,
                        new BorderStrokeStyle(
                                StrokeType.INSIDE,
                                StrokeLineJoin.MITER,
                                StrokeLineCap.SQUARE,
                                10, 0, null),
                        null,
                        new BorderWidths(0, j < 2 ? 10 : 0, i < 2 ? 10 : 0, 0)
                );

                button.setBorder(new Border(borderStroke));
                button.setOnAction((e) -> {
                    if (playerTurn && !gameOver) {
                        ticTacToeGame(grid, button, rowCol);
                    }
                });
                grid.getChildren().add(button);
            }
        }

        GridPane.setConstraints(totalLabel, 1, 1,
                1, 1, HPos.CENTER, VPos.CENTER);

        Button newGame = new Button();
        newGame.setMaxWidth(150);
        newGame.setText("New Game");
        newGame.setTextAlignment(TextAlignment.CENTER);
        newGame.setFont(new Font(15));
        GridPane.setConstraints(newGame, 2, 0,
                1, 1, HPos.RIGHT, VPos.TOP);
        newGame.setOnAction((e) -> {
            playerTurn = true;
            gameOver = false;
            totalLabel.setText("");
            for (Button button : getButtonList(grid)) {
                button.setBackground(buttBack);
                button.setText("T");
                button.setFont(new Font(0));
                button.setOnAction((e1) -> {
                    if (playerTurn && !gameOver)
                        ticTacToeGame(grid, button, rowCol);
                });
            }
        });

        grid.getChildren().addAll(newGame, totalLabel);

        Scene scene = new Scene(grid, sceneWidth, sceneHeight, null);

        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
