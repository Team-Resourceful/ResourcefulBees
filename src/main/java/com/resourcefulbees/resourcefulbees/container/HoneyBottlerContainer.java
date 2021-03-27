package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.mixin.ContainerAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyBottlerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class HoneyBottlerContainer extends Container {

    private final HoneyBottlerTileEntity honeyBottlerTileEntity;
    private final PlayerEntity player;

    public HoneyBottlerContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(ModContainers.HONEY_BOTTLER_CONTAINER.get(), id);

        this.player = inv.player;
        honeyBottlerTileEntity = (HoneyBottlerTileEntity) world.getBlockEntity(pos);

        if (getHoneyBottlerTileEntity() != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(getHoneyBottlerTileEntity().getTileStackHandler(), HoneyBottlerTileEntity.BOTTLE_INPUT, 93, 16) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() == Items.GLASS_BOTTLE;
                }
            });
            this.addSlot(new OutputSlot(getHoneyBottlerTileEntity().getTileStackHandler(), HoneyBottlerTileEntity.BOTTLE_OUTPUT, 93, 54));

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
            }
        }
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index <= 1) {
                if (!this.moveItemStackTo(itemstack1, 2, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (getHoneyBottlerTileEntity() == null) {
            return;
        }

        for (IContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            getHoneyBottlerTileEntity().sendGUINetworkPacket(listener);
        }
    }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
        return true;
    }

    public HoneyBottlerTileEntity getHoneyBottlerTileEntity() {
        return honeyBottlerTileEntity;
    }
}
