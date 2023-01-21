package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.platform.common.item.ItemAction;
import com.teamresourceful.resourcefulbees.platform.common.item.ItemExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScraperItem extends Item implements ItemExtension {

    public ScraperItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAction action) {
        return ModConstants.SCRAPE_ACTION.equals(action);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(TranslationConstants.Items.SCRAPER_TOOLTIP.withStyle(ChatFormatting.GOLD));
        components.add(TranslationConstants.Items.SCRAPER_TOOLTIP_1.withStyle(ChatFormatting.GOLD));
    }
}
