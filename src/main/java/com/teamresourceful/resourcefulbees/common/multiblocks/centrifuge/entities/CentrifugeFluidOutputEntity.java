package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeFluidOutputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeFluidOutputEntity extends AbstractGUICentrifugeEntity implements ICentrifugeOutput<FluidOutput> {

    private final FluidTank fluidTank;
    private final LazyOptional<IFluidHandler> fluidOptional;
    private boolean voidExcess = true;

    public CentrifugeFluidOutputEntity(RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> tileType, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileType.get(), tier, pos, state);
        this.fluidTank = new FluidTank(this.tier.getTankCapacity());
        this.fluidOptional = LazyOptional.of(() -> fluidTank);
    }

    public void setVoidExcess(boolean voidExcess) {
        this.voidExcess = voidExcess;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> capability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return fluidOptional.cast();
        return super.capability(cap, side);
    }

    public boolean depositResult(CentrifugeRecipe.Output<FluidOutput> output, int processQuantity) {
        FluidStack result = output.getPool().next().getFluidStack();
        if (result.isEmpty() || controller().dumpsContainFluid(result)) return true;
        result.setAmount(result.getAmount() * processQuantity);
        if ((voidExcess || simulateDeposit(result))) {
            fluidTank.fill(result, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    private boolean simulateDeposit(FluidStack result) {
        return result.isFluidEqual(fluidTank.getFluid()) && result.getAmount() + fluidTank.getFluidAmount() > fluidTank.getCapacity();
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("gui.centrifuge.output.fluid." + tier.getName());
    }



    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
        controller().updateCentrifugeState(centrifugeState);
        return new CentrifugeFluidOutputContainer(id, playerInventory, this);
    }

    //region NBT HANDLING
    @Override
    protected void readNBT(@NotNull CompoundTag tag) {
        fluidTank.readFromNBT(tag);
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundTag writeNBT() {
        CompoundTag tag = super.writeNBT();
        fluidTank.writeToNBT(tag);
        return tag;
    }
    //endregion
}
