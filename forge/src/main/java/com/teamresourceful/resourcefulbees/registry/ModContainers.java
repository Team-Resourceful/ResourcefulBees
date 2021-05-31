package com.teamresourceful.resourcefulbees.registry;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.container.*;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    private ModContainers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ResourcefulBees.MOD_ID);


    public static final RegistryObject<MenuType<CentrifugeContainer>> CENTRIFUGE_CONTAINER = CONTAINER_TYPES.register("centrifuge", () -> IForgeContainerType
            .create((id, inv, c) -> new CentrifugeContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<MechanicalCentrifugeContainer>> MECHANICAL_CENTRIFUGE_CONTAINER = CONTAINER_TYPES.register("mechanical_centrifuge", () -> IForgeContainerType
            .create((id, inv, c) -> new MechanicalCentrifugeContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<CentrifugeMultiblockContainer>> CENTRIFUGE_MULTIBLOCK_CONTAINER = CONTAINER_TYPES.register("centrifuge_multiblock", () -> IForgeContainerType
            .create((id, inv, c) -> new CentrifugeMultiblockContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<EliteCentrifugeMultiblockContainer>> ELITE_CENTRIFUGE_MULTIBLOCK_CONTAINER = CONTAINER_TYPES.register("elite_centrifuge_multiblock", () -> IForgeContainerType
            .create((id, inv, c) -> new EliteCentrifugeMultiblockContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<HoneyGeneratorContainer>> HONEY_GENERATOR_CONTAINER = CONTAINER_TYPES.register("honey_generator", () -> IForgeContainerType
            .create((id, inv, c) -> new HoneyGeneratorContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<UnvalidatedApiaryContainer>> UNVALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("unvalidated_apiary", () -> IForgeContainerType
            .create((id, inv, c) -> new UnvalidatedApiaryContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<ValidatedApiaryContainer>> VALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("validated_apiary", () -> IForgeContainerType
            .create((id, inv, c) -> new ValidatedApiaryContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<ApiaryStorageContainer>> APIARY_STORAGE_CONTAINER = CONTAINER_TYPES.register("apiary_storage", () -> IForgeContainerType
            .create((id, inv, c) -> new ApiaryStorageContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<ApiaryBreederContainer>> APIARY_BREEDER_CONTAINER = CONTAINER_TYPES.register("apiary_breeder", () -> IForgeContainerType
            .create((id, inv, c) -> new ApiaryBreederContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<EnderBeeconContainer>> ENDER_BEECON_CONTAINER = CONTAINER_TYPES.register("ender_beecon", () -> IForgeContainerType
            .create((id, inv, c) -> new EnderBeeconContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<HoneyCongealerContainer>> HONEY_CONGEALER_CONTAINER = CONTAINER_TYPES.register("honey_congealer", () -> IForgeContainerType
            .create((id, inv, c) -> new HoneyCongealerContainer(id, inv.player.level, c.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<HoneyTankContainer>> HONEY_TANK_CONTAINER = CONTAINER_TYPES.register("honey_tank", () -> IForgeContainerType
            .create((id, inv, c) -> new HoneyTankContainer(id, inv.player.level, c.readBlockPos(), inv)));
}
