package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.blocks.BeeBoxBlock;
import com.teamresourceful.resourcefulbees.common.blocks.HoneyGlass;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.registry.RegistryHelper;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.NotNull;

public final class ModBlocks {

    private ModBlocks() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<Block> BLOCKS = ResourcefulRegistries.create(Registry.BLOCK, ModConstants.MOD_ID);

    public static final BlockBehaviour.Properties WAXED_PLANKS_PROPERTIES = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD);
    public static final BlockBehaviour.Properties CENTRIFUGE_PROPERTIES = BlockBehaviour.Properties.of(Material.METAL).strength(2).sound(SoundType.METAL);

    public static final WoodType WAXED_WOOD_TYPE = RegistryHelper.registerWoodType(new ResourceLocation(ModConstants.MOD_ID, "waxed"));

    public static final RegistryEntry<Block> BEE_BOX = BLOCKS.register("bee_box", () -> new BeeBoxBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5f)));
    public static final RegistryEntry<Block> BEE_BOX_TEMP = BLOCKS.register("bee_box_temp", () -> new BeeBoxBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5f)));

    public static final RegistryEntry<Block> HONEY_GLASS = BLOCKS.register("honey_glass", () -> new HoneyGlass(BlockBehaviour.Properties.copy(Blocks.GLASS).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never).noCollission(), true));
    public static final RegistryEntry<Block> HONEY_GLASS_PLAYER = BLOCKS.register("honey_glass_player", () -> new HoneyGlass(BlockBehaviour.Properties.copy(Blocks.GLASS).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never).noCollission(), false));
    public static final RegistryEntry<Block> WAXED_PLANKS = BLOCKS.register("waxed_planks", () -> new Block(WAXED_PLANKS_PROPERTIES));
    public static final RegistryEntry<StairBlock> WAXED_STAIRS = BLOCKS.register("waxed_stairs", () -> new StairBlock(WAXED_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).dynamicShape()));
    public static final RegistryEntry<SlabBlock> WAXED_SLAB = BLOCKS.register("waxed_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).dynamicShape()));
    public static final RegistryEntry<FenceBlock> WAXED_FENCE = BLOCKS.register("waxed_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion()));
    public static final RegistryEntry<FenceGateBlock> WAXED_FENCE_GATE = BLOCKS.register("waxed_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion()));
    public static final RegistryEntry<WoodButtonBlock> WAXED_BUTTON = BLOCKS.register("waxed_button", () -> new WoodButtonBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion().noCollission()));
    public static final RegistryEntry<PressurePlateBlock> WAXED_PRESSURE_PLATE = BLOCKS.register("waxed_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion().noCollission()));
    public static final RegistryEntry<DoorBlock> WAXED_DOOR = BLOCKS.register("waxed_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion()));
    public static final RegistryEntry<TrapDoorBlock> WAXED_TRAPDOOR = BLOCKS.register("waxed_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion()));
    public static final RegistryEntry<Block> TRIMMED_WAXED_PLANKS = BLOCKS.register("trimmed_waxed_planks", () -> new Block(WAXED_PLANKS_PROPERTIES));
    public static final RegistryEntry<Block> WAXED_MACHINE_BLOCK = BLOCKS.register("waxed_machine_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryEntry<StandingSignBlock> WAXED_SIGN = BLOCKS.register("waxed_sign", () -> new StandingSignBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion().noCollission(), WAXED_WOOD_TYPE) {
        @Override
        public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return ModBlockEntityTypes.WAXED_SIGN_ENTITY.get().create(pos, state);
        }
    });
    public static final RegistryEntry<WallSignBlock> WAXED_WALL_SIGN = BLOCKS.register("waxed_wall_sign", () -> new WallSignBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion().noCollission(), WAXED_WOOD_TYPE) {
        @Override
        public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return ModBlockEntityTypes.WAXED_SIGN_ENTITY.get().create(pos, state);
        }
    });
    public static final RegistryEntry<Block> ACCELERATOR = BLOCKS.register("accelerator", () -> new TickingBlock<>(ModBlockEntityTypes.ACCELERATOR_TILE_ENTITY, CENTRIFUGE_PROPERTIES));


    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }
}
