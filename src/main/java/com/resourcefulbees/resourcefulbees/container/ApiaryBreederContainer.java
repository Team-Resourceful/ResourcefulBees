package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.item.UpgradeItem;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.tileentity.ApiaryBreederTileEntity;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.resourcefulbees.resourcefulbees.tileentity.ApiaryBreederTileEntity.*;

public class ApiaryBreederContainer extends Container {

    public ApiaryBreederTileEntity apiaryBreederTileEntity;
    public PlayerEntity player;
    public PlayerInventory playerInventory;
    public int numberOfBreeders;
    public boolean rebuild;

    public final IIntArray times;

    public ApiaryBreederContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        this(id, world, pos, inv, new IntArray(5));
    }

    public ApiaryBreederContainer(int id, World world, BlockPos pos, PlayerInventory inv, IIntArray times) {
        super(RegistryHandler.APIARY_BREEDER_CONTAINER.get(), id);
        this.player = inv.player;
        this.playerInventory = inv;
        apiaryBreederTileEntity = (ApiaryBreederTileEntity) world.getTileEntity(pos);
        this.times = times;
        this.trackIntArray(times);
        setupSlots(false);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        return true;
    }


    public void setupSlots(boolean rebuild) {
        if (apiaryBreederTileEntity != null) {
            this.inventorySlots.clear();
            numberOfBreeders = apiaryBreederTileEntity.numberOfBreeders;

            for (int i=0; i<4; i++){
                this.addSlot(new SlotItemHandlerUnconditioned(apiaryBreederTileEntity.h, UPGRADE_SLOTS[i], 6, 22 + (i *18)) {
                    @Override
                    public int getSlotStackLimit() {
                        return 1;
                    }

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return UpgradeItem.hasUpgradeData(stack) && (UpgradeItem.getUpgradeType(stack).contains(NBTConstants.NBT_BREEDER_UPGRADE));
                    }

                    @Override
                    public boolean canTakeStack(PlayerEntity playerIn) {
                        boolean flag = true;
                        int count;
                        ItemStack upgradeItem = getStack();
                        CompoundNBT data = UpgradeItem.getUpgradeData(upgradeItem);
                        if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE) && data.contains(NBTConstants.NBT_BREEDER_COUNT)) {
                            count = (int) MathUtils.clamp(data.getFloat(NBTConstants.NBT_BREEDER_COUNT), 1F, 5);
                            int numberOfBreeders = apiaryBreederTileEntity.numberOfBreeders;
                            for (int j = numberOfBreeders - count; j < numberOfBreeders; j++) {
                                if (!areSlotsEmpty(j)) {
                                    flag = false;
                                }
                            }
                        }
                        return flag;
                    }

                    public boolean areSlotsEmpty(int index){
                        boolean slotsAreEmpty;
                        slotsAreEmpty = apiaryBreederTileEntity.h.getStackInSlot(PARENT_1_SLOTS[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && apiaryBreederTileEntity.h.getStackInSlot(PARENT_2_SLOTS[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && apiaryBreederTileEntity.h.getStackInSlot(FEED_1_SLOTS[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && apiaryBreederTileEntity.h.getStackInSlot(FEED_2_SLOTS[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && apiaryBreederTileEntity.h.getStackInSlot(EMPTY_JAR_SLOTS[index]).isEmpty();
                        return slotsAreEmpty;
                    }
                });
            }

            for (int i=0; i<numberOfBreeders; i++) {
                this.addSlot(new SlotItemHandlerUnconditioned(apiaryBreederTileEntity.h, PARENT_1_SLOTS[i], 33, 18 +(i*20)){
                    public int getSlotStackLimit() {
                        return 1;
                    }

                    public boolean isItemValid(ItemStack stack) {
                        //noinspection ConstantConditions
                        return stack.getItem() instanceof BeeJar && BeeJar.isFilled(stack) && stack.getTag().getString(NBTConstants.NBT_ENTITY).startsWith(ResourcefulBees.MOD_ID);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(apiaryBreederTileEntity.h, FEED_1_SLOTS[i], 69, 18 +(i*20)){
                    public boolean isItemValid(ItemStack stack) {
                        return !(stack.getItem() instanceof BeeJar);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(apiaryBreederTileEntity.h, PARENT_2_SLOTS[i], 105, 18 +(i*20)){
                    public int getSlotStackLimit() {
                        return 1;
                    }

                    public boolean isItemValid(ItemStack stack) {
                        //noinspection ConstantConditions
                        return stack.getItem() instanceof BeeJar && BeeJar.isFilled(stack) && stack.getTag().getString(NBTConstants.NBT_ENTITY).startsWith(ResourcefulBees.MOD_ID);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(apiaryBreederTileEntity.h, FEED_2_SLOTS[i], 141, 18 +(i*20)){
                    public boolean isItemValid(ItemStack stack) {
                        return !(stack.getItem() instanceof BeeJar);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(apiaryBreederTileEntity.h, EMPTY_JAR_SLOTS[i], 177, 18 +(i*20)){
                    public int getSlotStackLimit() {
                        return 64;
                    }

                    public boolean isItemValid(ItemStack stack) {
                        return stack.getItem() instanceof BeeJar && !BeeJar.isFilled(stack);
                    }
                });
            }

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 33 + j * 18, 28 + (numberOfBreeders * 20) + (i * 18)));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k, 33 + k * 18, 86 + numberOfBreeders * 20));
            }

            this.rebuild = rebuild;
        }
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < 4 + numberOfBreeders * 5) {
                if (!this.mergeItemStack(itemstack1, 4 + numberOfBreeders * 5, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 4 + numberOfBreeders * 5, false)) {
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
}
