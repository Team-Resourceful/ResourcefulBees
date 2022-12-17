package com.teamresourceful.resourcefulbees.platform.common.registry.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

public class RegistryHelperImpl {

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(RegistryEntry<ArgumentTypeInfo<A, T>> entry, Class<A> tClass) {
        ArgumentTypeInfos.registerByClass(tClass, entry.get());
    }

    public static WoodType registerWoodType(ResourceLocation location) {
        return WoodType.register(WoodType.create(location.toString()));
    }
}
