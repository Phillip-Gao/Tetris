package org.cis1200;

import java.awt.*;

/**
 * A Piece in the game
 */
public abstract class Piece {
    /*
     * Current position of the object (in terms of graphics coordinates)
     *
     * Coordinates are given by the upper-left hand corner of the object. This
     * position should always be within bounds:
     * 0 <= px <= maxX 0 <= py <= maxY
     */
    private int px;
    private int py;

    /* Velocity: number of pixels to move every time move() is called. */
    private int vx;
    private int vy;

    private Block[][] blocks;

    private int color;

    private int order;

    /*
     * Upper bounds of the area in which the object can be positioned. Maximum
     * permissible x, y positions for the upper-left hand corner of the object.
     */
    private final int maxX;
    private final int maxY;

    /**
     * Constructor
     */
    public Piece(
            int vx, int vy, int px, int py, int courtWidth,
            int courtHeight, int order
    ) {
        this.vx = vx;
        this.vy = vy;
        this.px = px;
        this.py = py;

        this.blocks = new Block[4][4];
        for (int row = 0; row < blocks.length; row++) {
            for (int col = 0; col < blocks[row].length; col++) {
                blocks[row][col] = new Block(0);
            }
        }
        this.color = 0;
        this.maxX = courtWidth;
        this.maxY = courtHeight;
        this.order = order;
    }

    // **********************************************************************************
    // * GETTERS
    // **********************************************************************************
    public int getPx() {
        return this.px;
    }

    public int getPy() {
        return this.py;
    }

    public int getVx() {
        return this.vx;
    }

    public int getVy() {
        return this.vy;
    }

    public Block[][] getBlocks() {
        return blocks.clone();
    }

    public int getColor() {
        return color;
    }

    public int getOrder() {
        return order;
    }

    // **************************************************************************
    // * SETTERS
    // **************************************************************************
    public void setPx(int px) {
        this.px = px;
    }

    public void setPy(int py) {
        this.py = py;
    }

    public void setVx(int vx) {
        this.vx = vx;
    }

    public void setVy(int vy) {
        this.vy = vy;
    }

    public void setBlocks(Block[][] blocks) {
        this.blocks = blocks;
    }

    public void setColor(int color) {
        this.color = color;
    }

    // **************************************************************************
    // * UPDATES AND OTHER METHODS
    // **************************************************************************

    /**
     * Moves the object by its velocity. Ensures that the object does not go
     * outside its bounds by clipping.
     */
    public void move() {
        this.py += this.vy;
    }

    public Boolean hitBoard(Block[][] board) {
        for (int row = 0; row < blocks.length; row++) {
            for (int col = 0; col < blocks[row].length; col++) {
                if (blocks[row][col].getColor() != 0 && py + row < maxY - 1
                        && board[py + row + 1][px + col].getColor() != 0) {
                    return true;
                } else if (blocks[row][col].getColor() != 0 && py + row == maxY - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean intersectsWall(Block[][] blocks, int px, int py) {
        for (int row = 0; row < blocks.length; row++) {
            for (int col = 0; col < blocks[row].length; col++) {
                if (blocks[row][col].getColor() != 0
                        && (px + col >= maxX || px + col < 0 || py + row >= maxY || py + row < 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean intersectsBoard(Block[][] board, Block[][] blocks, int px, int py) {
        for (int row = 0; row < blocks.length; row++) {
            for (int col = 0; col < blocks[row].length; col++) {
                if (blocks[row][col].getColor() != 0 && board[py + row][px + col].getColor() != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void rotateCW() {
        this.blocks = ifRotateCW();
    }

    public Block[][] ifRotateCW() {
        Block[][] rotateCW = new Block[blocks.length][blocks[0].length];
        for (int row = 0; row < blocks.length; row++) {
            for (int col = 0; col < blocks[row].length; col++) {
                rotateCW[col][blocks.length - 1 - row] = blocks[row][col];
            }
        }
        return rotateCW;
    }

    /**
     * Default draw method that provides how the object should be drawn in the
     * GUI. This method does not draw anything. Subclass should override this
     * method based on how their object should appear.
     *
     * @param g The <code>Graphics</code> context used for drawing the object.
     *          Remember graphics contexts that we used in OCaml, it gives the
     *          context in which the object should be drawn (a canvas, a frame,
     *          etc.)
     */
    public void draw(Graphics g, int scale) {
        for (int row = 0; row < blocks.length; row++) {
            for (int col = 0; col < blocks[row].length; col++) {
                if (blocks[row][col].getColor() != 0) {
                    g.setColor(blocks[row][col].translateColor());
                    g.fillRect((px + col) * scale, (py + row) * scale, scale, scale);
                    g.setColor(Color.BLACK);
                    g.drawRect((px + col) * scale, (py + row) * scale, scale, scale);
                }
            }
        }
    }
}