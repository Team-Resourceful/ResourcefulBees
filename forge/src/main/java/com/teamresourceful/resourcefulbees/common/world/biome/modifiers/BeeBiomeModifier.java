package com.teamresourceful.resourcefulbees.common.world.biome.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.registry.forge.ForgeRegistrySubsystem;
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
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;

public record BeeBiomeModifier(
        List<HolderSet<Biome>> whitelist,
        List<HolderSet<Biome>> blacklist,
        MobSpawnSettings.SpawnerData spawns,
        Optional<LocationPredicate> spawnPredicate,
        Type type
) implements BiomeModifier, SpawnDataModifier {

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if(!type().abortCriteria().getAsBoolean()) return;
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
        return spawnPredicate;
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return type().modifier().get();
    }

    public static Codec<BeeBiomeModifier> customBeeCodec() {
        return makeCodec(Type.CUSTOM);
    }

    public static Codec<BeeBiomeModifier> devBeeCodec() {
        return makeCodec(Type.DEV);
    }

    public static Codec<BeeBiomeModifier> supporterBeeCodec() {
        return makeCodec(Type.SUPPORTER);
    }

    private static Codec<BeeBiomeModifier> makeCodec(Type type) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Biome.LIST_CODEC.listOf().fieldOf("whitelist").forGetter(BeeBiomeModifier::whitelist),
                Biome.LIST_CODEC.listOf().fieldOf("blacklist").orElse(new ArrayList<>()).forGetter(BeeBiomeModifier::blacklist),
                MobSpawnSettings.SpawnerData.CODEC.fieldOf("spawns").forGetter(BeeBiomeModifier::spawns),
                CodecExtras.passthrough(LocationPredicate::serializeToJson, LocationPredicate::fromJson).optionalFieldOf("spawnPredicate").forGetter(BeeBiomeModifier::spawnPredicate),
                RecordCodecBuilder.point(type)
        ).apply(instance, BeeBiomeModifier::new));
    }

    private enum Type {
        CUSTOM(() -> true, ForgeRegistrySubsystem.SPAWN_MODIFIER),
        DEV(() -> GeneralConfig.enableDevBees, ForgeRegistrySubsystem.DEV_SPAWN_MODIFIER),
        SUPPORTER(() -> GeneralConfig.enableSupporterBees, ForgeRegistrySubsystem.SUPPORTER_SPAWN_MODIFIER);

        private final BooleanSupplier abortCriteria;
        private final RegistryObject<Codec<BeeBiomeModifier>> modifier;

        Type(BooleanSupplier abortCriteria, RegistryObject<Codec<BeeBiomeModifier>> modifier) {
            this.abortCriteria = abortCriteria;
            this.modifier = modifier;
        }

        public BooleanSupplier abortCriteria() {
            return abortCriteria;
        }

        public RegistryObject<Codec<BeeBiomeModifier>> modifier() {
            return modifier;
        }
    }
}
