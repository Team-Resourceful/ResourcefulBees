package com.teamresourceful.resourcefulbees.common.forge;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.modifiers.BeeBiomeModifier;
import com.teamresourceful.resourcefulbees.common.modifiers.BeeNestBiomeModifier;
import com.teamresourceful.resourcefulbees.common.subsystems.RegistrySubsystem;

import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ForgeRegistrySubsystem implements RegistrySubsystem {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ModConstants.MOD_ID);

    public static final RegistryObject<Codec<BeeNestBiomeModifier>> NEST_MODIFIER = MODIFIERS.register("nests", BeeNestBiomeModifier::makeCodec);
    public static final RegistryObject<Codec<BeeBiomeModifier>> SPAWN_MODIFIER = MODIFIERS.register("spawns", BeeBiomeModifier::customBeeCodec);
    public static final RegistryObject<Codec<BeeBiomeModifier>> DEV_SPAWN_MODIFIER = MODIFIERS.register("dev_spawns", BeeBiomeModifier::devBeeCodec);
    public static final RegistryObject<Codec<BeeBiomeModifier>> SUPPORTER_SPAWN_MODIFIER = MODIFIERS.register("supporter_spawns", BeeBiomeModifier::supporterBeeCodec);

    @Override
    public void init() {
        MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
