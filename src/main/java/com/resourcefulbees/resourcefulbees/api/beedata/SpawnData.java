package com.resourcefulbees.resourcefulbees.api.beedata;

public class SpawnData extends AbstractBeeData {
    private final boolean canSpawnInWorld;
    private final int spawnWeight;
    private final String biomeWhitelist;
    private final String biomeBlacklist;

    private SpawnData(boolean canSpawnInWorld, int spawnWeight, String biomeWhitelist, String biomeBlacklist) {
        this.canSpawnInWorld = canSpawnInWorld;
        this.spawnWeight = spawnWeight;
        this.biomeWhitelist = biomeWhitelist;
        this.biomeBlacklist = biomeBlacklist;
    }

    public boolean canSpawnInWorld() { return canSpawnInWorld; }

    public int getSpawnWeight() { return spawnWeight <= 0 ? 100 : spawnWeight; }

    public String getBiomeWhitelist() { return biomeWhitelist == null ? "tag:overworld" : biomeWhitelist; }

    public String getBiomeBlacklist() { return biomeBlacklist == null ? "tag:ocean" : biomeBlacklist; }

    public static class Builder {
        private final boolean canSpawnInWorld;
        private int spawnWeight;
        private String biomeWhitelist;
        private String biomeBlacklist;

        public Builder(boolean canSpawnInWorld) {
            this.canSpawnInWorld = canSpawnInWorld;
        }

        public Builder setSpawnWeight(int spawnWeight) {
            this.spawnWeight = spawnWeight;
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

        public SpawnData createSpawnData() {
            return new SpawnData(canSpawnInWorld, spawnWeight, biomeWhitelist, biomeBlacklist);
        }
    }
}
