package com.dungeonderps.resourcefulbees.tileentity;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.block.HoneycombBlock;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class HoneycombBlockEntity extends TileEntity {

    public String beeType = "Default";
    public String combColor = "0xffffff";

    public HoneycombBlockEntity() {
        super(RegistryHandler.HONEYCOMB_BLOCK_ENTITY.get());
    }

    public TileEntityType<?> getType() {return RegistryHandler.HONEYCOMB_BLOCK_ENTITY.get(); }


    public int getColor(){
        return Color.parseInt(combColor);
    }


    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        if (compound.contains("ResourcefulBees")){
            CompoundNBT blockNBT = (CompoundNBT) compound.get("ResourcefulBees");
            beeType = blockNBT.getString("BeeType");
            combColor = blockNBT.getString("CombColor");

            HoneycombBlock combBlock = (HoneycombBlock)getBlockState().getBlock();
            combBlock.setBeeType(beeType);
            combBlock.setBlockColor(combColor);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);

        CompoundNBT blockNBT = new CompoundNBT();
        blockNBT.putString("BeeType", beeType);
        blockNBT.putString("CombColor", combColor);

        compound.put("ResourcefulBees", blockNBT);

        return compound;
    }


    public void setCombColor(String combColor) {
        this.combColor = combColor;
    }

    public void setBeeType(String beeType) {
        this.beeType = beeType;
    }
}
