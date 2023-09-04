package org.cis1200;

import org.cis1200.pieces.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * GameCourt
 *
 * This class holds the primary game logic for how different objects interact
 * with one another.
 */
public class GameCourt extends JPanel {

    // the state of the game logic
    private Piece piece;

    private Piece nextPiece;

    private Block[][] board;

    private TreeMap<Piece, Block[][]> moves;

    private boolean playing = false; // whether the game is running
    private final JLabel status; // Current status text, i.e. "Running..."

    // Game constants
    public static final int COURT_WIDTH = 10;
    public static final int COURT_HEIGHT = 20;
    public static final int VELOCITY = 1;

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 1000;

    public static final int SCALE = 40;

    public static final int INIT_POS_X = 3;

    public static final int INIT_POS_Y = 0;

    public static final String SAVE_FILE = "./files/saved_game.csv";

    private int level;

    private int lines;

    private int score;

    private Boolean paused = false;

    private Timer timer;

    private PlaySound sounds;

    public GameCourt(JLabel status) {
        /**
         * The timer is an object which triggers an action periodically with the
         * given INTERVAL. We register an ActionListener with this timer, whose
         * actionPerformed() method is called each time the timer triggers. We
         * define a helper method called tick() that actually does everything
         * that should be done in a single time step.
         */
        timer = new Timer(INTERVAL, e -> tick());
        timer.start(); // MAKE SURE TO START THE TIMER!

        /** Creates a new board with blocks that are all empty (white) */
        this.board = new Block[COURT_HEIGHT][COURT_WIDTH];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = new Block(0);
            }
        }

        /** Set variables to default values */
        this.piece = createPiece((int) (Math.random() * 7 + 1), 0);
        this.nextPiece = createPiece((int) (Math.random() * 7 + 1), 1);
        this.level = 1;
        this.lines = 0;
        this.score = 0;
        this.status = status;
        this.sounds = new PlaySound();
        this.moves = new TreeMap<>(Comparator.comparingInt(Piece::getOrder));

        /**
         * Does not play music or add KeyListeners/MouseListeners if the program
         * is in testing mode.
         */
        if (!this.status.getText().equals("Testing")) {
            String song = "files/tetris song.wav";
            String landEffect = "files/land effect.wav";
            String clearEffect = "files/clear effect.wav";
            String tetrisEffect = "files/tetris effect.wav";
            sounds.playSound(song, landEffect, clearEffect, tetrisEffect);

            // creates border around the court area, JComponent method
            setBorder(BorderFactory.createLineBorder(Color.BLACK));

            // Enable keyboard focus on the court area. When this component has the
            // keyboard focus, key events are handled by its key listener.
            setFocusable(true);

            // This key listener allows the square to move as long as an arrow key
            // is pressed, by changing the square's velocity accordingly. (The tick
            // method below actually moves the square.)
            addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (playing) {
                        if (e.getKeyCode() == KeyEvent.VK_LEFT
                                && !piece.intersectsWall(
                                        piece.getBlocks(), piece.getPx() - VELOCITY, piece.getPy()
                                )
                                && !piece.intersectsBoard(
                                        board, piece.getBlocks(), piece.getPx() - VELOCITY,
                                        piece.getPy()
                                )) {
                            piece.setPx(piece.getPx() - VELOCITY);
                        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT
                                && !piece.intersectsWall(
                                        piece.getBlocks(), piece.getPx() + VELOCITY, piece.getPy()
                                )
                                && !piece.intersectsBoard(
                                        board, piece.getBlocks(), piece.getPx() + VELOCITY,
                                        piece.getPy()
                                )) {
                            piece.setPx(piece.getPx() + VELOCITY);
                        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                            piece.setPy(piece.getPy() + VELOCITY);
                        } else if (e.getKeyCode() == KeyEvent.VK_UP
                                && !piece.intersectsWall(
                                        piece.ifRotateCW(), piece.getPx(), piece.getPy()
                                )
                                && !piece.intersectsBoard(
                                        board, piece.ifRotateCW(), piece.getPx(), piece.getPy()
                                )) {
                            piece.rotateCW();
                        } else if (e.getKeyCode() == KeyEvent.VK_U) {
                            undo();
                        }
                        clear();
                        if (piece.hitBoard(board)) {
                            newPiece();
                        }
                        repaint();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_P) {
                        pause();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_S) {
                        save();
                    }
                }

                public void keyReleased(KeyEvent e) {
                    piece.setVx(0);
                    piece.setVy(VELOCITY);
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getPoint().getX() >= 430
                            && e.getPoint().getX() <= 590
                            && e.getPoint().getY() >= 500
                            && e.getPoint().getY() <= 550) {
                        pause();
                    } else if (e.getPoint().getX() >= 630
                            && e.getPoint().getX() <= 765
                            && e.getPoint().getY() >= 500
                            && e.getPoint().getY() <= 550) {
                        save();
                    }
                }
            });
        }
    }

    /**
     * Creates a piece from an integer from (1-7) that represents the piece
     * shape and an integer that represents the piece's order in the moves
     * TreeMap.
     */
    public Piece createPiece(int shape, int order) {
        switch (shape) {
            case 1:
                return new BoxPiece(
                        0, VELOCITY, INIT_POS_X, INIT_POS_Y, COURT_WIDTH, COURT_HEIGHT, order
                );
            case 2:
                return new IPiece(
                        0, VELOCITY, INIT_POS_X, INIT_POS_Y, COURT_WIDTH, COURT_HEIGHT, order
                );
            case 3:
                return new LPiece1(
                        0, VELOCITY, INIT_POS_X, INIT_POS_Y, COURT_WIDTH, COURT_HEIGHT, order
                );
            case 4:
                return new LPiece2(
                        0, VELOCITY, INIT_POS_X, INIT_POS_Y, COURT_WIDTH, COURT_HEIGHT, order
                );
            case 5:
                return new TPiece(
                        0, VELOCITY, INIT_POS_X, INIT_POS_Y, COURT_WIDTH, COURT_HEIGHT, order
                );
            case 6:
                return new ZPiece1(
                        0, VELOCITY, INIT_POS_X, INIT_POS_Y, COURT_WIDTH, COURT_HEIGHT, order
                );
            case 7:
                return new ZPiece2(
                        0, VELOCITY, INIT_POS_X, INIT_POS_Y, COURT_WIDTH, COURT_HEIGHT, order
                );
            default:
                return null;
        }
    }

    /**
     * Called when the piece has hit the board. The method saves the
     * move (the current piece and board state) in the TreeMap. Then the
     * current piece is set to the next piece, and the next piece is set
     * to a random piece.
     */
    public void newPiece() {
        sounds.playLandEffect();
        Block[][] board = getBoardCopy(this.board);

        for (int row = 0; row < piece.getBlocks().length; row++) {
            for (int col = 0; col < piece.getBlocks()[row].length; col++) {
                if (piece.getBlocks()[row][col].getColor() != 0) {
                    board[piece.getPy() + row][piece.getPx() + col] = piece.getBlocks()[row][col];
                }
            }
        }

        moves.put(piece, board);
        this.board = getBoardCopy(board);
        nextPiece.setPx(INIT_POS_X);
        nextPiece.setPy(INIT_POS_Y);
        piece = nextPiece;
        nextPiece = createPiece((int) (Math.random() * 7 + 1), piece.getOrder() + 1);
    }

    /**
     * Called when the game is initially started. First, it resets the
     * game state. Then it retrieves information from the saved file (if
     * the status isn't Testing).
     */
    public void start() {
        reset();
        if (!status.getText().equals("Testing")) {
            try {
                GameParser parser = new GameParser(SAVE_FILE);
                board = parser.parseBoard();
                int piece = parser.parsePiece();
                if (piece != 0) {
                    this.piece = createPiece(piece, 0);
                }
                int nextPiece = parser.parseNextPiece();
                if (nextPiece != 0) {
                    this.nextPiece = createPiece(nextPiece, 1);
                }
                level = parser.parseLevel();
                lines = parser.parseLines();
                score = parser.parseScore();
            } catch (NullPointerException io) {
                System.out.println("New game file");
            }
        }
    }

    /**
     * (Re-)set the game to its initial state.
     */
    public void reset() {
        /**
         * Clears the board and sets it equal to a board with empty
         * blocks.
         */
        board = new Block[COURT_HEIGHT][COURT_WIDTH];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = new Block(0);
            }
        }
        /** Set variables to default values */
        piece = createPiece((int) (Math.random() * 7 + 1), 0);
        nextPiece = createPiece((int) (Math.random() * 7 + 1), 1);
        level = 1;
        lines = 0;
        score = 0;
        moves.clear();
        /** Restarts the game and the music */
        playing = true;
        if (!status.getText().equals("Testing")) {
            sounds.startMusic();
            status.setText("Running...");

            /** Make sure that this component has the keyboard focus */
            requestFocusInWindow();
        }
    }

    /**
     * Clears row(s) of blocks if they are full. Then, updates level,
     * lines, and score accordingly.
     */
    public void clear() {
        int blocksInRow = 0;
        ArrayList<Integer> fullRows = new ArrayList<>();
        /**
         * Checks if any rows are full, and if they are, the index of those rows will be
         * added to the fullRows ArrayList.
         */
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col].getColor() != 0) {
                    blocksInRow++;
                }
            }
            if (blocksInRow == board[0].length) {
                fullRows.add(row);
            }
            blocksInRow = 0;
        }
        /**
         * Replace filled rows with empty rows.
         */
        if (fullRows.size() > 0) {
            for (int index = 0; index < fullRows.size(); index++) {
                for (int col = 0; col < board[0].length; col++) {
                    board[fullRows.get(index)][col] = new Block(0);
                }
                for (int row = fullRows.get(index); row > 0; row--) {
                    for (int col = 0; col < board[0].length; col++) {
                        board[row][col] = board[row - 1][col];
                        board[row - 1][col] = new Block(0);
                    }
                }
            }
        }
        /**
         * Add to level, lines, and score according to the number of lines
         * that are cleared.
         */
        int linesCleared = fullRows.size();
        switch (linesCleared) {
            case 1:
                score += 4 * level;
                sounds.playClearEffect();
                break;
            case 2:
                score += 10 * level;
                sounds.playClearEffect();
                break;
            case 3:
                score += 30 * level;
                sounds.playClearEffect();
                break;
            case 4:
                score += 120 * level;
                sounds.playTetrisEffect();
                break;
            default:
                break;
        }
        lines += linesCleared;
        if (level * 5 <= lines) {
            level++;
        }
    }

    /**
     * If pieces have been placed, undoes the move so that the previous
     * block is the current block and the current block is the next block.
     * Updates the board state, level, lines, and score accordingly.
     */
    public void undo() {
        if (!moves.isEmpty() && playing) {
            /**
             * Calculates level, lines, and score of the previous move
             * and sets level, lines, and score to the values of the
             * previous move
             */
            int currLines = lines;
            int currScore = score;
            this.board = getBoardCopy(moves.lastEntry().getValue());
            clear();
            lines = currLines - (lines - currLines);
            level = lines / 5 + 1;
            score = currScore - (score - currScore);
            if (score < 0) {
                score = 0;
            }

            /**
             * Sets board to the board state of the previous move and
             * sets the nextPiece variable equal to the current piece
             */
            this.board = getBoardCopy(moves.lastEntry().getValue());
            nextPiece = createPiece(piece.getColor(), piece.getOrder());
            piece = moves.lastKey();

            /**
             * Replace the blocks where the previous piece landed
             * to empty blocks.
             */
            for (int row = 0; row < piece.getBlocks().length; row++) {
                for (int col = 0; col < piece.getBlocks()[row].length; col++) {
                    if (piece.getBlocks()[row][col].getColor() != 0) {
                        board[piece.getPy() + row][piece.getPx() + col] = new Block(0);
                    }
                }
            }

            /**
             * Sets the current piece equal to the previous piece and removes
             * the move from the TreeMap.
             */
            piece = createPiece(piece.getColor(), piece.getOrder());
            moves.remove(moves.lastKey());
            repaint();
        }
    }

    /**
     * Pauses or unpauses the game.
     */
    public void pause() {
        paused = !paused;
        playing = !playing;
        if (paused) {
            sounds.stopMusic();
        } else {
            sounds.startMusic();
        }
        repaint();
    }

    /**
     * Creates a gameWriter to save current game state to the defined
     * file name.
     */
    public void save() {
        GameWriter gameWriter = new GameWriter(
                board, piece, nextPiece, level, lines, score
        );
        gameWriter.writeGameStateToFile(SAVE_FILE, false);
    }

    /**
     * This method is called every time the timer defined in the constructor
     * triggers.
     */
    void tick() {
        if (playing) {
            /** advance the piece down 1 block */
            piece.move();

            /** checks to see if the piece has hit the board */
            if (piece.hitBoard(board)) {
                newPiece();
            }

            /** check if any lines have been filled up */
            clear();

            /** update timer according to level */
            timer.setDelay(INTERVAL - level * 100);

            /** check for the game end condition */
            for (int col = 0; col < board[0].length; col++) {
                if (board[2][col].getColor() != 0) {
                    playing = false;
                    sounds.stopMusic();
                    status.setText("You lose!");
                }
            }

            /** update the display */
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        /** Draw the background of the game */
        g.setColor(new Color(44, 44, 128));
        g.fillRect(10 * SCALE, 0, 10 * SCALE, 20 * SCALE);
        g.setColor(new Color(58, 112, 220));
        g.fillRect(0, 0, 10 * SCALE, 20 * SCALE);
        g.fillRect(11 * SCALE, 1 * SCALE, 8 * SCALE, 4 * SCALE);

        /** Draw the blocks that are currently on the board **/
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col].getColor() != 0) {
                    g.setColor(board[row][col].translateColor());
                    g.fillRect(col * SCALE, row * SCALE, SCALE, SCALE);
                    g.setColor(Color.BLACK);
                    g.drawRect(col * SCALE, row * SCALE, SCALE, SCALE);
                }
            }
        }

        /** Draw current piece **/
        piece.draw(g, SCALE);
        nextPiece.setPx(13);
        nextPiece.setPy(1);
        nextPiece.draw(g, SCALE);

        /** Draw the menu **/
        g.setFont(new Font("Futura", Font.BOLD, SCALE));
        g.setColor(Color.WHITE);
        g.drawString("LEVEL", 11 * SCALE, 7 * SCALE);
        g.drawString(String.valueOf(level), 17 * SCALE, 7 * SCALE);
        g.drawString("LINES", 11 * SCALE, 9 * SCALE);
        g.drawString(String.valueOf(lines), 17 * SCALE, 9 * SCALE);
        g.drawString("SCORE", 11 * SCALE, 11 * SCALE);
        g.drawString(String.valueOf(score), 17 * SCALE, 11 * SCALE);
        g.drawString("PAUSE", 11 * SCALE, (int) (13.5 * SCALE));
        g.drawRect(430, 500, 160, 50);
        g.drawString("SAVE", 16 * SCALE, (int) (13.5 * SCALE));
        g.drawRect(630, 500, 135, 50);

        /** Draw the menu controls **/
        g.setFont(new Font("Futura", Font.BOLD, (int) (0.5 * SCALE)));
        g.drawString("[U] UNDO", (int) (10.5 * SCALE), 15 * SCALE);
        g.drawString("[P] PAUSE", (int) (13.75 * SCALE), 15 * SCALE);
        g.drawString("[S] SAVE", 17 * SCALE, 15 * SCALE);

        /** Draw the image */
        BufferedImage img = null;
        String file = "files/tetris logo.png";

        try {
            if (img == null) {
                img = ImageIO.read(new File(file));
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
        g.drawImage(
                img, (int) (12.5 * SCALE), 16 * SCALE, (int) (5.5 * SCALE), (int) (3.5 * SCALE),
                null
        );

        /** Draw game over screen */
        g.setFont(new Font("Futura", Font.BOLD, SCALE));
        if (!playing) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 10 * SCALE, 20 * SCALE);
            g.setColor(new Color(44, 44, 128));
            g.drawString("GAME OVER", (int) (1.5 * SCALE), 10 * SCALE);
        }

        /** Draw paused screen */
        if (paused) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 10 * SCALE, 20 * SCALE);
            g.setColor(new Color(44, 44, 128));
            g.drawString("PAUSED", 3 * SCALE, 10 * SCALE);
        }
    }

    /** Set the preferred size of the Game window */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(2 * COURT_WIDTH * SCALE, COURT_HEIGHT * SCALE);
    }

    /**
     * Methods for testing
     */
    public Piece getPiece() {
        return this.piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getNextPiece() {
        return this.nextPiece;
    }

    public Block[][] getBoard() {
        return this.board;
    }

    public Block[][] getBoardCopy(Block[][] board) {
        Block[][] boardCopy = new Block[COURT_HEIGHT][COURT_WIDTH];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                boardCopy[row][col] = board[row][col];
            }
        }
        return boardCopy;
    }

    public void setBoard(Block[][] board) {
        this.board = board;
    }

    public Boolean getPlaying() {
        return this.playing;
    }

    public int getLevel() {
        return this.level;
    }

    public int getLines() {
        return this.lines;
    }

    public int getScore() {
        return this.score;
    }
}