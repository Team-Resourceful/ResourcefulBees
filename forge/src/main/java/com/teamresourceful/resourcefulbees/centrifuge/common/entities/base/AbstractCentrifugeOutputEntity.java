package com.teamresourceful.resourcefulbees.centrifuge.common.entities.base;

import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.AbstractOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCentrifugeOutputEntity<T extends AbstractOutput<E>, E> extends AbstractGUICentrifugeEntity {
    protected boolean voidExcess = false;

    protected AbstractCentrifugeOutputEntity(BlockEntityType<?> tileEntityTypeIn, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, tier, pos, state);
    }

    public abstract boolean depositResult(T result, int processQuantity);

    public final void setVoidExcess(boolean voidExcess) {
        //TODO this falls under the same issue as changing the processing stage in the in the input block.
        // ideally this would not send a full packet of data and would instead only send a packet containing
        // the changed data to players that are actively tracking the block, thus reducing the size, number, and frequency of packets being sent.
        // see read/write nbt regarding amount of data being sent
        if (level == null) return;
        this.voidExcess = voidExcess;
        this.sendToPlayersTrackingChunk();
    }

    @Override
    protected void readNBT(@NotNull CompoundTag tag) {
        voidExcess = tag.getBoolean("void_excess");
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundTag writeNBT() {
        CompoundTag tag = super.writeNBT();
        tag.putBoolean("void_excess", voidExcess);
        return tag;
    }

    @Override
    public final CompoundTag getSyncData() {
        return writeNBT();
    }

    @Override
    public final void readSyncData(@NotNull CompoundTag tag) {
        readNBT(tag);
    }

    public final boolean voidsExcess() {
        return voidExcess;
    }

    public abstract void purgeContents();
}
