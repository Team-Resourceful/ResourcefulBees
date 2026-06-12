package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.platform.common.item.ItemAction;
import com.teamresourceful.resourcefulbees.platform.common.item.ItemExtension;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ScraperItem extends Item implements ItemExtension {

    public ScraperItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAction action) {
        return ModConstants.SCRAPE_ACTION.equals(action);
    }

/*    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(ItemTranslations.SCRAPER_TOOLTIP.withStyle(ChatFormatting.GOLD));
        components.add(ItemTranslations.SCRAPER_TOOLTIP_1.withStyle(ChatFormatting.GOLD));
    }*/
}
