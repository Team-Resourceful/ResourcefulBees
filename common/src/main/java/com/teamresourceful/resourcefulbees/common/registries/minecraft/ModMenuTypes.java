package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.menus.*;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.menu.MenuContentHelper;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

    public static final ResourcefulRegistry<MenuType<?>> MENUS = RegistryHelper.create(BuiltInRegistries.MENU, ModConstants.MOD_ID);

    public static final RegistryEntry<MenuType<FakeFlowerMenu>> FAKE_FLOWER = MENUS.register("fake_flower",
            () -> MenuContentHelper.create(FakeFlowerMenu::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<ApiaryMenu>> APIARY = MENUS.register("apiary",
            () -> MenuContentHelper.create(ApiaryMenu::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<BreederMenu>> BREEDER = MENUS.register("breeder",
            () -> MenuContentHelper.create(BreederMenu::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<EnderBeeconMenu>> ENDER_BEECON = MENUS.register("ender_beecon", () -> MenuContentHelper
            .create(EnderBeeconMenu::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<HoneyPotMenu>> HONEY_POT = MENUS.register("honey_pot", () -> MenuContentHelper
            .create(HoneyPotMenu::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<SolidificationChamberMenu>> SOLIDIFICATION_CHAMBER_CONTAINER = MENUS.register("solidification_chamber", () -> MenuContentHelper
            .create(SolidificationChamberMenu::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<CentrifugeMenu>> CENTRIFUGE = MENUS.register("centrifuge", () -> MenuContentHelper
            .create(CentrifugeMenu::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<HoneyGeneratorMenu>> HONEY_GENERATOR = MENUS.register("honey_generator", () -> MenuContentHelper
            .create(HoneyGeneratorMenu::new, PositionContent.SERIALIZER));
}
