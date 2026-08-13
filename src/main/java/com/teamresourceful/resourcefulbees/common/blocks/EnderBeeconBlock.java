package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.MenuBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.lib.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class EnderBeeconBlock extends TickingBlock<EnderBeeconBlockEntity> implements MenuBlock {

    protected static final VoxelShape VOXEL_SHAPE_TOP = Util.make(() -> {
        VoxelShape shape = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);
        shape = Shapes.join(shape, Block.box(3.0D, 1.0D, 3.0D, 13.0D, 3.0D, 13.0D), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(4.0D, 3.0D, 4.0D, 12.0D, 11.0D, 12.0D), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(3.0D, 11.0D, 3.0D, 13.0D, 13.0D, 13.0D), BooleanOp.OR);
        return shape;
    });

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BEAM = BooleanProperty.create("beam");
    public static final BooleanProperty SOUND = BooleanProperty.create("sound");

    private static final MapCodec<EnderBeeconBlock> CODEC = BlockBehaviour.simpleCodec(EnderBeeconBlock::new);

    public EnderBeeconBlock(BlockBehaviour.Properties properties) {
        super(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(BEAM, true).setValue(SOUND, true));
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof EnderBeeconBlockEntity beecon) {
            if (!level.isClientSide()) {
                if (stack.is(ItemTags.WOOL)) {
                    level.setBlock(pos, state.cycle(SOUND), Block.UPDATE_ALL);
                    return InteractionResult.SUCCESS_SERVER;
                } else if (stack.is(Items.STICK)) {
                    level.setBlock(pos, state.cycle(BEAM), Block.UPDATE_ALL);
                    return InteractionResult.SUCCESS_SERVER;
                }
            }

            boolean moved = FluidUtil.interactWithFluidHandler(player, hand, pos, beecon.fluidHandler(), null);
            if(moved) return InteractionResult.SUCCESS_SERVER;

            return FluidUtils.fillOrEmptyBottle(beecon.fluidHandler(), player, hand);
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return VOXEL_SHAPE_TOP;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED).add(BEAM).add(SOUND);
    }

    @NotNull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    protected @NonNull BlockState updateShape(BlockState state, @NonNull LevelReader level, @NonNull ScheduledTickAccess ticks, @NonNull BlockPos pos, @NonNull Direction directionToNeighbour, @NonNull BlockPos neighbourPos, @NonNull BlockState neighbourState, @NonNull RandomSource random) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

//    @Override
//    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
//        tooltip.add(ItemTranslations.BEECON_TOOLTIP.withStyle(ChatFormatting.LIGHT_PURPLE));
//        tooltip.add(ItemTranslations.BEECON_TOOLTIP_1.withStyle(ChatFormatting.LIGHT_PURPLE));
//    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, Player player, @NonNull BlockHitResult hitResult) {
        return MenuBlock.super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
