package com.teamresourceful.resourcefulbees.platform.common.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import org.apache.commons.lang3.NotImplementedException;

public final class RegistryHelper {

    @ExpectPlatform
    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(RegistryEntry<ArgumentTypeInfo<A, T>> entry, Class<A> tClass) {
        throw new NotImplementedException("Not implemented on this platform");
    }
}
