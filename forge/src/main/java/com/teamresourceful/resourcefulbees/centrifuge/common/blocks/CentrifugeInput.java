package com.teamresourceful.resourcefulbees.centrifuge.common.blocks;

import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.roguelogix.phosphophyllite.multiblock2.IAssemblyStateBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CentrifugeInput extends AbstractGUICentrifuge implements IAssemblyStateBlock {

    private final Supplier<BlockEntityType<CentrifugeInputEntity>> entityType;

    public CentrifugeInput(@NotNull Properties properties, Supplier<BlockEntityType<CentrifugeInputEntity>> entityType, CentrifugeTier tier) {
        super(properties, tier);
        this.entityType = entityType;
    }

    //TODO make translatable
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Multiblock top only").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Cannot be used on edges").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Slots: " + tier.getSlots()).withStyle(ChatFormatting.GOLD));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return entityType.get().create(pos, state);
    }
}
