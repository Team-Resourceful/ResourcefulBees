package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.inventory.containers.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    private ModContainers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ResourcefulBees.MOD_ID);


    public static final RegistryObject<ContainerType<HoneyGeneratorContainer>> HONEY_GENERATOR_CONTAINER = CONTAINER_TYPES.register("honey_generator", () -> IForgeContainerType
            .create((id, inv, c) -> new HoneyGeneratorContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<UnvalidatedApiaryContainer>> UNVALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("unvalidated_apiary", () -> IForgeContainerType
            .create((id, inv, c) -> new UnvalidatedApiaryContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<ValidatedApiaryContainer>> VALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("validated_apiary", () -> IForgeContainerType
            .create((id, inv, c) -> new ValidatedApiaryContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<ApiaryStorageContainer>> APIARY_STORAGE_CONTAINER = CONTAINER_TYPES.register("apiary_storage", () -> IForgeContainerType
            .create((id, inv, c) -> new ApiaryStorageContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<ApiaryBreederContainer>> APIARY_BREEDER_CONTAINER = CONTAINER_TYPES.register("apiary_breeder", () -> IForgeContainerType
            .create((id, inv, c) -> new ApiaryBreederContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<EnderBeeconContainer>> ENDER_BEECON_CONTAINER = CONTAINER_TYPES.register("ender_beecon", () -> IForgeContainerType
            .create((id, inv, c) -> new EnderBeeconContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<HoneyCongealerContainer>> HONEY_CONGEALER_CONTAINER = CONTAINER_TYPES.register("honey_congealer", () -> IForgeContainerType
            .create((id, inv, c) -> new HoneyCongealerContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<HoneyPotContainer>> HONEY_POT_CONTAINER = CONTAINER_TYPES.register("honey_pot", () -> IForgeContainerType
            .create((id, inv, c) -> new HoneyPotContainer(id, inv.player.level, c.readBlockPos(), inv)));

    // region Centrifuge Containers
    public static final RegistryObject<ContainerType<CentrifugeTerminalContainer>> CENTRIFUGE_TERMINAL_CONTAINER = CONTAINER_TYPES.register("centrifuge/terminal", () -> IForgeContainerType
            .create(CentrifugeTerminalContainer::new));
    public static final RegistryObject<ContainerType<CentrifugeInputContainer>> CENTRIFUGE_INPUT_CONTAINER = CONTAINER_TYPES.register("centrifuge/input/item", () -> IForgeContainerType
            .create(CentrifugeInputContainer::new));
    public static final RegistryObject<ContainerType<CentrifugeItemOutputContainer>> CENTRIFUGE_ITEM_OUTPUT_CONTAINER = CONTAINER_TYPES.register("centrifuge/output/item", () -> IForgeContainerType
            .create(CentrifugeItemOutputContainer::new));
    public static final RegistryObject<ContainerType<CentrifugeFluidOutputContainer>> CENTRIFUGE_FLUID_OUTPUT_CONTAINER = CONTAINER_TYPES.register("centrifuge/output/fluid", () -> IForgeContainerType
            .create(CentrifugeFluidOutputContainer::new));
    public static final RegistryObject<ContainerType<CentrifugeVoidContainer>> CENTRIFUGE_VOID_CONTAINER = CONTAINER_TYPES.register("centrifuge/void", () -> IForgeContainerType
            .create(CentrifugeVoidContainer::new));
    // endregion
}
