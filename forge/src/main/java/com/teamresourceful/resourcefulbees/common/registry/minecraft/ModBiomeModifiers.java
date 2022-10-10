package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.world.BeeBiomeModifier;
import com.teamresourceful.resourcefulbees.common.world.BeeNestBiomeModifier;
import com.teamresourceful.resourcefulbees.common.world.DevBeeBiomeModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBiomeModifiers {

    private ModBiomeModifiers() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Codec<? extends BiomeModifier>> MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ResourcefulBees.MOD_ID);

    public static final RegistryObject<Codec<BeeNestBiomeModifier>> NEST_MODIFIER = MODIFIERS.register("nests", BeeNestBiomeModifier::makeCodec);
    public static final RegistryObject<Codec<BeeBiomeModifier>> SPAWN_MODIFIER = MODIFIERS.register("spawns", BeeBiomeModifier::makeCodec);
    public static final RegistryObject<Codec<DevBeeBiomeModifier>> DEV_SPAWN_MODIFIER = MODIFIERS.register("dev_spawns", DevBeeBiomeModifier::makeCodec);
}
