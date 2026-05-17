package OurTetrisGame;

import javafx.scene.paint.Color;

public abstract class Tetromino implements Displaceable, DirectionChanger {
    protected int[][] tetrominoShape;
    protected int x, y;               // indicate current position of tetromino on grid
    protected Color tetrominoColor;

    public Tetromino(int[][] shape, Color color) {
        this.tetrominoShape = shape;
        this.tetrominoColor = color;
        this.x = 3;                  // tetromino starts coming from forth column of grid
        this.y = 0;                  // tetromino starts coming from top i.e. first row
    }

    @Override
    public void displace(int horizontalMovement, int verticalMovement) {
        this.x += horizontalMovement; //move tetromino horizontally
        this.y += verticalMovement;   //move tetromino vertically
    }

    @Override
    public boolean canDisplace(int horizontalMovement, int verticalMovement, Grid grid) {
        for (int i = 0; i < tetrominoShape.length; i++) {
            for (int j = 0; j < tetrominoShape[0].length; j++) {
                if (tetrominoShape[i][j] != 0) {                 // when block of grid is occupied by tetromino
                    if (grid.isOccupied(horizontalMovement + j + x, verticalMovement + i + y)) { // calculates new vertical and horizontal position
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Default rotation logic (subclasses can override this)
    @Override
    public void changeDirection(Grid grid) {
        int[][] oldShape = this.tetrominoShape; // Store old shape
        
        int rows = tetrominoShape.length;
        int cols = tetrominoShape[0].length;
        
        // Create a new 2D array to store the rotated shape
        int[][] rotatedShape = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotatedShape[j][rows - i - 1] = tetrominoShape[i][j];
            }
        }
        
        // Apply rotation and check if it's valid
        this.tetrominoShape = rotatedShape;
        
        // If it can't exist in the new shape at current x,y, revert!
        if (!this.canDisplace(0, 0, grid)) {
            this.tetrominoShape = oldShape;
        }
    }
    
    //Getters
    public int[][] getTetrominoShape() { return tetrominoShape; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getTetrominoColor() { return tetrominoColor; }
}