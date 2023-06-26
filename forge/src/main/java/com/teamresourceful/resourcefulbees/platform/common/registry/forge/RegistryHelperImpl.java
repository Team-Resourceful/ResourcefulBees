package com.teamresourceful.resourcefulbees.platform.common.registry.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;

public class RegistryHelperImpl {

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(RegistryEntry<ArgumentTypeInfo<A, T>> entry, Class<A> tClass) {
        ArgumentTypeInfos.registerByClass(tClass, entry.get());
    }
}
