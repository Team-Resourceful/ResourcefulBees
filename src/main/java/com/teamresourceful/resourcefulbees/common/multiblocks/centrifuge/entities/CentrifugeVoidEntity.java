package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.inventory.AbstractFilterItemHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeVoidContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeVoidEntity extends AbstractGUICentrifugeEntity {

    private final CentrifugeVoidEntity.FilterInventory filterInventory;

    public CentrifugeVoidEntity(RegistryObject<BlockEntityType<CentrifugeVoidEntity>> entityType, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(entityType.get(), tier, pos, state);
        this.filterInventory = new FilterInventory(tier.getSlots() * 2);
    }

    public FilterInventory getFilterInventory() {
        return filterInventory;
    }

    public boolean containsItem(ItemStack stack) {
        return filterInventory.containsItem(stack);
    }

    public boolean containsFluid(FluidStack stack) {
        return filterInventory.containsFluid(stack);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("gui.centrifuge.void." + tier.getName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new CentrifugeVoidContainer(id, playerInventory, this, centrifugeState);
    }

    //region NBT HANDLING
    @Override
    protected void readNBT(@NotNull CompoundTag tag) {
        filterInventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundTag writeNBT() {
        CompoundTag tag = super.writeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, filterInventory.serializeNBT());
        return tag;
    }
    //endregion


    private static class FilterInventory extends AbstractFilterItemHandler {
        protected FilterInventory(int numSlots) {
            super(numSlots);
        }

        public boolean containsItem(ItemStack stack) {
            for (ItemStack slotStack : stacks) {
                if (ItemStack.isSameItemSameTags(stack, slotStack)) return true;
            }
            return false;
        }

        public boolean containsFluid(FluidStack stack) {
            for (ItemStack slotStack : stacks) {
                if (stack.isFluidEqual(slotStack)) return true;
            }
            return false;
        }
    }
}
