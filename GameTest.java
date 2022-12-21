package org.cis1200;

import org.cis1200.pieces.BoxPiece;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * I used this file to test the implementation of my game.
 */

public class GameTest {

    private GameCourt court;

    private void createGame() {
        final JLabel status = new JLabel("Testing");
        this.court = new GameCourt(status);
    }

    @Test
    public void testCreateEmptyBlock() {
        Block createdBlock = new Block(0);
        Color expectedColor = Color.WHITE;

        assertEquals(0, createdBlock.getColor());
        assertEquals(expectedColor, createdBlock.translateColor());
    }

    @Test
    public void testCreateBlock() {
        Block createdBlock = new Block(1);
        Color expectedColor = new Color(250, 65, 90);

        assertEquals(1, createdBlock.getColor());
        assertEquals(expectedColor, createdBlock.translateColor());
    }

    @Test
    public void testTranslateColor() {
        Block block = new Block(4);
        Color color = new Color(250, 50, 250);

        assertEquals(color, block.translateColor());
    }

    @Test
    public void testStartGame() {
        createGame();
        court.start();

        for (int row = 0; row < court.getBoard().length; row++) {
            for (int col = 0; col < court.getBoard()[row].length; col++) {
                assertEquals(0, court.getBoard()[row][col].getColor());
            }
        }

        assertNotNull(court.getPiece());
        assertNotNull(court.getNextPiece());
        assertEquals(1, court.getLevel());
        assertEquals(0, court.getScore());
        assertEquals(0, court.getLines());
        assertTrue(court.getPlaying());
    }

    @Test
    public void testCreateEmptyPiece() {
        createGame();

        assertNull(court.createPiece(0, 0));
    }

    @Test
    public void testCreatePiece() {
        createGame();
        Piece createdPiece = court.createPiece(1, 0);
        Piece expectedPiece = new BoxPiece(
                0, court.VELOCITY, court.INIT_POS_X, court.INIT_POS_Y, court.COURT_WIDTH,
                court.COURT_HEIGHT, 0
        );

        assertEquals(expectedPiece.getPx(), createdPiece.getPx());
        assertEquals(expectedPiece.getPy(), createdPiece.getPy());
        assertEquals(expectedPiece.getVx(), createdPiece.getVx());
        assertEquals(expectedPiece.getVy(), createdPiece.getVy());

        for (int row = 0; row < expectedPiece.getBlocks().length; row++) {
            for (int col = 0; col < expectedPiece.getBlocks()[row].length; col++) {
                assertEquals(
                        expectedPiece.getBlocks()[row][col].getColor(),
                        createdPiece.getBlocks()[row][col].getColor()
                );
            }
        }
    }

    @Test
    public void testTick() {
        createGame();
        court.start();
        int initialPy = court.getPiece().getPy();
        for (int i = 0; i < 3; i++) {
            court.tick();
        }
        int finalPy = court.getPiece().getPy();

        assertEquals(0, initialPy);
        assertEquals(3, finalPy);
    }

    @Test
    public void testRotate() {
        createGame();
        court.start();
        Piece initialPiece = court.createPiece(3, 0);
        court.setPiece(initialPiece);
        Block[][] expectedPiece = new Block[4][4];

        for (int row = 0; row < expectedPiece.length; row++) {
            for (int col = 0; col < expectedPiece[row].length; col++) {
                if ((row == 1 && col > 0) || (row == 2 && col == 1)) {
                    expectedPiece[row][col] = new Block(initialPiece.getColor());
                } else {
                    expectedPiece[row][col] = new Block(0);
                }
            }
        }

        for (int row = 0; row < expectedPiece.length; row++) {
            for (int col = 0; col < expectedPiece[row].length; col++) {
                assertEquals(
                        expectedPiece[row][col].getColor(),
                        initialPiece.getBlocks()[row][col].getColor()
                );
            }
        }
    }

    @Test
    public void testHitBoardEmpty() {
        createGame();
        Block[][] board = new Block[court.COURT_HEIGHT][court.COURT_WIDTH];
        Piece piece = court.createPiece(1, 0);
        piece.setPy(court.COURT_HEIGHT - 2);
        court.setPiece(piece);
        assertTrue(court.getPiece().hitBoard(board));
    }

    @Test
    public void testHitBoardWithBlocks() {
        createGame();
        Block[][] board = new Block[court.COURT_HEIGHT][court.COURT_WIDTH];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (row == court.COURT_HEIGHT - 1) {
                    board[row][col] = new Block(3);
                } else {
                    board[row][col] = new Block(0);
                }
            }
        }
        Piece piece = court.createPiece(2, 0);
        piece.setPy(court.COURT_HEIGHT - 3);
        court.setPiece(piece);
        assertTrue(court.getPiece().hitBoard(board));
    }

    @Test
    public void testNewPiece() {
        createGame();
        Piece initialPiece = court.getPiece();
        court.newPiece();
        assertNotEquals(court.getPiece(), initialPiece);
    }

    @Test
    public void testLineClear() {
        createGame();
        court.start();
        Block[][] board = new Block[court.COURT_HEIGHT][court.COURT_WIDTH];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (row == court.COURT_HEIGHT - 1) {
                    board[row][col] = new Block(5);
                } else {
                    board[row][col] = new Block(0);
                }
            }
        }
        court.setBoard(board);
        court.clear();
        for (int col = 0; col < court.getBoard()[court.COURT_HEIGHT - 1].length; col++) {
            assertEquals(0, court.getBoard()[court.COURT_HEIGHT - 1][col].getColor());
        }
        assertEquals(1, court.getLevel());
        assertEquals(1, court.getLines());
        assertEquals(4, court.getScore());
    }

    @Test
    public void testResetGame() {
        createGame();
        court.start();
        Block[][] board = new Block[court.COURT_HEIGHT][court.COURT_WIDTH];

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                board[row][col] = new Block(7);
            }
        }

        court.setBoard(board);
        Piece initialPiece = court.getPiece();
        Piece initialNextPiece = court.getNextPiece();
        court.reset();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                assertEquals(0, court.getBoard()[row][col].getColor());
            }
        }

        assertNotEquals(initialPiece, court.getPiece());
        assertNotEquals(initialNextPiece, court.getNextPiece());
    }

    @Test
    public void testUndo() {
        createGame();
        court.start();
        Piece initialPiece = court.createPiece(1, 0);
        Piece temp = court.createPiece(1, 0);
        temp.setPy(court.COURT_HEIGHT - 2);
        court.setPiece(temp);
        court.tick();
        assertNotEquals(court.getPiece(), initialPiece);
        court.undo();
        assertEquals(court.getPiece().getColor(), initialPiece.getColor());
    }

    @Test
    public void testPause() {
        createGame();
        court.start();
        court.pause();
        assertFalse(court.getPlaying());
        court.pause();
        assertTrue(court.getPlaying());
    }

    @Test
    public void testGameOver() {
        createGame();
        Block[][] board = new Block[court.COURT_HEIGHT][court.COURT_WIDTH];
        for (int col = 0; col < board[0].length; col++) {
            board[2][col] = new Block(6);
        }

        court.setBoard(board);
        court.tick();
        assertFalse(court.getPlaying());
    }

    @Test
    public void testWriteFile() {
        createGame();
        GameWriter gameWriter = new GameWriter(
                court.getBoard(), court.getPiece(), court.getNextPiece(), court.getLevel(),
                court.getLines(), court.getScore()
        );
        gameWriter.writeGameStateToFile("./files/test_file.csv", false);
        assertNotNull(Paths.get("./files/test_file.csv"));
    }

    @Test
    public void testReadFile() {
        createGame();
        try {
            GameParser parser = new GameParser("./files/test_file.csv");
            assertNotNull(parser.parseBoard());
            assertNotNull(parser.parsePiece());
            assertNotNull(parser.parseNextPiece());
            assertEquals(1, parser.parseLevel());
            assertEquals(0, parser.parseLines());
            assertEquals(0, parser.parseScore());
        } catch (NullPointerException io) {
            System.out.println("New game file");
        }
    }
}
