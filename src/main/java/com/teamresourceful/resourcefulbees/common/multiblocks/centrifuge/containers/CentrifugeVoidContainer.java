package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeVoid;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeVoidEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
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
        this(id, inv, getTileFromBuf(inv.player.level, buffer, CentrifugeVoidEntity.class));
    }

    public CentrifugeVoidContainer(int id, Inventory inv, CentrifugeVoidEntity entity) {
        super(ModMenus.CENTRIFUGE_VOID_CONTAINER.get(), id, inv, entity);
    }

    protected void addMenuSlots() {
        for (int r = 0; r < CentrifugeUtils.getRows(tier); r++) {
            for (int c = 0; c < CentrifugeUtils.getColumns(tier) * 2; c++) {
                if (entity != null) this.addSlot(new FilterSlot(entity.getFilterInventory(), c + r * 4, 162 + c * 17, 46 + r * 17));
            }
        }

        addPlayerInvSlots();
    }

    @Override
    public @NotNull void clicked(int pSlotId, int pDragType, @NotNull ClickType pClickType, @NotNull Player pPlayer) {
        if (pSlotId < tier.getSlots()) {
            switch (pClickType) {
                case PICKUP:
                case PICKUP_ALL:
                case SWAP: {
                    FilterSlot slot = (FilterSlot) this.getSlot(pSlotId);
                    ItemStack stack = getCarried(); //TODO SEE IF CORRECT, this does not exist anymore pPlayer.inventory.getCarried();
                    if (stack.getCount() > 0) {
                        ItemStack copy = stack.copy();
                        copy.setCount(1);
                        slot.set(copy);
                    } else if (slot.getItem().getCount() > 0) {
                        slot.set(ItemStack.EMPTY);
                    }
                    //TODO return slot.getItem().copy(); clicked no longer returns itemstack see what needs to be changed.
                }
                default:
                    //TODO READ ABOVE return ItemStack.EMPTY;
            }
        }
        //TODO READ ABOVE return super.clicked(pSlotId, pDragType, pClickType, pPlayer);
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
