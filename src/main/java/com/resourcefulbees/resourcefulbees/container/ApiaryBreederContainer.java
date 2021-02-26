package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.item.UpgradeItem;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
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

import static com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryBreederTileEntity.*;

public class ApiaryBreederContainer extends Container {

    private final ApiaryBreederTileEntity apiaryBreederTileEntity;
    private final PlayerInventory playerInventory;
    private int numberOfBreeders;
    private boolean rebuild;

    public final IIntArray times;

    public ApiaryBreederContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        this(id, world, pos, inv, new IntArray(5));
    }

    public ApiaryBreederContainer(int id, World world, BlockPos pos, PlayerInventory inv, IIntArray times) {
        super(ModContainers.APIARY_BREEDER_CONTAINER.get(), id);
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
        if (getApiaryBreederTileEntity() != null) {
            this.inventorySlots.clear();
            numberOfBreeders = getApiaryBreederTileEntity().getNumberOfBreeders();

            for (int i=0; i<4; i++){
                int finalI = i;
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), getUpgradeSlots()[finalI], 6, 22 + (finalI *18)) {
                    @Override
                    public int getSlotStackLimit() {
                        return getApiaryBreederTileEntity().getTileStackHandler().getSlotLimit(getUpgradeSlots()[finalI]);
                    }

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(getUpgradeSlots()[finalI], stack);
                    }

                    @Override
                    public boolean canTakeStack(PlayerEntity playerIn) {
                        boolean flag = true;
                        int count;
                        ItemStack upgradeItem = getStack();
                        CompoundNBT data = UpgradeItem.getUpgradeData(upgradeItem);
                        if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE) && data.contains(NBTConstants.NBT_BREEDER_COUNT)) {
                            count = (int) MathUtils.clamp(data.getFloat(NBTConstants.NBT_BREEDER_COUNT), 1F, 5);
                            int numBreeders = getApiaryBreederTileEntity().getNumberOfBreeders();
                            for (int j = numBreeders - count; j < numBreeders; j++) {
                                if (!areSlotsEmpty(j)) {
                                    flag = false;
                                }
                            }
                        }
                        return flag;
                    }

                    public boolean areSlotsEmpty(int index){
                        boolean slotsAreEmpty;
                        slotsAreEmpty = getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(getParent1Slots()[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(getParent2Slots()[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(getFeed1Slots()[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(getFeed2Slots()[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(getEmptyJarSlots()[index]).isEmpty();
                        return slotsAreEmpty;
                    }
                });
            }

            for (int i = 0; i< getNumberOfBreeders(); i++) {
                int finalI = i;
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), getParent1Slots()[finalI], 33, 18 +(finalI *20)){

                    @Override
                    public int getSlotStackLimit() {
                        return getApiaryBreederTileEntity().getTileStackHandler().getSlotLimit(getParent1Slots()[finalI]);
                    }

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(getParent1Slots()[finalI], stack);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), getFeed1Slots()[i], 69, 18 +(i*20)){

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(getFeed1Slots()[finalI], stack);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), getParent2Slots()[i], 105, 18 +(i*20)){

                    @Override
                    public int getSlotStackLimit() {
                        return getApiaryBreederTileEntity().getTileStackHandler().getSlotLimit(getParent2Slots()[finalI]);
                    }

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(getParent2Slots()[finalI], stack);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), getFeed2Slots()[i], 141, 18 +(i*20)){

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(getFeed2Slots()[finalI], stack);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), getEmptyJarSlots()[i], 177, 18 +(i*20)){

                    @Override
                    public int getSlotStackLimit() {
                        return getApiaryBreederTileEntity().getTileStackHandler().getSlotLimit(getEmptyJarSlots()[finalI]);
                    }

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(getEmptyJarSlots()[finalI], stack);
                    }
                });
            }

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(getPlayerInventory(), j + i * 9 + 9, 33 + j * 18, 28 + (getNumberOfBreeders() * 20) + (i * 18)));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(getPlayerInventory(), k, 33 + k * 18, 86 + getNumberOfBreeders() * 20));
            }

            this.setRebuild(rebuild);
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
            if (index < 4 + getNumberOfBreeders() * 5) {
                if (!this.mergeItemStack(itemstack1, 4 + getNumberOfBreeders() * 5, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 4 + getNumberOfBreeders() * 5, false)) {
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

    public ApiaryBreederTileEntity getApiaryBreederTileEntity() {
        return apiaryBreederTileEntity;
    }

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    public int getNumberOfBreeders() {
        return numberOfBreeders;
    }

    public boolean isRebuild() {
        return rebuild;
    }

    public void setRebuild(boolean rebuild) {
        this.rebuild = rebuild;
    }
}
