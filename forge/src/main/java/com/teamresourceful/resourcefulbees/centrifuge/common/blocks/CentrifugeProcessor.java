package com.teamresourceful.resourcefulbees.centrifuge.common.blocks;

import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeProcessorEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
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

public class CentrifugeProcessor extends AbstractCentrifuge {
    public CentrifugeProcessor(@NotNull Properties properties) {
        super(properties);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CentrifugeProcessorEntity(pos, state);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(CentrifugeTranslations.INTERIOR_ONLY_TOOLTIP);
        tooltip.add(CentrifugeTranslations.PROCESSOR_LIMIT_TOOLTIP);
    }

    @Override
    public boolean isGoodForInterior() {
        return true;
    }

    @Override
    public boolean isGoodForExterior() {
        return false;
    }
}
