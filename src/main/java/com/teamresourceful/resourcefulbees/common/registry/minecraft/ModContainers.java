package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.inventory.containers.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainers {

    private ModContainers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ResourcefulBees.MOD_ID);


    public static final RegistryObject<MenuType<HoneyGeneratorContainer>> HONEY_GENERATOR_CONTAINER = CONTAINER_TYPES.register("honey_generator", () -> IForgeMenuType
            .create((id, inv, c) -> new HoneyGeneratorContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<ValidatedApiaryContainer>> VALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("validated_apiary", () -> IForgeMenuType
            .create((id, inv, c) -> new ValidatedApiaryContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<ApiaryBreederContainer>> APIARY_BREEDER_CONTAINER = CONTAINER_TYPES.register("apiary_breeder", () -> IForgeMenuType
            .create((id, inv, c) -> new ApiaryBreederContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<EnderBeeconContainer>> ENDER_BEECON_CONTAINER = CONTAINER_TYPES.register("ender_beecon", () -> IForgeMenuType
            .create((id, inv, c) -> new EnderBeeconContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<HoneyCongealerContainer>> HONEY_CONGEALER_CONTAINER = CONTAINER_TYPES.register("honey_congealer", () -> IForgeMenuType
            .create((id, inv, c) -> new HoneyCongealerContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<HoneyPotContainer>> HONEY_POT_CONTAINER = CONTAINER_TYPES.register("honey_pot", () -> IForgeMenuType
            .create((id, inv, c) -> new HoneyPotContainer(id, inv.player.level, c.readBlockPos(), inv)));

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
