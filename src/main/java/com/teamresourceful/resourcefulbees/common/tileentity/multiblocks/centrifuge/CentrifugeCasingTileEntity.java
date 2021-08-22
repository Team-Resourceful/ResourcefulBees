package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public CompoundNBT save(@NotNull CompoundNBT tag) {
        if (isLinked())
            tag.put(NBTConstants.NBT_CONTROLLER_POS, NBTUtil.writeBlockPos(controllerPos));
        return super.save(tag);
    }

    @Override
    public void load(@NotNull BlockState state, CompoundNBT tag) {
        if (tag.contains(NBTConstants.NBT_CONTROLLER_POS))
            controllerPos = NBTUtil.readBlockPos(tag.getCompound(NBTConstants.NBT_CONTROLLER_POS));
        super.load(state, tag);
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }


}