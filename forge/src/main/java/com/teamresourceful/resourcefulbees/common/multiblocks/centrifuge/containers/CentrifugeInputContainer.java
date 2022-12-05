package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeInput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.ProcessContainerData;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CentrifugeInputContainer extends CentrifugeContainer<CentrifugeInputEntity> {

    private final ProcessContainerData processData;

    public CentrifugeInputContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, getTileFromBuf(inv.player.level, buffer, CentrifugeInputEntity.class), new CentrifugeState().deserializeBytes(buffer), dummyEnergyStorageData());
    }

    public CentrifugeInputContainer(int id, Inventory inv, CentrifugeInputEntity entity, CentrifugeState state, CentrifugeEnergyStorage energyStorage) {
        super(ModMenus.CENTRIFUGE_INPUT_CONTAINER.get(), id, inv, entity, state, energyStorage);
        this.processData = entity.getProcessData();
        this.addDataSlots(processData);
    }

    public ProcessContainerData getProcessData() {
        return processData;
    }

    protected void addCentrifugeSlots() {
        if (entity != null) {
            this.addSlot(new FilterSlot(entity.getFilterInventory(), CentrifugeInputEntity.RECIPE_SLOT, 126, 64) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return CentrifugeUtils.getFilterRecipe(level, stack).isPresent();
                }

                @Override
                public boolean isActive() {
                    return displaySlots;
                }
            });

            for (int r = 0; r < tier.getContainerRows(); r++) {
                for (int c = 0; c < tier.getContainerColumns(); c++) {
                    this.addSlot(new SlotItemHandler(entity.getInventoryHandler(), c+r*tier.getContainerColumns(), 161+c*17, 46+r*17) {
                        @Override
                        public boolean isActive() {
                            return displaySlots;
                        }
                    });
                }
            }
        }
    }

    @Override
    public void clicked(int pSlotId, int pDragType, @NotNull ClickType pClickType, @NotNull Player pPlayer) {
        if (pSlotId == CentrifugeInputEntity.RECIPE_SLOT && (pClickType.equals(ClickType.PICKUP) || pClickType.equals(ClickType.PICKUP_ALL) || pClickType.equals(ClickType.SWAP))) {
            Slot slot = this.getSlot(0);
            ItemStack stack = getCarried();
            if (stack.getCount() > 0 && slot.mayPlace(stack)) {
                slot.set(stack);
            } else if (slot.getItem().getCount() > 0) {
                slot.set(ItemStack.EMPTY);
            }
            if (entity != null) entity.updateRecipe();
        } else {
            super.clicked(pSlotId, pDragType, pClickType, pPlayer);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        assert entity != null;
        return ContainerLevelAccess.create(level, entity.getBlockPos()).evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() instanceof CentrifugeInput && player.distanceToSqr(pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D) <= 64.0D, true);
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
    public int startIndex() {
        return 1;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();

            if (index < getInventoryStart()) {
                if (!this.moveItemStackTo(slotItem, getInventoryStart(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 1, getContainerInputEnd(), false)) {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }
}
