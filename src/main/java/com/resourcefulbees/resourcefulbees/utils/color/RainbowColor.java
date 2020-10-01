package com.resourcefulbees.resourcefulbees.utils.color;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

public class RainbowColor {

    private static int r = 255, g = 0, b = 0;

    public static void init() { new Timer().scheduleAtFixedRate(new ColorChange(), 0, 40); }

    public static Color getColor() { return new Color(r,g,b); }

    public static float[] getColorFloats() { return getColor().getColorComponents(null); }

    public static int getRGB() { return getColor().getRGB(); }

    private static class ColorChange extends TimerTask {

        @Override
        public void run() {
            if(r > 0 && b == 0){
                r--;
                g++;
            }
            if(g > 0 && r == 0){
                g--;
                b++;
            }
            if(b > 0 && g == 0){
                r++;
                b--;
            }
        }
    }
}
