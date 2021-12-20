package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBreederBlockEntity;
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

    public ApiaryBreederContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, getTileFromBuf(inv.player.level, buffer, ApiaryBreederBlockEntity.class), new SimpleContainerData(5));
    }

    public ApiaryBreederContainer(int id, Inventory inv, ApiaryBreederBlockEntity entity, ContainerData times) {
        super(ModMenus.APIARY_BREEDER_CONTAINER.get(), id, inv, entity);
        this.times = times;
        this.addDataSlots(times);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }


    public void setupSlots(boolean rebuild) {
        this.slots.clear();
        numberOfBreeders = getEntity().getNumberOfBreeders();

        for (int i=0; i<4; i++){
            int finalI = i;
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getTileStackHandler(), ApiaryBreederBlockEntity.getUpgradeSlots()[finalI], 6, 22 + (finalI *18)) {
                @Override
                public int getMaxStackSize() {
                    return getEntity().getTileStackHandler().getSlotLimit(ApiaryBreederBlockEntity.getUpgradeSlots()[finalI]);
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return getEntity().getTileStackHandler().isItemValid(ApiaryBreederBlockEntity.getUpgradeSlots()[finalI], stack);
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
                    slotsAreEmpty = getEntity().getTileStackHandler().getStackInSlot(ApiaryBreederBlockEntity.getParent1Slots()[index]).isEmpty();
                    slotsAreEmpty = slotsAreEmpty && getEntity().getTileStackHandler().getStackInSlot(ApiaryBreederBlockEntity.getParent2Slots()[index]).isEmpty();
                    slotsAreEmpty = slotsAreEmpty && getEntity().getTileStackHandler().getStackInSlot(ApiaryBreederBlockEntity.getFeed1Slots()[index]).isEmpty();
                    slotsAreEmpty = slotsAreEmpty && getEntity().getTileStackHandler().getStackInSlot(ApiaryBreederBlockEntity.getFeed2Slots()[index]).isEmpty();
                    slotsAreEmpty = slotsAreEmpty && getEntity().getTileStackHandler().getStackInSlot(ApiaryBreederBlockEntity.getEmptyJarSlots()[index]).isEmpty();
                    return slotsAreEmpty;
                }
            });
        }

        for (int i = 0; i< getNumberOfBreeders(); i++) {
            int finalI = i;
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getTileStackHandler(), ApiaryBreederBlockEntity.getParent1Slots()[finalI], 33, 18 +(finalI *20)){

                @Override
                public int getMaxStackSize() {
                    return getEntity().getTileStackHandler().getSlotLimit(ApiaryBreederBlockEntity.getParent1Slots()[finalI]);
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getEntity().getTileStackHandler().isItemValid(ApiaryBreederBlockEntity.getParent1Slots()[finalI], stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getTileStackHandler(), ApiaryBreederBlockEntity.getFeed1Slots()[i], 69, 18 +(i*20)){

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getEntity().getTileStackHandler().isItemValid(ApiaryBreederBlockEntity.getFeed1Slots()[finalI], stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getTileStackHandler(), ApiaryBreederBlockEntity.getParent2Slots()[i], 105, 18 +(i*20)){

                @Override
                public int getMaxStackSize() {
                    return getEntity().getTileStackHandler().getSlotLimit(ApiaryBreederBlockEntity.getParent2Slots()[finalI]);
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getEntity().getTileStackHandler().isItemValid(ApiaryBreederBlockEntity.getParent2Slots()[finalI], stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getTileStackHandler(), ApiaryBreederBlockEntity.getFeed2Slots()[i], 141, 18 +(i*20)){

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return getEntity().getTileStackHandler().isItemValid(ApiaryBreederBlockEntity.getFeed2Slots()[finalI], stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(getEntity().getTileStackHandler(), ApiaryBreederBlockEntity.getEmptyJarSlots()[i], 177, 18 +(i*20)){

                @Override
                public int getMaxStackSize() {
                    return getEntity().getTileStackHandler().getSlotLimit(ApiaryBreederBlockEntity.getEmptyJarSlots()[finalI]);
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return getEntity().getTileStackHandler().isItemValid(ApiaryBreederBlockEntity.getEmptyJarSlots()[finalI], stack);
                }
            });
        }

        if (rebuild) addPlayerInvSlots();

        this.setRebuild(rebuild);
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

    @Override
    public int getPlayerInvXOffset() {
        return 28 + (getNumberOfBreeders() * 20);
    }

    @Override
    public int getPlayerInvYOffset() {
        return 86 + getNumberOfBreeders() * 20;
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
