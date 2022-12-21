package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeGearboxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CentrifugeGearbox extends AbstractCentrifuge {
    public CentrifugeGearbox(@NotNull Properties properties) {
        super(properties);
    }

    //TODO make translatable
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Multiblock interior only"));
        tooltip.add(Component.literal("Limit 64 per multiblock"));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CentrifugeGearboxEntity(pos, state);
    }

    @Override
    public boolean isGoodForExterior() {
        return false;
    }

    @Override
    public boolean isGoodForInterior() {
        return true;
    }
}
