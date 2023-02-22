package com.teamresourceful.resourcefulbees.platform.common.util.fabric;

import com.teamresourceful.resourcefulbees.common.entities.entity.ThrownMutatedPollen;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import com.teamresourceful.resourcefulbees.common.menus.FakeFlowerMenu;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class TempPlatformUtilsImpl {

    public static boolean isHoneyDipper(ItemStack stack) {
        return false;
    }

    public static RegistryEntry<MenuType<FakeFlowerMenu>> getFakeFlowerMenuType() {
        return null;
    }

    public static RegistryEntry<EntityType<? extends ThrownMutatedPollen>> getThrownMutatedPollenType() {
        return null;
    }

    public static RegistryEntry<MenuType<BreederMenu>> getBreederMenuType() {
        return null;
    }
}
