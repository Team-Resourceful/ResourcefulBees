package com.teamresourceful.resourcefulbees.common.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBiomeModifiers;
import com.teamresourceful.resourcefulbees.common.world.gen.SpawnDataModifier;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;

public record BeeBiomeModifier(
        List<HolderSet<Biome>> whitelist,
        List<HolderSet<Biome>> blacklist,
        Weighted<MobSpawnSettings.SpawnerData> spawn,
        Optional<LocationPredicate> spawnPredicate,
        Type type
) implements BiomeModifier, SpawnDataModifier {

    @Override
    public void modify(
            @NonNull Holder<Biome> biome,
            @NonNull Phase phase,
            ModifiableBiomeInfo.BiomeInfo.@NonNull Builder builder
    ) {
        if (!type.abortCriteria().getAsBoolean()) {
            return;
        }

        if (phase != Phase.ADD) {
            return;
        }

        if (!isInList(whitelist, biome) || isInList(blacklist, biome)) {
            return;
        }

        MobSpawnSettings.SpawnerData spawner = spawn.value();

        builder.getMobSpawnSettings().addSpawn(
                spawner.type().getCategory(),
                spawn.weight(),
                spawner
        );
    }

    private static boolean isInList(
            List<HolderSet<Biome>> biomes,
            Holder<Biome> checkingBiome
    ) {
        return biomes.stream()
                .anyMatch(set -> set.contains(checkingBiome));
    }

    @Override
    public EntityType<?> getEntityType() {
        return spawn.value().type();
    }

    @Override
    public Optional<LocationPredicate> getSpawnPredicate() {
        return spawnPredicate;
    }

    @Override
    public @NonNull MapCodec<? extends BiomeModifier> codec() {
        return type.modifier().get();
    }

    public static MapCodec<BeeBiomeModifier> customBeeCodec() {
        return makeCodec(Type.CUSTOM);
    }

    public static MapCodec<BeeBiomeModifier> devBeeCodec() {
        return makeCodec(Type.DEV);
    }

    public static MapCodec<BeeBiomeModifier> supporterBeeCodec() {
        return makeCodec(Type.SUPPORTER);
    }

    private static MapCodec<BeeBiomeModifier> makeCodec(Type type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Biome.LIST_CODEC
                        .listOf()
                        .fieldOf("whitelist")
                        .forGetter(BeeBiomeModifier::whitelist),

                Biome.LIST_CODEC
                        .listOf()
                        .optionalFieldOf("blacklist", List.of())
                        .forGetter(BeeBiomeModifier::blacklist),

                Weighted.codec(MobSpawnSettings.SpawnerData.CODEC)
                        .fieldOf("spawn")
                        .forGetter(BeeBiomeModifier::spawn),

                LocationPredicate.CODEC
                        .optionalFieldOf("spawnPredicate")
                        .forGetter(BeeBiomeModifier::spawnPredicate),

                RecordCodecBuilder.point(type)
        ).apply(instance, BeeBiomeModifier::new));
    }

    private enum Type {

        CUSTOM(
                () -> true,
                ModBiomeModifiers.SPAWN_MODIFIER
        ),

        DEV(
                () -> GeneralConfig.enableDevBees,
                ModBiomeModifiers.DEV_SPAWN_MODIFIER
        ),

        SUPPORTER(
                () -> GeneralConfig.enableSupporterBees,
                ModBiomeModifiers.SUPPORTER_SPAWN_MODIFIER
        );

        private final BooleanSupplier abortCriteria;

        private final DeferredHolder<
                        MapCodec<? extends BiomeModifier>,
                        MapCodec<BeeBiomeModifier>
                        > modifier;

        Type(
                BooleanSupplier abortCriteria,
                DeferredHolder<
                        MapCodec<? extends BiomeModifier>,
                        MapCodec<BeeBiomeModifier>
                        > modifier
        ) {
            this.abortCriteria = abortCriteria;
            this.modifier = modifier;
        }

        public BooleanSupplier abortCriteria() {
            return abortCriteria;
        }

        public DeferredHolder<
                MapCodec<? extends BiomeModifier>,
                MapCodec<BeeBiomeModifier>
                > modifier() {
            return modifier;
        }
    }
}