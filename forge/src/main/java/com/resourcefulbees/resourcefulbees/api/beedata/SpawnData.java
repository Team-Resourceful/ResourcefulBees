package com.resourcefulbees.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.resourcefulbees.resourcefulbees.lib.LightLevels;

public class SpawnData {

    public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("canSpawnInWorld").orElse(false).forGetter(SpawnData::canSpawnInWorld),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spawnWeight").orElse(8).forGetter(SpawnData::getSpawnWeight),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("minGroupSize").orElse(0).forGetter(SpawnData::getMinGroupSize),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("maxGroupSize").orElse(3).forGetter(SpawnData::getMaxGroupSize),
            Codec.STRING.fieldOf("biomeWhitelist").orElse("tag:overworld").forGetter(SpawnData::getBiomeWhitelist),
            Codec.STRING.fieldOf("biomeBlacklist").orElse("tag:ocean").forGetter(SpawnData::getBiomeBlacklist),
            LightLevels.CODEC.fieldOf("lightLevel").orElse(LightLevels.ANY).forGetter(SpawnData::getLightLevel),
            Codec.intRange(-1, 256).fieldOf("minYLevel").orElse(-1).forGetter(SpawnData::getMinYLevel),
            Codec.intRange(-1, 256).fieldOf("maxYLevel").orElse(256).forGetter(SpawnData::getMaxYLevel)
            ).apply(instance, SpawnData::new)
    );

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

    public SpawnData(boolean canSpawnInWorld, int spawnWeight, int minGroupSize, int maxGroupSize, String biomeWhitelist, String biomeBlacklist, LightLevels lightLevel, int minYLevel, int maxYLevel) {
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

    public int getSpawnWeight() { return spawnWeight; }

    public int getMinGroupSize() { return minGroupSize; }

    public int getMaxGroupSize() { return maxGroupSize; }

    public String getBiomeWhitelist() { return biomeWhitelist; }

    public String getBiomeBlacklist() { return biomeBlacklist; }

    public LightLevels getLightLevel() { return lightLevel; }

    public int getMinYLevel() { return minYLevel; }

    public int getMaxYLevel() { return maxYLevel; }

    public static SpawnData createDefault() {
        return new SpawnData(false, 0, 0, 0, "", "", LightLevels.ANY, 0, 0);
    }
}
