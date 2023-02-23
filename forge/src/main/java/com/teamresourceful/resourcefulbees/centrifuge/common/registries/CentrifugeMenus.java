package com.teamresourceful.resourcefulbees.centrifuge.common.registries;

import com.teamresourceful.resourcefulbees.centrifuge.common.containers.*;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class CentrifugeMenus {

    public static final ResourcefulRegistry<MenuType<?>> CENTRIFUGE_MENUS = ResourcefulRegistries.create(ModMenuTypes.MENUS);

    // region Centrifuge Containers
    public static final RegistryEntry<MenuType<CentrifugeTerminalContainer>> CENTRIFUGE_TERMINAL_CONTAINER = CENTRIFUGE_MENUS.register("centrifuge/terminal", () -> IForgeMenuType
            .create(CentrifugeTerminalContainer::new));
    public static final RegistryEntry<MenuType<CentrifugeInputContainer>> CENTRIFUGE_INPUT_CONTAINER = CENTRIFUGE_MENUS.register("centrifuge/input/item", () -> IForgeMenuType
            .create(CentrifugeInputContainer::new));
    public static final RegistryEntry<MenuType<CentrifugeItemOutputContainer>> CENTRIFUGE_ITEM_OUTPUT_CONTAINER = CENTRIFUGE_MENUS.register("centrifuge/output/item", () -> IForgeMenuType
            .create(CentrifugeItemOutputContainer::new));
    public static final RegistryEntry<MenuType<CentrifugeFluidOutputContainer>> CENTRIFUGE_FLUID_OUTPUT_CONTAINER = CENTRIFUGE_MENUS.register("centrifuge/output/fluid", () -> IForgeMenuType
            .create(CentrifugeFluidOutputContainer::new));
    public static final RegistryEntry<MenuType<CentrifugeVoidContainer>> CENTRIFUGE_VOID_CONTAINER = CENTRIFUGE_MENUS.register("centrifuge/void", () -> IForgeMenuType
            .create(CentrifugeVoidContainer::new));
    // endregion
}
