package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.ModelTypes;

import java.awt.*;

@SuppressWarnings("unused")
public class ColorData extends AbstractBeeData {

    private static final String WHITE = "#ffffff";

    /**
     * The color the bee is primary tinted in.
     */
    private final String primaryColor;

    /**
     * The color the bee is secondary tinted in.
     */
    private final String secondaryColor;

    /**
     * The color the comb is tinted in.
     */
    private final String honeycombColor;

    /**
     * A custom texture specified for the primary texture layer of the bee.
     */
    private final String primaryLayerTexture;

    /**
     * A custom texture specified for the secondary texture layer of the bee.
     */
    private final String secondaryLayerTexture;

    /**
     * A custom texture specified for the emissive texture layer of the bee.
     */
    private final String emissiveLayerTexture;

    /**
     * A custom texture specified for the gel layer of the bee.
     * Does not have a default value!!
     */
    private final String gelLayerTexture;

    /**
     * If the bee is colored.
     */
    private final boolean isBeeColored;

    /**
     * If the bee changes its color like a rainbow.
     */
    private final boolean isRainbowBee;

    /**
     * If the bee is glowing.
     */
    private final boolean isGlowing;

    /**
     * The color the bee is glowing in.
     */
    private final String glowColor;

    /**
     * If the comb has an enchantment glint.
     */
    private final boolean isEnchanted;

    private final int glowingPulse;

    private final ModelTypes modelType;

    private ColorData(String primaryColor, String secondaryColor, String honeycombColor, String primaryLayerTexture, String secondaryLayerTexture, String emissiveLayerTexture, String gelLayerTexture, boolean isBeeColored, boolean isRainbowBee, boolean isGlowing, String glowColor, boolean isEnchanted, int glowingPulse, ModelTypes modelType) {
        super("ColorData");
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.honeycombColor = honeycombColor;
        this.primaryLayerTexture = primaryLayerTexture;
        this.secondaryLayerTexture = secondaryLayerTexture;
        this.emissiveLayerTexture = emissiveLayerTexture;
        this.gelLayerTexture = gelLayerTexture;
        this.isBeeColored = isBeeColored;
        this.isRainbowBee = isRainbowBee;
        this.isGlowing = isGlowing;
        this.glowColor = glowColor;
        this.isEnchanted = isEnchanted;
        this.glowingPulse = glowingPulse;
        this.modelType = modelType;
    }

    public String getPrimaryColor() { return primaryColor == null ? WHITE :  primaryColor; }

    public String getSecondaryColor() { return secondaryColor == null ? "#303030" : secondaryColor; }

    public String getHoneycombColor() { return honeycombColor == null ? WHITE : honeycombColor; }

    public String getGlowColor() { return glowColor == null ? WHITE : glowColor; }

    public String getPrimaryLayerTexture() { return primaryLayerTexture == null ? "custom/primary_layer" : primaryLayerTexture; }

    public String getSecondaryLayerTexture() { return secondaryLayerTexture == null ? "custom/secondary_layer" : secondaryLayerTexture; }

    public String getEmissiveLayerTexture() { return emissiveLayerTexture == null ? "custom/emissive_layer" : emissiveLayerTexture; }

    public String getGelLayerTexture() { return gelLayerTexture; }

    public boolean isBeeColored() { return isBeeColored; }

    public boolean isRainbowBee() { return isRainbowBee; }

    public boolean isGlowing() { return isGlowing; }

    public boolean isEnchanted() { return isEnchanted; }

    public boolean hasPrimaryColor() { return primaryColor != null && !primaryColor.isEmpty(); }

    public boolean hasSecondaryColor() { return secondaryColor != null && !secondaryColor.isEmpty(); }

    public boolean hasHoneycombColor() { return honeycombColor != null && !honeycombColor.isEmpty(); }

    public boolean hasGlowColor() { return glowColor != null && !glowColor.isEmpty(); }

    public int getGlowingPulse() { return glowingPulse; }

    public int getPrimaryColorInt() { return com.resourcefulbees.resourcefulbees.utils.color.Color.parseInt(getPrimaryColor()); }

    public int getSecondaryColorInt() { return com.resourcefulbees.resourcefulbees.utils.color.Color.parseInt(getSecondaryColor()); }

    public int getHoneycombColorInt() { return com.resourcefulbees.resourcefulbees.utils.color.Color.parseInt(getHoneycombColor()); }

    public ModelTypes getModelType() { return modelType != null ? modelType : ModelTypes.DEFAULT; }

    public float[] getPrimaryColorFloats(){
        Color tempColor = Color.decode(getPrimaryColor());
        return tempColor.getComponents(null);
    }

    public float[] getSecondaryColorFloats(){
        Color tempColor = Color.decode(getSecondaryColor());
        return tempColor.getComponents(null);
    }

    public float[] getGlowColorFloats(){
        Color tempColor = Color.decode(getGlowColor());
        return tempColor.getComponents(null);
    }

    public static class Builder {
        private String primaryColor;
        private String secondaryColor;
        private String honeycombColor;
        private String glowColor;
        private String primaryLayerTexture;
        private String secondaryLayerTexture;
        private String emissiveLayerTexture;
        private String gelLayerTexture;
        private final boolean isBeeColored;
        private boolean isRainbowBee;
        private boolean isGlowing;
        private boolean isEnchanted;
        private int glowingPulse;
        private ModelTypes modelType;

        public Builder(boolean isBeeColored) {
            this.isBeeColored = isBeeColored;
        }

        public Builder setPrimaryColor(String primaryColor) {
            this.primaryColor = primaryColor;
            return this;
        }

        public Builder setSecondaryColor(String secondaryColor) {
            this.secondaryColor = secondaryColor;
            return this;
        }

        public Builder setHoneycombColor(String honeycombColor) {
            this.honeycombColor = honeycombColor;
            return this;
        }

        public Builder setPrimaryLayerTexture(String primaryLayerTexture) {
            this.primaryLayerTexture = primaryLayerTexture;
            return this;
        }

        public Builder setSecondaryLayerTexture(String secondaryLayerTexture) {
            this.secondaryLayerTexture = secondaryLayerTexture;
            return this;
        }

        public Builder setEmissiveLayerTexture(String emissiveLayerTexture) {
            this.emissiveLayerTexture = emissiveLayerTexture;
            return this;
        }

        public Builder setGelLayerTexture(String gelLayerTexture) {
            this.gelLayerTexture = gelLayerTexture;
            return this;
        }

        public Builder setIsRainbowBee(boolean isRainbowBee) {
            this.isRainbowBee = isRainbowBee;
            return this;
        }

        public Builder setIsGlowing(boolean isGlowing) {
            this.isGlowing = isGlowing;
            return this;
        }

        public Builder setGlowColor(String glowColor) {
            this.glowColor = glowColor;
            return this;
        }

        public Builder setIsEnchanted(boolean isEnchanted) {
            this.isEnchanted = isEnchanted;
            return this;
        }

        public Builder setGlowingPulse(int glowingPulse) {
            this.glowingPulse = glowingPulse;
            return this;
        }

        public Builder setModelType(ModelTypes modelType) {
            this.modelType = modelType;
            return this;
        }

        public ColorData createColorData() {
            return new ColorData(primaryColor, secondaryColor, honeycombColor, primaryLayerTexture, secondaryLayerTexture, emissiveLayerTexture, gelLayerTexture, isBeeColored, isRainbowBee, isGlowing, glowColor, isEnchanted, glowingPulse, modelType);
        }
    }

    public static ColorData createDefault() { return new Builder(false).createColorData(); }
}
