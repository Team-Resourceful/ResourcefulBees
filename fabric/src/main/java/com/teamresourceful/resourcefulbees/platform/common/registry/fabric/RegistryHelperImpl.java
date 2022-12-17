package com.teamresourceful.resourcefulbees.platform.common.registry.fabric;

import com.mojang.brigadier.arguments.ArgumentType;
import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

public class RegistryHelperImpl {

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(RegistryEntry<ArgumentTypeInfo<A, T>> entry, Class<A> tClass) {
        //TODO add to the map here.
        throw new NotImplementedError();
    }

    public static WoodType registerWoodType(ResourceLocation location) {
        //TODO accessor the register method and create an anon class.
        throw new NotImplementedError();
    }
}
