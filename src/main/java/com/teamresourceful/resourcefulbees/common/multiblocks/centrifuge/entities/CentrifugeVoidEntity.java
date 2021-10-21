package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.inventory.AbstractFilterItemHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeVoidContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.NetworkHooks;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CentrifugeVoidEntity extends AbstractTieredCentrifugeEntity implements INamedContainerProvider {

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

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        return new CentrifugeVoidContainer(id, playerInventory, this);
    }

    @Override
    @Nonnull
    public ActionResultType onBlockActivated(@Nonnull PlayerEntity player, @Nonnull Hand handIn) {
        assert level != null;
        if (Boolean.TRUE.equals(level.getBlockState(worldPosition).getValue(MultiblockBlock.ASSEMBLED))) {
            if (!level.isClientSide) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this, this.getBlockPos());
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(player, handIn);
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.centrifuge_void");
    }

    //region NBT HANDLING
    @NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @Override
    protected void readNBT(@NotNull CompoundNBT tag) {
        filterInventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT tag = new CompoundNBT();
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
