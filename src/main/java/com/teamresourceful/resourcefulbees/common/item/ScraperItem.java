package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScraperItem extends Item {

    public ScraperItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.add(TranslationConstants.Items.SCRAPER_TOOLTIP.withStyle(ChatFormatting.GOLD));
        tooltip.add(TranslationConstants.Items.SCRAPER_TOOLTIP_1.withStyle(ChatFormatting.GOLD));
        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ModConstants.SCRAPE_HIVE.equals(toolAction);
    }
}
