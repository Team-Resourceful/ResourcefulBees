package com.teamresourceful.resourcefulbees.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBiomeModifiers;
import com.teamresourceful.resourcefulbees.common.worldgen.SpawnDataModifier;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record DevBeeBiomeModifier(
        List<HolderSet<Biome>> whitelist,
        List<HolderSet<Biome>> blacklist,
        MobSpawnSettings.SpawnerData spawns,
        Optional<LocationPredicate> spawnPredicate
) implements BiomeModifier, SpawnDataModifier {

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (!GeneralConfig.enableDevBees) return;
        if (phase.equals(Phase.ADD) && isInList(whitelist(), biome) && !isInList(blacklist(), biome)) {
            builder.getMobSpawnSettings().addSpawn(spawns().type.getCategory(), spawns());
        }
    }

    private static boolean isInList(List<HolderSet<Biome>> biomes, Holder<Biome> checkingBiome) {
        for (HolderSet<Biome> biome : biomes) {
            if (biome.contains(checkingBiome)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public EntityType<?> getEntityType() {
        return this.spawns().type;
    }

    @Override
    public Optional<LocationPredicate> getSpawnPredicate() {
        return this.spawnPredicate();
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.DEV_SPAWN_MODIFIER.get();
    }

    public static Codec<DevBeeBiomeModifier> makeCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Biome.LIST_CODEC.listOf().fieldOf("whitelist").forGetter(DevBeeBiomeModifier::whitelist),
                Biome.LIST_CODEC.listOf().fieldOf("blacklist").orElse(new ArrayList<>()).forGetter(DevBeeBiomeModifier::blacklist),
                MobSpawnSettings.SpawnerData.CODEC.fieldOf("spawns").forGetter(DevBeeBiomeModifier::spawns),
                CodecExtras.passthrough(LocationPredicate::serializeToJson, LocationPredicate::fromJson).optionalFieldOf("spawnPredicate").forGetter(DevBeeBiomeModifier::spawnPredicate)
        ).apply(instance, DevBeeBiomeModifier::new));
    }
}
