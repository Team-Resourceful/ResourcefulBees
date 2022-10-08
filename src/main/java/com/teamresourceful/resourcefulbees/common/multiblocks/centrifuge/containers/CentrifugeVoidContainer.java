package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeVoid;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeVoidEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CentrifugeVoidContainer extends CentrifugeContainer<CentrifugeVoidEntity> {

    public CentrifugeVoidContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, getTileFromBuf(inv.player.level, buffer, CentrifugeVoidEntity.class), new CentrifugeState().deserializeBytes(buffer));
    }

    public CentrifugeVoidContainer(int id, Inventory inv, CentrifugeVoidEntity entity, CentrifugeState state) {
        super(ModMenus.CENTRIFUGE_VOID_CONTAINER.get(), id, inv, entity, state);
    }

    protected void addCentrifugeSlots() {
        for (int r = 0; r < tier.getContainerRows(); r++) {
            for (int c = 0; c < tier.getContainerColumns() * 2; c++) {
                this.addSlot(new FilterSlot(entity.getFilterInventory(), c + r * 4, 161 + c * 17, 46 + r * 17));
            }
        }
    }

    @Override
    public void clicked(int pSlotId, int pDragType, @NotNull ClickType pClickType, @NotNull Player pPlayer) {
        if (pSlotId < tier.getSlots() && (pClickType.equals(ClickType.PICKUP) || pClickType.equals(ClickType.PICKUP_ALL) || pClickType.equals(ClickType.SWAP))) {
            FilterSlot slot = (FilterSlot) this.getSlot(pSlotId);
            ItemStack stack = getCarried();
            if (stack.getCount() > 0) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                slot.set(copy);
            } else if (slot.getItem().getCount() > 0) {
                slot.set(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return entity != null && ContainerLevelAccess.create(level, entity.getBlockPos()).evaluate((world, pos) ->
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
}
