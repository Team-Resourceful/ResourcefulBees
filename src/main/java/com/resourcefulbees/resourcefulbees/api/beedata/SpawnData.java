package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.LightLevels;

public class SpawnData extends AbstractBeeData {
    private final boolean canSpawnInWorld;
    private final int spawnWeight;
    private final int minGroupSize;
    private final int maxGroupSize;
    private final String biomeWhitelist;
    private final String biomeBlacklist;
    private final LightLevels lightLevel;

    private SpawnData(boolean canSpawnInWorld, int spawnWeight, int minGroupSize, int maxGroupSize, String biomeWhitelist, String biomeBlacklist, LightLevels lightLevel) {
        this.canSpawnInWorld = canSpawnInWorld;
        this.spawnWeight = spawnWeight;
        this.minGroupSize = minGroupSize;
        this.maxGroupSize = maxGroupSize;
        this.biomeWhitelist = biomeWhitelist;
        this.biomeBlacklist = biomeBlacklist;
        this.lightLevel = lightLevel;
    }

    public boolean canSpawnInWorld() { return canSpawnInWorld; }

    public int getSpawnWeight() { return spawnWeight <= 0 ? 8 : spawnWeight; }

    public int getMinGroupSize() { return Math.max(minGroupSize, 0); }

    public int getMaxGroupSize() { return maxGroupSize <= 0 ? 3 : maxGroupSize; }

    public String getBiomeWhitelist() { return biomeWhitelist != null ? biomeWhitelist.toLowerCase() : "tag:overworld"; }

    public String getBiomeBlacklist() { return biomeBlacklist != null ? biomeBlacklist.toLowerCase() : "tag:ocean"; }

    public LightLevels getLightLevel() { return lightLevel != null ? lightLevel : LightLevels.ANY; }

    public static class Builder {
        private final boolean canSpawnInWorld;
        private int spawnWeight;
        private int minGroupSize;
        private int maxGroupSize;
        private String biomeWhitelist;
        private String biomeBlacklist;
        private LightLevels lightLevel;

        public Builder(boolean canSpawnInWorld) {
            this.canSpawnInWorld = canSpawnInWorld;
        }

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
            return new SpawnData(canSpawnInWorld, spawnWeight, minGroupSize, maxGroupSize, biomeWhitelist, biomeBlacklist, lightLevel);
        }
    }

    public static SpawnData createDefault() {
        return new Builder(false).createSpawnData();
    }
}
