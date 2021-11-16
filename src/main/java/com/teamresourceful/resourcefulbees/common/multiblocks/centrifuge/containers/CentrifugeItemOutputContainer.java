package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeItemOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeItemOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import org.jetbrains.annotations.NotNull;

public class CentrifugeItemOutputContainer extends CentrifugeContainer<CentrifugeItemOutputEntity> {

    public CentrifugeItemOutputContainer(int id, PlayerInventory inv, PacketBuffer buffer) {
        this(id, inv, getTileFromBuf(buffer, CentrifugeItemOutputEntity.class));
    }

    public CentrifugeItemOutputContainer(int id, PlayerInventory inv, CentrifugeItemOutputEntity entity) {
        super(ModContainers.CENTRIFUGE_ITEM_OUTPUT_CONTAINER.get(), id, inv, entity);
    }

    protected void setupSlots() {
        for (int r = 0; r < CentrifugeUtils.getRows(tier); r++) {
            for (int c = 0; c < CentrifugeUtils.getColumns(tier); c++) {
                if (entity != null) this.addSlot(new OutputSlot(entity.getInventoryHandler(), c + r * 4, 162 + c * 17, 46 + r * 17));
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
