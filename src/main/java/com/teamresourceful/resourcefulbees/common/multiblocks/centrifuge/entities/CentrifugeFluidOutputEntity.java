package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeFluidOutputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeFluidOutputEntity extends AbstractTieredCentrifugeEntity implements ICentrifugeOutput<FluidOutput> {

    private final FluidTank fluidTank;
    private final LazyOptional<IFluidHandler> fluidOptional;
    private boolean voidExcess = true;

    public CentrifugeFluidOutputEntity(RegistryObject<TileEntityType<CentrifugeFluidOutputEntity>> tileType, CentrifugeTier tier) {
        super(tileType.get(), tier);
        this.fluidTank = new FluidTank(this.tier.getTankCapacity());
        this.fluidOptional = LazyOptional.of(() -> fluidTank);
    }

    public void setVoidExcess(boolean voidExcess) {
        this.voidExcess = voidExcess;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return fluidOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.fluidOptional.invalidate();
        super.invalidateCaps();
    }

    public boolean depositResult(CentrifugeRecipe.Output<FluidOutput> output) {
        FluidStack result = output.getPool().next().getFluidStack();
        if (result.isEmpty() || controller.dumpsContainFluid(result)) return true;
        boolean canDeposit = (voidExcess || simulateDeposit(result)) && result.isFluidEqual(fluidTank.getFluid());

        if (canDeposit) {
            fluidTank.fill(result, IFluidHandler.FluidAction.EXECUTE);
        }
        return canDeposit;
    }

    private boolean simulateDeposit(FluidStack result) {
        return result.getAmount() + fluidTank.getFluidAmount() > fluidTank.getCapacity();
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.centrifuge_fluid_output");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity player) {
        return new CentrifugeFluidOutputContainer(id, playerInventory, this);
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
        fluidTank.readFromNBT(tag);
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT tag = new CompoundNBT();
        fluidTank.writeToNBT(tag);
        return tag;
    }
    //endregion
}
