package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public class HoneycombBlockItem extends BlockItem {

    public HoneycombBlockItem() {
        super(RegistryHandler.HONEYCOMBBLOCK.get(), new Properties().group(ItemGroup.DECORATIONS));
    }

    public static int getColor(ItemStack stack, int tintIndex){

        LOGGER.info("Getting Comb Color");
        CompoundNBT honeycombNBT = stack.getChildTag("ResourcefulBees");
        return honeycombNBT != null && honeycombNBT.contains("Color") ? Color.parseInt(honeycombNBT.getString("Color")) : 0x000000;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        //LOGGER.info("Setting Translation Key");
        CompoundNBT beeType = stack.getChildTag("ResourcefulBees");
        String name;
        if ((beeType != null && beeType.contains("BeeType"))) {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + beeType.getString("BeeType").toLowerCase() + "_honeycomb_block";
        } else {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + "resourceful_honeycomb_block";
        }
        return name;
    }
}
