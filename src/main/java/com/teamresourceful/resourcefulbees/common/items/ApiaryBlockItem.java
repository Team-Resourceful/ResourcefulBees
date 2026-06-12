package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.blocks.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeehiveTranslations;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class ApiaryBlockItem extends BlockItem {

    private final ApiaryTier tier;

    public ApiaryBlockItem(ApiaryBlock block, Properties properties) {
        super(block, properties);
        tier = block.getTier();
    }

    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable(BeehiveTranslations.MAX_BEES, tier.maxBees()).withStyle(ChatFormatting.GOLD));
        builder.accept(Component.translatable(BeehiveTranslations.HIVE_TIME, tier.getTimeModificationAsPercent()).withStyle(ChatFormatting.GOLD));
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
