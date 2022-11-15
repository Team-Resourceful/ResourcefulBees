package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.inventory.menus.*;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.*;
import com.teamresourceful.resourcefulbees.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistry;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;

public final class ModMenus {

    private ModMenus() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<MenuType<?>> CONTAINER_TYPES = ResourcefulRegistries.create(Registry.MENU, ResourcefulBees.MOD_ID);


    public static final RegistryEntry<MenuType<HoneyGeneratorMenu>> HONEY_GENERATOR_CONTAINER = CONTAINER_TYPES.register("honey_generator", () -> IForgeMenuType
            .create(HoneyGeneratorMenu::new));
    public static final RegistryEntry<MenuType<ApiaryMenu>> VALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("validated_apiary", () -> IForgeMenuType
            .create(ApiaryMenu::new));
    public static final RegistryEntry<MenuType<BreederMenu>> BREEDER_MENU = CONTAINER_TYPES.register("breeder", () -> IForgeMenuType
            .create(BreederMenu::new));
    public static final RegistryEntry<MenuType<EnderBeeconMenu>> ENDER_BEECON_CONTAINER = CONTAINER_TYPES.register("ender_beecon", () -> IForgeMenuType
            .create(EnderBeeconMenu::new));
    public static final RegistryEntry<MenuType<SolidificationChamberMenu>> SOLIDIFICATION_CHAMBER_CONTAINER = CONTAINER_TYPES.register("solidification_chamber", () -> IForgeMenuType
            .create(SolidificationChamberMenu::new));
    public static final RegistryEntry<MenuType<HoneyPotMenu>> HONEY_POT_CONTAINER = CONTAINER_TYPES.register("honey_pot", () -> IForgeMenuType
            .create(HoneyPotMenu::new));

    public static final RegistryEntry<MenuType<CentrifugeMenu>> CENTRIFUGE_MENU = CONTAINER_TYPES.register("centrifuge", () -> IForgeMenuType
            .create(CentrifugeMenu::new));

    // region Centrifuge Containers
    public static final RegistryEntry<MenuType<CentrifugeTerminalContainer>> CENTRIFUGE_TERMINAL_CONTAINER = CONTAINER_TYPES.register("centrifuge/terminal", () -> IForgeMenuType
            .create(CentrifugeTerminalContainer::new));
    public static final RegistryEntry<MenuType<CentrifugeInputContainer>> CENTRIFUGE_INPUT_CONTAINER = CONTAINER_TYPES.register("centrifuge/input/item", () -> IForgeMenuType
            .create(CentrifugeInputContainer::new));
    public static final RegistryEntry<MenuType<CentrifugeItemOutputContainer>> CENTRIFUGE_ITEM_OUTPUT_CONTAINER = CONTAINER_TYPES.register("centrifuge/output/item", () -> IForgeMenuType
            .create(CentrifugeItemOutputContainer::new));
    public static final RegistryEntry<MenuType<CentrifugeFluidOutputContainer>> CENTRIFUGE_FLUID_OUTPUT_CONTAINER = CONTAINER_TYPES.register("centrifuge/output/fluid", () -> IForgeMenuType
            .create(CentrifugeFluidOutputContainer::new));
    public static final RegistryEntry<MenuType<CentrifugeVoidContainer>> CENTRIFUGE_VOID_CONTAINER = CONTAINER_TYPES.register("centrifuge/void", () -> IForgeMenuType
            .create(CentrifugeVoidContainer::new));
    // endregion
}
