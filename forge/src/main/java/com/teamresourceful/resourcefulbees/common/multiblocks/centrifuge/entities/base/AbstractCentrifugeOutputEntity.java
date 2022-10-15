package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.AbstractOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractCentrifugeOutputEntity<T extends AbstractOutput<E>, E> extends AbstractGUICentrifugeEntity {
    protected boolean voidExcess = true;

    protected AbstractCentrifugeOutputEntity(BlockEntityType<?> tileEntityTypeIn, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, tier, pos, state);
    }

    public abstract boolean depositResult(T result, int processQuantity);

    public void setVoidExcess(boolean voidExcess) {
        //TODO this falls under the same issue as changing the processing stage in the in the input block.
        // ideally this would not send a full packet of data and would instead only send a packet containing
        // the changed data to players that are actively tracking the block, thus reducing the size, number, and frequency of packets being sent.
        // see read/write nbt regarding amount of data being sent
        if (level == null) return;
        this.voidExcess = voidExcess;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public boolean voidsExcess() {
        return voidExcess;
    }

    public abstract void purgeContents();
}
