package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ItemGroupResourcefulBees;
import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class HoneycombBlockItem extends BlockItem {

    public HoneycombBlockItem() {
        super(RegistryHandler.HONEYCOMB_BLOCK.get(), new Item.Properties());
    }


    @Override
    public String getTranslationKey(ItemStack stack) {
        //LOGGER.info("Setting Translation Key");
        CompoundNBT beeType = stack.getChildTag(BeeConst.NBT_ROOT);
        String name;
        if ((beeType != null && beeType.contains(BeeConst.NBT_BEE_TYPE))) {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + beeType.getString(BeeConst.NBT_BEE_TYPE).toLowerCase() + "_honeycomb_block";
        } else {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + "resourceful_honeycomb_block";
        }
        return name;
    }
}
