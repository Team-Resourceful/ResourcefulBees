package com.teamresourceful.resourcefulbees.item;

import com.teamresourceful.resourcefulbees.lib.constants.NBTConstants;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

public class TieredBeehiveItem extends BlockItem {

    public TieredBeehiveItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull ITextComponent getName(ItemStack stack) {
        return !stack.hasTag() || stack.getTag() == null
                ? super.getName(stack)
                : new StringTextComponent("Tier " + stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).getInt(NBTConstants.NBT_TIER) + " " + super.getName(stack).getString());
    }
}
