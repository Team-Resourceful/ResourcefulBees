package com.teamresourceful.resourcefulbees.common.inventory.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.SlotItemHandlerUnconditioned;
import com.teamresourceful.resourcefulbees.common.item.UpgradeItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ApiaryBreederContainer extends ContainerWithStackMove {

    private final ApiaryBreederTileEntity apiaryBreederTileEntity;
    private final Inventory playerInventory;
    private int numberOfBreeders;
    private boolean rebuild;

    public final ContainerData times;

    public ApiaryBreederContainer(int id, Level world, BlockPos pos, Inventory inv) {
        this(id, world, pos, inv, new SimpleContainerData(5));
    }

    public ApiaryBreederContainer(int id, Level world, BlockPos pos, Inventory inv, ContainerData times) {
        super(ModContainers.APIARY_BREEDER_CONTAINER.get(), id);
        this.playerInventory = inv;
        apiaryBreederTileEntity = (ApiaryBreederTileEntity) world.getBlockEntity(pos);
        this.times = times;
        this.addDataSlots(times);
        setupSlots(false);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }


    public void setupSlots(boolean rebuild) {
        if (getApiaryBreederTileEntity() != null) {
            this.slots.clear();
            numberOfBreeders = getApiaryBreederTileEntity().getNumberOfBreeders();

            for (int i=0; i<4; i++){
                int finalI = i;
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), ApiaryBreederTileEntity.getUpgradeSlots()[finalI], 6, 22 + (finalI *18)) {
                    @Override
                    public int getMaxStackSize() {
                        return getApiaryBreederTileEntity().getTileStackHandler().getSlotLimit(ApiaryBreederTileEntity.getUpgradeSlots()[finalI]);
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(ApiaryBreederTileEntity.getUpgradeSlots()[finalI], stack);
                    }

                    @Override
                    public boolean mayPickup(Player playerIn) {
                        boolean flag = true;
                        int count;
                        ItemStack upgradeItem = getItem();
                        CompoundTag data = UpgradeItem.getUpgradeData(upgradeItem);
                        if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE) && data.contains(NBTConstants.NBT_BREEDER_COUNT)) {
                            count = (int) Mth.clamp(data.getFloat(NBTConstants.NBT_BREEDER_COUNT), 1F, 5);
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
                        slotsAreEmpty = getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(ApiaryBreederTileEntity.getParent1Slots()[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(ApiaryBreederTileEntity.getParent2Slots()[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(ApiaryBreederTileEntity.getFeed1Slots()[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(ApiaryBreederTileEntity.getFeed2Slots()[index]).isEmpty();
                        slotsAreEmpty = slotsAreEmpty && getApiaryBreederTileEntity().getTileStackHandler().getStackInSlot(ApiaryBreederTileEntity.getEmptyJarSlots()[index]).isEmpty();
                        return slotsAreEmpty;
                    }
                });
            }

            for (int i = 0; i< getNumberOfBreeders(); i++) {
                int finalI = i;
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), ApiaryBreederTileEntity.getParent1Slots()[finalI], 33, 18 +(finalI *20)){

                    @Override
                    public int getMaxStackSize() {
                        return getApiaryBreederTileEntity().getTileStackHandler().getSlotLimit(ApiaryBreederTileEntity.getParent1Slots()[finalI]);
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(ApiaryBreederTileEntity.getParent1Slots()[finalI], stack);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), ApiaryBreederTileEntity.getFeed1Slots()[i], 69, 18 +(i*20)){

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(ApiaryBreederTileEntity.getFeed1Slots()[finalI], stack);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), ApiaryBreederTileEntity.getParent2Slots()[i], 105, 18 +(i*20)){

                    @Override
                    public int getMaxStackSize() {
                        return getApiaryBreederTileEntity().getTileStackHandler().getSlotLimit(ApiaryBreederTileEntity.getParent2Slots()[finalI]);
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(ApiaryBreederTileEntity.getParent2Slots()[finalI], stack);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), ApiaryBreederTileEntity.getFeed2Slots()[i], 141, 18 +(i*20)){

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(ApiaryBreederTileEntity.getFeed2Slots()[finalI], stack);
                    }
                });
                this.addSlot(new SlotItemHandlerUnconditioned(getApiaryBreederTileEntity().getTileStackHandler(), ApiaryBreederTileEntity.getEmptyJarSlots()[i], 177, 18 +(i*20)){

                    @Override
                    public int getMaxStackSize() {
                        return getApiaryBreederTileEntity().getTileStackHandler().getSlotLimit(ApiaryBreederTileEntity.getEmptyJarSlots()[finalI]);
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return getApiaryBreederTileEntity().getTileStackHandler().isItemValid(ApiaryBreederTileEntity.getEmptyJarSlots()[finalI], stack);
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

    @Override
    public int getContainerInputEnd() {
        return 4 + getNumberOfBreeders() * 5;
    }

    @Override
    public int getInventoryStart() {
        return getContainerInputEnd();
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }

    public ApiaryBreederTileEntity getApiaryBreederTileEntity() {
        return apiaryBreederTileEntity;
    }

    public Inventory getPlayerInventory() {
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
