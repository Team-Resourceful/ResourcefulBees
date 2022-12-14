package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.ModList;

public class ModUtilsImpl {
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static void openEntityInJEI(EntityType<?> entity) {
        JEICompat.searchEntity(entity);
    }
}
