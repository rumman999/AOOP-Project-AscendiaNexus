package com.example.aoop_project.games.chess;

import com.example.aoop_project.games.chess.figures.ChessPiece;
import com.example.aoop_project.games.chess.game.*;
import com.example.aoop_project.games.chess.game.*;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;

import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.lang.Thread.sleep;

public class Interface extends Application {

    private byte[] currentSave;

    private BorderPane boardPane;
    private BorderPane customizerPane;
    private ChessBoardController controller = new ChessBoardController();
    private ChessBoardView boardView = new ChessBoardView();
    private ChessBoardModel boardModel = new ChessBoardModel();
    private Stage primaryStage;
    private static final int SCREEN_HEIGHT = 665;
    private static final int SCREEN_WIDTH = 640;

    private boolean rotationOn;
    private boolean rotationAnimationOn;

    private int rotationCounter = 0;

    private String DEFAULT_DARK_COLOR = "#e6ae6a";
    private String DEFAULT_LIGHT_COLOR = "#eeeed2";
    private String darkPieceColor;
    private String lightPieceColor;

    private String whitePlayerName;
    private String blackPlayerName;


    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        setDefaultSettings();
        stage.setTitle("OnChess");
        stage.setResizable(false);
        setupEndGameDialogue();
        initiateStartScreen();
    }

    public String getButtonStyle() {
        return "-fx-font-weight: bold;\n" +
                "-fx-font-size: 1.1em;\n" +
                "-fx-padding: 8 15 15 15;\n" +
                "-fx-background-insets: 0,0 0 6 0, 0 0 7 0, 0 0 8 0;\n" +
                "-fx-background-radius: 10;\n" +
                "-fx-background-color: \n" +
                "linear-gradient(from 0% 86% to 0% 95%, #a34313 0%, #903b12 60%),\n" +
                "#ffe0b3,\n" +
                "#d86e3a,\n" +
                "radial-gradient(center 50% 50%, radius 100%, #eeeed2, #ffe0b3);\n" +
                "-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.73) , 4,0,0,1 );";
    }

    public String getTextStyle() {
        return "-fx-font-weight: bold;\n" +
                "-fx-font-size: 1.1em;\n" +
                "-fx-padding: 8 15 15 15;\n" +
                "-fx-background-insets: 0;\n" +
                "-fx-background-radius: 0;\n" +
                "-fx-background-color: \n" +
                "linear-gradient(from 0% 86% to 0% 95%, #a34313 0%, #903b12 60%),\n" +
                "#ffe0b3,\n" +
                "#d86e3a,\n" +
                "radial-gradient(center 50% 50%, radius 100%, #eeeed2, #ffe0b3);\n" +
                "-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.73) , 4,0,0,1 );";
    }

    public String getHelpTextStyle() {
        return "-fx-font-weight: bold;\n" +
                "-fx-font-size: 1.1em;\n" +
                "-fx-padding: 8 15 15 15;\n" +
                "-fx-background-insets: 0;\n" +
                "-fx-background-radius: 0;\n" +
                "-fx-background-color: \n" +
                "linear-gradient(from 0% 86% to 0% 95%, #a34313 0%, #903b12 60%),\n" +
                "#ffe0b3,\n" +
                "#d86e3a,\n" +
                "radial-gradient(center 50% 50%, radius 100%, #eeeed2, #ffe0b3);" ;
    }

    private Button generateLargeButtons(String name) {
        Button button = new Button(name);
        button.setPrefSize(250, 50);
        button.setStyle(getButtonStyle());
        button.setTranslateY(-30);
        return button;
    }

    private Button getNewGameButton() {
        Button button = generateLargeButtons("NEW GAME");
        button.setOnAction(actionEvent -> {
            boardPane = generateBoardPane();
            button.getScene().setRoot(boardPane);
        });
        return button;
    }

    private Button getOpenSaveButton() {
        Button button = generateLargeButtons("OPEN SAVED/CUSTOM GAME");
        button.setOnAction(actionEvent -> {
            try {
                if (!loadFileChooserOpen()) {
                    return;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            boardPane = generateBoardPane();
            button.getScene().setRoot(boardPane);
            try {
                loadGame(currentSave, controller);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        return button;
    }

    private Button getCustomizerButton() {
        Button button = generateLargeButtons("CREATE CUSTOM GAME");
        customizerPane = generateCustomizePane();
        button.setOnAction(e -> {
            primaryStage.setTitle("Customizer - OnChess");
            button.getScene().setRoot(customizerPane);
        });
        return button;
    }

    private Button getSettingsButton() {
        Button button = generateLargeButtons("SETTINGS");
        Pane settingsPane = generateSettingsPane();
        button.setOnAction(e -> {
            primaryStage.setTitle("Settings - OnChess");
            button.getScene().setRoot(settingsPane);
        });
        return button;
    }

    private Button getAboutButton() {
        Button button = generateLargeButtons("ABOUT");
        Pane aboutPane = generateAboutPane();
        button.setOnAction(e -> {
            primaryStage.setTitle("About - OnChess");
            button.getScene().setRoot(aboutPane);
        });
        return button;
    }

    private VBox generateMenuVBox() {

        VBox menu = new VBox();
        menu.setStyle("-fx-background-color: #ffe0b3");
        primaryStage.setTitle("OnChess");

        String imageURI = "images/logo.png";
        Image image = new Image(Interface.class.getResourceAsStream(imageURI));

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(220);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);
        imageView.setTranslateY(-50);

        menu.setSpacing(10);
        menu.getChildren().addAll(imageView, getNewGameButton(),
                getOpenSaveButton(), getCustomizerButton(),
                getSettingsButton(), getAboutButton());

        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(0, 0, -SCREEN_HEIGHT / 8, 0));
        return menu;
    }

    public void loadGame(byte[] gameContent, ChessBoardController ctrl) throws InterruptedException {
        ChessPiece.PieceColor prevColor = controller.getGame().getColorInPlay();
        try {
            GameLoader.loadGame(gameContent, ctrl);
        } catch (IllegalArgumentException ex) {
            primaryStage.getScene().setRoot(generateMenuVBox());
            final Stage dialogue = new Stage();
            dialogue.setResizable(false);
            dialogue.initModality(Modality.APPLICATION_MODAL);
            dialogue.initOwner(primaryStage);

            VBox vBox = new VBox();
            vBox.setPrefSize(250, 200);
            vBox.setSpacing(22);

            Label errorLabel = new Label("      Invalid file selected.\n" +
                    "Please select a valid .csv file");
            errorLabel.setAlignment(Pos.CENTER);
            HBox labelHBox = new HBox(errorLabel);
            labelHBox.setAlignment(Pos.CENTER);
            errorLabel.setStyle(getTextStyle());

            Button closeButton = getSmallButton();
            closeButton.setText("Close");
            HBox buttonHBox = new HBox(closeButton);
            buttonHBox.setAlignment(Pos.CENTER);
            closeButton.setTranslateX(4);
            closeButton.setStyle(getButtonStyle());

            vBox.setStyle("-fx-background-color: #ffe0b3;");
            vBox.setAlignment(Pos.CENTER);

            closeButton.setOnAction(arg -> {
                dialogue.close();
            });
            vBox.getChildren().addAll(labelHBox, buttonHBox);
            dialogue.setScene(new Scene(vBox));
            dialogue.setTitle("File Load Error");
            dialogue.show();
        }
        setupRotation();
        if (controller.getGame().getColorInPlay() != prevColor) {
            performRotation();
            rotationCounter--;
        }

    }

    public void setupRotation() {
        rotationCounter = 0;
        controller.getGame().getMoveCounterProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                try {
                    performRotationIfValid();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void setupEndGameDialogue() {
        controller.getGame().isRunning.addListener((observableValue, prevRunningStatus, curRunningStatus) -> {
            if (!curRunningStatus) {
                final Stage dialogue = new Stage();
                dialogue.setResizable(false);
                dialogue.initModality(Modality.APPLICATION_MODAL);
                dialogue.initOwner(primaryStage);

                VBox vBox = new VBox();
                vBox.setSpacing(5);

                String endGameMessage = "";
                Game.Status status = controller.getGame().getStatus();
                switch (status) {
                    case OVER_W:
                        endGameMessage = "CHECKMATE! WHITE WINS";
                        break;
                    case OVER_B:
                        endGameMessage = "CheckMate! BLACK WINS";
                        break;
                    case OVER_DRAW:
                        endGameMessage = "STALEMATE! DRAW GAME";
                        break;
                }

                Label endLabel = new Label(endGameMessage);
                endLabel.setStyle(getTextStyle());
                endLabel.setAlignment(Pos.TOP_CENTER);

                vBox.getChildren().add(endLabel);
                vBox.setPrefSize(250, 250);
                vBox.setStyle("-fx-background-color: #ffe0b3;");
                vBox.setAlignment(Pos.CENTER);

                HBox buttonsBox = new HBox();
                buttonsBox.setSpacing(5);

                Button newButton = getSmallButton();
                newButton.setText("New");
                Button homeButton = getSmallButton();
                homeButton.setText("Home");


                homeButton.setOnAction(arg -> {
                    primaryStage.getScene().setRoot(generateMenuVBox());
                    dialogue.close();
                });

                newButton.setOnAction(arg -> {
                    primaryStage.getScene().setRoot(boardPane = generateBoardPane());
                    dialogue.close();
                });

                buttonsBox.getChildren().addAll(homeButton, newButton);
                vBox.getChildren().add(buttonsBox);
                buttonsBox.setAlignment(Pos.CENTER);
                buttonsBox.setTranslateX(-20);
                dialogue.setScene(new Scene(vBox));
                dialogue.setTitle("Game Over!");
                dialogue.show();
            }
        });
    }

    public void performRotation() throws InterruptedException {
        controller.getView().flipBoardImages();
        if (rotationAnimationOn) {
            RotateTransition rt = new RotateTransition(Duration.millis(500), controller.getView());
            rt.setByAngle(180);
            rt.setCycleCount(1);
            rt.setInterpolator(Interpolator.LINEAR);
            rt.play();
        } else {
            sleep(100);
            Rotate rt = new Rotate(180, controller.getView().getWidth() / 2, controller.getView().getHeight() / 2);
            controller.getView().getTransforms().addAll(rt);

        }

        rotationCounter++;
    }

    public void performRotationIfValid() throws InterruptedException {
        if (rotationOn && controller.getGame().isRunning.get()) {
            if ((rotationCounter % 2 == 0 && controller.getGame().getMoveCounterProperty().getValue() % 2 == 0) ||
                    (rotationCounter % 2 != 0 && controller.getGame().getMoveCounterProperty().getValue() % 2 != 0)) {
                return;
            } else {
                performRotation();
            }

        }
    }

    private BorderPane generateCustomizePane() {
        customizerPane = new BorderPane();
        CustomizerController customizeController = new CustomizerController();
        customizerPane.setCenter(customizeController.getCView());

        Menu[] menuItems = new Menu[2];
        menuItems[0] = new Menu("File");
        menuItems[1] = new Menu("Help");

        MenuItem help = new MenuItem("Help");
        help.setOnAction(e -> {
            Pane helpScreen = generateHelpPane();
            final Stage dialogue = new Stage();
            dialogue.setResizable(false);
            dialogue.setTitle("Customizer - Help");
            dialogue.initModality(Modality.APPLICATION_MODAL);
            dialogue.initOwner(primaryStage);
            dialogue.setScene(new Scene(helpScreen));
            dialogue.show();
        });


        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            Pane aboutScreen = generateAboutPane();
            final Stage dialogue = new Stage();
            dialogue.setResizable(false);
            dialogue.initModality(Modality.APPLICATION_MODAL);
            dialogue.initOwner(primaryStage);
            dialogue.setScene(new Scene(aboutScreen));
            dialogue.show();
        });
        menuItems[1].getItems().addAll(help, about);


        MenuItem[] menuItemsFile = new MenuItem[4];

        menuItemsFile[0] = new MenuItem("Save");
        menuItemsFile[0].setOnAction(e -> {
            currentSave = GameLoader.saveGame(
                    customizeController.getCModel(),
                    customizeController.getGame());
            String saveFileName = generateFileName(true);
            File saveToFile = new File(saveFileName);
            saveToCSVFile(currentSave, saveToFile);
            String absFilePathName = saveToFile.getAbsolutePath();
        });
        menuItemsFile[1] = new MenuItem("Save As...");
        menuItemsFile[1].setOnAction(e -> {
            currentSave = GameLoader.saveGame(
                    customizeController.getCModel(),
                    customizeController.getGame());
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a Directory To Save Your Current Game...");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(".csv Files", "*.csv"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            if (selectedFile != null) {
                saveToCSVFile(currentSave, selectedFile);
            }
        });
        menuItemsFile[2] = new MenuItem("Exit Customizer");
        menuItemsFile[2].setOnAction(e -> {
            primaryStage.getScene().setRoot(generateMenuVBox());
        });

        menuItemsFile[3] = new MenuItem("Exit Application");
        menuItemsFile[3].setOnAction(e -> System.exit(0));
        menuItems[0].getItems().addAll(menuItemsFile);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuItems);
        customizerPane.setTop(menuBar);
        return customizerPane;
    }

    private Pane generateHelpPane() {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #ffe0b3");
        Button backButton = getSmallButton();
        backButton.setText("\uD83E\uDC78");
        backButton.setOnAction(e -> {
            ((Node) (e.getSource())).getScene().getWindow().hide();
        });
        Label label = new Label(getTextStyle());
        label.setText("Help");


        Text helpInfo = new Text();

        if(boardPane != null && primaryStage.getScene() == boardPane.getScene()){
            helpInfo.setText(boardHelpInfo);
            helpInfo.setTranslateX(50);
        }
        else{
            helpInfo.setText(customizerHelpInfo);
            helpInfo.setTranslateX(80);

        }

        helpInfo.setStyle(getHelpTextStyle());
        helpInfo.setTranslateY(90);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(helpInfo);
        scrollPane.setPrefHeight(SCREEN_HEIGHT*0.8);
        scrollPane.setPrefWidth(SCREEN_WIDTH);
        scrollPane.setFitToWidth(true);
        scrollPane.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        scrollPane.setStyle("-fx-background: #ffe0b3;");

        helpInfo.setTextAlignment(TextAlignment.CENTER);


        pane.getChildren().addAll(scrollPane, backButton);
        return pane;
    }

    private String generateFileName(boolean isCustom) {
        String fileName = "saves/OnChess_";
        if (whitePlayerName.length() > 0 || blackPlayerName.length() > 0) {
            fileName += whitePlayerName + "_vs_" + blackPlayerName + "_";
        }
        if (isCustom) fileName += "Custom_";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = df.format(new Date());
        return fileName + timestamp + ".csv";
    }


    private BorderPane generateBoardPane() {
        primaryStage.setTitle("Play - OnChess");
        BorderPane gamePane = new BorderPane();
        controller = new ChessBoardController(darkPieceColor, lightPieceColor);
        gamePane.setCenter(controller.getView());
        setupRotation();

        Menu[] menuItems = new Menu[3];
        menuItems[0] = new Menu("File");
        menuItems[1] = new Menu("Options");
        menuItems[2] = new Menu("Help");

        MenuItem[] menuItemsFile = new MenuItem[7];
        menuItemsFile[0] = new MenuItem("New Game");
        menuItemsFile[0].setOnAction(e ->
                primaryStage.getScene().setRoot(
                        boardPane = generateBoardPane()));

        menuItemsFile[1] = new MenuItem("Open...");
        menuItemsFile[1].setOnAction(e -> {
            currentSave = null;
            loadFileChooserOpen();
            if (currentSave == null)
                return;
            try {
                loadGame(currentSave, controller);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

        });


        menuItemsFile[2] = new MenuItem("Save");
        menuItemsFile[2].setOnAction(e -> {
                    currentSave = GameLoader.saveGame(
                            controller.getGame().getModel(),
                            controller.getGame());
                    String saveFileName = generateFileName(false);
                    File saveToFile = new File(saveFileName);
                    saveToCSVFile(currentSave, saveToFile);
                    String absFilePathName = saveToFile.getAbsolutePath();

                }
        );

        menuItemsFile[3] = new MenuItem("Save As...");
        menuItemsFile[3].setOnAction(e -> {
            currentSave = GameLoader.saveGame(
                    controller.getGame().getModel(),
                    controller.getGame());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a Directory To Save Your Current Game...");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(".csv Files", "*.csv"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            if (selectedFile != null) {
                saveToCSVFile(currentSave, selectedFile);
            }
        });

        menuItemsFile[4] = new MenuItem("Load Recent");
        menuItemsFile[4].setOnAction(e -> {
            try {
                loadGame(currentSave, controller);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }


        });
        menuItemsFile[5] = new MenuItem("Exit Game");
        menuItemsFile[5].setOnAction(e -> {
            primaryStage.getScene().setRoot(generateMenuVBox());
        });
        menuItemsFile[6] = new MenuItem("Exit Application");
        menuItemsFile[6].setOnAction(e -> System.exit(0));
        menuItems[0].getItems().addAll(menuItemsFile);


        MenuItem flipBoard = new MenuItem("Flip Board \uD83D\uDDD8");
        flipBoard.setOnAction(e -> {
            try {
                performRotation();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        MenuItem settings = new MenuItem("Settings...");
        settings.setOnAction(e -> {
            Pane setScreen = generateSettingsPane();

            final Stage dialogue = new Stage();
            dialogue.setResizable(false);
            dialogue.initModality(Modality.APPLICATION_MODAL);
            dialogue.initOwner(primaryStage);
            dialogue.setScene(new Scene(setScreen));
            dialogue.show();
        });

        menuItems[1].getItems().addAll(flipBoard, settings);


        MenuItem help = new MenuItem("Help");
        help.setOnAction(e -> {
            Pane helpScreen = generateHelpPane();
            final Stage dialogue = new Stage();
            dialogue.setResizable(false);
            dialogue.setTitle("Play - Help");
            dialogue.initModality(Modality.APPLICATION_MODAL);
            dialogue.initOwner(primaryStage);
            dialogue.setScene(new Scene(helpScreen));
            dialogue.show();
        });
        menuItems[2].getItems().add(help);

        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            Pane aboutScreen = generateAboutPane();
            final Stage dialogue = new Stage();
            dialogue.setResizable(false);
            dialogue.initModality(Modality.APPLICATION_MODAL);
            dialogue.initOwner(primaryStage);
            dialogue.setScene(new Scene(aboutScreen));
            dialogue.show();
        });
        menuItems[2].getItems().add(about);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuItems);
        gamePane.setTop(menuBar);
        return gamePane;
    }

    private boolean loadFileChooserOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a Previously Saved Game");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(".csv Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            try {
                currentSave = Files.readAllBytes(selectedFile.toPath());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return true;
        } else {
            return false;
        }

    }

    private void saveToCSVFile(byte[] content, File fileToSave) {
        try {
            FileOutputStream fos = new FileOutputStream(fileToSave);
            fos.write(content);
            fos.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private AnchorPane generateSettingsPane() {
        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: #ffe0b3");
        Label darkLabel = new Label("DARK SQUARES COLOR: ");
        Label lightLabel = new Label("LIGHT SQUARES COLOR: ");
        darkLabel.setStyle("-fx-font-size:15;\n"
                + "-fx-font-weight: bold;");
        lightLabel.setStyle("-fx-font-size:15;\n"
                + "-fx-font-weight: bold;");


        Button lightButton = new Button("✎");
        Button darkButton = new Button("✎");
        lightButton.setPrefSize(60, 60);
        darkButton.setPrefSize(60, 60);
        lightButton.setStyle(getColorStyle(lightPieceColor));
        darkButton.setStyle(getColorStyle(darkPieceColor));

        VBox darkBox = new VBox(darkLabel, darkButton);
        VBox lightBox = new VBox(lightLabel, lightButton);
        darkBox.setSpacing(20);
        lightBox.setSpacing(20);


        lightButton.setOnAction(e -> {
            Color lightColor = JColorChooser.showDialog(null, "Choose The Color of Light Squares", Color.WHITE);
            if (lightColor != null) {
                lightPieceColor = getHexColor(lightColor);
                lightButton.setStyle(getColorStyle(lightPieceColor));
            }
        });

        darkButton.setOnAction(e -> {
            Color darkColor = JColorChooser.showDialog(null, "Choose The Color of Dark Squares", Color.BLACK);
            if (darkColor != null) {
                darkPieceColor = getHexColor(darkColor);
                darkButton.setStyle(getColorStyle(darkPieceColor));
            }
        });


        VBox vBox = new VBox();
        vBox.setSpacing(20);

        VBox colors = new VBox();
        colors.setSpacing(30);
        colors.setPadding(new Insets(90, 50, 20, 50));
        colors.getChildren().addAll(darkBox, lightBox);


        Label rotationLabel = new Label("ROTATE BOARD AFTER EACH TURN");
        Label rotationAnimationLabel = new Label("ENABLE ROTATION ANIMATION");
        rotationLabel.setStyle("-fx-font-size:15;\n"
                + "-fx-font-weight: bold;");
        rotationAnimationLabel.setStyle("-fx-font-size:15;\n"
                + "-fx-font-weight: bold;");
        RadioButton rotationButton = new RadioButton();
        RadioButton rotationAnimationButton = new RadioButton();
        rotationButton.setSelected(rotationOn);
        rotationButton.setOnAction(arg -> {
            rotationOn = !rotationOn;
            if (rotationOn) {
                rotationAnimationButton.setDisable(false);
                if ((controller.getGame().getMoveCounterProperty().get() + rotationCounter) % 2 != 0) {
                    try {
                        performRotation();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        rotationAnimationButton.setSelected(rotationAnimationOn);
        rotationAnimationButton.setOnAction(arg -> {
            rotationAnimationOn = !rotationAnimationOn;
        });


        rotationButton.setAlignment(Pos.BOTTOM_RIGHT);
        rotationAnimationButton.setAlignment(Pos.BOTTOM_RIGHT);

        HBox rotationBox = new HBox(rotationButton, rotationLabel);
        HBox rotationAnimationBox = new HBox(rotationAnimationButton, rotationAnimationLabel);
        rotationBox.setSpacing(10);
        rotationAnimationBox.setSpacing(10);

        VBox radioBox = new VBox(rotationBox, rotationAnimationBox);
        radioBox.setSpacing(20);
        radioBox.setPadding(new Insets(0, 50, 0, 50));


        Button backButton = getSmallButton();
        backButton.setText("\uD83E\uDC78");
        backButton.setOnAction(e -> {
            if (boardPane != null && primaryStage.getScene() == boardPane.getScene()) {
                ((Node) (e.getSource())).getScene().getWindow().hide();
                controller.getView().changeSquareColors(darkPieceColor, lightPieceColor);
                rotationButton.setSelected(rotationOn);
                rotationAnimationButton.setSelected(rotationAnimationOn);
            } else {
                backButton.getScene().setRoot(generateMenuVBox());
            }

        });

        Label whitePlayerLabel = new Label("NAME OF PLAYER WHITE: ");
        Label blackPlayerLabel = new Label("NAME OF PLAYER BLACK: ");
        whitePlayerLabel.setStyle("-fx-font-size:15;\n"
                + "-fx-font-weight: bold;");
        blackPlayerLabel.setStyle("-fx-font-size:15;\n"
                + "-fx-font-weight: bold;");

        javafx.scene.control.TextField whitePlayerTF = new javafx.scene.control.TextField(whitePlayerName);
        javafx.scene.control.TextField blackPlayerTF = new javafx.scene.control.TextField(blackPlayerName);
        whitePlayerTF.textProperty().addListener(arg ->
                whitePlayerName = whitePlayerTF.getText());
        blackPlayerTF.textProperty().addListener(arg ->
                blackPlayerName = blackPlayerTF.getText());
        HBox whitePlayerBox = new HBox(whitePlayerLabel, whitePlayerTF);
        HBox blackPlayerBox = new HBox(blackPlayerLabel, blackPlayerTF);
        whitePlayerBox.setSpacing(10);
        blackPlayerBox.setSpacing(10);

        VBox playersBox = new VBox(whitePlayerBox, blackPlayerBox);
        playersBox.setSpacing(10);
        playersBox.setPadding(new Insets(10, 50, 10, 50));

        Label restoreLabel = new Label("RESTORE DEFAULT SETTINGS");
        restoreLabel.setStyle("-fx-font-size:15;\n"
                + "-fx-font-weight: bold;");
        Button restoreButton = getSmallButton();
        restoreButton.setTranslateY(15);
        restoreButton.setTranslateX(0);
        restoreButton.setPrefSize(140, 10);
        restoreButton.setText("Restore Defaults");
        restoreButton.setOnAction(e -> {
            setDefaultSettings();
            restoreButton.getScene().setRoot(generateSettingsPane());
        });
        VBox restoreBox = new VBox(restoreLabel, restoreButton);
        restoreBox.setPadding(new Insets(0, 50, 50, 50));

        vBox.getChildren().addAll(colors, radioBox, playersBox, restoreBox);
        root.getChildren().addAll(vBox, backButton);
        root.setTopAnchor(backButton, 5.0);
        return root;
    }

    private String getColorStyle(String color) {
        return "-fx-background-color: " + color + ";\n"
                + "-fx-font-size: 20;\n"
                + "-fx-border-insets: -5; \n"
                + "-fx-border-radius: 5;\n"
                + "-fx-border-style: solid inside;\n"
                + "-fx-border-width: 2;\n"
                + "-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.73) , 4,0,0,1 );";
    }

    private String getHexColor(Color color) {
        String r = Integer.toHexString(color.getRed());
        String g = Integer.toHexString(color.getGreen());
        String b = Integer.toHexString(color.getBlue());
        if (r.length() == 1) r = "0" + r;
        if (g.length() == 1) g = "0" + g;
        if (b.length() == 1) b = "0" + b;
        return "#" + r + g + b;
    }

    private Button getSmallButton() {
        Button button = new Button();
        button.setTranslateX(20);
        button.setTranslateY(20);
        button.setStyle(getButtonStyle());
        button.setPrefSize(70, 20);
        return button;
    }

    private Pane generateAboutPane() {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #ffe0b3");
        Button backButton = getSmallButton();
        backButton.setText("\uD83E\uDC78");
        backButton.setOnAction(e -> {
            if ((boardPane != null && primaryStage.getScene() == boardPane.getScene()) ||
                    (customizerPane != null && primaryStage.getScene() == customizerPane.getScene())) {
                ((Node) (e.getSource())).getScene().getWindow().hide();
            } else {
                backButton.getScene().setRoot(generateMenuVBox());
            }
        });

        Label aboutInfo = new Label("OnChess: Customizable Multiplayer Chess Program\n" +
                "Created By Jun Lee, All Rights Reserved © 2022");
        aboutInfo.setStyle(getTextStyle());

        aboutInfo.setTextAlignment(TextAlignment.CENTER);
        aboutInfo.setAlignment(Pos.BASELINE_CENTER);
        aboutInfo.setTranslateX(150);
        aboutInfo.setTranslateY(15);

        String imageURI = "images/logo.png";
        Image image = new Image(Interface.class.getResourceAsStream(imageURI));
        ImageView imageView = new ImageView(image);

        pane.getChildren().addAll(aboutInfo, backButton, imageView);
        return pane;
    }

    private void initiateStartScreen() {
        try {

            Pane splashPane = FXMLLoader.load(getClass().getResource("/com/example/aoop_project/games/chess/splash.fxml"));
            splashPane.setStyle("-fx-background-color: #ffe0b3");
            primaryStage.setScene(new Scene(splashPane, SCREEN_WIDTH, SCREEN_HEIGHT));
            primaryStage.show();


            FadeTransition transitionIn = new FadeTransition(Duration.seconds(1), splashPane);
            transitionIn.setFromValue(0);
            transitionIn.setToValue(1);
            transitionIn.setCycleCount(1);

            FadeTransition stillTransition = new FadeTransition(Duration.seconds(2), splashPane);
            stillTransition.setFromValue(1);
            stillTransition.setToValue(1);
            stillTransition.setCycleCount(1);

            FadeTransition transitionOut = new FadeTransition(Duration.seconds(1), splashPane);
            transitionOut.setFromValue(1);
            transitionOut.setToValue(0);
            transitionOut.setCycleCount(1);

            transitionIn.play();

            transitionIn.setOnFinished((e) -> {
                stillTransition.play();
            });
            stillTransition.setOnFinished((e) -> {
                transitionOut.play();
            });

            transitionOut.setOnFinished((e) -> {
                StackPane root = new StackPane();
                root.setStyle("-fx-background-color: #ffe0b3");
                VBox menu = generateMenuVBox();
                root.getChildren().add(menu);
                Scene homeScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
                primaryStage.setScene(homeScene);
                primaryStage.show();

            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setDefaultSettings() {
        whitePlayerName = "";
        blackPlayerName = "";
        darkPieceColor = DEFAULT_DARK_COLOR;
        lightPieceColor = DEFAULT_LIGHT_COLOR;
        rotationOn = true;
        rotationAnimationOn = false;
    }

    private String customizerHelpInfo = "Creating/Changing/Deleting a Piece:\n" +
            "\n" +
            "A click (left-click for white and right-click for black) will create or transition\n" +
            "the piece in the clicked square using the following sequence:\n" +
            "\n" +
            "Pawn->Knight->Bishop->Rook->Queen \n" +
            "\n" +
            "For instance, right clicking on an empty square will create a black pawn. \n" +
            "Clicking the same square again will transition that pawn into a Knight, \n" +
            "and the piece's color will depend on whether or not the click was a \n" +
            "left-click (white) or a right-click (black).\n" +
            "\n" +
            "The king may not be created, nor deleted. To relocate the king, save the \n" +
            "customized file and re-open it in the PLAY screen, then perform the desired \n" +
            "moves with the king and save the game again.\n" +
            "\n" +
            "\n" +
            "Saving The File:\n" +
            "\n" +
            "When finished, click File -> Save to download your customized game file. \n" +
            "By default, the file will be saved within the \"saves\" folder, under a file name \n" +
            "with the timestamp of the exact date/time it was saved.\n\n";

    private String boardHelpInfo = "Moving The Pieces On The Board:\n" +
            "\n" +
            "Method 1) \n" +
            "Click-Click – Click over a chess piece you wish to move and click again on \n" +
            "one of the highlighted squares.\n" +
            "\n" +
            "Method 2) \n" +
            "Click-and-Drag – Click over a chess piece you wish to move, and without letting \n" +
            "go, move the cursor to one of the highlighted squares. Once your cursor reaches\n" +
            " the destination of your choice (one of the highlighted squares), you may let go \n" +
            "to execute the move. Prior to a release, the field of the destination square \n" +
            "should be populated with a miniature transparent “ghost” piece of the piece \n" +
            "being moved. \n" +
            "\n" +
            "After you move a piece, the square on which you moved will be highlighted with \n" +
            "a light-blue color, to let the opponent player which piece on the board you moved \n" +
            "and where it was moved to. \n" +
            "\n" +
            "\n" +
            "Pieces:\n" +
            "\n" +
            "Pawn – The pawn may move only in the forward direction to an empty spot, \n" +
            "one square at a time (except during the first move, which the game allows \n" +
            "for it to move two squares). It captures pieces (i.e., deletes an opponent’s \n" +
            "piece off of the board) by moving  diagonally in the forward direction, which is \n" +
            "exclusively allowed when there already exists a piece occupying by the square. \n" +
            "When the pawn reaches the top of the board, it is immediately promoted to a \n" +
            "queen.\n" +
            "\n" +
            "Bishop – The bishop piece can freely move across the board in the diagonal \n" +
            "direction, so long as a piece does not block its path, whether it be a piece owned \n" +
            "by the opponent, or a piece owned by the player. If the piece that is blocking the \n" +
            "bishop’s path is the opponent’s piece, then the bishop can capture that piece, \n" +
            "replacing the occupied space with itself.\n" +
            "\n" +
            "Knight – The knight is the only piece in the game that is not affected by being \n" +
            "“blocked” by other pieces occupying the path in which it moves, and the only \n" +
            "square it cannot move to is a square owned by a player’s piece. The knight moves \n" +
            "one square diagonally and one square straight in any direction.\n" +
            "\n" +
            "Rook – The rook, located in all corners of the board, may move horizontally or \n" +
            "vertically across the board. Its blockage and piece overtaking concept is the same \n" +
            "as that of the bishop, and the only difference is that it moves up, down, left, or right.\n" +
            "\n" +
            "The Queen – Like the king, each side starts with only a single queen on the board. \n" +
            "As the most powerful piece on the board, this piece’s abilities combine that of the \n" +
            "bishop’s and the rook’s, allowing it to move freely in the diagonal, horizontal and \n" +
            "vertical directions. Its blockage and piece overtaking concept is also the same as \n" +
            "both the bishop and the rook.\n" +
            "\n" +
            "The King – As the most crucial  piece in the game, the survival of the king decides \n" +
            "whether or not a game plays on or terminates as a loss. However, it is not a very \n" +
            "mobile piece, as it can only move (and overtake a piece if the moved square is \n" +
            "occupied by an unprotected piece) one square at a time, albeit in any direction. \n" +
            "\n" +
            "\n" +
            "Special Cases: \n" +
            "\n" +
            "Castling: As the only time two pieces can be displaced in one move, castling \n" +
            "allows the king to move two squares to the right or left and the rook moves \n" +
            "directly to the other side of the king. The only time one can castle is if neither \n" +
            "the rook or king have moved and there are no pieces in the castling path. \n" +
            "Castling is not permitted when the king is checked, nor can one castle into \n" +
            "check or through check.\n" +
            "\n" +
            "\n" +
            "How To Win:\n" +
            "\n" +
            "The game is won by checkmating the opponent’s king, which may be done by \n" +
            "threatening to take the square occupied by it (a.k.a. “check”) on the next move. \n" +
            "Once the king has nowhere to move after a “check,” the game is over!\n" +
            "\n\n\n\n\n";
}
