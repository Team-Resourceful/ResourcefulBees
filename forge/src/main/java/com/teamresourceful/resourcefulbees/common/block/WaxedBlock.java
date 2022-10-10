package com.teamresourceful.resourcefulbees.common.block;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class WaxedBlock extends Block {

    private final Supplier<Block> unwaxedBlock;

    public WaxedBlock(Supplier<Block> unwaxedBlock, Properties pProperties) {
        super(pProperties);
        this.unwaxedBlock = Suppliers.memoize(unwaxedBlock::get);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (ToolActions.AXE_WAX_OFF == toolAction) {
            return Optional.ofNullable(unwaxedBlock.get())
                    .map(block -> block.withPropertiesOf(state))
                    .orElse(null);
        }
        return null;
    }

    public Block getUnwaxedBlock() {
        return unwaxedBlock.get();
    }
}
