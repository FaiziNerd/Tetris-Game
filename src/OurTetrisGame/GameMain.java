package OurTetrisGame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameMain extends Application {
    private Grid grid;
    private Tetromino currentTetromino;
    private Tetromino nextTetromino;
    private Score score;
    private boolean isGameOver = false;
    private volatile int gameSpeed = 500;
    private int linesCleared = 0;

    @Override
    public void start(Stage primaryStage) {
        int highestScore = FileHandler.readHighScore();

        // Setup the Root Pane with a Cyber Dark Theme
        StackPane welcomePane = new StackPane();
        welcomePane.setStyle("-fx-background-color: #0f0c29;");
        Canvas welcomeCanvas = new Canvas(500, 600);
        GraphicsContext gc = welcomeCanvas.getGraphicsContext2D();

        // Add Tetrominoes in the background
        gc.setGlobalAlpha(0.15);

        // Ghost I piece
        gc.setFill(Color.CYAN);
        gc.fillRect(50, 450, 120, 30);

        // Ghost O piece
        gc.setFill(Color.YELLOW);
        gc.fillRect(350, 100, 60, 60);

        // Ghost T piece
        gc.setFill(Color.PURPLE);
        gc.fillRect(100, 50, 30, 30);
        gc.fillRect(70, 80, 90, 30);

        gc.setGlobalAlpha(1.0); // Reset transparency for text

        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);

        // Title with 3D Shadow Effect
        // Shadow layer
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 65));
        gc.setFill(Color.web("#2c3e50"));
        gc.fillText("TETRIS", 253, 203);

        // Main Title layer
        gc.setFill(Color.CYAN);
        gc.fillText("TETRIS", 250, 200);

        // High Score Display
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", FontWeight.NORMAL, 22));
        gc.fillText("BEST SCORE: " + highestScore, 250, 260);

        // Button
        javafx.scene.control.Button playButton = new javafx.scene.control.Button("START GAME");
        playButton.setStyle(
                "-fx-background-color: #2ecc71; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 20px; " +
                        "-fx-font-family: 'Verdana'; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12 40; " +
                        "-fx-background-radius: 30; " +
                        "-fx-cursor: hand;");

        // Hover effect for the button
        playButton.setOnMouseEntered(e -> playButton.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 20px; " +
                        "-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-padding: 12 40; " +
                        "-fx-background-radius: 30; -fx-cursor: hand;"));
        playButton.setOnMouseExited(e -> playButton.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 20px; " +
                        "-fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-padding: 12 40; " +
                        "-fx-background-radius: 30; -fx-cursor: hand;"));

        playButton.setOnAction(e -> startGame(primaryStage));

        playButton.setTranslateY(80);

        welcomePane.getChildren().addAll(welcomeCanvas, playButton);

        Scene scene = new Scene(welcomePane, 500, 600);
        primaryStage.setTitle("Tetris OOP Project");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGame(Stage primaryStage) {
        grid = new Grid();
        currentTetromino = generateNewTetromino();
        nextTetromino = generateNewTetromino();
        score = new Score();

        Canvas canvas = new Canvas(500, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        Scene gameScene = new Scene(root, 500, 600);
        gameScene.setOnKeyPressed(event -> handleKeyPress(event, gc));

        primaryStage.setScene(gameScene);

        // Game loop
        new Thread(() -> {
            while (!isGameOver) {
                try {
                    Thread.sleep(gameSpeed);
                    javafx.application.Platform.runLater(() -> updateGame(gc));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Handle key press for movement and actions
    private void handleKeyPress(KeyEvent event, GraphicsContext gc) {
        if (isGameOver)
            return;

        switch (event.getCode()) {
            case LEFT:
                if (currentTetromino.canDisplace(-1, 0, grid)) {
                    currentTetromino.displace(-1, 0);
                }
                break;
            case RIGHT:
                if (currentTetromino.canDisplace(1, 0, grid)) {
                    currentTetromino.displace(1, 0);
                }
                break;
            case DOWN:
                if (currentTetromino.canDisplace(0, 1, grid)) {
                    currentTetromino.displace(0, 1);
                }
                break;
            case UP:
                currentTetromino.changeDirection(grid);
                break;
            case SPACE:
                while (currentTetromino.canDisplace(0, 1, grid)) {
                    currentTetromino.displace(0, 1);
                }
                updateGame(gc);
                break;
            default:
                break;
        }
    }

    private void updateGame(GraphicsContext gc) {
        // When the Tetromino Can Move Down
        if (currentTetromino.canDisplace(0, 1, grid)) {
            currentTetromino.displace(0, 1);
        } else {
            // When the Tetromino Cannot Move Down
            // Place the tetromino on the grid
            grid.placeTetromino(currentTetromino);

            // Check for and remove full lines
            int linesClearedThisRound = grid.removeFullLines();
            if (linesClearedThisRound > 0) {
                linesCleared += linesClearedThisRound;
                // Score increases by 5 for each line cleared
                score.addPoints(linesClearedThisRound * 5);
                // Increase speed every 5 lines by 50ms but don't go below 100ms
                gameSpeed = Math.max(100, 500 - (linesCleared / 5) * 50);
            }

            // Check for game over
            if (checkGameOver()) {
                gameOver(gc);
                return;
            }

            // Promote the next piece and generate a new preview
            currentTetromino = nextTetromino;
            nextTetromino = generateNewTetromino();
        }
        draw(gc);
    }

    // Check if the tetromino reaches the top of the grid, game over if true
    private boolean checkGameOver() {
        for (int j = 0; j < grid.WIDTH; j++) {
            if (grid.isOccupied(j, 0)) {
                return true; // Game over if any cell in the top row is occupied
            }
        }
        return false;
    }

    private void gameOver(GraphicsContext gc) {
        isGameOver = true;

        // Check and save high score
        int highestScore = FileHandler.readHighScore();
        if (score.getScore() > highestScore) {
            FileHandler.writeHighScore(score.getScore());
            highestScore = score.getScore();
        }

        // Create a semi-transparent black overlay over the GRID area
        gc.setGlobalAlpha(0.7); // 70% transparency
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, grid.WIDTH * 30, grid.HEIGHT * 30);
        gc.setGlobalAlpha(1.0); // Reset transparency for the text

        // Draw the text on top of the overlay
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
        gc.fillText("GAME OVER", 40, 280);

        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        gc.fillText("High Score: " + highestScore, 65, 330);
    }

    private void draw(GraphicsContext gc) {

        // Clear with a background color for the side panel area
        gc.setFill(Color.web("#F5F5F5"));
        gc.fillRect(0, 0, 500, 600);

        // Draw the grid and current Tetromino
        grid.drawGrid(gc);
        drawGhost(gc);
        drawTetromino(currentTetromino, gc);

        // Side Panel
        gc.setFill(Color.web("#2c3e50"));
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 22));

        // Draw Score
        gc.fillText("SCORE", 340, 50);
        gc.setFont(Font.font("Verdana", FontWeight.NORMAL, 30));
        gc.fillText(String.valueOf(score.getScore()), 340, 90);

        // Draw Lines
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        gc.fillText("LINES", 340, 160);
        gc.setFont(Font.font("Verdana", FontWeight.NORMAL, 30));
        gc.fillText(String.valueOf(linesCleared), 340, 200);

        // Draw NEXT label and the preview piece
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        gc.fillText("NEXT", 340, 280);
        drawNextPreview(nextTetromino, gc);
    }

    // Generate a new random Tetromino
    private Tetromino generateNewTetromino() {
        int random = (int) (Math.random() * 7);
        switch (random) {
            case 0:
                return new ITetromino();
            case 1:
                return new OTetromino();
            case 2:
                return new TTetromino();
            case 3:
                return new STetromino();
            case 4:
                return new ZTetromino();
            case 5:
                return new JTetromino();
            case 6:
                return new LTetromino();
            default:
                return new ITetromino();
        }
    }

    // Draws the next piece at preview location
    private void drawNextPreview(Tetromino tetromino, GraphicsContext gc) {
        Color color = tetromino.getTetrominoColor();
        int[][] shape = tetromino.getTetrominoShape();

        // Fixed coordinates for the preview box in the side panel
        int previewX = 350;
        int previewY = 310;
        int blockSize = 25;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    gc.setFill(color);
                    gc.fillRect(previewX + (j * blockSize), previewY + (i * blockSize), blockSize, blockSize);

                    gc.setStroke(Color.GRAY);
                    gc.strokeRect(previewX + (j * blockSize), previewY + (i * blockSize), blockSize, blockSize);
                }
            }
        }
    }

    // To find the landing Y-coordinate
    private int getGhostY() {
        int ghostYOffset = 0;
        while (currentTetromino.canDisplace(0, ghostYOffset + 1, grid)) {
            ghostYOffset++;
        }
        return currentTetromino.getY() + ghostYOffset;
    }

    // Draw the ghost preview of tetromino block on grid
    private void drawGhost(GraphicsContext gc) {
        int ghostY = getGhostY();
        Color color = currentTetromino.getTetrominoColor();
        int[][] shape = currentTetromino.getTetrominoShape();

        gc.setGlobalAlpha(0.2); // Make it 20% transparent
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    gc.setFill(color);
                    // Draw at the ghostY position instead of currentTetromino.getY()
                    gc.fillRect((currentTetromino.getX() + j) * 30, (ghostY + i) * 30, 30, 30);
                }
            }
        }
        gc.setGlobalAlpha(1.0); // Reset alpha for other drawing
    }

    // Draw the current Tetromino
    private void drawTetromino(Tetromino tetromino, GraphicsContext gc) {
        Color color = tetromino.getTetrominoColor();
        for (int i = 0; i < tetromino.getTetrominoShape().length; i++) {
            for (int j = 0; j < tetromino.getTetrominoShape()[i].length; j++) {
                if (tetromino.getTetrominoShape()[i][j] != 0) {
                    gc.setFill(color);
                    gc.fillRect((tetromino.getX() + j) * 30, (tetromino.getY() + i) * 30, 30, 30);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
