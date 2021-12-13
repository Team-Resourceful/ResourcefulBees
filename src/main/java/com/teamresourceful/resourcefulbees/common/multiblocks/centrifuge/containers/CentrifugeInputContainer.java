package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeInput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CentrifugeInputContainer extends CentrifugeContainer<CentrifugeInputEntity> {

    public CentrifugeInputContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, getTileFromBuf(buffer, CentrifugeInputEntity.class));
    }

    public CentrifugeInputContainer(int id, Inventory inv, CentrifugeInputEntity entity) {
        super(ModContainers.CENTRIFUGE_INPUT_CONTAINER.get(), id, inv, entity);
    }

    protected void setupSlots() {
        if (entity != null) {
            this.addSlot(new FilterSlot(entity.getFilterInventory(), CentrifugeInputEntity.RECIPE_SLOT, 126, 64) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return CentrifugeUtils.getRecipe(level, stack).isPresent();
                }
            });

            for (int r = 0; r < CentrifugeUtils.getRows(tier); r++) {
                for (int c = 0; c < CentrifugeUtils.getColumns(tier); c++) {
                    this.addSlot(new SlotItemHandler(entity.getInventoryHandler(), c + r * 4, 162 + c * 17, 46 + r * 17));
                }
            }
        }

        addPlayerInvSlots();
    }

    @Override
    public void clicked(int pSlotId, int pDragType, @NotNull ClickType pClickType, @NotNull Player pPlayer) {
        if (pSlotId == CentrifugeInputEntity.RECIPE_SLOT) {
            switch (pClickType) {
                case PICKUP:
                case PICKUP_ALL:
                case SWAP: {
                    FilterSlot slot = (FilterSlot) this.getSlot(0);
                    ItemStack stack = getCarried(); //TODO MAKE SURE THIS IS CORRECT because inventory.getCarried() does not exist
                    if (stack.getCount() > 0 && slot.mayPlace(stack)) {
                        ItemStack copy = stack.copy();
                        copy.setCount(1);
                        slot.set(copy);
                    } else if (slot.getItem().getCount() > 0) {
                        slot.set(ItemStack.EMPTY);
                    }
                    if (entity != null) entity.updateRecipe();
                    // TODO return slot.getItem().copy(); clicked no longer returns itemStack check what needs to be changed!
                }
                default:
                    //TODO READ ABOVE return ItemStack.EMPTY;
            }
        }
        //TODO READ ABOVE return super.clicked(pSlotId, pDragType, pClickType, pPlayer);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        assert entity != null;
        return ContainerLevelAccess.create(level, entity.getBlockPos()).evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() instanceof CentrifugeInput && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
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
    public int getContainerInputStart() {
        return 1;
    }
}
