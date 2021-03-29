package util;

import java.awt.*;

/**
 *  Helper class to convert colors from java.awt.Color to an own String
 *  representation and vice versa.
 */
abstract public class MyColor {

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    /**
     * Converts a color name in a awt.Color object.
     */
    public static Color getColor(String color) {
        switch (color) {
            case "yellow":
                return Color.yellow;
            case "red":
                return Color.red;
            case "green":
                return Color.green;
            case "blue":
                return Color.blue;
            case "pink":
                return Color.pink;
            case "magenta":
                return Color.magenta;
            default:
                return Color.black;
        }
    }

    /**
     * Converts a awt.Color object into a string representation.
     */
    public static String getName(Color color) {
        if (color.equals(Color.yellow)) {
            return "yellow";
        } else if (color.equals(Color.red)) {
            return "red";
        } else if (color.equals(Color.green)) {
            return "green";
        } else if (color.equals(Color.blue)) {
            return "blue";
        } else if (color.equals(Color.pink)) {
            return "pink";
        } else if (color.equals(Color.magenta)) {
            return "magenta";
        } else {
            return "black";
        }
    }

    public static Color getColor(int i) {
        try {
            return colors[i];
        } catch (ArrayIndexOutOfBoundsException exc) {
            System.err.println("Color doesn't exist in util.MyColor!");
            return null;
        }
    }

    public static int getNbDefinedColors() {
        return colors.length;
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    private static final Color y = Color.yellow;
    private static final Color r = Color.red;
    private static final Color g = Color.green;
    private static final Color b = Color.blue;
    private static final Color p = Color.pink;
    private static final Color m = Color.magenta;
    private static final Color[] colors = {
        y, r, g, b, p, m};

}