package com.dungeonderps.resourcefulbees.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.silentchaos512.utils.Lazy;
import net.silentchaos512.utils.Color;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public class ResourcefulHoneycomb extends Item {

    public static final Lazy<ResourcefulHoneycomb> INSTANCE = Lazy.of(ResourcefulHoneycomb::new);

    private String combColor = "#FF00FF";


    public ResourcefulHoneycomb() {
        super(new Properties().group(ItemGroup.MATERIALS));
    }

    public static int getColor(ItemStack stack, int tintIndex){

        CompoundNBT honeycombNBT = stack.getChildTag("ResourcefulBees");
        return honeycombNBT != null && honeycombNBT.contains("color") ? Color.parseInt(honeycombNBT.getString("color")) : 0x000000;
    }
}
