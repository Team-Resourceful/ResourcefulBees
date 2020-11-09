package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge;

import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CentrifugeCasingTileEntity extends TileEntity {
    private BlockPos controllerPos;

    public CentrifugeCasingTileEntity() { super(ModTileEntityTypes.CENTRIFUGE_CASING_ENTITY.get()); }

    public void setControllerPos(@Nullable BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    public boolean isLinked() {
        return controllerPos != null;
    }

    @Override
    public void remove() {
        CentrifugeControllerTileEntity controller = getController();
        if (controller != null) {
            controller.invalidateStructure();
        }
        super.remove();
    }

    public CentrifugeControllerTileEntity getController() {
        if (isLinked() && this.world != null) {
            TileEntity tileEntity = this.world.getTileEntity(controllerPos);
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
        if (isLinked() && this.world != null) {
            CentrifugeControllerTileEntity controller = getController();
            if (controller != null && controller.isValidStructure()) {
/*                if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return controller.lazyOptional.cast();
                if (cap.equals(CapabilityEnergy.ENERGY)) return controller.energyOptional.cast();*/
                /*if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) */return controller.getCapability(cap, side);
            }
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT tag) {
        if (isLinked())
            tag.put("controllerPos", NBTUtil.writeBlockPos(controllerPos));
        return super.write(tag);
    }

    @Override
    public void fromTag(@Nonnull BlockState state, CompoundNBT tag) {
        if (tag.contains("controllerPos"))
            controllerPos = NBTUtil.readBlockPos(tag.getCompound("controllerPos"));
        super.fromTag(state, tag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, CompoundNBT tag) {
        this.fromTag(state, tag);
    }


}
