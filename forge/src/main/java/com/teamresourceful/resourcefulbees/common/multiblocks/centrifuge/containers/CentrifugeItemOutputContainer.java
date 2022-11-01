package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeItemOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeItemOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

public class CentrifugeItemOutputContainer extends CentrifugeContainer<CentrifugeItemOutputEntity> {

    public CentrifugeItemOutputContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, getTileFromBuf(inv.player.level, buffer, CentrifugeItemOutputEntity.class), new CentrifugeState().deserializeBytes(buffer));
    }

    public CentrifugeItemOutputContainer(int id, Inventory inv, CentrifugeItemOutputEntity entity, CentrifugeState state) {
        super(ModMenus.CENTRIFUGE_ITEM_OUTPUT_CONTAINER.get(), id, inv, entity, state);
    }

    protected void addCentrifugeSlots() {
        for (int r = 0; r < tier.getContainerRows(); r++) {
            for (int c = 0; c < tier.getContainerColumns(); c++) {
                if (entity != null) this.addSlot(new OutputSlot(entity.getInventoryHandler(), c+r*tier.getContainerColumns(), 161+c*17, 46+r*17));
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return entity != null && ContainerLevelAccess.create(level, entity.getBlockPos()).evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() instanceof CentrifugeItemOutput && player.distanceToSqr(pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D) <= 64.0D, true);
    }

    @Override
    public int getContainerInputEnd() {
        return tier.getSlots();
    }

    @Override
    public int getInventoryStart() {
        return getContainerInputEnd();
    }
}
