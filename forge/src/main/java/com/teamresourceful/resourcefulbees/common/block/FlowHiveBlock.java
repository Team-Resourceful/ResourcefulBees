package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.block.base.BeeHouseBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.FlowHiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.item.IShiftingToolTip;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FlowHiveBlock extends BeeHouseBlock implements IShiftingToolTip {

    public FlowHiveBlock() {
        super(Properties.of(Material.WOOD).strength(5f, 6f).sound(SoundType.WOOD));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof FlowHiveBlockEntity flowHive) {
            if (!level.isClientSide) {
                Item item = player.getItemInHand(hand).getItem();
                if (item instanceof BottleItem) {
                    HoneyFluidTank.fillBottle(flowHive.getTank(), player, hand);
                } else if (item instanceof HoneyBottleItem) {
                    HoneyFluidTank.emptyBottle(flowHive.getTank(), player, hand);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        setupTooltip(stack, level, components, flag);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FlowHiveBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntityTypes.FLOW_HIVE_ENTITY.get(), BeeHolderBlockEntity::serverTick);
    }

    @Override
    public Component getShiftingDisplay() {
        return TranslationConstants.Items.TOOLTIP_STATS;
    }

    @Override
    public void appendShiftTooltip(@NotNull ItemStack stack, @Nullable BlockGetter pLevel, @NotNull List<Component> components, @NotNull TooltipFlag flag) {

    }
}
