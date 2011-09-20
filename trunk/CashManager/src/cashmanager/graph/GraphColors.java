
package cashmanager.graph;

import java.awt.Color;

/**
 *
 * @author Kiko
 */
public class GraphColors {
    public static final int COLORS_LENGTH = 4;
    private static final int MAX_VALUE = 255;
    private static final int COLOR_VALUE = 100;
    public static final Color IN_COLOR = Color.BLUE;
    public static final Color OUT_COLOR = Color.RED;
    public static Color[] inColors = initIncomeColors();
    public static Color[] outColors = initOutcomeColors();
    public static final Color BALANCE_COLOR = Color.GRAY;

    private static Color[] initIncomeColors(){
        Color[] inCol = new Color[COLORS_LENGTH];
//        inCol[0] = Color.BLUE;
        int colorVal = COLOR_VALUE;
        for(int i = 0; i < COLORS_LENGTH; i++){
            inCol[i] = new Color(colorVal, colorVal, MAX_VALUE);
            colorVal += 50;
        }
        return inCol;
    }
    private static Color[] initOutcomeColors(){
        Color[] outCol = new Color[COLORS_LENGTH];
//        outCol[0] = Color.RED;
        int colorVal = COLOR_VALUE;
        for(int i = 0; i < COLORS_LENGTH; i++){
            outCol[i] = new Color(MAX_VALUE, colorVal, colorVal);
            colorVal += 50;
        }
        return outCol;
    }
}//GraphColors
