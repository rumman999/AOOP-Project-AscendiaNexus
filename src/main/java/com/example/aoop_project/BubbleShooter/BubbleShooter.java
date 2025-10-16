package com.example.aoop_project.BubbleShooter;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class BubbleShooter extends Application {

    static final int WIDTH = 600;
    static final int HEIGHT = 700;
    static final int ROWS = 8;
    static final int COLS = 10;
    static final int BUBBLE_RADIUS = 20;
    static final int GRID_SPACING = 45;
    static final int SHOOTER_X = WIDTH / 2;
    static final int SHOOTER_Y = 650;
    static final int ROW_OFFSET = GRID_SPACING / 2;

    Canvas canvas;
    GraphicsContext gc;

    Grid grid;
    Shooter shooter;
    FlyingBubble flyingBubble;

    double mouseX, mouseY;
    boolean gameOver = false;
    boolean gameWon = false;
    int score = 0;
    int shotsFired = 0;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Bubble Shooter");
        Group root = new Group();
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        initGame();

        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
            shooter.aim(mouseX, mouseY);
        });

        scene.setOnMouseClicked(e -> {
            if (!gameOver && flyingBubble == null) {
                flyingBubble = shooter.shoot();
                shooter.generateNextBubble();
                shotsFired++;
                if (shotsFired % 8 == 0) {
                    grid.addNewRow();
                }
            } else if (gameOver && e.getButton().name().equals("PRIMARY")) {
                initGame();
            }
        });

        scene.setOnKeyPressed(e -> {
            if (gameOver && e.getCode() == KeyCode.R) initGame();
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        timer.start();

        stage.show();
    }

    void initGame() {
        grid = new Grid(ROWS, COLS);
        shooter = new Shooter();
        flyingBubble = null;
        score = 0;
        shotsFired = 0;
        gameOver = false;
        gameWon = false;
    }

    void update() {
        if (flyingBubble != null && !gameOver) {
            flyingBubble.update();
            if (flyingBubble.checkWallCollision()) flyingBubble.checkWallCollision();
            Bubble collided = flyingBubble.checkGridCollision(grid);
            if (collided != null || flyingBubble.y <= 50) {
                // Snap to grid
                int[] pos = grid.snapToGrid(flyingBubble.x, flyingBubble.y);
                flyingBubble.x = pos[0];
                flyingBubble.y = pos[1];
                grid.addBubble(flyingBubble);
                List<Bubble> matched = grid.findMatches(flyingBubble.row, flyingBubble.col, flyingBubble.color);
                if (matched.size() >= 3) {
                    for (Bubble b : matched) grid.removeBubble(b.row, b.col);
                    score += matched.size() * 10;
                    List<Bubble> floating = grid.findFloatingBubbles();
                    for (Bubble fb : floating) grid.removeBubble(fb.row, fb.col);
                    score += floating.size() * 20;
                }
                flyingBubble = null;

                if (grid.checkGameOver()) {
                    gameOver = true;
                } else if (grid.checkWin()) {
                    gameOver = true;
                    gameWon = true;
                }
            }
        }
    }

    void render() {
        // Background
        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        grid.draw(gc);
        shooter.draw(gc);
        if (flyingBubble != null) flyingBubble.draw(gc);

        // Score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(24));
        gc.fillText("Score: " + score, 20, 30);

        // Shots until new row
        int shotsUntilRow = 8 - (shotsFired % 8);
        gc.setFont(new Font(20));
        gc.fillText("Shots until new row: " + shotsUntilRow, 400, 30);

        // Game over or win message
        if (gameOver) {
            gc.setFont(new Font(50));
            gc.setFill(gameWon ? Color.LIME : Color.RED);
            gc.fillText(gameWon ? "YOU WIN!" : "GAME OVER", 150, HEIGHT / 2);
            gc.setFont(new Font(24));
            gc.setFill(Color.WHITE);
            gc.fillText("Press R to Restart", 200, HEIGHT / 2 + 50);
        }
    }

    // --- Nested Classes ---

    class Bubble {
        double x, y;
        Color color;
        int row, col;
        double radius;

        Bubble(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.radius = BUBBLE_RADIUS;
        }

        void draw(GraphicsContext gc) {
            gc.setFill(color);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
        }

        boolean contains(double mx, double my) {
            double dx = mx - x;
            double dy = my - y;
            return dx * dx + dy * dy <= radius * radius;
        }
    }

    class Grid {
        Bubble[][] bubbles;
        int rows, cols;
        Random rand = new Random();
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE};

        Grid(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
            bubbles = new Bubble[rows][cols];
            initGrid();
        }

        void initGrid() {
            for (int r = 0; r < rows; r++) {
                if (r < 5) { // top 5 rows random
                    for (int c = 0; c < cols; c++) {
                        double x = 50 + c * GRID_SPACING + (r % 2 == 0 ? ROW_OFFSET : 0);
                        double y = 50 + r * GRID_SPACING;
                        bubbles[r][c] = new Bubble(x, y, colors[rand.nextInt(colors.length)]);
                        bubbles[r][c].row = r;
                        bubbles[r][c].col = c;
                    }
                }
            }
        }

        void draw(GraphicsContext gc) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (bubbles[r][c] != null) bubbles[r][c].draw(gc);
                }
            }
        }

        void addBubble(FlyingBubble fb) {
            bubbles[fb.row][fb.col] = new Bubble(fb.x, fb.y, fb.color);
            bubbles[fb.row][fb.col].row = fb.row;
            bubbles[fb.row][fb.col].col = fb.col;
        }

        void removeBubble(int row, int col) {
            bubbles[row][col] = null;
        }

        Bubble getBubbleAt(int row, int col) {
            if (row < 0 || row >= rows || col < 0 || col >= cols) return null;
            return bubbles[row][col];
        }

        int[] snapToGrid(double x, double y) {
            int row = (int) ((y - 50 + GRID_SPACING / 2) / GRID_SPACING);
            row = Math.min(row, rows - 1);
            double rowOffset = row % 2 == 0 ? ROW_OFFSET : 0;
            int col = (int) ((x - 50 - rowOffset + GRID_SPACING / 2) / GRID_SPACING);
            col = Math.min(Math.max(col, 0), cols - 1);
            double snapX = 50 + col * GRID_SPACING + rowOffset;
            double snapY = 50 + row * GRID_SPACING;
            return new int[]{(int) snapX, (int) snapY, row, col};
        }

        List<Bubble> findMatches(int row, int col, Color color) {
            List<Bubble> matched = new ArrayList<>();
            boolean[][] visited = new boolean[rows][cols];
            dfs(row, col, color, matched, visited);
            return matched;
        }

        void dfs(int r, int c, Color color, List<Bubble> matched, boolean[][] visited) {
            if (r < 0 || r >= rows || c < 0 || c >= cols) return;
            if (visited[r][c]) return;
            Bubble b = bubbles[r][c];
            if (b == null || !b.color.equals(color)) return;
            visited[r][c] = true;
            matched.add(b);
            int[][] neighbors = (r % 2 == 0) ?
                    new int[][]{{-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 0}} :
                    new int[][]{{-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1}};
            for (int[] n : neighbors) dfs(r + n[0], c + n[1], color, matched, visited);
        }

        List<Bubble> findFloatingBubbles() {
            boolean[][] connected = new boolean[rows][cols];
            for (int c = 0; c < cols; c++) {
                markConnected(0, c, connected);
            }
            List<Bubble> floating = new ArrayList<>();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (bubbles[r][c] != null && !connected[r][c]) floating.add(bubbles[r][c]);
                }
            }
            return floating;
        }

        void markConnected(int r, int c, boolean[][] connected) {
            if (r < 0 || r >= rows || c < 0 || c >= cols) return;
            if (connected[r][c]) return;
            Bubble b = bubbles[r][c];
            if (b == null) return;
            connected[r][c] = true;
            int[][] neighbors = (r % 2 == 0) ?
                    new int[][]{{-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 0}} :
                    new int[][]{{-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1}};
            for (int[] n : neighbors) markConnected(r + n[0], c + n[1], connected);
        }

        void addNewRow() {
            for (int r = rows - 2; r >= 0; r--) {
                for (int c = 0; c < cols; c++) {
                    bubbles[r + 1][c] = bubbles[r][c];
                    if (bubbles[r + 1][c] != null) bubbles[r + 1][c].row = r + 1;
                }
            }
            for (int c = 0; c < cols; c++) {
                double x = 50 + c * GRID_SPACING + ROW_OFFSET;
                double y = 50;
                bubbles[0][c] = new Bubble(x, y, colors[rand.nextInt(colors.length)]);
                bubbles[0][c].row = 0;
                bubbles[0][c].col = c;
            }
        }

        boolean checkGameOver() {
            for (int c = 0; c < cols; c++) {
                if (bubbles[rows - 1][c] != null) return true;
            }
            return false;
        }

        boolean checkWin() {
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    if (bubbles[r][c] != null) return false;
            return true;
        }
    }

    class Shooter {
        double x, y;
        double angle;
        Color currentColor, nextColor;
        Random rand = new Random();

        Shooter() {
            x = SHOOTER_X;
            y = SHOOTER_Y;
            generateNextBubble();
            currentColor = nextColor;
            generateNextBubble();
        }

        void generateNextBubble() {
            nextColor = grid.colors[rand.nextInt(grid.colors.length)];
        }

        void aim(double mx, double my) {
            angle = Math.atan2(my - y, mx - x);
        }

        FlyingBubble shoot() {
            FlyingBubble fb = new FlyingBubble(x, y, angle, currentColor);
            currentColor = nextColor;
            generateNextBubble();
            return fb;
        }

        void draw(GraphicsContext gc) {
            // Shooter bubble
            gc.setFill(currentColor);
            gc.fillOval(x - BUBBLE_RADIUS, y - BUBBLE_RADIUS, BUBBLE_RADIUS * 2, BUBBLE_RADIUS * 2);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(x - BUBBLE_RADIUS, y - BUBBLE_RADIUS, BUBBLE_RADIUS * 2, BUBBLE_RADIUS * 2);

            // Next bubble preview
            gc.setFill(nextColor);
            gc.fillOval(x - 12.5, y + 30, 25, 25);
            gc.strokeOval(x - 12.5, y + 30, 25, 25);

            // Aiming line
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.setLineDashes(10);
            gc.strokeLine(x, y, mouseX, mouseY);
            gc.setLineDashes(0);
        }
    }

    class FlyingBubble extends Bubble {
        double vx, vy;

        FlyingBubble(double x, double y, double angle, Color color) {
            super(x, y, color);
            vx = Math.cos(angle) * 12;
            vy = Math.sin(angle) * 12;
        }

        void update() {
            x += vx;
            y += vy;
        }

        boolean checkWallCollision() {
            boolean collided = false;
            if (x < 30) {
                x = 30;
                vx = -vx;
                collided = true;
            } else if (x > WIDTH - 30) {
                x = WIDTH - 30;
                vx = -vx;
                collided = true;
            }
            return collided;
        }

        Bubble checkGridCollision(Grid grid) {
            for (int r = 0; r < grid.rows; r++) {
                for (int c = 0; c < grid.cols; c++) {
                    Bubble b = grid.bubbles[r][c];
                    if (b != null) {
                        double dx = x - b.x;
                        double dy = y - b.y;
                        if (Math.sqrt(dx * dx + dy * dy) < BUBBLE_RADIUS * 2) {
                            int[] pos = grid.snapToGrid(x, y);
                            this.row = pos[2];
                            this.col = pos[3];
                            return b;
                        }
                    }
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
