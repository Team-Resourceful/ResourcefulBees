package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.lib.LightLevels;
import com.teamresourceful.resourcefulbees.registry.BiomeDictionary;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class SpawnData {
    public static final SpawnData DEFAULT = new SpawnData(false, 0, 0, 0, Collections.emptySet(), Collections.emptySet(), LightLevels.ANY, 0, 0);

    private static final Set<ResourceLocation> DEFAULT_WHITELIST = Collections.singleton(new ResourceLocation("tag:overworld"));
    private static final Set<ResourceLocation> DEFAULT_BLACKLIST = Collections.singleton(new ResourceLocation("tag:ocean"));

    public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("canSpawnInWorld").orElse(false).forGetter(SpawnData::canSpawnInWorld),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spawnWeight").orElse(8).forGetter(SpawnData::getSpawnWeight),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("minGroupSize").orElse(0).forGetter(SpawnData::getMinGroupSize),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("maxGroupSize").orElse(3).forGetter(SpawnData::getMaxGroupSize),
            CodecUtils.createSetCodec(ResourceLocation.CODEC).fieldOf("biomeWhitelist").orElse(DEFAULT_WHITELIST).forGetter(SpawnData::getBiomeWhitelist),
            CodecUtils.createSetCodec(ResourceLocation.CODEC).fieldOf("biomeBlacklist").orElse(DEFAULT_BLACKLIST).forGetter(SpawnData::getBiomeBlacklist),
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
    private final Set<ResourceLocation> biomeWhitelist;

    /**
     * What biomes the bee will not spawn. No matter if the are in the whitelist.
     */
    private final Set<ResourceLocation> biomeBlacklist;

    /**
     * What LightLevel the Bee needs to spawn.
     */
    private final LightLevels lightLevel;

    private final int minYLevel;

    private final int maxYLevel;

    private final Set<ResourceLocation> spawnableBiomes = new HashSet<>();

    private SpawnData(boolean canSpawnInWorld, int spawnWeight, int minGroupSize, int maxGroupSize, Set<ResourceLocation> biomeWhitelist, Set<ResourceLocation> biomeBlacklist, LightLevels lightLevel, int minYLevel, int maxYLevel) {
        this.canSpawnInWorld = canSpawnInWorld;
        this.spawnWeight = spawnWeight;
        this.minGroupSize = minGroupSize;
        this.maxGroupSize = maxGroupSize;
        this.biomeWhitelist = biomeWhitelist;
        this.biomeBlacklist = biomeBlacklist;
        this.lightLevel = lightLevel;
        this.minYLevel = minYLevel;
        this.maxYLevel = maxYLevel;
        if (!biomeWhitelist.isEmpty()) buildSpawnableBiomes();
    }

    public boolean canSpawnInWorld() { return canSpawnInWorld; }

    public int getSpawnWeight() { return spawnWeight; }

    public int getMinGroupSize() { return minGroupSize; }

    public int getMaxGroupSize() { return maxGroupSize; }

    public Set<ResourceLocation> getBiomeWhitelist() { return biomeWhitelist; }

    public Set<ResourceLocation> getBiomeBlacklist() { return biomeBlacklist; }

    public LightLevels getLightLevel() { return lightLevel; }

    public int getMinYLevel() { return minYLevel; }

    public int getMaxYLevel() { return maxYLevel; }

    public Set<ResourceLocation> getSpawnableBiomes() {
        return spawnableBiomes;
    }

    public String getSpawnableBiomesAsString() {
        StringJoiner returnList = new StringJoiner(", ");
        spawnableBiomes.forEach(resourceLocation -> returnList.add(WordUtils.capitalize(resourceLocation.getPath().replace("_", " "))));
        return returnList.toString();
    }

    private void buildSpawnableBiomes() {
        biomeWhitelist.stream()
                .filter(resourceLocation -> resourceLocation.getNamespace().equals("tag"))
                .forEach(this::addBiomesFromTag);
        biomeWhitelist.stream()
                .filter(resourceLocation -> !resourceLocation.getNamespace().equals("tag"))
                .forEach(spawnableBiomes::add);
        biomeBlacklist.stream()
                .filter(resourceLocation -> resourceLocation.getNamespace().equals("tag"))
                .forEach(this::removeBiomesFromTag);
        biomeBlacklist.stream()
                .filter(resourceLocation -> resourceLocation.getNamespace().equals("tag"))
                .forEach(spawnableBiomes::remove);
    }

    private void addBiomesFromTag(ResourceLocation resourceLocation) {
        if (Config.USE_FORGE_DICTIONARIES.get()) {
            net.minecraftforge.common.BiomeDictionary.Type type = BiomeDictionary.getForgeType(resourceLocation);
            if (type != null) {
                spawnableBiomes.addAll(BiomeDictionary.getForgeBiomeLocations(type));
            }
        }  else {
            if (BiomeDictionary.get().containsKey(resourceLocation.getPath())) {
                spawnableBiomes.addAll(BiomeDictionary.get().get(resourceLocation.getPath()).getBiomes());
            }
        }
    }

    private void removeBiomesFromTag(ResourceLocation resourceLocation) {
        if (Config.USE_FORGE_DICTIONARIES.get()) {
            net.minecraftforge.common.BiomeDictionary.Type type = BiomeDictionary.getForgeType(resourceLocation);
            if (type != null) {
                spawnableBiomes.removeAll(BiomeDictionary.getForgeBiomeLocations(type));
            }
        }  else {
            if (BiomeDictionary.get().containsKey(resourceLocation.getPath())) {
                spawnableBiomes.removeAll(BiomeDictionary.get().get(resourceLocation.getPath()).getBiomes());
            }
        }
    }
}
