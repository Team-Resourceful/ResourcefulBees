package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBreederBlockEntity;
import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.inventory.slots.SlotItemHandlerUnconditioned;
import com.teamresourceful.resourcefulbees.common.item.UpgradeItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ApiaryBreederContainer extends AbstractModContainerMenu<ApiaryBreederBlockEntity> {

    private int numberOfBreeders;
    private boolean rebuild;
    public final ContainerData times;
    public final ContainerData endTimes;

    public ApiaryBreederContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, getTileFromBuf(inv.player.level, buffer, ApiaryBreederBlockEntity.class), new SimpleContainerData(2), new SimpleContainerData(2));
    }

    public ApiaryBreederContainer(int id, Inventory inv, ApiaryBreederBlockEntity entity, ContainerData times, ContainerData endTimes) {
        super(ModMenus.APIARY_BREEDER_CONTAINER.get(), id, inv, entity);
        this.times = times;
        this.endTimes = endTimes;
        this.addDataSlots(times);
        this.addDataSlots(endTimes);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }


    public void setupSlots(boolean rebuild) {
        //this.slots.clear();
        numberOfBreeders = getEntity().getNumberOfBreeders();

        this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getInventory(), 0, 6, 18) {
            @Override
            public int getMaxStackSize() {
                return getEntity().getInventory().getSlotLimit(0);
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return getEntity().getInventory().isItemValid(0, stack);
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                boolean flag = true;
                int count;
                ItemStack upgradeItem = getItem();
                CompoundTag data = UpgradeItem.getUpgradeData(upgradeItem);
                if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE) && data.contains(NBTConstants.NBT_BREEDER_COUNT)) {
                    count = (int) Mth.clamp(data.getFloat(NBTConstants.NBT_BREEDER_COUNT), 1F, 5);
                    int numBreeders = getEntity().getNumberOfBreeders();
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
                slotsAreEmpty = getEntity().getInventory().getStackInSlot(ApiaryBreederBlockEntity.PARENT_1_SLOTS.get(index)).isEmpty();
                slotsAreEmpty = slotsAreEmpty && getEntity().getInventory().getStackInSlot(ApiaryBreederBlockEntity.PARENT_2_SLOTS.get(index)).isEmpty();
                slotsAreEmpty = slotsAreEmpty && getEntity().getInventory().getStackInSlot(ApiaryBreederBlockEntity.FEED_1_SLOTS.get(index)).isEmpty();
                slotsAreEmpty = slotsAreEmpty && getEntity().getInventory().getStackInSlot(ApiaryBreederBlockEntity.FEED_2_SLOTS.get(index)).isEmpty();
                slotsAreEmpty = slotsAreEmpty && getEntity().getInventory().getStackInSlot(ApiaryBreederBlockEntity.EMPTY_JAR_SLOTS.get(index)).isEmpty();
                return slotsAreEmpty;
            }
        });

        for (int i = 0; i < getNumberOfBreeders(); i++) {
            int finalI = i;
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getInventory(), ApiaryBreederBlockEntity.PARENT_1_SLOTS.get(finalI), 30, 18 +(finalI *20)){

                @Override
                public int getMaxStackSize() {
                    return getEntity().getInventory().getSlotLimit(ApiaryBreederBlockEntity.PARENT_1_SLOTS.get(finalI));
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getEntity().getInventory().isItemValid(ApiaryBreederBlockEntity.PARENT_1_SLOTS.get(finalI), stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getInventory(), ApiaryBreederBlockEntity.FEED_1_SLOTS.get(finalI), 66, 18 +(i*20)){

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getEntity().getInventory().isItemValid(ApiaryBreederBlockEntity.FEED_1_SLOTS.get(finalI), stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getInventory(), ApiaryBreederBlockEntity.PARENT_2_SLOTS.get(finalI), 102, 18 +(i*20)){

                @Override
                public int getMaxStackSize() {
                    return getEntity().getInventory().getSlotLimit(ApiaryBreederBlockEntity.PARENT_2_SLOTS.get(finalI));
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getEntity().getInventory().isItemValid(ApiaryBreederBlockEntity.PARENT_2_SLOTS.get(finalI), stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getInventory(), ApiaryBreederBlockEntity.FEED_2_SLOTS.get(finalI), 138, 18 +(i*20)){

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return getEntity().getInventory().isItemValid(ApiaryBreederBlockEntity.FEED_2_SLOTS.get(finalI), stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getInventory(), ApiaryBreederBlockEntity.EMPTY_JAR_SLOTS.get(finalI), 174, 18 +(i*20)){

                @Override
                public int getMaxStackSize() {
                    return getEntity().getInventory().getSlotLimit(ApiaryBreederBlockEntity.EMPTY_JAR_SLOTS.get(finalI));
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return getEntity().getInventory().isItemValid(ApiaryBreederBlockEntity.EMPTY_JAR_SLOTS.get(finalI), stack);
                }
            });
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new OutputSlot(getEntity().getInventory(), 11 + (j + i * 9), 30+(j*18), 58 + (i*18)));
            }
        }


        //addPlayerInvSlots();

        this.setRebuild(rebuild);
    }

    @Override
    public int getContainerInputEnd() {
        return 1 + getNumberOfBreeders() * 5;
    }

    @Override
    public int getInventoryStart() {
        return 19 + getNumberOfBreeders() * 5;
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 30;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 106;
    }

    @Override
    protected void addMenuSlots() {
        setupSlots(false);
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
