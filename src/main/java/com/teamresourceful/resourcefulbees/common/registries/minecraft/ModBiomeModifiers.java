package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.modifiers.BeeBiomeModifier;
import com.teamresourceful.resourcefulbees.common.modifiers.BeeNestBiomeModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModBiomeModifiers {

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ModConstants.MOD_ID);

    public static final DeferredHolder<
            MapCodec<? extends BiomeModifier>,
            MapCodec<BeeNestBiomeModifier>
            > NEST_MODIFIER =
            MODIFIERS.register("nests", BeeNestBiomeModifier::makeCodec);

    public static final DeferredHolder<
            MapCodec<? extends BiomeModifier>,
            MapCodec<BeeBiomeModifier>
            > SPAWN_MODIFIER =
            MODIFIERS.register("spawns", BeeBiomeModifier::customBeeCodec);

    public static final DeferredHolder<
            MapCodec<? extends BiomeModifier>,
            MapCodec<BeeBiomeModifier>
            > DEV_SPAWN_MODIFIER =
            MODIFIERS.register("dev_spawns", BeeBiomeModifier::devBeeCodec);

    public static final DeferredHolder<
            MapCodec<? extends BiomeModifier>,
            MapCodec<BeeBiomeModifier>
            > SUPPORTER_SPAWN_MODIFIER =
            MODIFIERS.register(
                    "supporter_spawns",
                    BeeBiomeModifier::supporterBeeCodec
            );

    private ModBiomeModifiers() {}

    public static void init(IEventBus bus) {
        MODIFIERS.register(bus);
    }
}