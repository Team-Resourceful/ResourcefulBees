package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.LockBeeMessage;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ValidatedApiaryContainer extends Container {

    private final IntReferenceHolder selectedBee = IntReferenceHolder.single();
    public ApiaryTileEntity apiaryTileEntity;
    public BlockPos pos;
    public PlayerEntity player;
    public String[] beeList;

    public ValidatedApiaryContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(ModContainers.VALIDATED_APIARY_CONTAINER.get(), id);

        this.player = inv.player;
        this.pos = pos;
        this.apiaryTileEntity = (ApiaryTileEntity) world.getTileEntity(pos);

        if (apiaryTileEntity != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(apiaryTileEntity.h, ApiaryTileEntity.IMPORT, 74, 37) {
                @Override
                public int getSlotStackLimit() {
                    return apiaryTileEntity.h.getSlotLimit(ApiaryTileEntity.IMPORT);
                }

                public boolean isItemValid(ItemStack stack) {
                    return apiaryTileEntity.h.isItemValid(ApiaryTileEntity.IMPORT, stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(apiaryTileEntity.h, ApiaryTileEntity.EMPTY_JAR, 128, 37) {
                @Override
                public int getSlotStackLimit() {
                    return apiaryTileEntity.h.getSlotLimit(ApiaryTileEntity.EMPTY_JAR);
                }

                public boolean isItemValid(ItemStack stack) {
                    return apiaryTileEntity.h.isItemValid(ApiaryTileEntity.EMPTY_JAR, stack);
                }
            });
            this.addSlot(new OutputSlot(apiaryTileEntity.h, ApiaryTileEntity.EXPORT, 182, 37));
            if (!world.isRemote) {
                this.apiaryTileEntity.numPlayersUsing++;
                ApiaryTileEntity.syncApiaryToPlayersUsing(world, pos, this.apiaryTileEntity.saveToNBT(new CompoundNBT()));
            }
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 56 + j * 18, 70 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 56 + k * 18, 128));
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(@Nonnull PlayerEntity playerIn) {
        World world = this.apiaryTileEntity.getWorld();
        if (world != null && !world.isRemote)
            this.apiaryTileEntity.numPlayersUsing--;
        super.onContainerClosed(playerIn);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index <= 1) {
                if (!this.mergeItemStack(itemstack1, 2, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 2, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    public boolean selectBee(int id) {
        if (id >= -1 && id < apiaryTileEntity.getBeeCount()) {
            this.selectedBee.set(id);
        }
        return true;
    }

    public boolean lockOrUnlockBee(int id) {
        if (id >= 0 && id < apiaryTileEntity.getBeeCount()) {
            NetPacketHandler.sendToServer(new LockBeeMessage(apiaryTileEntity.getPos(), beeList[id]));
        }
        return true;
    }

    public int getSelectedBee() { return this.selectedBee.get(); }

    public ApiaryTileEntity.ApiaryBee getApiaryBee(int i) {
        return apiaryTileEntity.BEES.get(beeList[i]);
    }
}
