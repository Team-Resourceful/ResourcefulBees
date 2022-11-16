package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScraperItem extends Item implements IShiftingToolTip {

    private static final ToolAction SCRAPE = ToolAction.get("scrape_hive");

    public ScraperItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return SCRAPE.equals(toolAction);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        setupTooltip(stack, level, components, flag);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getShiftingDisplay() {
        return TranslationConstants.Items.FOR_MORE_INFO;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendShiftTooltip(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(TranslationConstants.Items.SCRAPER_TOOLTIP.withStyle(ChatFormatting.GOLD));
        components.add(TranslationConstants.Items.SCRAPER_TOOLTIP_1.withStyle(ChatFormatting.GOLD));
    }

    public static boolean isScraper(ItemStack stack) {
        return stack.canPerformAction(SCRAPE);
    }
}
