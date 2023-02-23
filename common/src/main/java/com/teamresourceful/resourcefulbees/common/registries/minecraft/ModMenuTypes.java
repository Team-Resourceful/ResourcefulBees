package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.menus.ApiaryMenu;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import com.teamresourceful.resourcefulbees.common.menus.FakeFlowerMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.platform.common.menu.MenuContentHelper;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

    public static final ResourcefulRegistry<MenuType<?>> MENUS = ResourcefulRegistries.create(Registry.MENU, ModConstants.MOD_ID);

    public static final RegistryEntry<MenuType<FakeFlowerMenu>> FAKE_FLOWER = MENUS.register("fake_flower",
            () -> MenuContentHelper.create(FakeFlowerMenu::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<ApiaryMenu>> APIARY = MENUS.register("apiary",
            () -> MenuContentHelper.create(ApiaryMenu::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<BreederMenu>> BREEDER = MENUS.register("breeder",
            () -> MenuContentHelper.create(BreederMenu::new, PositionContent.SERIALIZER));
}
