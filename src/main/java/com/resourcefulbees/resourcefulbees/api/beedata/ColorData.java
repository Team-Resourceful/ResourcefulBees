package com.resourcefulbees.resourcefulbees.api.beedata;

import java.awt.*;

public class ColorData {
    private final String primaryColor;
    private final String secondaryColor;
    private final String honeycombColor;
    private final String primaryLayerTexture;
    private final String secondaryLayerTexture;
    private final String emissiveLayerTexture;
    private final boolean isBeeColored;
    private final boolean isRainbowBee;
    private final boolean isGlowing;
    private final String glowColor;
    private final boolean isEnchanted;
    private final int glowingPulse;

    private ColorData(String primaryColor, String secondaryColor, String honeycombColor, String primaryLayerTexture, String secondaryLayerTexture, String emissiveLayerTexture, boolean isBeeColored, boolean isRainbowBee, boolean isGlowing, String glowColor, boolean isEnchanted, int glowingPulse) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.honeycombColor = honeycombColor;
        this.primaryLayerTexture = primaryLayerTexture;
        this.secondaryLayerTexture = secondaryLayerTexture;
        this.emissiveLayerTexture = emissiveLayerTexture;
        this.isBeeColored = isBeeColored;
        this.isRainbowBee = isRainbowBee;
        this.isGlowing = isGlowing;
        this.glowColor = glowColor;
        this.isEnchanted = isEnchanted;
        this.glowingPulse = glowingPulse;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public String getHoneycombColor() {
        return honeycombColor;
    }

    public String getPrimaryLayerTexture() {
        return primaryLayerTexture;
    }

    public String getSecondaryLayerTexture() {
        return secondaryLayerTexture;
    }

    public String getEmissiveLayerTexture() {
        return emissiveLayerTexture;
    }

    public boolean isBeeColored() {
        return isBeeColored;
    }

    public boolean isRainbowBee() {
        return isRainbowBee;
    }

    public boolean isGlowing() {
        return isGlowing;
    }

    public String getGlowColor() {
        return glowColor;
    }

    public boolean isEnchanted() {
        return isEnchanted;
    }

    public int getGlowingPulse() {
        return glowingPulse;
    }

    public boolean hasPrimaryColor() {
        return primaryColor != null;
    }

    public boolean hasSecondaryColor() {
        return secondaryColor != null;
    }

    public boolean hasHoneycombColor() {
        return honeycombColor != null;
    }

    public boolean hasGlowingColor() {
        return glowColor != null;
    }

    public float[] getPrimaryColorFloats(){
        Color tempColor = Color.decode(primaryColor);
        return tempColor.getComponents(null);
    }

    public float[] getSecondaryColorFloats(){
        Color tempColor = Color.decode(secondaryColor);
        return tempColor.getComponents(null);
    }

    public float[] getGlowColorFloats(){
        Color tempColor = Color.decode(glowColor);
        return tempColor.getComponents(null);
    }

    public static class Builder {
        private String primaryColor;
        private String secondaryColor;
        private String honeycombColor;
        private String glowColor = "#ffffff";
        private String primaryLayerTexture = "/custom/primary_layer";
        private String secondaryLayerTexture = "/custom/secondary_layer";
        private String emissiveLayerTexture = "/custom/emissive_layer";
        private final boolean isBeeColored;
        private boolean isRainbowBee = false;
        private boolean isGlowing = false;
        private boolean isEnchanted = false;
        private int glowingPulse = 0;

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

        public ColorData createColorData() {
            return new ColorData(primaryColor, secondaryColor, honeycombColor, primaryLayerTexture, secondaryLayerTexture, emissiveLayerTexture, isBeeColored, isRainbowBee, isGlowing, glowColor, isEnchanted, glowingPulse);
        }
    }
}
