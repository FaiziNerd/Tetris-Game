package OurTetrisGame;

import java.io.*;

public class FileHandler {
    private static final String FILE_PATH = "highscore.txt";

    // Read the highest score from the file
    public static int readHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0; // Default to 0 if file not found or invalid data
        }
    }

    // Write the new highest score to the file
    public static void writeHighScore(int highScore) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

