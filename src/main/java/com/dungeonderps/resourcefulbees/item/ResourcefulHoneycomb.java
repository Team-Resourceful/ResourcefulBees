package com.dungeonderps.resourcefulbees.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.silentchaos512.utils.Color;

public class ResourcefulHoneycomb extends Item {

    public ResourcefulHoneycomb() {
        super(new Properties().group(ItemGroup.MATERIALS));
    }

    //TODO Consider using two separate textures for honeycomb
    // -One texture for outline
    // -Another texture to be colored.
    public static int getColor(ItemStack stack, int tintIndex){

        CompoundNBT honeycombNBT = stack.getChildTag("ResourcefulBees");
        return honeycombNBT != null && honeycombNBT.contains("color") ? Color.parseInt(honeycombNBT.getString("color")) : 0x000000;
    }


    //TODO - Add or Change to getDisplayName vs getTranslationKey.
    @Override
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT beeType = stack.getChildTag("ResourcefulBees");
        String name;
        if ((beeType != null && beeType.contains("beeType"))) {
            name = beeType.getString("beeType") + " Honeycomb";
        } else {
            name = "Resourceful Honeycomb";
        }
        return name;
    }
}
