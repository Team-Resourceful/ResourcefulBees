package com.resourcefulbees.resourcefulbees.utils.color;
/*
 * SilentLib - Color
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.google.common.primitives.UnsignedInts;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "MagicNumber"})
public class Color {
    private static final Map<String, Color> NAMED_MAP = new HashMap<>();
    private static final Pattern PATTERN_LEADING_JUNK = Pattern.compile("(#|0x)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_HEX_CODE = Pattern.compile("(#|0x)[0-9a-f]{1,8}", Pattern.CASE_INSENSITIVE); //Removed `?` to force requirement

    public static final int VALUE_WHITE = 0xFFFFFF;

    //region CSS Colors
    public static final Color ALICEBLUE = named("AliceBlue", 0xF0F8FF);
    public static final Color ANTIQUEWHITE = named("AntiqueWhite", 0xFAEBD7);
    public static final Color AQUA = named("Aqua", 0x00FFFF);
    public static final Color AQUAMARINE = named("Aquamarine", 0x7FFFD4);
    public static final Color AZURE = named("Azure", 0xF0FFFF);
    public static final Color BEIGE = named("Beige", 0xF5F5DC);
    public static final Color BISQUE = named("Bisque", 0xFFE4C4);
    public static final Color BLACK = named("Black", 0x000000);
    public static final Color BLANCHEDALMOND = named("BlanchedAlmond", 0xFFEBCD);
    public static final Color BLUE = named("Blue", 0x0000FF);
    public static final Color BLUEVIOLET = named("BlueViolet", 0x8A2BE2);
    public static final Color BROWN = named("Brown", 0xA52A2A);
    public static final Color BURLYWOOD = named("BurlyWood", 0xDEB887);
    public static final Color CADETBLUE = named("CadetBlue", 0x5F9EA0);
    public static final Color CHARTREUSE = named("Chartreuse", 0x7FFF00);
    public static final Color CHOCOLATE = named("Chocolate", 0xD2691E);
    public static final Color CORAL = named("Coral", 0xFF7F50);
    public static final Color CORNFLOWERBLUE = named("CornflowerBlue", 0x6495ED);
    public static final Color CORNSILK = named("Cornsilk", 0xFFF8DC);
    public static final Color CRIMSON = named("Crimson", 0xDC143C);
    public static final Color CYAN = named("Cyan", 0x00FFFF);
    public static final Color DARKBLUE = named("DarkBlue", 0x00008B);
    public static final Color DARKCYAN = named("DarkCyan", 0x008B8B);
    public static final Color DARKGOLDENROD = named("DarkGoldenRod", 0xB8860B);
    public static final Color DARKGRAY = named("DarkGray", 0xA9A9A9);
    public static final Color DARKGREY = named("DarkGrey", 0xA9A9A9);
    public static final Color DARKGREEN = named("DarkGreen", 0x006400);
    public static final Color DARKKHAKI = named("DarkKhaki", 0xBDB76B);
    public static final Color DARKMAGENTA = named("DarkMagenta", 0x8B008B);
    public static final Color DARKOLIVEGREEN = named("DarkOliveGreen", 0x556B2F);
    public static final Color DARKORANGE = named("DarkOrange", 0xFF8C00);
    public static final Color DARKORCHID = named("DarkOrchid", 0x9932CC);
    public static final Color DARKRED = named("DarkRed", 0x8B0000);
    public static final Color DARKSALMON = named("DarkSalmon", 0xE9967A);
    public static final Color DARKSEAGREEN = named("DarkSeaGreen", 0x8FBC8F);
    public static final Color DARKSLATEBLUE = named("DarkSlateBlue", 0x483D8B);
    public static final Color DARKSLATEGRAY = named("DarkSlateGray", 0x2F4F4F);
    public static final Color DARKSLATEGREY = named("DarkSlateGrey", 0x2F4F4F);
    public static final Color DARKTURQUOISE = named("DarkTurquoise", 0x00CED1);
    public static final Color DARKVIOLET = named("DarkViolet", 0x9400D3);
    public static final Color DEEPPINK = named("DeepPink", 0xFF1493);
    public static final Color DEEPSKYBLUE = named("DeepSkyBlue", 0x00BFFF);
    public static final Color DIMGRAY = named("DimGray", 0x696969);
    public static final Color DIMGREY = named("DimGrey", 0x696969);
    public static final Color DODGERBLUE = named("DodgerBlue", 0x1E90FF);
    public static final Color FIREBRICK = named("FireBrick", 0xB22222);
    public static final Color FLORALWHITE = named("FloralWhite", 0xFFFAF0);
    public static final Color FORESTGREEN = named("ForestGreen", 0x228B22);
    public static final Color FUCHSIA = named("Fuchsia", 0xFF00FF);
    public static final Color GAINSBORO = named("Gainsboro", 0xDCDCDC);
    public static final Color GHOSTWHITE = named("GhostWhite", 0xF8F8FF);
    public static final Color GOLD = named("Gold", 0xFFD700);
    public static final Color GOLDENROD = named("GoldenRod", 0xDAA520);
    public static final Color GRAY = named("Gray", 0x808080);
    public static final Color GREY = named("Grey", 0x808080);
    public static final Color GREEN = named("Green", 0x008000);
    public static final Color GREENYELLOW = named("GreenYellow", 0xADFF2F);
    public static final Color HONEYDEW = named("HoneyDew", 0xF0FFF0);
    public static final Color HOTPINK = named("HotPink", 0xFF69B4);
    public static final Color INDIANRED = named("IndianRed", 0xCD5C5C);
    public static final Color INDIGO = named("Indigo", 0x4B0082);
    public static final Color IVORY = named("Ivory", 0xFFFFF0);
    public static final Color KHAKI = named("Khaki", 0xF0E68C);
    public static final Color LAVENDER = named("Lavender", 0xE6E6FA);
    public static final Color LAVENDERBLUSH = named("LavenderBlush", 0xFFF0F5);
    public static final Color LAWNGREEN = named("LawnGreen", 0x7CFC00);
    public static final Color LEMONCHIFFON = named("LemonChiffon", 0xFFFACD);
    public static final Color LIGHTBLUE = named("LightBlue", 0xADD8E6);
    public static final Color LIGHTCORAL = named("LightCoral", 0xF08080);
    public static final Color LIGHTCYAN = named("LightCyan", 0xE0FFFF);
    public static final Color LIGHTGOLDENRODYELLOW = named("LightGoldenRodYellow", 0xFAFAD2);
    public static final Color LIGHTGRAY = named("LightGray", 0xD3D3D3);
    public static final Color LIGHTGREY = named("LightGrey", 0xD3D3D3);
    public static final Color LIGHTGREEN = named("LightGreen", 0x90EE90);
    public static final Color LIGHTPINK = named("LightPink", 0xFFB6C1);
    public static final Color LIGHTSALMON = named("LightSalmon", 0xFFA07A);
    public static final Color LIGHTSEAGREEN = named("LightSeaGreen", 0x20B2AA);
    public static final Color LIGHTSKYBLUE = named("LightSkyBlue", 0x87CEFA);
    public static final Color LIGHTSLATEGRAY = named("LightSlateGray", 0x778899);
    public static final Color LIGHTSLATEGREY = named("LightSlateGrey", 0x778899);
    public static final Color LIGHTSTEELBLUE = named("LightSteelBlue", 0xB0C4DE);
    public static final Color LIGHTYELLOW = named("LightYellow", 0xFFFFE0);
    public static final Color LIME = named("Lime", 0x00FF00);
    public static final Color LIMEGREEN = named("LimeGreen", 0x32CD32);
    public static final Color LINEN = named("Linen", 0xFAF0E6);
    public static final Color MAGENTA = named("Magenta", 0xFF00FF);
    public static final Color MAROON = named("Maroon", 0x800000);
    public static final Color MEDIUMAQUAMARINE = named("MediumAquaMarine", 0x66CDAA);
    public static final Color MEDIUMBLUE = named("MediumBlue", 0x0000CD);
    public static final Color MEDIUMORCHID = named("MediumOrchid", 0xBA55D3);
    public static final Color MEDIUMPURPLE = named("MediumPurple", 0x9370DB);
    public static final Color MEDIUMSEAGREEN = named("MediumSeaGreen", 0x3CB371);
    public static final Color MEDIUMSLATEBLUE = named("MediumSlateBlue", 0x7B68EE);
    public static final Color MEDIUMSPRINGGREEN = named("MediumSpringGreen", 0x00FA9A);
    public static final Color MEDIUMTURQUOISE = named("MediumTurquoise", 0x48D1CC);
    public static final Color MEDIUMVIOLETRED = named("MediumVioletRed", 0xC71585);
    public static final Color MIDNIGHTBLUE = named("MidnightBlue", 0x191970);
    public static final Color MINTCREAM = named("MintCream", 0xF5FFFA);
    public static final Color MISTYROSE = named("MistyRose", 0xFFE4E1);
    public static final Color MOCCASIN = named("Moccasin", 0xFFE4B5);
    public static final Color NAVAJOWHITE = named("NavajoWhite", 0xFFDEAD);
    public static final Color NAVY = named("Navy", 0x000080);
    public static final Color OLDLACE = named("OldLace", 0xFDF5E6);
    public static final Color OLIVE = named("Olive", 0x808000);
    public static final Color OLIVEDRAB = named("OliveDrab", 0x6B8E23);
    public static final Color ORANGE = named("Orange", 0xFFA500);
    public static final Color ORANGERED = named("OrangeRed", 0xFF4500);
    public static final Color ORCHID = named("Orchid", 0xDA70D6);
    public static final Color PALEGOLDENROD = named("PaleGoldenRod", 0xEEE8AA);
    public static final Color PALEGREEN = named("PaleGreen", 0x98FB98);
    public static final Color PALETURQUOISE = named("PaleTurquoise", 0xAFEEEE);
    public static final Color PALEVIOLETRED = named("PaleVioletRed", 0xDB7093);
    public static final Color PAPAYAWHIP = named("PapayaWhip", 0xFFEFD5);
    public static final Color PEACHPUFF = named("PeachPuff", 0xFFDAB9);
    public static final Color PERU = named("Peru", 0xCD853F);
    public static final Color PINK = named("Pink", 0xFFC0CB);
    public static final Color PLUM = named("Plum", 0xDDA0DD);
    public static final Color POWDERBLUE = named("PowderBlue", 0xB0E0E6);
    public static final Color PURPLE = named("Purple", 0x800080);
    public static final Color REBECCAPURPLE = named("RebeccaPurple", 0x663399);
    public static final Color RED = named("Red", 0xFF0000);
    public static final Color ROSYBROWN = named("RosyBrown", 0xBC8F8F);
    public static final Color ROYALBLUE = named("RoyalBlue", 0x4169E1);
    public static final Color SADDLEBROWN = named("SaddleBrown", 0x8B4513);
    public static final Color SALMON = named("Salmon", 0xFA8072);
    public static final Color SANDYBROWN = named("SandyBrown", 0xF4A460);
    public static final Color SEAGREEN = named("SeaGreen", 0x2E8B57);
    public static final Color SEASHELL = named("SeaShell", 0xFFF5EE);
    public static final Color SIENNA = named("Sienna", 0xA0522D);
    public static final Color SILVER = named("Silver", 0xC0C0C0);
    public static final Color SKYBLUE = named("SkyBlue", 0x87CEEB);
    public static final Color SLATEBLUE = named("SlateBlue", 0x6A5ACD);
    public static final Color SLATEGRAY = named("SlateGray", 0x708090);
    public static final Color SLATEGREY = named("SlateGrey", 0x708090);
    public static final Color SNOW = named("Snow", 0xFFFAFA);
    public static final Color SPRINGGREEN = named("SpringGreen", 0x00FF7F);
    public static final Color STEELBLUE = named("SteelBlue", 0x4682B4);
    public static final Color TAN = named("Tan", 0xD2B48C);
    public static final Color TEAL = named("Teal", 0x008080);
    public static final Color THISTLE = named("Thistle", 0xD8BFD8);
    public static final Color TOMATO = named("Tomato", 0xFF6347);
    public static final Color TURQUOISE = named("Turquoise", 0x40E0D0);
    public static final Color VIOLET = named("Violet", 0xEE82EE);
    public static final Color WHEAT = named("Wheat", 0xF5DEB3);
    public static final Color WHITE = named("White", 0xFFFFFF);
    public static final Color WHITESMOKE = named("WhiteSmoke", 0xF5F5F5);
    public static final Color YELLOW = named("Yellow", 0xFFFF00);
    public static final Color YELLOWGREEN = named("YellowGreen", 0x9ACD32);
    //endregion

    private final int c;
    private final int r;
    private final int g;
    private final int b;
    private final int alpha;

    public Color(int c) {
        this.r = (c >> 16) & 0xFF;
        this.g = (c >> 8) & 0xFF;
        this.b = c & 0xFF;
        int a = (c >> 24) & 0xFF;
        this.alpha = a > 0 ? a : 0xFF;
        this.c = (this.alpha << 24) | (c & 0xFFFFFF);
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int alpha) {
        this.alpha = alpha > 0 ? alpha : 0xFF;
        this.r = r;
        this.g = g;
        this.b = b;
        this.c = (this.alpha << 24) | (this.r << 16) | (this.g << 8) | this.b;
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1f);
    }

    public Color(float r, float g, float b, float alpha) {
        this((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (alpha * 255));
    }

    private static Color named(String name, int color) {
        Color c = new Color(color);
        NAMED_MAP.put(name.toLowerCase(Locale.ROOT), c);
        return c;
    }

    //region Forge Config

    /**
     * Validator for ForgeConfigSpec. Accepts CSS color names or hex codes with optional leading '#'
     * or '0x'.
     *
     * @param str The string to validate
     * @return True if and only if the string can be parsed
     */
    public static boolean validate(String str) {
        return NAMED_MAP.containsKey(str.toLowerCase(Locale.ROOT))
                || PATTERN_HEX_CODE.matcher(str).matches();
    }

    //endregion

    //region Parsing and Formatting

    /**
     * Format the color as it would appear in a config file.
     *
     * @param color The color
     * @return A string in the form "#XXXXXX" or "#XXXXXXXX", where 'X' is a hex digit.
     */
    public static String format(int color) {
        return String.format(color > 0xFFFFFF ? "#%08X" : "#%06X", color);
    }

    /**
     * Attempt to parse the string as a color. May be either a common CSS color name or a hex code
     * with optional leading '#' or '0x'. Consider using {@code define} or {@code defineInt}
     * instead, which handles validation.
     *
     * @param str The string to try to parse
     * @return A Color object based on str
     * @throws NumberFormatException If the string cannot be parsed
     * @throws NullPointerException  If the string is null
     * Uses {@link UnsignedInts#parseUnsignedInt(String, int)} for parsing
     */
    public static Color parse(String str) {
        return new Color(parseInt(str));
    }

    /**
     * Attempt to parse a color, returning the default if it is not valid.
     *
     * @param str          The string to parse
     * @param defaultValue The fallback value
     * @return A color parsed from str, or from defaultValue if str is invalid
     */
    public static Color tryParse(String str, int defaultValue) {
        if (!validate(str)) return new Color(defaultValue);
        return parse(str);
    }

    /**
     * Attempt to parse the string as a color, but returns the integer representation instead of
     * creating a Color object. May be either a common CSS color name or a hex code with optional
     * leading '#' or '0x'.
     *
     * @param str The string to try to parse
     * @return A Color object based on str
     * @throws NumberFormatException If the string cannot be parsed
     * @throws NullPointerException  If the string is null
     * Uses {@link UnsignedInts#parseUnsignedInt(String, int)} for parsing
     */
    public static int parseInt(String str) {
        // Named color?
        str = str.toLowerCase(Locale.ROOT);
        if (NAMED_MAP.containsKey(str)) return NAMED_MAP.get(str).getC();

        // Hex code
        str = PATTERN_LEADING_JUNK.matcher(str).replaceFirst("");
        return UnsignedInts.parseUnsignedInt(str, 16);
    }

    /**
     * Read a color from JSON. The property must be a string. If the JsonObject does not contain a
     * property with the given name, a Color is created from defaultValue. If the valid from JSON is
     * invalid, an exception will be thrown.
     *
     * @param json         The JSON object
     * @param propertyName The property to read from json
     * @param defaultValue A default value to use if json does not have the given property
     * @return A color parsed from the value read from json
     * @throws NumberFormatException If the value from json cannot be parsed
     */
    public static Color from(JsonObject json, String propertyName, int defaultValue) {
        String defaultStr = Integer.toHexString(defaultValue);
        JsonElement element = json.get(propertyName);
        if (element == null || !element.isJsonPrimitive())
            return Color.parse(defaultStr);
        return Color.parse(element.getAsString());
    }

    //endregion

    public Color blendWith(Color other) {
        return blend(this, other);
    }

    public static Color blend(Color color1, Color color2) {
        return blend(color1, color2, 0.5f);
    }

    public static Color blend(Color color1, Color color2, float ratio) {

        int color = blend(color1.c, color2.c, ratio);
        return new Color(color);
    }

    public static int blend(int color1, int color2) {
        return blend(color1, color2, 0.5f);
    }

    public static int blend(int color1, int color2, float ratio) {
        ratio = MathUtils.clamp(ratio, 0f, 1f);
        float iRatio = 1f - ratio;

        int a1 = (color1 >> 24 & 0xff);
        int r1 = ((color1 & 0xff0000) >> 16);
        int g1 = ((color1 & 0xff00) >> 8);
        int b1 = (color1 & 0xff);

        int a2 = (color2 >> 24 & 0xff);
        int r2 = ((color2 & 0xff0000) >> 16);
        int g2 = ((color2 & 0xff00) >> 8);
        int b2 = (color2 & 0xff);

        int a = (int) ((a1 * iRatio) + (a2 * ratio));
        int r = (int) ((r1 * iRatio) + (r2 * ratio));
        int g = (int) ((g1 * iRatio) + (g2 * ratio));
        int b = (int) ((b1 * iRatio) + (b2 * ratio));

        return a << 24 | r << 16 | g << 8 | b;
    }

    public int getC() {
        return c;
    }

    public float getR() {
        return r / 255f;
    }

    public float getG() {
        return g / 255f;
    }

    public float getB() {
        return b / 255f;
    }

    public float getAlpha() {
        return alpha / 255f;
    }

    public int getRedInt() {
        return r;
    }

    public int getGreenInt() {
        return g;
    }

    public int getBlueInt() {
        return b;
    }

    public int getAlphaInt() {
        return alpha;
    }
}
