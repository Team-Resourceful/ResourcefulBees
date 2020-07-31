package com.dungeonderps.resourcefulbees.tileentity;

import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public class HoneycombTileEntity extends TileEntity {

    public String beeType;
    public String blockColor;

    public HoneycombTileEntity() {
        super(RegistryHandler.HONEYCOMB_BLOCK_ENTITY.get());
    }

    @Nonnull
    public TileEntityType<?> getType() {
        return RegistryHandler.HONEYCOMB_BLOCK_ENTITY.get();
    }

    public int getColor() {
        return (blockColor != null && !blockColor.isEmpty()) ? Color.parseInt(blockColor) : BeeConstants.DEFAULT_ITEM_COLOR;
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT compound) {
        this.loadFromNBT(compound);
        super.read(state, compound);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        super.write(compound);
        return this.saveToNBT(compound);
    }

    public void loadFromNBT(@Nonnull CompoundNBT compound) {
        blockColor = compound.getString(BeeConstants.NBT_COLOR);
        beeType = compound.getString(BeeConstants.NBT_BEE_TYPE);
    }

    CompoundNBT saveToNBT(CompoundNBT compound) {
        if (this.blockColor != null)
            if (!this.blockColor.equals(String.valueOf(BeeConstants.DEFAULT_ITEM_COLOR))) {
                compound.putString(BeeConstants.NBT_COLOR, this.blockColor);
            }

        if (this.beeType != null) {
            compound.putString(BeeConstants.NBT_BEE_TYPE, this.beeType);
        }
        return compound;
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
        this.read(state, tag);
    }
}
