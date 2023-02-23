package com.teamresourceful.resourcefulbees.centrifuge.common.entities;

import com.teamresourceful.resourcefulbees.centrifuge.common.CentrifugeController;
import com.teamresourceful.resourcefulbees.centrifuge.common.containers.CentrifugeFluidOutputContainer;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.FluidOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static net.roguelogix.phosphophyllite.multiblock2.IAssemblyStateBlock.ASSEMBLED;

public class CentrifugeFluidOutputEntity extends AbstractCentrifugeOutputEntity<FluidOutput, FluidStack> {

    private final ExtractOnlyFluidTank fluidTank;
    private final LazyOptional<IFluidHandler> fluidOptional;

    public CentrifugeFluidOutputEntity(Supplier<BlockEntityType<CentrifugeFluidOutputEntity>> tileType, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileType.get(), tier, pos, state);
        this.fluidTank = new ExtractOnlyFluidTank(this.tier.getTankCapacity()) {
            @Override
            protected void onContentsChanged() {
                sendToPlayersTrackingChunk();
                setChanged();
            }
        };
        this.fluidOptional = LazyOptional.of(() -> fluidTank);
    }

    public ExtractOnlyFluidTank getFluidTank() {
        return fluidTank;
    }

    public void setVoidExcess(boolean voidExcess) {
        //TODO this falls under the same issue as changing the processing stage in the in the input block.
        // ideally this would not send a full packet of data and would instead only send a packet containing
        // the changed data to players that are actively tracking the block, thus reducing the size, number, and frequency of packets being sent.
        // see read/write nbt regarding amount of data being sent
        if (level == null) return;
        this.voidExcess = voidExcess;
        sendToPlayersTrackingChunk();
    }

    @Override
    public void purgeContents() {
        fluidTank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> capability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(ForgeCapabilities.FLUID_HANDLER)
                && this.getBlockState().hasProperty(ASSEMBLED)
                && Boolean.TRUE.equals(this.getBlockState().getValue(ASSEMBLED))
                ? fluidOptional.cast()
                : super.capability(cap, side);
    }

    public boolean depositResult(FluidOutput result, int processQuantity) {
        FluidStack fluidStack = result.multiply(processQuantity);
        CentrifugeController controller = nullableController();
        if (fluidStack.isEmpty() || controller != null && controller.filtersContainFluid(fluidStack)) return true;
        if (voidExcess || simulateDeposit(fluidStack)) {
            fluidTank.fillTank(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    private boolean simulateDeposit(FluidStack result) {
        return (fluidTank.isEmpty() || result.isFluidEqual(fluidTank.getFluid())) && result.getAmount() + fluidTank.getFluidAmount() < fluidTank.getCapacity();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("gui.centrifuge.output.fluid." + tier.getName());
    }



    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
        CentrifugeController controller = nullableController();
        if (controller == null) return null;
        return new CentrifugeFluidOutputContainer(id, playerInventory, this, centrifugeState, controller.getEnergyStorage());
    }

    //region NBT HANDLING
    @Override
    protected void readNBT(@NotNull CompoundTag tag) {
        fluidTank.readFromNBT(tag);
        voidExcess = tag.getBoolean("void_excess");
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundTag writeNBT() {
        CompoundTag tag = super.writeNBT();
        fluidTank.writeToNBT(tag);
        tag.putBoolean("void_excess", voidExcess);
        return tag;
    }

    @Override
    public CompoundTag getSyncData() {
        return writeNBT();
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        readNBT(tag);
    }

    //endregion

    public static class ExtractOnlyFluidTank extends FluidTank {

        public ExtractOnlyFluidTank(int capacity) {
            super(capacity);
        }

        @Override
        public final int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        /**For internal use only!*/
        @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
        private int fillTank(FluidStack resource, FluidAction action) {
            return super.fill(resource, action);
        }
    }
}
