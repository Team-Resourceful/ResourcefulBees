package com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge;

import com.teamresourceful.resourcefulbees.lib.NBTConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeCasingTileEntity extends BlockEntity {
    private BlockPos controllerPos;

    public CentrifugeCasingTileEntity(BlockEntityType<?> tileEntityType) { super(tileEntityType); }

    public void setControllerPos(@Nullable BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    public boolean isLinked() {
        return controllerPos != null;
    }

    @Override
    public void setRemoved() {
        CentrifugeControllerTileEntity controller = getController();
        if (controller != null) {
            controller.invalidateStructure();
        }
        super.setRemoved();
    }

    public CentrifugeControllerTileEntity getController() {
        if (isLinked() && this.level != null) {
            BlockEntity tileEntity = this.level.getBlockEntity(controllerPos);
            if (tileEntity instanceof CentrifugeControllerTileEntity) {
                return (CentrifugeControllerTileEntity) tileEntity;
            } else {
                setControllerPos(null);
            }
        }
        return null;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (isLinked() && this.level != null) {
            CentrifugeControllerTileEntity controller = getController();
            if (controller != null && controller.isValidStructure()) {
                return controller.getCapability(cap, side);
            }
        }
        return super.getCapability(cap, side);
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag tag) {
        if (isLinked())
            tag.put(NBTConstants.NBT_CONTROLLER_POS, NbtUtils.writeBlockPos(controllerPos));
        return super.save(tag);
    }

    @Override
    public void load(@NotNull BlockState state, CompoundTag tag) {
        if (tag.contains(NBTConstants.NBT_CONTROLLER_POS))
            controllerPos = NbtUtils.readBlockPos(tag.getCompound(NBTConstants.NBT_CONTROLLER_POS));
        super.load(state, tag);
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundTag tag) {
        this.load(state, tag);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbtTagCompound = new CompoundTag();
        save(nbtTagCompound);
        return nbtTagCompound;
    }


}
