package org.cis1200;

import java.awt.*;

public class Block {

    private final int color;

    /** Constructor with default color (0) **/
    public Block() {
        this(0);
    }

    /**
     * Constructor that sets the Blocks color variable
     * equal to the given color variable.
     */
    public Block(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    /**
     * Method that translates integer color of the Block
     * to type Color
     */
    public Color translateColor() {
        switch (color) {
            case 0:
                return Color.WHITE;
            case 1:
                return new Color(250, 65, 90);
            case 2:
                return new Color(250, 138, 15);
            case 3:
                return new Color(253, 210, 34);
            case 4:
                return new Color(250, 50, 250);
            case 5:
                return new Color(24, 48, 205);
            case 6:
                return new Color(85, 214, 68);
            case 7:
                return new Color(0, 227, 248);
            default:
                return null;
        }
    }
}
