//package com.teamresourceful.resourcefulbees.common.blocks;
//
//import com.mojang.serialization.MapCodec;
//import com.teamresourceful.resourcefulbees.common.blockentities.FlowHiveBlockEntity;
//import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
//import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHolderBlock;
//import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHouseBlock;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.MenuProvider;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.BaseEntityBlock;
//import net.minecraft.world.level.block.SoundType;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.BlockEntityTicker;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//public class FlowHiveBlock extends BeeHouseBlock implements BeeHolderBlock {
//
//    public FlowHiveBlock() {
//        super(Properties.of().strength(5f, 6f).sound(SoundType.WOOD));
//    }
//
////    @Override
////    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
////        if (level.getBlockEntity(pos) instanceof FlowHiveBlockEntity flowHive) {
////            if (!level.isClientSide()) {
////                Item item = player.getItemInHand(hand).getItem();
////                if (item instanceof BottleItem) {
////                    FluidUtils.fillBottle(flowHive.container(), player, hand);
////                } else if (item == Items.HONEY_BOTTLE) {
////                    FluidUtils.emptyBottle(flowHive.container(), player, hand);
////                }
////            }
////            return InteractionResult.SUCCESS_SERVER;
////        }
////        return super.use(state, level, pos, player, hand, hitResult);
////    }
//
////    @Override
////    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
////        components.add(FlowHiveTranslations.INFO.withStyle(ChatFormatting.GOLD));
////        components.add(FlowHiveTranslations.HARVEST.withStyle(ChatFormatting.GOLD));
////        components.add(FlowHiveTranslations.CAPACITY.withStyle(ChatFormatting.GOLD));
////    }
//
//    @Nullable
//    @Override
//    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
//        return new FlowHiveBlockEntity(pos, state);
//    }
//
//    @Override
//    protected MapCodec<? extends BaseEntityBlock> codec() {
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
//        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntityTypes.FLOW_HIVE_ENTITY.get(), BeeHolderBlockEntity::serverTick);
//    }
//}
