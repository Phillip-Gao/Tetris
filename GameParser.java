package org.cis1200;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that handles parsing the game state from a save file
 */
public class GameParser {

    private List<String> data;

    public GameParser(String saveFile) {
        try (Stream<String> lines = Files.lines(Paths.get(saveFile))) {
            data = lines.collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Block[][] parseBoard() {
        int[][] board = new int[GameCourt.COURT_HEIGHT][GameCourt.COURT_WIDTH];
        data.remove("board");
        board = getBlocks(board, "piece");
        return translateBlock(board);
    }

    public int parsePiece() {
        data.remove("piece");
        return Integer.valueOf(data.remove(0));
    }

    public int parseNextPiece() {
        data.remove("nextPiece");
        return Integer.valueOf(data.remove(0));
    }

    public int parseLevel() {
        data.remove("level");
        return Integer.valueOf(data.remove(0));
    }

    public int parseLines() {
        data.remove("lines");
        return Integer.valueOf(data.remove(0));
    }

    public int parseScore() {
        data.remove("score");
        return Integer.valueOf(data.remove(0));
    }

    public int[][] getBlocks(int[][] blocks, String stopWord) {
        int row = 0;
        for (String d : data) {
            if (!d.equals(stopWord)) {
                String[] strings = d.split(" ");
                int[] integers = new int[strings.length];
                for (int col = 0; col < strings.length; col++) {
                    integers[col] = Integer.valueOf(strings[col]);
                }
                blocks[row] = integers;
                row++;
            } else {
                break;
            }
        }
        for (int i = row - 1; i >= 0; i--) {
            data.remove(i);
        }
        return blocks;
    }

    public Block[][] translateBlock(int[][] integers) {
        Block[][] blocks = new Block[integers.length][integers[0].length];
        for (int row = 0; row < blocks.length; row++) {
            for (int col = 0; col < blocks[row].length; col++) {
                blocks[row][col] = new Block(integers[row][col]);
            }
        }
        return blocks;
    }
}
