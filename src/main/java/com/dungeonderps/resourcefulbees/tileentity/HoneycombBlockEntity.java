package com.dungeonderps.resourcefulbees.tileentity;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.block.HoneycombBlock;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class HoneycombBlockEntity extends TileEntity {

    public String beeType;
    public String blockColor;

    public HoneycombBlockEntity() {
        super(RegistryHandler.HONEYCOMB_BLOCK_ENTITY.get());
    }

    public TileEntityType<?> getType() {
        return RegistryHandler.HONEYCOMB_BLOCK_ENTITY.get();
    }


    public int getColor() {
        return (blockColor != null && !blockColor.isEmpty()) ? Color.parseInt(blockColor) : BeeConst.DEFAULT_COLOR;
    }

    @Override
    public void read(CompoundNBT compound) {
        this.loadFromNBT(compound);
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        return this.saveToNBT(compound);
    }

    public void loadFromNBT(CompoundNBT compound) {
        blockColor = compound.getString(BeeConst.NBT_COLOR);
        beeType = compound.getString(BeeConst.NBT_BEE_TYPE);

        if (this.world != null && this.pos != null) {
            Block block = this.world.getBlockState(this.pos).getBlock();
            if (block instanceof HoneycombBlock) {
                HoneycombBlock honeycombBlock = (HoneycombBlock) block;
                honeycombBlock.setBlockColor(this.blockColor);
                honeycombBlock.setBeeType(this.beeType);
            }
        }
    }

    CompoundNBT saveToNBT(CompoundNBT compound) {
        if (!this.blockColor.equals(String.valueOf(BeeConst.DEFAULT_COLOR))) {
            compound.putString(BeeConst.NBT_COLOR, this.blockColor);
        }

        if (this.beeType != null) {
            compound.putString(BeeConst.NBT_BEE_TYPE, this.beeType);
        }
        return compound;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        this.read(tag);
    }

    @Override
    public void onLoad() {

    }
}
