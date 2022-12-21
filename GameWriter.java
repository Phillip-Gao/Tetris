package org.cis1200;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Class that handles writing the current game state into a save file
 */
public class GameWriter {

    ArrayList<String> gameState;

    public GameWriter(
            Block[][] board, Piece piece, Piece nextPiece, int level, int lines, int score
    ) {
        ArrayList<String> gameState = new ArrayList<>();

        gameState.add("board");
        for (int row = 0; row < board.length; row++) {
            String s = "";
            for (int col = 0; col < board[row].length; col++) {
                s += (board[row][col].getColor() + " ");
            }
            gameState.add(s);
        }

        gameState.add("piece");
        gameState.add(String.valueOf(piece.getColor()));
        gameState.add("nextPiece");
        gameState.add(String.valueOf(nextPiece.getColor()));
        gameState.add("level");
        gameState.add(String.valueOf(level));
        gameState.add("lines");
        gameState.add(String.valueOf(lines));
        gameState.add("score");
        gameState.add(String.valueOf(score));

        this.gameState = gameState;
    }

    public void writeGameStateToFile(String filePath, boolean append) {
        writeStringsToFile(gameState, filePath, append);
    }

    public void writeStringsToFile(
            ArrayList<String> stringsToWrite, String filePath, boolean append
    ) {
        File file = Paths.get(filePath).toFile();
        BufferedWriter bw;
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file, append);
            bw = new BufferedWriter(fileWriter);
            for (String s : stringsToWrite) {
                bw.write(s);
                bw.newLine();
            }
            bw.flush();
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

}
