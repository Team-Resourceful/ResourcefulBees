package com.teamresourceful.resourcefulbees.tileentity;

import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyPipeTileEntity extends BlockEntity implements TickableBlockEntity {

    private Direction inputSide = null;

    final FluidTank tank = new FluidTank(Config.MAX_PIPE_FLOW.get(), BeeInfoUtils.getHoneyPredicate());
    private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(this::getFluidTank);

    public HoneyPipeTileEntity() {
        super(ModBlockEntityTypes.HONEY_PIPE_TILE_ENTITY.get());
    }

    public Direction getInputSide() {
        return inputSide;
    }

    public void setInputSide(Direction inputSide) {
        this.inputSide = inputSide;
    }

    @Override
    public void tick() {
        if (inputSide == null) {
        }
    }

    public @NotNull FluidTank getFluidTank() {
        return tank;
    }

    // read from tag
    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundTag tag) {
        super.load(state, tag);
        readNBT(tag);
    }

    // write to tag
    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag tag) {
        super.save(tag);
        writeNBT(tag);
        return tag;
    }

    @Override
    protected void invalidateCaps() {
        this.fluidOptional.invalidate();
        super.invalidateCaps();
    }

    public CompoundTag writeNBT(CompoundTag tag) {
        if (getFluidTank().isEmpty()) return tag;
        tag.put("fluid", getFluidTank().writeToNBT(new CompoundTag()));
        return tag;
    }

    public void readNBT(CompoundTag tag) {
        getFluidTank().readFromNBT(tag.getCompound("fluid"));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, 0, writeNBT(new CompoundTag()));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        readNBT(nbt);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        return writeNBT(nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundTag tag) {
        super.handleUpdateTag(state, tag);
        readNBT(tag);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (side == null || side == inputSide)) {
            return fluidOptional.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }
}
