package com.teamresourceful.resourcefulbees.platform.common.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class RegistryHelper {

    @ExpectPlatform
    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(RegistryEntry<ArgumentTypeInfo<A, T>> entry, Class<A> tClass) {
        throw new NotImplementedError();
    }

    @ExpectPlatform
    public static WoodType registerWoodType(ResourceLocation location) {
        throw new NotImplementedError();
    }
}
