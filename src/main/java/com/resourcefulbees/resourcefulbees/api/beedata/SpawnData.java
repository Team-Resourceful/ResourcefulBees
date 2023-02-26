package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.LightLevels;

import java.util.Locale;

public class SpawnData extends AbstractBeeData {
    /**
     * If the bee can spawn naturally
     */
    private final boolean canSpawnInWorld;

    /**
     * How common a bee is
     */
    private final int spawnWeight;

    /**
     * How big a group has to be
     */
    private final int minGroupSize;

    /**
     * The maximum size a group of bees can be.
     */
    private final int maxGroupSize;

    /**
     * What biomes the bee can only spawn in.
     */
    private final String biomeWhitelist;

    /**
     * What biomes the bee will not spawn. No matter if the are in the whitelist.
     */
    private final String biomeBlacklist;

    /**
     * What LightLevel the Bee needs to spawn.
     */
    private final LightLevels lightLevel;

    private final int minYLevel;

    private final int maxYLevel;

    private SpawnData(boolean canSpawnInWorld, int spawnWeight, int minGroupSize, int maxGroupSize, String biomeWhitelist, String biomeBlacklist, LightLevels lightLevel, int minYLevel, int maxYLevel) {
        super("SpawnData");
        this.canSpawnInWorld = canSpawnInWorld;
        this.spawnWeight = spawnWeight;
        this.minGroupSize = minGroupSize;
        this.maxGroupSize = maxGroupSize;
        this.biomeWhitelist = biomeWhitelist;
        this.biomeBlacklist = biomeBlacklist;
        this.lightLevel = lightLevel;
        this.minYLevel = minYLevel;
        this.maxYLevel = maxYLevel;
    }

    public boolean canSpawnInWorld() { return canSpawnInWorld; }

    public int getSpawnWeight() { return spawnWeight <= 0 ? 8 : spawnWeight; }

    public int getMinGroupSize() { return Math.max(minGroupSize, 0); }

    public int getMaxGroupSize() { return maxGroupSize <= 0 ? 3 : maxGroupSize; }

    public String getBiomeWhitelist() { return biomeWhitelist != null ? biomeWhitelist.toLowerCase(Locale.ENGLISH) : "tag:overworld"; }

    public String getBiomeBlacklist() {
        if (biomeBlacklist != null) {
            return biomeBlacklist.toLowerCase(Locale.ENGLISH);
        }

        if (biomeWhitelist != null) {
            return biomeWhitelist.equals("tag:ocean") ? "" : "tag:ocean";
        }
        
        return "";
    }

    public LightLevels getLightLevel() { return lightLevel != null ? lightLevel : LightLevels.ANY; }

    public int getMinYLevel() {
        return minYLevel <= 0 ? 1 : minYLevel;
    }

    public int getMaxYLevel() {
        return maxYLevel <= 0 ? 256 : maxYLevel;
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private final boolean canSpawnInWorld;
        private int spawnWeight;
        private int minGroupSize;
        private int maxGroupSize;
        private String biomeWhitelist;
        private String biomeBlacklist;
        private LightLevels lightLevel;
        private int minYLevel;
        private int maxYLevel;

        public Builder(boolean canSpawnInWorld) { this.canSpawnInWorld = canSpawnInWorld; }

        public Builder setSpawnWeight(int spawnWeight) {
            this.spawnWeight = spawnWeight;
            return this;
        }

        public Builder setMinGroupSize(int minGroupSize) {
            this.minGroupSize = minGroupSize;
            return this;
        }

        public Builder setMaxGroupSize(int maxGroupSize) {
            this.maxGroupSize = maxGroupSize;
            return this;
        }

        public Builder setMinYLevel(int minYLevel) {
            this.minYLevel = minYLevel;
            return this;
        }

        public Builder setMaxYLevel(int maxYLevel) {
            this.maxYLevel = maxYLevel;
            return this;
        }

        public Builder setBiomeWhitelist(String biomeWhitelist) {
            this.biomeWhitelist = biomeWhitelist;
            return this;
        }

        public Builder setBiomeBlacklist(String biomeBlacklist) {
            this.biomeBlacklist = biomeBlacklist;
            return this;
        }

        public Builder setLightLevel(LightLevels lightLevel) {
            this.lightLevel = lightLevel;
            return this;
        }

        public SpawnData createSpawnData() {
            return new SpawnData(canSpawnInWorld, spawnWeight, minGroupSize, maxGroupSize, biomeWhitelist, biomeBlacklist, lightLevel, minYLevel, maxYLevel);
        }
    }

    public static SpawnData createDefault() { return new Builder(false).createSpawnData(); }
}
