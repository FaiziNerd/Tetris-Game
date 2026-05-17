package OurTetrisGame;

public class Score {
    private int score;

    public Score() {
        this.score = 0;
    }

    // Method to add points to the score
    public void addPoints(int points) {
        this.score += points;
    }

    // Getter for the score
    public int getScore() { return score; }
}

