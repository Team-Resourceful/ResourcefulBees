package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.neoforged.neoforge.common.ItemAbility;
import org.jspecify.annotations.NonNull;

public class ScraperItem extends Item {

    public ScraperItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canPerformAction(@NonNull ItemInstance stack, ItemAbility itemAbility) {
        return itemAbility.equals(ModConstants.SCRAPE_ACTION);
    }
}
