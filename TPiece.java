package org.cis1200.pieces;

import org.cis1200.Block;
import org.cis1200.Piece;

import java.awt.*;

public class TPiece extends Piece {

    public TPiece(int vx, int vy, int px, int py, int courtwidth, int courtheight, int order) {
        super(vx, vy, px, py, courtwidth, courtheight, order);
        setColor(5);
        Block[][] blocks = new Block[4][4];
        for (int row = 0; row < blocks.length; row++) {
            for (int col = 0; col < blocks[row].length; col++) {
                if ((row == 1 && col > 0) || (row == 2 && col == 2)) {
                    blocks[row][col] = new Block(getColor());
                } else {
                    blocks[row][col] = new Block(0);
                }
            }
        }
        setBlocks(blocks);
    }

}
