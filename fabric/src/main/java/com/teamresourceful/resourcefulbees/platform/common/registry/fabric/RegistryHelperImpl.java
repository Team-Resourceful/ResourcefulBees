package com.teamresourceful.resourcefulbees.platform.common.registry.fabric;

import com.mojang.brigadier.arguments.ArgumentType;
import com.teamresourceful.resourcefulbees.mixin.fabric.ArgumentTypeInfosAccessor;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

public class RegistryHelperImpl {

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(RegistryEntry<ArgumentTypeInfo<A, T>> entry, Class<A> tClass) {
        ArgumentTypeInfosAccessor.getByClass().put(tClass, entry.get());
    }
}
