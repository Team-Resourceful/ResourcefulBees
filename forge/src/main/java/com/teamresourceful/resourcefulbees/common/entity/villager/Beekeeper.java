package com.teamresourceful.resourcefulbees.common.entity.villager;

import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModVillagerProfessions;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraftforge.event.village.VillagerTradesEvent;

import java.util.List;

public final class Beekeeper {

    private Beekeeper() {
        throw new UtilityClassError();
    }

    public static void setupBeekeeper(VillagerTradesEvent event) {
        //TODO UPDATE TRADES
        List<VillagerTrades.ItemListing> level1 = event.getTrades().get(1);
        List<VillagerTrades.ItemListing> level2 = event.getTrades().get(2);
        List<VillagerTrades.ItemListing> level3 = event.getTrades().get(3);
        List<VillagerTrades.ItemListing> level4 = event.getTrades().get(4);
        List<VillagerTrades.ItemListing> level5 = event.getTrades().get(5);

        if (event.getType() == ModVillagerProfessions.BEEKEEPER.get()) {
            ItemStack queenBeeBanner = new ItemStack(Items.BLACK_BANNER);
            CompoundTag compoundnbt = queenBeeBanner.getOrCreateTagElement("BlockEntityTag");
            ListTag listnbt = new BannerPattern.Builder().addPattern(BannerPatterns.RHOMBUS_MIDDLE, DyeColor.LIGHT_BLUE).addPattern(BannerPatterns.STRIPE_DOWNRIGHT, DyeColor.YELLOW).addPattern(BannerPatterns.STRIPE_DOWNLEFT, DyeColor.YELLOW).addPattern(BannerPatterns.STRIPE_BOTTOM, DyeColor.YELLOW).addPattern(BannerPatterns.TRIANGLE_TOP, DyeColor.YELLOW).addPattern(BannerPatterns.CURLY_BORDER, DyeColor.YELLOW).toListTag();
            compoundnbt.put("Patterns", listnbt);
            queenBeeBanner.setHoverName(TranslationConstants.Items.QUEEN_BEE_BANNER.withStyle(ChatFormatting.GOLD));
            queenBeeBanner.setCount(1);

            level1.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 3),
                    new ItemStack(ModItems.WAX_BLOCK_ITEM.get(), 1),
                    32, 4, 1));
            level2.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(Items.HONEYCOMB, 3),
                    10, 4, 1));
            level3.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(ModItems.WAX.get(), 6),
                    15, 4, 1));
            level4.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.HONEY_BOTTLE, 4),
                    new ItemStack(Items.EMERALD, 2),
                    10, 4, 0));
            level5.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 12),
                    new ItemStack(ModItems.SMOKER.get(), 1),
                    10, 4, 0));
            level5.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 64),
                    queenBeeBanner,
                    2, 4, 0));
        }
    }
}
