package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge;

import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (isLinked() && this.level != null) {
            CentrifugeControllerTileEntity controller = getController();
            if (controller != null && controller.isValidStructure()) {
                return controller.getCapability(cap, side);
            }
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag tag) {
        if (isLinked())
            tag.put(NBTConstants.NBT_CONTROLLER_POS, NbtUtils.writeBlockPos(controllerPos));
        return super.save(tag);
    }

    @Override
    public void load(@Nonnull BlockState state, CompoundTag tag) {
        if (tag.contains(NBTConstants.NBT_CONTROLLER_POS))
            controllerPos = NbtUtils.readBlockPos(tag.getCompound(NBTConstants.NBT_CONTROLLER_POS));
        super.load(state, tag);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbtTagCompound = new CompoundTag();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, CompoundTag tag) {
        this.load(state, tag);
    }


}
