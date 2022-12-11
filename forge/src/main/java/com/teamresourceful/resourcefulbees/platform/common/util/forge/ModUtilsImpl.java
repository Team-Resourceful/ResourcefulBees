package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import net.minecraftforge.fml.ModList;

public class ModUtilsImpl {
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
