package com.teamresourceful.resourcefulbees.platform.common.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

public final class RegistryHelper {

    @ExpectPlatform
    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(RegistryEntry<ArgumentTypeInfo<A, T>> entry, Class<A> tClass) {
        throw new NotImplementedException();
    }
}
