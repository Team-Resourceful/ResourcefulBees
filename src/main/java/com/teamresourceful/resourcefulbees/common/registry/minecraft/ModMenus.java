package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.inventory.menus.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {

    private ModMenus() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ResourcefulBees.MOD_ID);


    public static final RegistryObject<MenuType<HoneyGeneratorMenu>> HONEY_GENERATOR_CONTAINER = CONTAINER_TYPES.register("honey_generator", () -> IForgeMenuType
            .create(HoneyGeneratorMenu::new));
    public static final RegistryObject<MenuType<ApiaryMenu>> VALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("validated_apiary", () -> IForgeMenuType
            .create(ApiaryMenu::new));
    public static final RegistryObject<MenuType<BreederMenu>> BREEDER_MENU = CONTAINER_TYPES.register("breeder", () -> IForgeMenuType
            .create(BreederMenu::new));
    public static final RegistryObject<MenuType<EnderBeeconMenu>> ENDER_BEECON_CONTAINER = CONTAINER_TYPES.register("ender_beecon", () -> IForgeMenuType
            .create(EnderBeeconMenu::new));
    public static final RegistryObject<MenuType<SolidificationChamberMenu>> SOLIDIFICATION_CHAMBER_CONTAINER = CONTAINER_TYPES.register("solidification_chamber", () -> IForgeMenuType
            .create(SolidificationChamberMenu::new));
    public static final RegistryObject<MenuType<HoneyPotMenu>> HONEY_POT_CONTAINER = CONTAINER_TYPES.register("honey_pot", () -> IForgeMenuType
            .create(HoneyPotMenu::new));

    public static final RegistryObject<MenuType<CentrifugeMenu>> CENTRIFUGE_MENU = CONTAINER_TYPES.register("centrifuge", () -> IForgeMenuType
            .create(CentrifugeMenu::new));

    // region Centrifuge Containers
    public static final RegistryObject<MenuType<CentrifugeTerminalContainer>> CENTRIFUGE_TERMINAL_CONTAINER = CONTAINER_TYPES.register("centrifuge/terminal", () -> IForgeMenuType
            .create(CentrifugeTerminalContainer::new));
    public static final RegistryObject<MenuType<CentrifugeInputContainer>> CENTRIFUGE_INPUT_CONTAINER = CONTAINER_TYPES.register("centrifuge/input/item", () -> IForgeMenuType
            .create(CentrifugeInputContainer::new));
    public static final RegistryObject<MenuType<CentrifugeItemOutputContainer>> CENTRIFUGE_ITEM_OUTPUT_CONTAINER = CONTAINER_TYPES.register("centrifuge/output/item", () -> IForgeMenuType
            .create(CentrifugeItemOutputContainer::new));
    public static final RegistryObject<MenuType<CentrifugeFluidOutputContainer>> CENTRIFUGE_FLUID_OUTPUT_CONTAINER = CONTAINER_TYPES.register("centrifuge/output/fluid", () -> IForgeMenuType
            .create(CentrifugeFluidOutputContainer::new));
    public static final RegistryObject<MenuType<CentrifugeVoidContainer>> CENTRIFUGE_VOID_CONTAINER = CONTAINER_TYPES.register("centrifuge/void", () -> IForgeMenuType
            .create(CentrifugeVoidContainer::new));
    // endregion
}
