package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.inventory.AbstractFilterItemHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeVoidContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeVoidEntity extends AbstractGUICentrifugeEntity {

    private final CentrifugeVoidEntity.FilterInventory filterInventory;

    public CentrifugeVoidEntity(RegistryObject<TileEntityType<CentrifugeVoidEntity>> entityType, CentrifugeTier tier) {
        super(entityType.get(), tier);
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
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("gui.centrifuge.void." + tier.getName());
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        controller.updateCentrifugeState(centrifugeState);
        return new CentrifugeVoidContainer(id, playerInventory, this);
    }

    //region NBT HANDLING
    //TODO DON'T SYNC INVENTORY THIS WAY!
    @Override
    protected void readNBT(@NotNull CompoundNBT tag) {
        filterInventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT tag = super.writeNBT();
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
                if (Container.consideredTheSameItem(stack, slotStack)) return true;
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
