package com.teamresourceful.resourcefulbees.platform.common.registry.fabric;

import com.mojang.brigadier.arguments.ArgumentType;
import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

public class RegistryHelperImpl {

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(RegistryEntry<ArgumentTypeInfo<A, T>> entry, Class<A> tClass) {
        //TODO add to the map here.
        throw new NotImplementedError();
    }
}
