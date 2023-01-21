package com.teamresourceful.resourcefulbees.centrifuge.common.blocks;

import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.roguelogix.phosphophyllite.multiblock2.IAssemblyStateBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CentrifugeTerminal extends AbstractGUICentrifuge implements IAssemblyStateBlock {

    private final Supplier<BlockEntityType<CentrifugeTerminalEntity>> entityType;

    public CentrifugeTerminal(@NotNull Properties properties, Supplier<BlockEntityType<CentrifugeTerminalEntity>> entityType, CentrifugeTier tier) {
        super(properties, tier);
        this.entityType = entityType;
        this.registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void buildStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.buildStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    //TODO make translatable
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Multiblock sides only").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Cannot be used on edges").withStyle(ChatFormatting.GOLD));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return entityType.get().create(pos, state);
    }

    @Override
    public boolean usesCentrifugeState() {
        return true;
    }
}
