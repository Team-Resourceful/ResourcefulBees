package com.teamresourceful.resourcefulbees.platform.common.util.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EntityType;
import org.apache.commons.lang3.NotImplementedException;

public class ModUtilsImpl {
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static void openEntityInJEI(EntityType<?> entity) {
        throw new NotImplementedException("openEntityInJEI not implemented on Fabric");
    }
}
