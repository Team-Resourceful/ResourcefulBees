package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge;

import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CentrifugeCasingTileEntity extends TileEntity {
    private BlockPos controllerPos;

    public CentrifugeCasingTileEntity(TileEntityType<?> tileEntityType) { super(tileEntityType); }

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
            TileEntity tileEntity = this.level.getBlockEntity(controllerPos);
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
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        if (isLinked())
            tag.put(NBTConstants.NBT_CONTROLLER_POS, NBTUtil.writeBlockPos(controllerPos));
        return super.save(tag);
    }

    @Override
    public void load(@Nonnull BlockState state, CompoundNBT tag) {
        if (tag.contains(NBTConstants.NBT_CONTROLLER_POS))
            controllerPos = NBTUtil.readBlockPos(tag.getCompound(NBTConstants.NBT_CONTROLLER_POS));
        super.load(state, tag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }


}
