package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeVoid;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeVoidEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import org.jetbrains.annotations.NotNull;

public class CentrifugeVoidContainer extends CentrifugeContainer<CentrifugeVoidEntity> {

    public CentrifugeVoidContainer(int id, PlayerInventory inv, PacketBuffer buffer) {
        this(id, inv, getTileFromBuf(buffer, CentrifugeVoidEntity.class));
    }

    public CentrifugeVoidContainer(int id, PlayerInventory inv, CentrifugeVoidEntity entity) {
        super(ModContainers.CENTRIFUGE_VOID_CONTAINER.get(), id, inv, entity);
    }

    protected void setupSlots() {
        int rows = tier.equals(CentrifugeTier.BASIC) ? 1 : tier.getSlots()/4;
        int columns = tier.equals(CentrifugeTier.BASIC) ? 1 : 4;
        for (int r = 1; r <= rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (entity != null) this.addSlot(new FilterSlot(entity.getFilterInventory(), -1+c+r*4, 30 + c * 20, -10+r*20));
            }
        }

        addPlayerInvSlots();
    }

    @Override
    protected int getInvOffsetX() {
        return 0;
    }

    @Override
    protected int getInvOffsetY() {
        return 0;
    }

    @Override
    public @NotNull ItemStack clicked(int pSlotId, int pDragType, @NotNull ClickType pClickType, @NotNull PlayerEntity pPlayer) {
        if (pSlotId < tier.getSlots()) {
            switch (pClickType) {
                case PICKUP:
                case PICKUP_ALL:
                case SWAP: {
                    FilterSlot slot = (FilterSlot) this.getSlot(pSlotId);
                    ItemStack stack = pPlayer.inventory.getCarried();
                    if (stack.getCount() > 0) {
                        ItemStack copy = stack.copy();
                        copy.setCount(1);
                        slot.set(copy);
                    } else if (slot.getItem().getCount() > 0) {
                        slot.set(ItemStack.EMPTY);
                    }
                    return slot.getItem().copy();
                }
                default:
                    return ItemStack.EMPTY;
            }
        }
        return super.clicked(pSlotId, pDragType, pClickType, pPlayer);
    }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
        return entity != null && IWorldPosCallable.create(level, entity.getBlockPos()).evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() instanceof CentrifugeVoid && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public int getContainerInputEnd() {
        return tier.getSlots();
    }

    @Override
    public int getInventoryStart() {
        return getContainerInputEnd();
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }

/*    @Override
    public void executeRequest(String requestName, Object requestData) {
        assert voidEntity.getLevel() != null;
        if (voidEntity.getLevel().isClientSide()) {
            runRequest(requestName, requestData);
            return;
        }

        voidEntity.runRequest(requestName, requestData);
    }*/
}
