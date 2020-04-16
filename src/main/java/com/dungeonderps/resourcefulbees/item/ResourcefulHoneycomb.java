package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.utils.Color;

public class ResourcefulHoneycomb extends Item {

    public ResourcefulHoneycomb() {
        super(new Properties().group(ItemGroup.MATERIALS));
    }

    public static int getColor(ItemStack stack, int tintIndex){

        CompoundNBT honeycombNBT = stack.getChildTag("ResourcefulBees");
        return honeycombNBT != null && honeycombNBT.contains("color") ? Color.parseInt(honeycombNBT.getString("color")) : 0x000000;
    }

    //TODO Implement Custom Display Name based on NBT data supplied.
    // - look at onCreated method to read NBT data and set an instance variable to "Bee Type"
    // - Use that to set the ITextComponent properly
    /*
    protected ITextComponent func_225513_by_() {
        return new TranslationTextComponent("entity" + '.' + ResourcefulBees.MOD_ID + '.' + this.Bee_Type);
    }
     */


}
