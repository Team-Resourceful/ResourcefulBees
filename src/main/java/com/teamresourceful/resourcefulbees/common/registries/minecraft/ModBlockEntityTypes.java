package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.blockentities.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultApiaryTiers;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.stream.Collectors;

public final class ModBlockEntityTypes {

    private ModBlockEntityTypes() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = RegistryHelper.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> TIERED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("tiered_beehive",
        () -> {
            var validHives = ModBlocks.HIVES.getEntries().stream().map(RegistryEntry::get).collect(Collectors.toSet());
            return new BlockEntityType<>(TieredBeehiveBlockEntity::new, validHives);
        });

    //region Apiaries
    public static final RegistryEntry<BlockEntityType<? extends ApiaryBlockEntity>> T1_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t1_apiary",
            () -> new BlockEntityType<>((pos, state) -> new ApiaryBlockEntity(DefaultApiaryTiers.T1_APIARY, pos, state), ModBlocks.T1_APIARY_BLOCK.get()));
    public static final RegistryEntry<BlockEntityType<? extends ApiaryBlockEntity>> T2_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t2_apiary",
            () -> new BlockEntityType<>((pos, state) -> new ApiaryBlockEntity(DefaultApiaryTiers.T2_APIARY, pos, state), ModBlocks.T2_APIARY_BLOCK.get()));
    public static final RegistryEntry<BlockEntityType<? extends ApiaryBlockEntity>> T3_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t3_apiary",
            () -> new BlockEntityType<>((pos, state) -> new ApiaryBlockEntity(DefaultApiaryTiers.T3_APIARY, pos, state), ModBlocks.T3_APIARY_BLOCK.get()));
    public static final RegistryEntry<BlockEntityType<? extends ApiaryBlockEntity>> T4_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t4_apiary",
            () -> new BlockEntityType<>((pos, state) -> new ApiaryBlockEntity(DefaultApiaryTiers.T4_APIARY, pos, state), ModBlocks.T4_APIARY_BLOCK.get()));
    //endregion

    public static final RegistryEntry<BlockEntityType<BreederBlockEntity>> BREEDER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("breeder",
            () -> new BlockEntityType<>((BreederBlockEntity::new), ModBlocks.BREEDER_BLOCK.get()));

    public static final RegistryEntry<BlockEntityType<BeeBoxBlockEntity>> BEE_BOX_ENTITY = BLOCK_ENTITY_TYPES.register("bee_box",
            () -> new BlockEntityType<>((BeeBoxBlockEntity::new), ModBlocks.BEE_BOX.get(), ModBlocks.BEE_BOX_TEMP.get()));
    public static final RegistryEntry<BlockEntityType<WaxedSignBlockEntity>> WAXED_SIGN_ENTITY = BLOCK_ENTITY_TYPES.register("waxed_sign",
            () -> new BlockEntityType<>((WaxedSignBlockEntity::new), ModBlocks.WAXED_SIGN.get(), ModBlocks.WAXED_WALL_SIGN.get()));
    public static final RegistryEntry<BlockEntityType<WaxedHangingSignBlockEntity>> WAXED_HANGING_SIGN_ENTITY = BLOCK_ENTITY_TYPES.register("waxed_hanging_sign",
            () -> new BlockEntityType<>((WaxedHangingSignBlockEntity::new), ModBlocks.WAXED_HANGING_SIGN.get(), ModBlocks.WAXED_WALL_HANGING_SIGN.get()));
    //    public static final RegistryEntry<BlockEntityType<AcceleratorBlockEntity>> ACCELERATOR_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("accelerator",
    //            () -> new BlockEntityType<>((AcceleratorBlockEntity::new), ModBlocks.ACCELERATOR.get()));

    //public static final RegistryEntry<BlockEntityType<? extends FakeFlowerBlockEntity>> FAKE_FLOWER_ENTITY = BLOCK_ENTITY_TYPES.register("fake_flower",
    //        () -> new BlockEntityType<>((FakeFlowerBlockEntity::new), ModBlocks.FAKE_FLOWER.get()));

        public static final RegistryEntry<BlockEntityType<? extends FlowHiveBlockEntity>> FLOW_HIVE_ENTITY = BLOCK_ENTITY_TYPES.register("flow_hive",
                () -> new BlockEntityType<>((FlowHiveBlockEntity::new), ModBlocks.FLOW_HIVE.get()));

    //    public static final RegistryEntry<BlockEntityType<CreativeGenBlockEntity>> CREATIVE_GEN_ENTITY = BLOCK_ENTITY_TYPES.register("creative_gen",
    //            () -> new BlockEntityType<>((CreativeGenBlockEntity::new), ModBlocks.CREATIVE_GEN.get()));

        public static final RegistryEntry<BlockEntityType<EnderBeeconBlockEntity>> ENDER_BEECON_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("ender_beecon",
                () -> new BlockEntityType<>((EnderBeeconBlockEntity::new), ModBlocks.ENDER_BEECON.get()));

        public static final RegistryEntry<BlockEntityType<HoneyPotBlockEntity>> HONEY_POT_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("honey_pot",
                () -> new BlockEntityType<>((HoneyPotBlockEntity::new), ModBlocks.HONEY_POT.get()));

        public static final RegistryEntry<BlockEntityType<SolidificationChamberBlockEntity>> SOLIDIFICATION_CHAMBER_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("solidification_chamber",
                () -> new BlockEntityType<>((SolidificationChamberBlockEntity::new), ModBlocks.SOLIDIFICATION_CHAMBER.get()));

        public static final RegistryEntry<BlockEntityType<CentrifugeBlockEntity>> BASIC_CENTRIFUGE_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge",
                () -> new BlockEntityType<>(CentrifugeBlockEntity::new, ModBlocks.BASIC_CENTRIFUGE.get()));

        public static final RegistryEntry<BlockEntityType<CentrifugeCrankBlockEntity>> CENTRIFUGE_CRANK_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge_crank",
                () -> new BlockEntityType<>((pos, state) -> new CentrifugeCrankBlockEntity(ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get(), pos, state), ModBlocks.CENTRIFUGE_CRANK.get()));

        public static final RegistryEntry<BlockEntityType<HoneyGeneratorBlockEntity>> HONEY_GENERATOR_ENTITY = BLOCK_ENTITY_TYPES.register("honey_generator",
                () -> new BlockEntityType<>((HoneyGeneratorBlockEntity::new), ModBlocks.HONEY_GENERATOR.get()));
}
