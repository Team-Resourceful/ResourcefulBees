package com.teamresourceful.resourcefulbees.common.entity.villager;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.items.Tradeable;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ItemTranslations;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModVillagerProfessions;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraftforge.event.village.VillagerTradesEvent;

import java.util.Collection;
import java.util.List;

public final class Beekeeper {
/*
    arg1=input1
    arg2=input2
    arg3=result
    i=uses
    j=maxUses
    k=xpCost
    f=priceMultiplier
    l=demand
 */
    private Beekeeper() {
        throw new UtilityClassError();
    }

    public static void setupBeekeeper(VillagerTradesEvent event) {
        if (event.getType() != ModVillagerProfessions.BEEKEEPER.get()) return;
        createLevelOneTrades(event.getTrades().get(1));
        createLevelTwoTrades(event.getTrades().get(2));
        createLevelThreeTrades(event.getTrades().get(3));
        createLevelFourTrades(event.getTrades().get(4));
        createLevelFiveTrades(event.getTrades().get(5));
    }

    private static void addTrade(List<VillagerTrades.ItemListing> list, UniformInt flowerQty, Item costB, UniformInt costBQty, Item result, UniformInt resultQty, int maxUses, float priceMultiplier, int xp) {
        list.add((trader, random) -> new MerchantOffer(
                new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.GOLD_FLOWER_ITEM.get(), flowerQty.sample(random)),
                new ItemStack(costB, costBQty.sample(random)),
                new ItemStack(result, resultQty.sample(random)),
                0, maxUses, xp, priceMultiplier
        ));
    }

    private static void addTrade(List<VillagerTrades.ItemListing> list, UniformInt flowerQty, Item costB, UniformInt costBQty, Item result, UniformInt resultQty, int maxUses, int xp) {
        addTrade(list, flowerQty, costB, costBQty, result, resultQty, maxUses, .05f, xp);
    }

    private static void addTrade(List<VillagerTrades.ItemListing> list, UniformInt flowerQty, Item result, UniformInt resultQty, int maxUses, int xp) {
        addTrade(list, flowerQty, ItemStack.EMPTY.getItem(), UniformInt.of(0,0), result, resultQty, maxUses, xp);
    }

    private static void addTrade(List<VillagerTrades.ItemListing> list, UniformInt flowerQty, Item result, UniformInt resultQty, int maxUses) {
        addTrade(list, flowerQty, result, resultQty, maxUses, 3);
    }

    //region Level One Trades
    private static void createLevelOneTrades(List<VillagerTrades.ItemListing> list) {
        addTrade(list, UniformInt.of(1,2), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAX.get(), UniformInt.of(1,4), 12, 3);
        addTrade(list, UniformInt.of(2,4), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_JAR.get(), UniformInt.of(1,4), 8, 3);
    }
    //endregion

    //region Level Two Trades
    private static void createLevelTwoTrades(List<VillagerTrades.ItemListing> list) {
        addTrade(list, UniformInt.of(2,6), Items.IRON_INGOT, UniformInt.of(1,3), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SCRAPER.get(), UniformInt.of(1,1), 4, 3);
        addTrade(list, UniformInt.of(4,8), Items.IRON_INGOT, UniformInt.of(2,6), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SMOKER.get(), UniformInt.of(1,1), 8, 3);
        addTrade(list, UniformInt.of(2,6), Items.IRON_INGOT, UniformInt.of(1,3), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SMOKERCAN.get(), UniformInt.of(1,1), 8, 3);
        addTrade(list, UniformInt.of(1,3), Items.LEATHER, UniformInt.of(2,4), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BELLOW.get(), UniformInt.of(1,1), 8, 3);
        addTrade(list, UniformInt.of(1,3), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_DIPPER.get(), UniformInt.of(1,1), 4, 3);
        addTrade(list, UniformInt.of(6,12), Items.REDSTONE, UniformInt.of(1,4), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEEPEDIA.get(), UniformInt.of(1,1), 4, 3);
        addTrade(list, UniformInt.of(6,12), Items.COMPASS, UniformInt.of(1,4), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_LOCATOR.get(), UniformInt.of(1,1), 4, 3);
        addTrade(list, UniformInt.of(6,12), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_PLANKS.get(), UniformInt.of(1,2), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_BOX.get(), UniformInt.of(1,1), 4, 3);
    }
    //endregion

    //region Level Three Trades
    private static void createLevelThreeTrades(List<VillagerTrades.ItemListing> list) {
        addHoneycombs(list);
        addHoneyBottles(list);
        addHoneyBuckets(list);
        addHoneyBlocks(list);
    }

    private static void addVanillaBeeProduct(List<VillagerTrades.ItemListing> list, Item product) {
        addTrade(list, UniformInt.of(8,16), product, UniformInt.of(1,1), 8, 3);
    }

    private static void addBeeProduct(List<VillagerTrades.ItemListing> list, ItemStack product, BeekeeperTradeData tradeData) {
        list.add((trader, random) -> tradeData.getMerchantOffer(random, product, 8, 16));
    }

    private static void addHoneycombs(List<VillagerTrades.ItemListing> list) {
        addVanillaBeeProduct(list, Items.HONEYCOMB);

        Collection<RegistryEntry<Item>> honeycombs = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEYCOMB_ITEMS.getEntries();
        honeycombs.forEach(registryEntry -> {
            Item item = registryEntry.get();
            if (item instanceof Tradeable tradeable && tradeable.isTradable()) {
                addBeeProduct(list, item.getDefaultInstance(), tradeable.getTradeData());
            }
        });
    }

    private static void addHoneyBottles(List<VillagerTrades.ItemListing> list) {
        addVanillaBeeProduct(list, Items.HONEY_BOTTLE);

        Collection<RegistryEntry<Item>> honeyBottles = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BOTTLE_ITEMS.getEntries();
        honeyBottles.forEach(registryEntry -> {
            Item item = registryEntry.get();
            if (item instanceof Tradeable tradeable && tradeable.isTradable()) {
                addBeeProduct(list, item.getDefaultInstance(), tradeable.getTradeData());
            }
        });
    }

    private static void addHoneyBuckets(List<VillagerTrades.ItemListing> list) {
        addTrade(list, UniformInt.of(8,16), Items.BUCKET, UniformInt.of(1,1), ModItems.HONEY_FLUID_BUCKET.get(), UniformInt.of(1,1), 8, 3);

        Collection<RegistryEntry<Item>> honeyBuckets = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BUCKET_ITEMS.getEntries();
        honeyBuckets.forEach(registryEntry -> {
            Item item = registryEntry.get();
            if (item instanceof Tradeable tradeable && tradeable.isTradable()) {
                addBeeProduct(list, item.getDefaultInstance(), tradeable.getTradeData());
            }
        });
    }

    private static void addHoneyBlocks(List<VillagerTrades.ItemListing> list) {
        addVanillaBeeProduct(list, Items.HONEY_BLOCK);

        Collection<RegistryEntry<Item>> entries = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BLOCK_ITEMS.getEntries();
        entries.forEach(registryEntry -> {
            Item item = registryEntry.get();
            if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof Tradeable tradeable && tradeable.isTradable()) {
                addBeeProduct(list, item.getDefaultInstance(), tradeable.getTradeData());
            }
        });
    }
    //endregion

    //region Level Four Trades
    private static void createLevelFourTrades(List<VillagerTrades.ItemListing> list) {
        addNests(list);
        for (PotionType type : PotionType.values()) {
            addCalmingPotion(list, type, false);
            addCalmingPotion(list, type, true);
        }
    }

    private static void addNests(List<VillagerTrades.ItemListing> list) {
        addNest(list, Items.BEEHIVE);
        addNest(list, Items.BEE_NEST);
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T1_NEST_ITEMS.getEntries().forEach(nest -> addNest(list, nest.get()));
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T2_NEST_ITEMS.getEntries().forEach(nest -> addNest(list, nest.get()));
    }

    private static void addNest(List<VillagerTrades.ItemListing> list, Item nest) {
        addTrade(list, UniformInt.of(16,32), Items.GRASS, UniformInt.of(8,24), nest, UniformInt.of(1,1), 4, 0.02f, 3);
    }

    private static void addCalmingPotion(List<VillagerTrades.ItemListing> list, PotionType potionType, boolean isLong) {
        list.add((trader, random) -> new MerchantOffer(
                new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.GOLD_FLOWER_ITEM.get(), random.nextIntBetweenInclusive(4, 12)),
                waterBottle(random, potionType),
                calmingPotion(random, potionType, isLong),
                random.nextIntBetweenInclusive(0, 12), 12, 3, 0.05f
        ));
    }

    private static ItemStack waterBottle(RandomSource random, PotionType potionType) {
        ItemStack waterBottle = potionType.itemStack();
        CompoundTag tag = new CompoundTag();
        tag.putString("Potion", "minecraft:water");
        waterBottle.setTag(tag);
        waterBottle.setCount(random.nextIntBetweenInclusive(1, 3));
        return waterBottle;
    }

    private static ItemStack calmingPotion(RandomSource random, PotionType potionType, boolean isLong) {
        ItemStack potion = potionType.itemStack();
        potion.setTag(calmingPotionTag(isLong));
        potion.setCount(random.nextIntBetweenInclusive(1, 3));
        return potion;
    }

    private static CompoundTag calmingPotionTag(boolean isLong) {
        String type = isLong ? "resourcefulbees:calming" : "resourcefulbees:long_calming";
        CompoundTag tag = new CompoundTag();
        tag.putString("Potion", type);
        return tag;
    }
    //endregion

    //region Level Five Trades
    private static void createLevelFiveTrades(List<VillagerTrades.ItemListing> list) {
        list.add((trader, random) -> new MerchantOffer(
                new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.GOLD_FLOWER_ITEM.get(), random.nextIntBetweenInclusive(32, 64)),
                new ItemStack(Items.BLACK_BANNER),
                getQueenBeeBanner(),
                random.nextIntBetweenInclusive(1, 3), 1, 3, .05f
        ));

        addBees(list);
    }

    private static void addBees(List<VillagerTrades.ItemListing> list) {
        BeeRegistry.get().getStreamOfBees()
            .filter(beeData -> beeData.getTradeData().isTradable())
            .forEach(bee -> {
                var tradeData = bee.getTradeData();
                list.add((trader, random) -> {
                    ItemStack beeJar = BeeJarItem.createFilledJar(bee.id(), bee.getRenderData().colorData().jarColor());
                    beeJar.setCount(tradeData.amount().sample(random));
                    return tradeData.getMerchantOffer(random, beeJar, 32, 64);
                });
            });
    }

    private static ItemStack getQueenBeeBanner() {
        ItemStack queenBeeBanner = new ItemStack(Items.BLACK_BANNER);
        CompoundTag tag = queenBeeBanner.getOrCreateTagElement("BlockEntityTag");
        ListTag listTag = new BannerPattern.Builder()
                .addPattern(BannerPatterns.RHOMBUS_MIDDLE, DyeColor.LIGHT_BLUE)
                .addPattern(BannerPatterns.STRIPE_DOWNRIGHT, DyeColor.YELLOW)
                .addPattern(BannerPatterns.STRIPE_DOWNLEFT, DyeColor.YELLOW)
                .addPattern(BannerPatterns.STRIPE_BOTTOM, DyeColor.YELLOW)
                .addPattern(BannerPatterns.TRIANGLE_TOP, DyeColor.YELLOW)
                .addPattern(BannerPatterns.CURLY_BORDER, DyeColor.YELLOW)
                .toListTag();
        tag.put("Patterns", listTag);
        queenBeeBanner.setHoverName(ItemTranslations.QUEEN_BEE_BANNER.withStyle(ChatFormatting.GOLD));
        queenBeeBanner.setCount(1);
        return queenBeeBanner;
    }
    //endregion

    private enum PotionType {
        POTION(Items.POTION),
        SPLASH(Items.SPLASH_POTION),
        LINGERING(Items.LINGERING_POTION);

        private final Item item;

        PotionType(Item item) {
            this.item = item;
        }

        private ItemStack itemStack() {
            return item.getDefaultInstance();
        }
    }
}
