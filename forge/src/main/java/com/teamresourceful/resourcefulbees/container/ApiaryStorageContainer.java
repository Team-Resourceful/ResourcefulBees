package com.teamresourceful.resourcefulbees.container;

import com.teamresourceful.resourcefulbees.item.UpgradeItem;
import com.teamresourceful.resourcefulbees.lib.NBTConstants;
import com.teamresourceful.resourcefulbees.registry.ModContainers;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ApiaryStorageContainer extends ContainerWithStackMove {

    private final ApiaryStorageTileEntity apiaryStorageTileEntity;
    private final Inventory playerInventory;
    private int numberOfSlots;
    private boolean rebuild;

    public ApiaryStorageContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.APIARY_STORAGE_CONTAINER.get(), id);
        this.playerInventory = inv;
        this.apiaryStorageTileEntity = (ApiaryStorageTileEntity) world.getBlockEntity(pos);
        setupSlots(false);
    }

    /**
     * Determines whether supplied player can use this container
     *
     * @param player the player
     */
    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }


    public void setupSlots(boolean rebuild) {
        if (getApiaryStorageTileEntity() != null) {
            this.slots.clear();
            numberOfSlots = getApiaryStorageTileEntity().getNumberOfSlots();
            this.addSlot(new SlotItemHandlerUnconditioned(getApiaryStorageTileEntity().getItemStackHandler(), ApiaryStorageTileEntity.UPGRADE_SLOT, 7, 18) {

                @Override
                public int getMaxStackSize() { return 1; }

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) { return UpgradeItem.hasUpgradeData(stack) && (UpgradeItem.getUpgradeType(stack).contains(NBTConstants.NBT_STORAGE_UPGRADE)); }

                @Override
                public boolean mayPickup(Player playerIn) {
                    boolean flag = true;

                    for (int i = 10; i <= getNumberOfSlots(); ++i) {
                        if (!getApiaryStorageTileEntity().getItemStackHandler().getStackInSlot(i).isEmpty()) {
                            flag = false;
                            break;
                        }
                    }
                    return flag;
                }
            });

            int rows;
            if (getNumberOfSlots() != 108) {
                rows = getNumberOfSlots() / 9;
                for (int r = 0; r < rows; ++r) {
                    for (int c = 0; c < 9; ++c) {
                        this.addSlot(new OutputSlot(getApiaryStorageTileEntity().getItemStackHandler(), c + r * 9 + 1, 26 + 8 + c * 18, 18 + r * 18));
                    }
                }
            } else {
                rows = 9;
                for (int r = 0; r < 9; ++r) {
                    for (int c = 0; c < 12; ++c) {
                        this.addSlot(new OutputSlot(getApiaryStorageTileEntity().getItemStackHandler(), c + r * 12 + 1, 26 + 8 + c * 18, 18 + r * 18));
                    }
                }
            }

            int invX = getNumberOfSlots() == 108 ? 35 : 8;

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(getPlayerInventory(), j + i * 9 + 9, 26 + invX + j * 18, 32 + (rows * 18) + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(getPlayerInventory(), k, 26 + invX + k * 18, 90 + rows * 18));
            }

            this.setRebuild(rebuild);
        }
    }


    @Override
    public int getContainerInputEnd() {
        return 1;
    }

    @Override
    public int getInventoryStart() {
        return 1 + getNumberOfSlots();
    }

    public Inventory getPlayerInventory() {
        return playerInventory;
    }

    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    public ApiaryStorageTileEntity getApiaryStorageTileEntity() {
        return apiaryStorageTileEntity;
    }

    public boolean isRebuild() {
        return rebuild;
    }

    public void setRebuild(boolean rebuild) {
        this.rebuild = rebuild;
    }
}
