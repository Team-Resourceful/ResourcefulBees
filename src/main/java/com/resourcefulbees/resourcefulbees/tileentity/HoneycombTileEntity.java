package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.config.BeeRegistry;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.utils.Color;
import com.resourcefulbees.resourcefulbees.utils.RainbowColor;
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
        if (BeeRegistry.getInfo(beeType).isRainbowBee()) {
            return RainbowColor.getRGB();
        }
        return (blockColor != null && !blockColor.isEmpty() && !blockColor.equals(BeeConstants.STRING_DEFAULT_ITEM_COLOR)) ? Color.parseInt(blockColor) : BeeConstants.DEFAULT_ITEM_COLOR;
    }

    @Override
    public void deserializeNBT(@Nonnull BlockState state, @Nonnull CompoundNBT compound) {
        this.loadFromNBT(compound);
        super.deserializeNBT(state, compound);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        super.write(compound);
        return this.saveToNBT(compound);
    }

    public void loadFromNBT(@Nonnull CompoundNBT compound) {
        blockColor = compound.getString(NBTConstants.NBT_COLOR);
        beeType = compound.getString(NBTConstants.NBT_BEE_TYPE);
    }

    CompoundNBT saveToNBT(CompoundNBT compound) {
        if (this.blockColor != null)
            if (!this.blockColor.equals(String.valueOf(BeeConstants.DEFAULT_ITEM_COLOR))) {
                compound.putString(NBTConstants.NBT_COLOR, this.blockColor);
            }

        if (this.beeType != null) {
            compound.putString(NBTConstants.NBT_BEE_TYPE, this.beeType);
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
        this.deserializeNBT(state, tag);
    }
}
