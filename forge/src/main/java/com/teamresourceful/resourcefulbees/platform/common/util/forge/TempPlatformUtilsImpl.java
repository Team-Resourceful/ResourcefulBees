package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import com.teamresourceful.resourcefulbees.common.entities.entity.ThrownMutatedPollen;
import com.teamresourceful.resourcefulbees.common.item.HoneyDipperItem;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import com.teamresourceful.resourcefulbees.common.menus.FakeFlowerMenu;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class TempPlatformUtilsImpl {

    public static boolean isHoneyDipper(ItemStack stack) {
        return stack.getItem() instanceof HoneyDipperItem;
    }

    public static RegistryEntry<MenuType<FakeFlowerMenu>> getFakeFlowerMenuType() {
        return ModMenus.FAKE_FLOWER_CONTAINER;
    }

    public static RegistryEntry<EntityType<? extends ThrownMutatedPollen>> getThrownMutatedPollenType() {
        return ModEntities.THROWN_MUTATED_POLLEN;
    }

    public static RegistryEntry<MenuType<BreederMenu>> getBreederMenuType() {
        return ModMenus.BREEDER_MENU;
    }
}
