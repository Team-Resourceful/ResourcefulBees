//package com.teamresourceful.resourcefulbees.common.blocks;
//
//import com.mojang.serialization.MapCodec;
//import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
//import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
//import com.teamresourceful.resourcefulbees.common.lib.util.FluidUtils;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.context.BlockPlaceContext;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.BaseEntityBlock;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.HorizontalDirectionalBlock;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateDefinition;
//import net.minecraft.world.level.block.state.properties.BooleanProperty;
//import net.minecraft.world.level.block.state.properties.EnumProperty;
//import net.minecraft.world.phys.BlockHitResult;
//import org.jspecify.annotations.NonNull;
//
//@SuppressWarnings("deprecation")
//public class HoneyGenerator extends TickingBlock<HoneyGeneratorBlockEntity> {
//    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
//    public static final BooleanProperty PROPERTY_ON = BooleanProperty.create("on");
//
//    public HoneyGenerator(Properties properties) {
//        super(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY, properties);
//        registerDefaultState(defaultBlockState().setValue(PROPERTY_ON, false).setValue(FACING, Direction.NORTH));
//    }
//
//    @Override
//    protected MapCodec<? extends BaseEntityBlock> codec() {
//        return null;
//    }
//
//    @Override
//    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
//        if (level.getBlockEntity(pos) instanceof HoneyGeneratorBlockEntity generator) {
//            if (!level.isClientSide()) {
//                FluidUtils.checkBottleAndCapability(generator.getFluidContainer(), generator, player, level, pos, player.getUsedItemHand());
//            }
//            return InteractionResult.SUCCESS;
//        }
//        return super.useWithoutItem(state, level, pos, player, hitResult);
//    }
//
//    @Override
//    public BlockState getStateForPlacement(BlockPlaceContext context) {
//        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
//    }
//
//    @Override
//    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
//        builder.add(PROPERTY_ON, FACING);
//    }
//
////    @Override
////    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
////        tooltip.add(ItemTranslations.GEN_TOOLTIP.withStyle(ChatFormatting.GOLD));
////        super.appendHoverText(stack, worldIn, tooltip, flagIn);
////    }
//}
