package com.teamresourceful.resourcefulbees.platform.common.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.EntityType;
import org.apache.commons.lang3.NotImplementedException;

public class ModUtils {

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new NotImplementedException("Not implemented on this platform");
    }

    @ExpectPlatform
    public static void openEntityInJEI(EntityType<?> entity) {
        throw new NotImplementedException("Not implemented on this platform");
    }
}
