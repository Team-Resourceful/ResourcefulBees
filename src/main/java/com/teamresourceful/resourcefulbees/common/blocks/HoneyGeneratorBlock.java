package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.MenuBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.lib.util.FluidUtils;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jspecify.annotations.NonNull;

public class HoneyGeneratorBlock extends TickingBlock<HoneyGeneratorBlockEntity> implements MenuBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ACTIVE_PROPERTY = BooleanProperty.create("active");

    private static final MapCodec<HoneyGeneratorBlock> CODEC = BlockBehaviour.simpleCodec(HoneyGeneratorBlock::new);

    public HoneyGeneratorBlock(Properties properties) {
        super(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY, properties);
        registerDefaultState(defaultBlockState().setValue(ACTIVE_PROPERTY, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        return MenuBlock.super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof HoneyGeneratorBlockEntity honeyGen) {
            boolean moved = FluidUtil.interactWithFluidHandler(player, hand, pos, honeyGen.tank(), null);
            if (moved) {
                return InteractionResult.SUCCESS_SERVER;
            }
            return FluidUtils.fillOrEmptyBottle(honeyGen.tank(), player, hand);
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE_PROPERTY, FACING);
    }

//    @Override
//    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
//        tooltip.add(ItemTranslations.GEN_TOOLTIP.withStyle(ChatFormatting.GOLD));
//        super.appendHoverText(stack, worldIn, tooltip, flagIn);
//    }
}
