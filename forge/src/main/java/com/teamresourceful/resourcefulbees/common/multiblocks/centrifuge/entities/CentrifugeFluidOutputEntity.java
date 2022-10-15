package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.CentrifugeController;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeFluidOutputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.FluidOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeFluidOutputEntity extends AbstractCentrifugeOutputEntity<FluidOutput, FluidStack> {

    private final FluidTank fluidTank;
    private final LazyOptional<IFluidHandler> fluidOptional;
    private boolean voidExcess = true;

    public CentrifugeFluidOutputEntity(RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> tileType, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileType.get(), tier, pos, state);
        this.fluidTank = new FluidTank(this.tier.getTankCapacity()) {
            @Override
            protected void onContentsChanged() {
                sendToPlayersTrackingChunk();
                setChanged();
            }
        };
        this.fluidOptional = LazyOptional.of(() -> fluidTank);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public void setVoidExcess(boolean voidExcess) {
        //TODO this falls under the same issue as changing the processing stage in the in the input block.
        // ideally this would not send a full packet of data and would instead only send a packet containing
        // the changed data to players that are actively tracking the block, thus reducing the size, number, and frequency of packets being sent.
        // see read/write nbt regarding amount of data being sent
        if (level == null) return;
        this.voidExcess = voidExcess;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public boolean voidsExcess() {
        return voidExcess;
    }

    @Override
    public void purgeContents() {
        fluidTank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> capability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(ForgeCapabilities.FLUID_HANDLER) ? fluidOptional.cast() : super.capability(cap, side);
    }

    public boolean depositResult(FluidOutput result, int processQuantity) {
        FluidStack fluidStack = result.multiply(processQuantity);
        CentrifugeController controller = nullableController();
        if (fluidStack.isEmpty() || controller != null && controller.dumpsContainFluid(fluidStack)) return true;
        if ((voidExcess || simulateDeposit(fluidStack))) {
            fluidTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    private boolean simulateDeposit(FluidStack result) {
        return result.isFluidEqual(fluidTank.getFluid()) && result.getAmount() + fluidTank.getFluidAmount() > fluidTank.getCapacity();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("gui.centrifuge.output.fluid." + tier.getName());
    }



    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new CentrifugeFluidOutputContainer(id, playerInventory, this, centrifugeState);
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
        CompoundTag tag = super.getSyncData();
        fluidTank.writeToNBT(tag);
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        fluidTank.readFromNBT(tag);
    }

    //endregion
}
