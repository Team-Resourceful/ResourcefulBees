package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeFluidOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeFluidOutputEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import org.jetbrains.annotations.NotNull;

public class CentrifugeFluidOutputContainer extends CentrifugeContainer<CentrifugeFluidOutputEntity> {

    public CentrifugeFluidOutputContainer(int id, PlayerInventory inv, PacketBuffer buffer) {
        this(id, inv, getTileFromBuf(buffer, CentrifugeFluidOutputEntity.class));
    }

    public CentrifugeFluidOutputContainer(int id, PlayerInventory inv, CentrifugeFluidOutputEntity entity) {
        super(ModContainers.CENTRIFUGE_FLUID_OUTPUT_CONTAINER.get(), id, inv, entity);
    }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
        return entity != null && IWorldPosCallable.create(level, entity.getBlockPos()).evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() instanceof CentrifugeFluidOutput && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public int getContainerInputEnd() {
        return 0;
    }

    @Override
    public int getInventoryStart() {
        return 0;
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }

    @Override
    protected void setupSlots() {
        addPlayerInvSlots();
    }

    @Override
    protected int getInvOffsetX() {
        return 10;
    }

    @Override
    protected int getInvOffsetY() {
        return 90;
    }
}
