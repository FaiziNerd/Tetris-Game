package OurTetrisGame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Grid {
    public static final int WIDTH = 10; // Number of columns
    public static final int HEIGHT = 20; // Number of rows
    private Color[][] grid; // Store colors instead of integers

    public Grid() {
        this.grid = new Color[HEIGHT][WIDTH];
    }

    // Method to place a Tetromino on the grid
    public void placeTetromino(Tetromino tetromino) {
        int[][] shape = tetromino.getTetrominoShape();
        int x = tetromino.getX(); //x coordinate of tetromino
        int y = tetromino.getY(); //y coordinate of tetromino
        // x & y tell where the Tetromino is positioned on the grid
        Color color = tetromino.getTetrominoColor();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {   // i & j refer to the exact position of a block within the Tetromino shape
                    grid[y + i][x + j] = color; // Set the color of the block
                }
            }
        }
    }

    // Check if a cell is occupied
    public boolean isOccupied(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return true; // Outside of grid boundaries is considered occupied
        }
        return grid[y][x] != null; // Return true if cell is occupied
    }

    // Method to check and remove completed lines
    public int removeFullLines() {
        int linesCleared = 0;

        for (int i = HEIGHT - 1; i >= 0; i--) {
            if (isLineFull(i)) {
                clearLine(i);
                shiftDown(i);
                linesCleared++;
                i++; // Recheck the current row since it has moved down
            }
        }
        return linesCleared;
    }

    // Check if a line is full (all cells are occupied)
    private boolean isLineFull(int row) {
        for (int col = 0; col < WIDTH; col++) {
            if (grid[row][col] == null) {
                return false; // If any cell is empty, line is not full
            }
        }
        return true;
    }

    // Clear a line by setting all cells to null
    private void clearLine(int row) {
        for (int col = 0; col < WIDTH; col++) {
            grid[row][col] = null;
        }
    }

    // Shift all rows above the cleared line down by 1
    private void shiftDown(int clearedRow) {
        for (int row = clearedRow - 1; row >= 0; row--) {
            for (int col = 0; col < WIDTH; col++) {
                grid[row + 1][col] = grid[row][col];
                grid[row][col] = null;
            }
        }
    }

    // Draw the grid on the screen
    public void drawGrid(GraphicsContext gc) {
        // Draw cells
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (grid[row][col] != null) 
                    { //occupied
                    gc.setFill(grid[row][col]); // Use the stored color
                    gc.fillRect(col * 30, row * 30, 30, 30);
                } else {
                    gc.setFill(Color.web("#1e1e1e"));
                    gc.fillRect(col * 30, row * 30, 30, 30); // Empty cell
                }

                // Draw grid lines
                gc.setStroke(Color.web("#333333"));
                gc.strokeRect(col * 30, row * 30, 30, 30);
            }
        }
    }
}
