package com.teamresourceful.resourcefulbees.common.entity.villager;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModVillagerProfessions;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.village.VillagerTradesEvent;

import java.util.List;

public class Beekeeper {

    private Beekeeper() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setupBeekeeper(VillagerTradesEvent event) {
        List<VillagerTrades.ITrade> level1 = event.getTrades().get(1);
        List<VillagerTrades.ITrade> level2 = event.getTrades().get(2);
        List<VillagerTrades.ITrade> level3 = event.getTrades().get(3);
        List<VillagerTrades.ITrade> level4 = event.getTrades().get(4);
        List<VillagerTrades.ITrade> level5 = event.getTrades().get(5);

        if (event.getType() == ModVillagerProfessions.BEEKEEPER.get()) {
            ItemStack queenBeeBanner = new ItemStack(Items.BLACK_BANNER);
            CompoundNBT compoundnbt = queenBeeBanner.getOrCreateTagElement("BlockEntityTag");
            ListNBT listnbt = new BannerPattern.Builder().addPattern(BannerPattern.RHOMBUS_MIDDLE, DyeColor.LIGHT_BLUE).addPattern(BannerPattern.STRIPE_DOWNRIGHT, DyeColor.YELLOW).addPattern(BannerPattern.STRIPE_DOWNLEFT, DyeColor.YELLOW).addPattern(BannerPattern.STRIPE_BOTTOM, DyeColor.YELLOW).addPattern(BannerPattern.TRIANGLE_TOP, DyeColor.YELLOW).addPattern(BannerPattern.CURLY_BORDER, DyeColor.YELLOW).toListTag();
            compoundnbt.put("Patterns", listnbt);
            queenBeeBanner.setHoverName(new TranslationTextComponent("block.resourcefulbees.queen_bee_banner").withStyle(TextFormatting.GOLD));
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
