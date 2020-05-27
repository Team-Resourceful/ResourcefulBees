package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;

import javax.annotation.Nonnull;

public class HoneycombBlockItem extends BlockItem {

    public HoneycombBlockItem() {
        super(RegistryHandler.HONEYCOMB_BLOCK.get(), new Item.Properties());
    }

    @Nonnull
    @Override
    public ActionResultType tryPlace(BlockItemUseContext context) {
        CompoundNBT combBlockData = context.getItem().getOrCreateChildTag(BeeConst.NBT_ROOT);
        if (combBlockData.getString(BeeConst.NBT_BEE_TYPE).equals("") || combBlockData.getString(BeeConst.NBT_COLOR).equals("")){
            return ActionResultType.FAIL;
        }
        else
            return super.tryPlace(context);
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT beeType = stack.getChildTag(BeeConst.NBT_ROOT);
        String name;
        if ((beeType != null && beeType.contains(BeeConst.NBT_BEE_TYPE))) {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + beeType.getString(BeeConst.NBT_BEE_TYPE) + "_honeycomb_block";
        } else {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + "resourceful_honeycomb_block";
        }
        return name;
    }
}
