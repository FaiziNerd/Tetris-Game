package OurTetrisGame;

import javafx.scene.paint.Color;

public class OTetromino extends Tetromino {
    
    public OTetromino() {
        super(new int[][]{{1, 1}, {1, 1}}, Color.YELLOW);
    }

    @Override
    public void changeDirection(Grid grid) {
        // Squares do not change shape when rotated
    }
}