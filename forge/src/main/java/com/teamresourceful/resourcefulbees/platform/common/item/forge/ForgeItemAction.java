package com.teamresourceful.resourcefulbees.platform.common.item.forge;

import com.teamresourceful.resourcefulbees.platform.common.item.ItemAction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.versions.forge.ForgeVersion;

public class ForgeItemAction implements ItemAction {

    private final String id;
    private final TagKey<Item> tag;
    private final ToolAction action;

    public ForgeItemAction(String id) {
        this.id = id;
        this.tag = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "actions/" + id));
        this.action = ToolAction.get(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TagKey<Item> getTag() {
        return tag;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.canPerformAction(action)) {
            return true;
        }
        return ItemAction.super.test(stack);
    }

    @Override
    public boolean test(ItemStack stack, BlockState state, BlockPos pos, BlockGetter level) {
        if (stack.canPerformAction(action)) {
            return true;
        }
        return ItemAction.super.test(stack, state, pos, level);
    }
}
