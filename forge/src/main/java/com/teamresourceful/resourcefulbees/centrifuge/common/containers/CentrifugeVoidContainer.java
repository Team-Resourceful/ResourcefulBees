package com.teamresourceful.resourcefulbees.centrifuge.common.containers;

import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeMenus;
import com.teamresourceful.resourcefulbees.common.inventory.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.centrifuge.common.blocks.CentrifugeVoid;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeVoidEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CentrifugeVoidContainer extends CentrifugeContainer<CentrifugeVoidEntity> {

    public CentrifugeVoidContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, getTileFromBuf(inv.player.level, buffer, CentrifugeVoidEntity.class), new CentrifugeState().deserializeBytes(buffer), dummyEnergyStorageData());
    }

    public CentrifugeVoidContainer(int id, Inventory inv, CentrifugeVoidEntity entity, CentrifugeState state, CentrifugeEnergyStorage energyStorage) {
        super(CentrifugeMenus.CENTRIFUGE_VOID_CONTAINER.get(), id, inv, entity, state, energyStorage);
    }

    protected void addCentrifugeSlots() {
        int columns = tier.getContainerColumns()*2;
        for (int r = 0; r < tier.getContainerRows(); r++) {
            for (int c = 0; c < columns; c++) {
                this.addSlot(new FilterSlot(entity.getFilterInventory(), c+r*columns, 161+c*17, 46+r*17) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return true;
                    }

                    @Override
                    public boolean isActive() {
                        return displaySlots;
                    }
                });
            }
        }
    }

    @Override
    public void clicked(int pSlotId, int pDragType, @NotNull ClickType pClickType, @NotNull Player pPlayer) {
        if (pSlotId < tier.getSlots()*2 && (pClickType.equals(ClickType.PICKUP) || pClickType.equals(ClickType.PICKUP_ALL) || pClickType.equals(ClickType.SWAP))) {
            Slot slot = this.getSlot(pSlotId);
            ItemStack stack = getCarried();
            if (stack.getCount() > 0) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                slot.set(copy);
            } else if (slot.getItem().getCount() > 0) {
                slot.set(ItemStack.EMPTY);
            }
        } else {
            super.clicked(pSlotId, pDragType, pClickType, pPlayer);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return entity != null && ContainerLevelAccess.create(level, entity.getBlockPos()).evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() instanceof CentrifugeVoid && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public int getContainerInputEnd() {
        return tier.getSlots()*2;
    }

    @Override
    public int getInventoryStart() {
        return getContainerInputEnd();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }
}
