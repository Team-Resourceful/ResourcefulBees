package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.inventory.menus.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;

public final class ModMenus {

    private ModMenus() {
        throw new UtilityClassError();
    }


    public static final ResourcefulRegistry<MenuType<?>> CONTAINER_TYPES = ResourcefulRegistries.create(BuiltInRegistries.MENU, ModConstants.MOD_ID);


    public static final RegistryEntry<MenuType<HoneyGeneratorMenu>> HONEY_GENERATOR_CONTAINER = CONTAINER_TYPES.register("honey_generator", () -> IForgeMenuType
            .create(HoneyGeneratorMenu::new));
    public static final RegistryEntry<MenuType<EnderBeeconMenu>> ENDER_BEECON_CONTAINER = CONTAINER_TYPES.register("ender_beecon", () -> IForgeMenuType
            .create(EnderBeeconMenu::new));
    public static final RegistryEntry<MenuType<SolidificationChamberMenu>> SOLIDIFICATION_CHAMBER_CONTAINER = CONTAINER_TYPES.register("solidification_chamber", () -> IForgeMenuType
            .create(SolidificationChamberMenu::new));
    public static final RegistryEntry<MenuType<HoneyPotMenu>> HONEY_POT_CONTAINER = CONTAINER_TYPES.register("honey_pot", () -> IForgeMenuType
            .create(HoneyPotMenu::new));

    public static final RegistryEntry<MenuType<CentrifugeMenu>> CENTRIFUGE_MENU = CONTAINER_TYPES.register("centrifuge", () -> IForgeMenuType
            .create(CentrifugeMenu::new));
}
