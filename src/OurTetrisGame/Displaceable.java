package OurTetrisGame;

public interface Displaceable {
    boolean canDisplace(int horizontalMovement, int verticalMovement, Grid grid);
    void displace(int horizontalMovement, int verticalMovement);
}
