package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class HoneycombBlockItem extends BlockItem {

    public HoneycombBlockItem() {
        super(RegistryHandler.HONEYCOMBBLOCK.get(), new Properties().group(ItemGroup.DECORATIONS));
    }

    public static int getColor(ItemStack stack, int tintIndex){

        CompoundNBT honeycombNBT = stack.getChildTag("ResourcefulBees");
        return honeycombNBT != null && honeycombNBT.contains("Color") ? Color.parseInt(honeycombNBT.getString("Color")) : 0x000000;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT beeType = stack.getChildTag("ResourcefulBees");
        String name;
        if ((beeType != null && beeType.contains("BeeType"))) {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + beeType.getString("BeeType").toLowerCase() + "_honeycomb_block";
        } else {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + "resourceful_honeycomb_block";
        }
        return name;
    }
}
