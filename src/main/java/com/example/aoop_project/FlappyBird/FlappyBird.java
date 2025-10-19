package com.example.aoop_project.FlappyBird;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.net.URL;

public class FlappyBird extends Application {

    // Window dimensions
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    // Bird & Pipe constants
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -10;
    private static final int BIRD_SIZE = 25;
    private static final int PIPE_WIDTH = 60;
    private static final int PIPE_GAP = 150;
    private static final int PIPE_SPEED = 3;

    private Pane root;
    private Bird bird;
    private ArrayList<Pipe> pipes = new ArrayList<>();
    private Random random = new Random();
    private int score = 0;
    private boolean gameOver = false;

    private Text scoreText;
    private Text gameOverText;
    private Text restartText;
    private Text instructionsText;

    // --- Singleton stage and BGM ---
    private static Stage flappyBirdStage;
    private AudioClip bgm;

    @Override
    public void start(Stage stage) {
        // Ensure single instance
        if (flappyBirdStage != null) {
            flappyBirdStage.toFront();
            return;
        }
        flappyBirdStage = stage;

        // Stop music when window is closed
        stage.setOnHiding(e -> {
            if (bgm != null) {
                bgm.stop();  // Stop music
                bgm = null;  // Dereference so it can be garbage collected
            }
            flappyBirdStage = null;
        });

        root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB, #1E90FF);"); // nicer sky gradient

        initGameObjects();

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                if (!gameOver) {
                    bird.jump();
                } else {
                    resetGame();
                }
            }
        });

        stage.setScene(scene);
        stage.setTitle("Flappy Bird");
        stage.setResizable(false);
        stage.show();

        // Load background music
        try {
            URL resource = getClass().getResource("/com/example/aoop_project/Music/GameMusic.mp3");
            if (resource != null) {
                bgm = new AudioClip(resource.toString());
                bgm.setCycleCount(AudioClip.INDEFINITE);
                bgm.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        timer.start();
    }

    private void initGameObjects() {
        // Bird
        bird = new Bird(100, 300);
        root.getChildren().add(bird.shape);

        // Initial Pipe
        spawnPipe();

        // Score text
        scoreText = new Text(10, 40, "Score: 0");
        scoreText.setFont(Font.font(28));
        scoreText.setFill(Color.WHITE);
        scoreText.setStroke(Color.BLACK);
        scoreText.setStrokeWidth(1.5);
        root.getChildren().add(scoreText);

        // Instructions on top
        instructionsText = new Text(WIDTH / 2 - 120, 50, "Press SPACE to jump");
        instructionsText.setFont(Font.font(20));
        instructionsText.setFill(Color.WHITE);
        instructionsText.setStroke(Color.BLACK);
        instructionsText.setStrokeWidth(1);
        root.getChildren().add(instructionsText);

        // Game over text
        gameOverText = new Text(WIDTH / 2 - 100, 100, "GAME OVER");
        gameOverText.setFont(Font.font(36));
        gameOverText.setFill(Color.RED);
        gameOverText.setStroke(Color.BLACK);
        gameOverText.setStrokeWidth(2);
        gameOverText.setVisible(false);
        root.getChildren().add(gameOverText);

        // Restart text on top
        restartText = new Text(WIDTH / 2 - 150, 140, "Press SPACE to restart");
        restartText.setFont(Font.font(20));
        restartText.setFill(Color.YELLOW);
        restartText.setStroke(Color.BLACK);
        restartText.setStrokeWidth(1);
        restartText.setVisible(false);
        root.getChildren().add(restartText);
    }

    private void update() {
        if (!gameOver) {
            bird.update();

            Iterator<Pipe> iterator = pipes.iterator();
            while (iterator.hasNext()) {
                Pipe pipe = iterator.next();
                pipe.update();

                if (!pipe.passed && pipe.x + PIPE_WIDTH < bird.x) {
                    score++;
                    pipe.passed = true;
                }

                if (pipe.collidesWith(bird)) {
                    gameOver = true;
                    showGameOver();
                }

                if (pipe.x < -PIPE_WIDTH) {
                    root.getChildren().remove(pipe.topRect);
                    root.getChildren().remove(pipe.bottomRect);
                    iterator.remove();
                }
            }

            if (pipes.isEmpty() || pipes.get(pipes.size() - 1).x <= 200) {
                spawnPipe();
            }

            if (bird.y - BIRD_SIZE / 2 < 0 || bird.y + BIRD_SIZE / 2 > HEIGHT) {
                gameOver = true;
                showGameOver();
            }

            scoreText.setText("Score: " + score);
        }
    }

    private void render() {
        bird.render();
        for (Pipe pipe : pipes) {
            pipe.render();
        }
    }

    private void spawnPipe() {
        int topHeight = 50 + random.nextInt(HEIGHT - PIPE_GAP - 100);
        Pipe pipe = new Pipe(WIDTH, topHeight);
        pipes.add(pipe);
        root.getChildren().addAll(pipe.topRect, pipe.bottomRect);
    }

    private void showGameOver() {
        gameOverText.setVisible(true);
        restartText.setVisible(true);
    }

    private void resetGame() {
        bird.x = 100;
        bird.y = 300;
        bird.velocity = 0;

        for (Pipe pipe : pipes) {
            root.getChildren().remove(pipe.topRect);
            root.getChildren().remove(pipe.bottomRect);
        }
        pipes.clear();
        spawnPipe();

        score = 0;
        gameOver = false;

        gameOverText.setVisible(false);
        restartText.setVisible(false);
    }

    // ===== Nested Classes =====
    private class Bird {
        double x, y;
        double velocity = 0;
        Circle shape;

        Bird(double x, double y) {
            this.x = x;
            this.y = y;
            shape = new Circle(BIRD_SIZE / 2, Color.YELLOW);
            shape.setCenterX(x);
            shape.setCenterY(y);
            shape.setStroke(Color.ORANGE);
            shape.setStrokeWidth(2);
        }

        void jump() {
            velocity = JUMP_STRENGTH;
        }

        void update() {
            velocity += GRAVITY;
            y += velocity;
        }

        void render() {
            shape.setCenterX(x);
            shape.setCenterY(y);
        }
    }

    private class Pipe {
        double x;
        int topHeight;
        boolean passed = false;
        Rectangle topRect, bottomRect;

        Pipe(double x, int topHeight) {
            this.x = x;
            this.topHeight = topHeight;
            topRect = new Rectangle(PIPE_WIDTH, topHeight, Color.DARKGREEN);
            bottomRect = new Rectangle(PIPE_WIDTH, HEIGHT - topHeight - PIPE_GAP, Color.DARKGREEN);
            topRect.setX(x);
            topRect.setY(0);
            bottomRect.setX(x);
            bottomRect.setY(topHeight + PIPE_GAP);
        }

        void update() {
            x -= PIPE_SPEED;
        }

        void render() {
            topRect.setX(x);
            bottomRect.setX(x);
        }

        boolean collidesWith(Bird bird) {
            return bird.x + BIRD_SIZE / 2 > x &&
                    bird.x - BIRD_SIZE / 2 < x + PIPE_WIDTH &&
                    (bird.y - BIRD_SIZE / 2 < topHeight ||
                            bird.y + BIRD_SIZE / 2 > topHeight + PIPE_GAP);
        }
    }

    // ===== Main Method =====
    public static void main(String[] args) {
        launch(args);
    }
}
