package com.dungeonderps.resourcefulbees.container;

import com.dungeonderps.resourcefulbees.item.BeeJar;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.network.NetPacketHandler;
import com.dungeonderps.resourcefulbees.network.packets.LockBeeMessage;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryTileEntity;
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

    public ApiaryTileEntity apiaryTileEntity;
    public BlockPos pos;
    public PlayerEntity player;
    private final IntReferenceHolder selectedBee = IntReferenceHolder.single();
    public String[] beeList;

    public ValidatedApiaryContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(RegistryHandler.VALIDATED_APIARY_CONTAINER.get(), id);

        this.player = inv.player;
        this.pos = pos;
        this.apiaryTileEntity = (ApiaryTileEntity)world.getTileEntity(pos);

        if (apiaryTileEntity != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(apiaryTileEntity.h, ApiaryTileEntity.IMPORT, 74, 37) {
                public boolean isItemValid(ItemStack stack) {
                    if (stack.getItem() instanceof BeeJar) {
                        BeeJar jarItem = (BeeJar) stack.getItem();
                        if(jarItem.isFilled(stack)){
                            CompoundNBT data = stack.getTag();
                            //noinspection ConstantConditions
                            String type = data.getString(BeeConstants.NBT_ENTITY);
                            String s = RegistryHandler.CUSTOM_BEE.getId().toString();
                            boolean valid = type.equals(s);
                            return valid;
                        }
                    }
                    return false;
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(apiaryTileEntity.h, ApiaryTileEntity.EMPTY_JAR, 128, 37){
                public boolean isItemValid(ItemStack stack) {
                    if (stack.getItem() instanceof BeeJar) {
                        BeeJar jarItem = (BeeJar) stack.getItem();
                        boolean isEmpty = !jarItem.isFilled(stack);
                        return (isEmpty);
                    }
                    return false;
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
            if (index <= 2) {
                if (!this.mergeItemStack(itemstack1, 3, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 1, 1, false)) {
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

    public boolean selectBee(int id){
        if (id >= 0 && id < apiaryTileEntity.getBeeCount()) {
            this.selectedBee.set(id);
            //this.updateRecipeResultSlot();
        }
        return true;
    }

    public boolean lockOrUnlockBee(int id){
        if (id >= 0 && id < apiaryTileEntity.getBeeCount()) {
            //apiaryTileEntity.BEES.get(beeList[id]).isLocked = !apiaryTileEntity.BEES.get(beeList[id]).isLocked;
            NetPacketHandler.sendToServer(new LockBeeMessage(apiaryTileEntity.getPos(), beeList[id]));
        }
        return true;
    }

    public int getSelectedBee() {
        return this.selectedBee.get();
    }
}
