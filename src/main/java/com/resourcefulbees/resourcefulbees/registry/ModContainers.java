package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.container.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ResourcefulBees.MOD_ID);


    public static final RegistryObject<ContainerType<CentrifugeContainer>> CENTRIFUGE_CONTAINER = CONTAINER_TYPES.register("centrifuge", () -> IForgeContainerType
            .create((id, inv, c) -> new CentrifugeContainer(id, inv.player.world, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<MechanicalCentrifugeContainer>> MECHANICAL_CENTRIFUGE_CONTAINER = CONTAINER_TYPES.register("mechanical_centrifuge", () -> IForgeContainerType
            .create((id, inv, c) -> new MechanicalCentrifugeContainer(id, inv.player.world, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<CentrifugeMultiblockContainer>> CENTRIFUGE_MULTIBLOCK_CONTAINER = CONTAINER_TYPES.register("centrifuge_multiblock", () -> IForgeContainerType
            .create((id, inv, c) -> new CentrifugeMultiblockContainer(id, inv.player.world, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<EliteCentrifugeMultiblockContainer>> ELITE_CENTRIFUGE_MULTIBLOCK_CONTAINER = CONTAINER_TYPES.register("elite_centrifuge_multiblock", () -> IForgeContainerType
            .create((id, inv, c) -> new EliteCentrifugeMultiblockContainer(id, inv.player.world, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<HoneyGeneratorContainer>> HONEY_GENERATOR_CONTAINER = CONTAINER_TYPES.register("honey_generator", () -> IForgeContainerType
            .create((id, inv, c) -> new HoneyGeneratorContainer(id, inv.player.world, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<UnvalidatedApiaryContainer>> UNVALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("unvalidated_apiary", () -> IForgeContainerType
            .create((id, inv, c) -> new UnvalidatedApiaryContainer(id, inv.player.world, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<ValidatedApiaryContainer>> VALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("validated_apiary", () -> IForgeContainerType
            .create((id, inv, c) -> new ValidatedApiaryContainer(id, inv.player.world, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<ApiaryStorageContainer>> APIARY_STORAGE_CONTAINER = CONTAINER_TYPES.register("apiary_storage", () -> IForgeContainerType
            .create((id, inv, c) -> new ApiaryStorageContainer(id, inv.player.world, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<ApiaryBreederContainer>> APIARY_BREEDER_CONTAINER = CONTAINER_TYPES.register("apiary_breeder", () -> IForgeContainerType
            .create((id, inv, c) -> new ApiaryBreederContainer(id, inv.player.world, c.readBlockPos(), inv)));
    public static final RegistryObject<ContainerType<EnderBeeconContainer>> ENDER_BEECON_CONTAINER = CONTAINER_TYPES.register("ender_beecon", () -> IForgeContainerType
            .create((id, inv, c) -> new EnderBeeconContainer(id, inv.player.world, c.readBlockPos(), inv)));
}
