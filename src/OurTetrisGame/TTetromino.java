package OurTetrisGame;

import javafx.scene.paint.Color;

public class TTetromino extends Tetromino {
    
    public TTetromino() {
        super(new int[][]{{0, 1, 0}, {1, 1, 1}}, Color.ORANGE);
    }
}