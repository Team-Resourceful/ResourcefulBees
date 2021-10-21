package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeItemOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeItemOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CentrifugeItemOutputContainer extends CentrifugeContainer<CentrifugeItemOutputEntity> {

    public CentrifugeItemOutputContainer(int id, PlayerInventory inv, PacketBuffer buffer) {
        this(id, inv, getTileFromBuf(buffer, CentrifugeItemOutputEntity.class));
    }

    public CentrifugeItemOutputContainer(int id, PlayerInventory inv, CentrifugeItemOutputEntity entity) {
        super(ModContainers.CENTRIFUGE_ITEM_OUTPUT_CONTAINER.get(), id, inv, entity);
    }

    protected void setupSlots() {
        int rows = tier.equals(CentrifugeTier.BASIC) ? 1 : tier.getSlots()/4;
        int columns = tier.equals(CentrifugeTier.BASIC) ? 1 : 4;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (entity != null) this.addSlot(new SlotItemHandler(entity.getInventoryHandler(), c+r*4, 30 + c * 18, 10 + r * 18));
            }
        }

        addPlayerInvSlots();
    }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
        return entity != null && IWorldPosCallable.create(level, entity.getBlockPos()).evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() instanceof CentrifugeItemOutput && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public int getInvOffsetX() {
        return 10;
    }

    @Override
    public int getInvOffsetY() {
        return 90;
    }

    @Override
    public int getContainerInputEnd() {
        return entity == null ? 0 : entity.getTier().getSlots();
    }

    @Override
    public int getInventoryStart() {
        return getContainerInputEnd();
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }
}
