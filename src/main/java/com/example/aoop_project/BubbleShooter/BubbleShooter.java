package com.example.aoop_project.BubbleShooter;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class BubbleShooter extends Application {

    private final int WIDTH = 600;
    private final int HEIGHT = 700;

    private GraphicsContext gc;
    private Grid grid;
    private Shooter shooter;
    private FlyingBubble flyingBubble = null;

    private int score = 0;
    private int shots = 0;
    private boolean gameOver = false;
    private boolean win = false;

    private double mouseX = WIDTH / 2.0;
    private double mouseY = HEIGHT / 2.0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        stage.setTitle("Bubble Shooter");
        stage.setScene(scene);
        stage.show();

        initGame();

        scene.setOnMouseMoved(this::handleMouseMove);
        scene.setOnMouseClicked(e -> handleShoot());
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.R && gameOver) initGame();
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        timer.start();
    }

    private void handleMouseMove(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private void initGame() {
        grid = new Grid();
        shooter = new Shooter(WIDTH / 2.0, 650);
        flyingBubble = null;
        score = 0;
        shots = 0;
        gameOver = false;
        win = false;
    }

    private void handleShoot() {
        if (!gameOver && flyingBubble == null) {
            flyingBubble = shooter.shoot(mouseX, mouseY);
            shooter.prepareNextBubble();
            shots++;
        }
    }

    private void update() {
        if (gameOver) return;

        if (flyingBubble != null) {
            flyingBubble.update();
            flyingBubble.checkWallCollision(WIDTH);

            if (grid.checkGridCollision(flyingBubble)) {
                grid.snapBubble(flyingBubble);

                // Find and remove matches
                ArrayList<Bubble> popped = grid.findMatches(flyingBubble.row, flyingBubble.col, flyingBubble.color);
                for (Bubble b : popped) {
                    grid.removeBubble(b);
                }

                // Find and remove floating bubbles
                int floatingCount = grid.findFloatingBubbles();

                // Update score
                score += popped.size() * 10 + floatingCount * 20;

                flyingBubble = null;

                // Add new row every 8 shots
                if (shots % 8 == 0) {
                    grid.addNewRow();
                }

                // Check win/lose conditions
                if (grid.checkWin()) {
                    win = true;
                    gameOver = true;
                }
                if (grid.checkLose(HEIGHT)) {
                    gameOver = true;
                    win = false;
                }
            }
        }
    }

    private void render() {
        // Background gradient
        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw grid bubbles
        grid.draw(gc);

        // Draw trajectory
        if (!gameOver && flyingBubble == null) {
            shooter.drawTrajectory(gc, mouseX, mouseY, WIDTH);
        }

        // Draw shooter
        shooter.draw(gc, mouseX, mouseY);

        // Draw flying bubble
        if (flyingBubble != null) {
            flyingBubble.draw(gc);
        }

        // Draw UI
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(24));
        gc.fillText("Score: " + score, 20, 30);
        gc.setFont(Font.font(20));
        gc.fillText("Next Row In: " + (8 - (shots % 8)), 450, 30);

        if (gameOver) {
            gc.setFont(Font.font(50));
            gc.setFill(win ? Color.GREEN : Color.RED);
            gc.fillText(win ? "YOU WIN!" : "GAME OVER", WIDTH / 2.0 - 150, HEIGHT / 2.0);
            gc.setFont(Font.font(30));
            gc.setFill(Color.WHITE);
            gc.fillText("Press R to Restart", WIDTH / 2.0 - 120, HEIGHT / 2.0 + 50);
        }
    }

    // -------------------- Nested Classes --------------------

    class Bubble {
        double x, y;
        Color color;
        int row, col;
        final double radius = 20;

        Bubble(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        void draw(GraphicsContext gc) {
            gc.setFill(color);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
        }
    }

    class FlyingBubble extends Bubble {
        double vx, vy;

        FlyingBubble(double x, double y, Color color, double angle, double speed) {
            super(x, y, color);
            vx = speed * Math.cos(angle);
            vy = speed * Math.sin(angle);
        }

        void update() {
            x += vx;
            y += vy;
        }

        void checkWallCollision(double width) {
            // Left wall
            if (x - radius <= 0) {
                x = radius;
                vx = Math.abs(vx); // Force positive (move right)
            }
            // Right wall
            if (x + radius >= width) {
                x = width - radius;
                vx = -Math.abs(vx); // Force negative (move left)
            }
        }
    }

    class Shooter {
        double x, y;
        Bubble currentBubble;
        Bubble nextBubble;
        final double speed = 12;

        Shooter(double x, double y) {
            this.x = x;
            this.y = y;
            currentBubble = getNextRandomBubble();
            nextBubble = getNextRandomBubble();
        }

        void draw(GraphicsContext gc, double targetX, double targetY) {
            // Draw shooter triangle pointing to mouse
            double angle = Math.atan2(targetY - y, targetX - x);
            gc.setFill(Color.WHITE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);

            double size = 25;
            double tipX = x + size * Math.cos(angle);
            double tipY = y + size * Math.sin(angle);
            double baseX1 = x + size/2 * Math.cos(angle + Math.PI/2);
            double baseY1 = y + size/2 * Math.sin(angle + Math.PI/2);
            double baseX2 = x + size/2 * Math.cos(angle - Math.PI/2);
            double baseY2 = y + size/2 * Math.sin(angle - Math.PI/2);

            double[] xs = {tipX, baseX1, baseX2};
            double[] ys = {tipY, baseY1, baseY2};
            gc.fillPolygon(xs, ys, 3);
            gc.strokePolygon(xs, ys, 3);

            // Current bubble at shooter position
            currentBubble.x = x;
            currentBubble.y = y;
            currentBubble.draw(gc);

            // Next bubble preview (smaller, below)
            gc.setFill(nextBubble.color);
            gc.fillOval(x - 12.5, y + 35, 25, 25);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(x - 12.5, y + 35, 25, 25);
        }

        FlyingBubble shoot(double targetX, double targetY) {
            double angle = Math.atan2(targetY - y, targetX - x);
            return new FlyingBubble(x, y, currentBubble.color, angle, speed);
        }

        void prepareNextBubble() {
            currentBubble = nextBubble;
            nextBubble = getNextRandomBubble();
        }

        Bubble getNextRandomBubble() {
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE};
            return new Bubble(x, y, colors[new Random().nextInt(colors.length)]);
        }

        void drawTrajectory(GraphicsContext gc, double mouseX, double mouseY, double width) {
            double tempX = x;
            double tempY = y;
            double angle = Math.atan2(mouseY - y, mouseX - x);
            double vx = speed * Math.cos(angle);
            double vy = speed * Math.sin(angle);

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.setLineDashes(5, 5);

            for (int i = 0; i < 50; i++) {
                tempX += vx;
                tempY += vy;

                // Check wall bounce
                if (tempX - 20 < 0) {
                    tempX = 20;
                    vx = -vx;
                }
                if (tempX + 20 > width) {
                    tempX = width - 20;
                    vx = -vx;
                }

                gc.fillOval(tempX - 2, tempY - 2, 4, 4);

                if (tempY < 50) break; // Stop at top
            }

            gc.setLineDashes(0);
        }
    }

    class Grid {
        ArrayList<Bubble> bubbles = new ArrayList<>();
        final int cols = 10;
        final double startX = 50;
        final double startY = 50;
        final double spacing = 45;

        Grid() {
            Random rand = new Random();
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE};

            // Create initial 5 rows
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < cols; c++) {
                    double offset = r % 2 == 1 ? spacing / 2 : 0;
                    double x = startX + c * spacing + offset;
                    double y = startY + r * spacing;
                    Bubble b = new Bubble(x, y, colors[rand.nextInt(colors.length)]);
                    b.row = r;
                    b.col = c;
                    bubbles.add(b);
                }
            }
        }

        void draw(GraphicsContext gc) {
            for (Bubble b : bubbles) {
                b.draw(gc);
            }
        }

        boolean checkGridCollision(FlyingBubble fb) {
            // Check collision with existing bubbles (check distance from center to center)
            for (Bubble b : bubbles) {
                double dx = fb.x - b.x;
                double dy = fb.y - b.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                // Collision when bubbles are touching or overlapping (radius + radius = 40)
                if (distance <= 40) return true;
            }

            // Check if hit top (allow bubble to reach just below the top edge)
            if (fb.y - fb.radius <= startY - spacing / 2) return true;

            return false;
        }

        void snapBubble(FlyingBubble fb) {
            // Find closest valid grid position
            int bestRow = -1;
            int bestCol = -1;
            double minDist = Double.MAX_VALUE;

            // Determine which rows to check based on flying bubble position
            int centerRow = Math.max(0, (int)Math.round((fb.y - startY) / spacing));
            int rowStart = Math.max(0, centerRow - 2);
            int rowEnd = Math.min(14, centerRow + 2);

            // Check nearby rows only
            for (int r = rowStart; r <= rowEnd; r++) {
                double offset = r % 2 == 1 ? spacing / 2 : 0;

                // Determine column range to check
                int centerCol = Math.max(0, Math.min(cols - 1,
                        (int)Math.round((fb.x - startX - offset) / spacing)));
                int colStart = Math.max(0, centerCol - 2);
                int colEnd = Math.min(cols - 1, centerCol + 2);

                for (int c = colStart; c <= colEnd; c++) {
                    double gx = startX + c * spacing + offset;
                    double gy = startY + r * spacing;

                    // Check if this position is empty
                    boolean occupied = false;
                    for (Bubble b : bubbles) {
                        if (b.row == r && b.col == c) {
                            occupied = true;
                            break;
                        }
                    }

                    if (!occupied) {
                        // Check if at least one neighbor exists (can't float in air)
                        boolean hasNeighbor = false;

                        // If it's row 0, it's always valid (attached to ceiling)
                        if (r == 0) {
                            hasNeighbor = true;
                        } else {
                            // Check for neighboring bubbles
                            int[][] neighborOffsets;
                            if (r % 2 == 0) {
                                neighborOffsets = new int[][]{
                                        {-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 0}
                                };
                            } else {
                                neighborOffsets = new int[][]{
                                        {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1}
                                };
                            }

                            for (int[] nOffset : neighborOffsets) {
                                int nRow = r + nOffset[0];
                                int nCol = c + nOffset[1];

                                for (Bubble b : bubbles) {
                                    if (b.row == nRow && b.col == nCol) {
                                        hasNeighbor = true;
                                        break;
                                    }
                                }
                                if (hasNeighbor) break;
                            }
                        }

                        if (hasNeighbor) {
                            double dx = fb.x - gx;
                            double dy = fb.y - gy;
                            double dist = Math.sqrt(dx * dx + dy * dy);

                            if (dist < minDist) {
                                minDist = dist;
                                bestRow = r;
                                bestCol = c;
                            }
                        }
                    }
                }
            }

            // Snap to best position if found
            if (bestRow != -1 && bestCol != -1) {
                double offset = bestRow % 2 == 1 ? spacing / 2 : 0;
                fb.row = bestRow;
                fb.col = bestCol;
                fb.x = startX + bestCol * spacing + offset;
                fb.y = startY + bestRow * spacing;
                bubbles.add(fb);
            }
        }

        ArrayList<Bubble> findMatches(int row, int col, Color color) {
            HashSet<Bubble> visited = new HashSet<>();
            ArrayList<Bubble> matched = new ArrayList<>();
            Queue<Bubble> queue = new LinkedList<>();

            // Find starting bubble
            Bubble start = null;
            for (Bubble b : bubbles) {
                if (b.row == row && b.col == col) {
                    start = b;
                    break;
                }
            }

            if (start == null) return matched;

            queue.add(start);
            visited.add(start);

            while (!queue.isEmpty()) {
                Bubble current = queue.poll();
                matched.add(current);

                // Get neighbors in honeycomb pattern
                int[][] neighborOffsets;
                if (current.row % 2 == 0) {
                    // Even row
                    neighborOffsets = new int[][]{
                            {-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 0}
                    };
                } else {
                    // Odd row
                    neighborOffsets = new int[][]{
                            {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1}
                    };
                }

                for (int[] offset : neighborOffsets) {
                    int newRow = current.row + offset[0];
                    int newCol = current.col + offset[1];

                    for (Bubble b : bubbles) {
                        if (b.row == newRow && b.col == newCol &&
                                b.color.equals(color) && !visited.contains(b)) {
                            visited.add(b);
                            queue.add(b);
                        }
                    }
                }
            }

            // Only return if 3 or more matched
            if (matched.size() < 3) {
                matched.clear();
            }

            return matched;
        }

        int findFloatingBubbles() {
            HashSet<Bubble> connected = new HashSet<>();
            Queue<Bubble> queue = new LinkedList<>();

            // Start from all bubbles in row 0 (connected to ceiling)
            for (Bubble b : bubbles) {
                if (b.row == 0) {
                    queue.add(b);
                    connected.add(b);
                }
            }

            // BFS to find all connected bubbles
            while (!queue.isEmpty()) {
                Bubble current = queue.poll();

                // Get neighbors
                int[][] neighborOffsets;
                if (current.row % 2 == 0) {
                    neighborOffsets = new int[][]{
                            {-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 0}
                    };
                } else {
                    neighborOffsets = new int[][]{
                            {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1}
                    };
                }

                for (int[] offset : neighborOffsets) {
                    int newRow = current.row + offset[0];
                    int newCol = current.col + offset[1];

                    for (Bubble b : bubbles) {
                        if (b.row == newRow && b.col == newCol && !connected.contains(b)) {
                            connected.add(b);
                            queue.add(b);
                        }
                    }
                }
            }

            // Remove floating bubbles (not connected)
            ArrayList<Bubble> toRemove = new ArrayList<>();
            for (Bubble b : bubbles) {
                if (!connected.contains(b)) {
                    toRemove.add(b);
                }
            }

            for (Bubble b : toRemove) {
                bubbles.remove(b);
            }

            return toRemove.size();
        }

        void removeBubble(Bubble b) {
            bubbles.remove(b);
        }

        boolean checkWin() {
            return bubbles.isEmpty();
        }

        boolean checkLose(double height) {
            for (Bubble b : bubbles) {
                if (b.y > height - 100) return true;
            }
            return false;
        }

        void addNewRow() {
            Random rand = new Random();
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE};

            // Shift all bubbles down by 1 row
            for (Bubble b : bubbles) {
                b.row++;
                double offset = b.row % 2 == 1 ? spacing / 2 : 0;
                b.y = startY + b.row * spacing;
                b.x = startX + b.col * spacing + offset;
            }

            // Add new row at top (row 0)
            for (int c = 0; c < cols; c++) {
                double x = startX + c * spacing;
                double y = startY;
                Bubble newB = new Bubble(x, y, colors[rand.nextInt(colors.length)]);
                newB.row = 0;
                newB.col = c;
                bubbles.add(newB);
            }
        }
    }
}