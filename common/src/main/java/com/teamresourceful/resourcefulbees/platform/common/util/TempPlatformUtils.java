package com.teamresourceful.resourcefulbees.platform.common.util;

import com.teamresourceful.resourcefulbees.common.entities.entity.ThrownMutatedPollen;
import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.EntityType;

/**
 * This class is to be used for any platform specific code that is common but defined in a platform specific module.
 * This is to be used as a temporary solution until the platform specific modules are merged into the common module.
 */
public class TempPlatformUtils {

    @ExpectPlatform
    public static RegistryEntry<EntityType<? extends ThrownMutatedPollen>> getThrownMutatedPollenType() {
        throw new NotImplementedError();
    }
}
