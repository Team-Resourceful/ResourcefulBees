package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HoneycombBlockItem extends BlockItem {

    public HoneycombBlockItem() {
        super(RegistryHandler.HONEYCOMB_BLOCK.get(), new Item.Properties());
    }

    @Nonnull
    @Override
    public ActionResultType tryPlace(BlockItemUseContext context) {
        CompoundNBT combBlockData = context.getItem().getOrCreateChildTag(BeeConstants.NBT_ROOT);
        if (combBlockData.getString(BeeConstants.NBT_BEE_TYPE).equals("") || combBlockData.getString(BeeConstants.NBT_COLOR).equals("")){
            return ActionResultType.FAIL;
        }
        else
            return super.tryPlace(context);
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT beeType = stack.getChildTag(BeeConstants.NBT_ROOT);
        String name;
        if ((beeType != null && beeType.contains(BeeConstants.NBT_BEE_TYPE))) {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + beeType.getString(BeeConstants.NBT_BEE_TYPE) + "_honeycomb_block";
        } else {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + "resourceful_honeycomb_block";
        }
        return name;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (!stack.hasTag()) {
            ITextComponent craftingTip = new StringTextComponent("Unless otherwise specified,").applyTextStyle(TextFormatting.AQUA);
            tooltip.add(craftingTip);
            craftingTip = new StringTextComponent("*ANY* resourceful honeycomb block variant can be used for crafting recipes").applyTextStyle(TextFormatting.AQUA);
            tooltip.add(craftingTip);
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
