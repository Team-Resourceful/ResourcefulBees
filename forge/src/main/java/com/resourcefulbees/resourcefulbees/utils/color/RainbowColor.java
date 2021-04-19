package com.resourcefulbees.resourcefulbees.utils.color;

import com.resourcefulbees.resourcefulbees.lib.ModConstants;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

public class RainbowColor {

    private RainbowColor() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static int r = 255;
    private static int g = 0;
    private static int b = 0;

    public static void init() { new Timer().scheduleAtFixedRate(new ColorChange(), 0, 40); }

    public static Color getColor() { return new Color(r,g,b); }

    public static float[] getColorFloats() { return getColor().getColorComponents(null); }

    public static int getRGB() { return getColor().getRGB(); }


    private static class ColorChange extends TimerTask {

        private static void incrementRed() {
            r++;
        }

        private static void decrementRed() {
            r--;
        }

        private static void incrementGreen() {
            g++;
        }

        private static void decrementGreen() {
            g--;
        }

        private static void incrementBlue() {
            b++;
        }

        private static void decrementBlue() {
            b--;
        }

        @Override
        public void run() {
            if(r > 0 && b == 0){
                decrementRed();
                incrementGreen();
            }
            if(g > 0 && r == 0){
                decrementGreen();
                incrementBlue();
            }
            if(b > 0 && g == 0){
                incrementRed();
                decrementBlue();
            }
        }
    }
}
