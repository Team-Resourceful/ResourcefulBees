package com.teamresourceful.resourcefulbees.common.entities.villager;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.items.base.Tradeable;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ItemTranslations;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModVillagerProfessions;
import com.teamresourceful.resourcefulbees.platform.common.events.RegisterVillagerTradesEvent;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;

import java.util.Collection;

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

    public static void setupBeekeeper(RegisterVillagerTradesEvent event) {
        if (event.profession() != ModVillagerProfessions.BEEKEEPER.get()) return;
        createLevelOneTrades(event);
        createLevelTwoTrades(event);
        createLevelThreeTrades(event);
        createLevelFourTrades(event);
        createLevelFiveTrades(event);
    }

    private static void addTrade(int level, RegisterVillagerTradesEvent event, UniformInt flowerQty, Item costB, UniformInt costBQty, Item result, UniformInt resultQty, int maxUses, float priceMultiplier, int xp) {
        event.register(level, (trader, random) -> new MerchantOffer(
                new ItemStack(ModItems.GOLD_FLOWER_ITEM.get(), flowerQty.sample(random)),
                new ItemStack(costB, costBQty.sample(random)),
                new ItemStack(result, resultQty.sample(random)),
                0, maxUses, xp, priceMultiplier
        ));
    }

    private static void addTrade(int level, RegisterVillagerTradesEvent event, UniformInt flowerQty, Item costB, UniformInt costBQty, Item result, UniformInt resultQty, int maxUses) {
        addTrade(level, event, flowerQty, costB, costBQty, result, resultQty, maxUses, .05f, 3);
    }

    private static void addTrade(int level, RegisterVillagerTradesEvent event, UniformInt flowerQty, Item result, UniformInt resultQty, int maxUses) {
        addTrade(level, event, flowerQty, ItemStack.EMPTY.getItem(), UniformInt.of(0,0), result, resultQty, maxUses);
    }

    //region Level One Trades
    private static void createLevelOneTrades(RegisterVillagerTradesEvent event) {
        addTrade(1, event, UniformInt.of(1,2), ModItems.WAX.get(), UniformInt.of(1,4), 12);
        addTrade(1, event, UniformInt.of(2,4), ModItems.BEE_JAR.get(), UniformInt.of(1,4), 8);
    }
    //endregion

    //region Level Two Trades
    private static void createLevelTwoTrades(RegisterVillagerTradesEvent event) {
        addTrade(2, event, UniformInt.of(2,6), Items.IRON_INGOT, UniformInt.of(1,3), ModItems.SCRAPER.get(), UniformInt.of(1,1), 4);
        addTrade(2, event, UniformInt.of(4,8), Items.IRON_INGOT, UniformInt.of(2,6), ModItems.SMOKER.get(), UniformInt.of(1,1), 8);
        addTrade(2, event, UniformInt.of(2,6), Items.IRON_INGOT, UniformInt.of(1,3), ModItems.SMOKERCAN.get(), UniformInt.of(1,1), 8);
        addTrade(2, event, UniformInt.of(1,3), Items.LEATHER, UniformInt.of(2,4), ModItems.BELLOW.get(), UniformInt.of(1,1), 8);
        addTrade(2, event, UniformInt.of(1,3), ModItems.HONEY_DIPPER.get(), UniformInt.of(1,1), 4);
        addTrade(2, event, UniformInt.of(6,12), Items.REDSTONE, UniformInt.of(1,4), ModItems.BEEPEDIA.get(), UniformInt.of(1,1), 4);
        addTrade(2, event, UniformInt.of(6,12), Items.COMPASS, UniformInt.of(1,4), ModItems.BEE_LOCATOR.get(), UniformInt.of(1,1), 4);
        addTrade(2, event, UniformInt.of(6,12), ModItems.WAXED_PLANKS.get(), UniformInt.of(1,2), ModItems.BEE_BOX.get(), UniformInt.of(1,1), 4);
    }
    //endregion

    //region Level Three Trades
    private static void createLevelThreeTrades(RegisterVillagerTradesEvent event) {
        addHoneycombs(event);
        addHoneyBottles(event);
        addHoneyBuckets(event);
        addHoneyBlocks(event);
    }

    private static void addVanillaBeeProduct(RegisterVillagerTradesEvent event, Item product) {
        addTrade(3, event, UniformInt.of(8,16), product, UniformInt.of(1,1), 8);
    }

    private static void addBeeProduct(RegisterVillagerTradesEvent event, ItemStack product, BeekeeperTradeData tradeData) {
        event.register(3, (trader, random) -> tradeData.getMerchantOffer(random, product, 8, 16));
    }

    private static void addHoneycombs(RegisterVillagerTradesEvent event) {
        addVanillaBeeProduct(event, Items.HONEYCOMB);

        Collection<RegistryEntry<Item>> honeycombs = ModItems.HONEYCOMB_ITEMS.getEntries();
        honeycombs.forEach(registryEntry -> {
            Item item = registryEntry.get();
            if (item instanceof Tradeable tradeable && tradeable.isTradable()) {
                addBeeProduct(event, item.getDefaultInstance(), tradeable.getTradeData());
            }
        });
    }

    private static void addHoneyBottles(RegisterVillagerTradesEvent event) {
        addVanillaBeeProduct(event, Items.HONEY_BOTTLE);

        Collection<RegistryEntry<Item>> honeyBottles = ModItems.HONEY_BOTTLE_ITEMS.getEntries();
        honeyBottles.forEach(registryEntry -> {
            Item item = registryEntry.get();
            if (item instanceof Tradeable tradeable && tradeable.isTradable()) {
                addBeeProduct(event, item.getDefaultInstance(), tradeable.getTradeData());
            }
        });
    }

    private static void addHoneyBuckets(RegisterVillagerTradesEvent event) {
        addTrade(3, event, UniformInt.of(8,16), Items.BUCKET, UniformInt.of(1,1), ModItems.HONEY_BUCKET.get(), UniformInt.of(1,1), 8);

        Collection<RegistryEntry<Item>> honeyBuckets = ModItems.HONEY_BUCKET_ITEMS.getEntries();
        honeyBuckets.forEach(registryEntry -> {
            Item item = registryEntry.get();
            if (item instanceof Tradeable tradeable && tradeable.isTradable()) {
                addBeeProduct(event, item.getDefaultInstance(), tradeable.getTradeData());
            }
        });
    }

    private static void addHoneyBlocks(RegisterVillagerTradesEvent event) {
        addVanillaBeeProduct(event, Items.HONEY_BLOCK);

        Collection<RegistryEntry<Item>> entries = ModItems.HONEY_BLOCK_ITEMS.getEntries();
        entries.forEach(registryEntry -> {
            Item item = registryEntry.get();
            if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof Tradeable tradeable && tradeable.isTradable()) {
                addBeeProduct(event, item.getDefaultInstance(), tradeable.getTradeData());
            }
        });
    }
    //endregion

    //region Level Four Trades
    private static void createLevelFourTrades(RegisterVillagerTradesEvent event) {
        addNests(event);
        for (PotionType type : PotionType.values()) {
            addCalmingPotion(event, type, false);
            addCalmingPotion(event, type, true);
        }
    }

    private static void addNests(RegisterVillagerTradesEvent event) {
        addNest(event, Items.BEEHIVE);
        addNest(event, Items.BEE_NEST);
        ModItems.T1_NEST_ITEMS.getEntries().forEach(nest -> addNest(event, nest.get()));
        ModItems.T2_NEST_ITEMS.getEntries().forEach(nest -> addNest(event, nest.get()));
    }

    private static void addNest(RegisterVillagerTradesEvent event, Item nest) {
        addTrade(4, event, UniformInt.of(16,32), Items.GRASS, UniformInt.of(8,24), nest, UniformInt.of(1,1), 4, 0.02f, 3);
    }

    private static void addCalmingPotion(RegisterVillagerTradesEvent event, PotionType potionType, boolean isLong) {
        event.register(4, (trader, random) -> new MerchantOffer(
                new ItemStack(ModItems.GOLD_FLOWER_ITEM.get(), random.nextIntBetweenInclusive(4, 12)),
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
    private static void createLevelFiveTrades(RegisterVillagerTradesEvent event) {
        event.register(5, (trader, random) -> new MerchantOffer(
                new ItemStack(ModItems.GOLD_FLOWER_ITEM.get(), random.nextIntBetweenInclusive(32, 64)),
                new ItemStack(Items.BLACK_BANNER),
                getQueenBeeBanner(),
                random.nextIntBetweenInclusive(1, 3), 1, 3, .05f
        ));

        addBees(event);
    }

    private static void addBees(RegisterVillagerTradesEvent event) {
        BeeRegistry.get().getStreamOfBees()
            .filter(beeData -> beeData.getTradeData().isTradable())
            .forEach(bee -> {
                var tradeData = bee.getTradeData();
                event.register(5, (trader, random) -> {
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
