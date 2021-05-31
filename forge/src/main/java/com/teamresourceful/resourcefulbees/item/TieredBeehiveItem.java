package com.teamresourceful.resourcefulbees.item;

import com.teamresourceful.resourcefulbees.lib.constants.NBTConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class TieredBeehiveItem extends BlockItem {

    public TieredBeehiveItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return !stack.hasTag() || stack.getTag() == null
                ? super.getName(stack)
                : new TextComponent("Tier " + stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).getInt(NBTConstants.NBT_TIER) + " " + super.getName(stack).getString());
    }
}
