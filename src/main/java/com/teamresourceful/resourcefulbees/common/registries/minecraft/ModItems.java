package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.blocks.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.components.BeehiveUpgrade;
import com.teamresourceful.resourcefulbees.common.components.Upgrade;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.config.HoneyGenConfig;
import com.teamresourceful.resourcefulbees.common.items.*;
import com.teamresourceful.resourcefulbees.common.items.upgrade.BreederTimeUpgradeItem;
import com.teamresourceful.resourcefulbees.common.items.upgrade.nestupgrade.NestUpgradeItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.builtin.ResourcefulItemRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModItems {

    private ModItems() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulItemRegistry ITEMS = ResourcefulRegistries.createForItems(ModConstants.MOD_ID);

    public static final ResourcefulItemRegistry NEST_ITEMS = ResourcefulRegistries.createForItems(ITEMS);
    public static final ResourcefulItemRegistry T1_NEST_ITEMS = ResourcefulRegistries.createForItems(NEST_ITEMS);
    public static final ResourcefulItemRegistry T2_NEST_ITEMS = ResourcefulRegistries.createForItems(NEST_ITEMS);
    public static final ResourcefulItemRegistry T3_NEST_ITEMS = ResourcefulRegistries.createForItems(NEST_ITEMS);
    public static final ResourcefulItemRegistry T4_NEST_ITEMS = ResourcefulRegistries.createForItems(NEST_ITEMS);

    public static final ResourcefulItemRegistry SPAWN_EGG_ITEMS = ResourcefulRegistries.createForItems(ITEMS);
    public static final ResourcefulItemRegistry HONEYCOMB_ITEMS = ResourcefulRegistries.createForItems(ITEMS);
    public static final ResourcefulItemRegistry HONEYCOMB_BLOCK_ITEMS = ResourcefulRegistries.createForItems(ITEMS);
    public static final ResourcefulItemRegistry HONEY_BOTTLE_ITEMS = ResourcefulRegistries.createForItems(ITEMS);
    public static final ResourcefulItemRegistry HONEY_BLOCK_ITEMS = ResourcefulRegistries.createForItems(ITEMS);
    public static final ResourcefulItemRegistry HONEY_BUCKET_ITEMS = ResourcefulRegistries.createForItems(ITEMS);

    private static RegistryEntry<Item> registerBlockItem(ResourcefulItemRegistry registry, String id, RegistryEntry<Block> block, Supplier<Item.Properties> getter) {
        return registerItem(registry, id, properties -> new BlockItem(block.get(), properties), getter);
    }

    private static RegistryEntry<Item> registerItem(ResourcefulItemRegistry registry, String id, Function<Item.Properties, Item> factory, Supplier<Item.Properties> getter) {
        return registry.register(id, factory, getter);
    }

    public static void registerHiveItem(ResourcefulItemRegistry registry, String id, RegistryEntry<Block> block) {
        registerBlockItem(registry, id, block, Item.Properties::new);
    }


//    //region Nests
//    //region Acacia
//    public static final RegistryEntry<Item> ACACIA_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/acacia/1", ModBlocks.ACACIA_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_ACACIA_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/acacia/2", ModBlocks.T1_ACACIA_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_ACACIA_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/acacia/3", ModBlocks.T2_ACACIA_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_ACACIA_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/acacia/4", ModBlocks.T3_ACACIA_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Birch
//    public static final RegistryEntry<Item> BIRCH_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/birch/1", ModBlocks.BIRCH_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_BIRCH_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/birch/2", ModBlocks.T1_BIRCH_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_BIRCH_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/birch/3", ModBlocks.T2_BIRCH_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_BIRCH_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/birch/4", ModBlocks.T3_BIRCH_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Brown Mushroom
//    public static final RegistryEntry<Item> BROWN_MUSHROOM_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/brown_mushroom/1", ModBlocks.BROWN_MUSHROOM_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_BROWN_MUSHROOM_NEST_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/brown_mushroom/2", ModBlocks.T1_BROWN_MUSHROOM_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_BROWN_MUSHROOM_NEST_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/brown_mushroom/3", ModBlocks.T2_BROWN_MUSHROOM_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_BROWN_MUSHROOM_NEST_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/brown_mushroom/4", ModBlocks.T3_BROWN_MUSHROOM_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Crimson
//    public static final RegistryEntry<Item> CRIMSON_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/crimson/1", ModBlocks.CRIMSON_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_CRIMSON_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/crimson/2", ModBlocks.T1_CRIMSON_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_CRIMSON_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/crimson/3", ModBlocks.T2_CRIMSON_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_CRIMSON_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/crimson/4", ModBlocks.T3_CRIMSON_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Crimson Nylium
//    public static final RegistryEntry<Item> CRIMSON_NYLIUM_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/crimson_nylium/1", ModBlocks.CRIMSON_NYLIUM_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_CRIMSON_NYLIUM_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/crimson_nylium/2", ModBlocks.T1_CRIMSON_NYLIUM_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_CRIMSON_NYLIUM_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/crimson_nylium/3", ModBlocks.T2_CRIMSON_NYLIUM_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_CRIMSON_NYLIUM_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/crimson_nylium/4", ModBlocks.T3_CRIMSON_NYLIUM_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Dark Oak
//    public static final RegistryEntry<Item> DARK_OAK_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/dark_oak/1", ModBlocks.DARK_OAK_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_DARK_OAK_NEST_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/dark_oak/2", ModBlocks.T1_DARK_OAK_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_DARK_OAK_NEST_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/dark_oak/3", ModBlocks.T2_DARK_OAK_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_DARK_OAK_NEST_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/dark_oak/4", ModBlocks.T3_DARK_OAK_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Grass
//    public static final RegistryEntry<Item> GRASS_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/grass/1", ModBlocks.GRASS_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_GRASS_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/grass/2", ModBlocks.T1_GRASS_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_GRASS_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/grass/3", ModBlocks.T2_GRASS_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_GRASS_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/grass/4", ModBlocks.T3_GRASS_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Jungle
//    public static final RegistryEntry<Item> JUNGLE_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/jungle/1", ModBlocks.JUNGLE_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_JUNGLE_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/jungle/2", ModBlocks.T1_JUNGLE_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_JUNGLE_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/jungle/3", ModBlocks.T2_JUNGLE_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_JUNGLE_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/jungle/4", ModBlocks.T3_JUNGLE_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Nether
//    public static final RegistryEntry<Item> NETHER_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/netherrack/1", ModBlocks.NETHER_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_NETHER_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/netherrack/2", ModBlocks.T1_NETHER_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_NETHER_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/netherrack/3", ModBlocks.T2_NETHER_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_NETHER_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/netherrack/4", ModBlocks.T3_NETHER_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Oak
//    public static final RegistryEntry<Item> OAK_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/oak/1", ModBlocks.OAK_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_OAK_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/oak/2", ModBlocks.T1_OAK_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_OAK_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/oak/3", ModBlocks.T2_OAK_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_OAK_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/oak/4", ModBlocks.T3_OAK_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Prismarine
//    public static final RegistryEntry<Item> PRISMARINE_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/prismarine/1", ModBlocks.PRISMARINE_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_PRISMARINE_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/prismarine/2", ModBlocks.T1_PRISMARINE_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_PRISMARINE_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/prismarine/3", ModBlocks.T2_PRISMARINE_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_PRISMARINE_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/prismarine/4", ModBlocks.T3_PRISMARINE_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Purpur
//    public static final RegistryEntry<Item> PURPUR_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/chorus/1", ModBlocks.PURPUR_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_PURPUR_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/chorus/2", ModBlocks.T1_PURPUR_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_PURPUR_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/chorus/3", ModBlocks.T2_PURPUR_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_PURPUR_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/chorus/4", ModBlocks.T3_PURPUR_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Red Mushroom
//    public static final RegistryEntry<Item> RED_MUSHROOM_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/red_mushroom/1", ModBlocks.RED_MUSHROOM_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_RED_MUSHROOM_NEST_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/red_mushroom/2", ModBlocks.T1_RED_MUSHROOM_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_RED_MUSHROOM_NEST_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/red_mushroom/3", ModBlocks.T2_RED_MUSHROOM_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_RED_MUSHROOM_NEST_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/red_mushroom/4", ModBlocks.T3_RED_MUSHROOM_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Spruce
//    public static final RegistryEntry<Item> SPRUCE_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/spruce/1", ModBlocks.SPRUCE_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_SPRUCE_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/spruce/2", ModBlocks.T1_SPRUCE_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_SPRUCE_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/spruce/3", ModBlocks.T2_SPRUCE_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_SPRUCE_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/spruce/4", ModBlocks.T3_SPRUCE_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Warped
//    public static final RegistryEntry<Item> WARPED_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/warped/1", ModBlocks.WARPED_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_WARPED_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/warped/2", ModBlocks.T1_WARPED_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_WARPED_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/warped/3", ModBlocks.T2_WARPED_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_WARPED_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/warped/4", ModBlocks.T3_WARPED_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Warped Nylium
//    public static final RegistryEntry<Item> WARPED_NYLIUM_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/warped_nylium/1", ModBlocks.WARPED_NYLIUM_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_WARPED_NYLIUM_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/warped_nylium/2", ModBlocks.T1_WARPED_NYLIUM_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_WARPED_NYLIUM_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/warped_nylium/3", ModBlocks.T2_WARPED_NYLIUM_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_WARPED_NYLIUM_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/warped_nylium/4", ModBlocks.T3_WARPED_NYLIUM_BEEHIVE, Item.Properties::new);
//    //endregion
//    //region Wither
//    public static final RegistryEntry<Item> WITHER_BEE_NEST_ITEM = registerBlockItem(T1_NEST_ITEMS, "nest/wither/1", ModBlocks.WITHER_BEE_NEST, Item.Properties::new);
//    public static final RegistryEntry<Item> T1_WITHER_BEEHIVE_ITEM = registerBlockItem(T2_NEST_ITEMS, "nest/wither/2", ModBlocks.T1_WITHER_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T2_WITHER_BEEHIVE_ITEM = registerBlockItem(T3_NEST_ITEMS, "nest/wither/3", ModBlocks.T2_WITHER_BEEHIVE, Item.Properties::new);
//    public static final RegistryEntry<Item> T3_WITHER_BEEHIVE_ITEM = registerBlockItem(T4_NEST_ITEMS, "nest/wither/4", ModBlocks.T3_WITHER_BEEHIVE, Item.Properties::new);
//    //endregion
//    //endregion

    public static final RegistryEntry<Item> T1_APIARY_ITEM = registerItem(T1_NEST_ITEMS, "t1_apiary", properties -> new ApiaryBlockItem((ApiaryBlock) ModBlocks.T1_APIARY_BLOCK.get(), properties), Item.Properties::new);
    public static final RegistryEntry<Item> T2_APIARY_ITEM = registerItem(T1_NEST_ITEMS,"t2_apiary", properties -> new ApiaryBlockItem((ApiaryBlock) ModBlocks.T2_APIARY_BLOCK.get(), properties), Item.Properties::new);
    public static final RegistryEntry<Item> T3_APIARY_ITEM = registerItem(T1_NEST_ITEMS,"t3_apiary", properties -> new ApiaryBlockItem((ApiaryBlock) ModBlocks.T3_APIARY_BLOCK.get(), properties), Item.Properties::new);
    public static final RegistryEntry<Item> T4_APIARY_ITEM = registerItem(T1_NEST_ITEMS,"t4_apiary", properties -> new ApiaryBlockItem((ApiaryBlock) ModBlocks.T4_APIARY_BLOCK.get(), properties), Item.Properties::new);


    public static final RegistryEntry<Item> WAX = registerItem(ITEMS, "wax", WaxItem::new, Item.Properties::new);
public static final RegistryEntry<Item> WAX_BLOCK_ITEM = registerBlockItem(ITEMS, "wax_block", ModBlocks.WAX_BLOCK, Item.Properties::new);

    public static final RegistryEntry<Item> SCRAPER = registerItem(ITEMS,"scraper", ScraperItem::new, () -> new Item.Properties().stacksTo(1));

    public static final RegistryEntry<Item> SMOKER = registerItem(ITEMS, "smoker", SmokerItem::new, () -> new Item.Properties().durability(GeneralConfig.smokerDurability));
    public static final RegistryEntry<Item> BELLOW = registerItem(ITEMS, "bellow", Item::new, Item.Properties::new);
    public static final RegistryEntry<Item> SMOKER_CAN = registerItem(ITEMS, "smoker_can", Item::new, Item.Properties::new);

    public static final RegistryEntry<Item> BEE_BOX_TEMP = registerItem(ITEMS, "bee_box_temp", properties -> BeeBoxItem.temp(ModBlocks.BEE_BOX_TEMP.get(), properties), () -> new Item.Properties().stacksTo(1));
    public static final RegistryEntry<Item> BEE_BOX = registerItem(ITEMS, "bee_box", properties -> BeeBoxItem.of(ModBlocks.BEE_BOX.get(), properties), () -> new Item.Properties().stacksTo(1));
    //public static final RegistryEntry<Item> BEEPEDIA = registerItem(ITEMS, "beepedia", () -> new BeepediaItem(new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> HONEY_DIPPER = registerItem(ITEMS, "honey_dipper", HoneyDipperItem::new, () -> new Item.Properties().stacksTo(1));

    public static final RegistryEntry<Item> BEE_JAR = registerItem(ITEMS, "bee_jar", BeeJarItem::new, () -> new Item.Properties().stacksTo(16));
//    public static final RegistryEntry<Item> POLLEN_SPREADER_FAN = registerItem(ITEMS, "pollen_spreader_fan", () -> new BlockItem(ModBlocks.POLLEN_SPREADER_FAN.get(), new Item.Properties()));
//    public static final RegistryEntry<Item> POLLEN_SPREADER = registerItem(ITEMS, "pollen_spreader", () -> new BlockItem(ModBlocks.POLLEN_SPREADER.get(), new Item.Properties()));
    //public static final RegistryEntry<Item> MUTATED_POLLEN = registerItem(ITEMS, "mutated_pollen", () -> new MutatedPollenItem(new Item.Properties()));
//    public static final RegistryEntry<Item> FAKE_FLOWER = registerItem(ITEMS, "fake_flower", () -> new BlockItem(ModBlocks.FAKE_FLOWER.get(), new Item.Properties()));

    public static final RegistryEntry<Item> GOLD_FLOWER_ITEM = registerBlockItem(ITEMS, "gold_flower", ModBlocks.GOLD_FLOWER, Item.Properties::new);

//    public static final RegistryEntry<Item> BREEDER_ITEM = NEST_registerItem(ITEMS, "breeder", () -> new BlockItem(ModBlocks.BREEDER_BLOCK.get(), new Item.Properties()));

    public static final RegistryEntry<Item> T2_NEST_UPGRADE = registerItem(ITEMS, "t2_nest_upgrade", Item::new, () -> new Item.Properties().stacksTo(16).component(ModDataComponents.BEEHIVE_UPGRADE, BeehiveUpgrade.create(BeehiveUpgrade.Tier.T1_TO_T2)));
    public static final RegistryEntry<Item> T3_NEST_UPGRADE = registerItem(ITEMS, "t3_nest_upgrade", Item::new, () -> new Item.Properties().stacksTo(16).component(ModDataComponents.BEEHIVE_UPGRADE, BeehiveUpgrade.create(BeehiveUpgrade.Tier.T2_TO_T3)));
    public static final RegistryEntry<Item> T4_NEST_UPGRADE = registerItem(ITEMS, "t4_nest_upgrade", Item::new, () -> new Item.Properties().stacksTo(16).component(ModDataComponents.BEEHIVE_UPGRADE, BeehiveUpgrade.create(BeehiveUpgrade.Tier.T3_TO_T4)));
    public static final RegistryEntry<Item> BREED_TIME_UPGRADE = registerItem(ITEMS, "breed_time_upgrade", Item::new, () -> new Item.Properties().stacksTo(4).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.BREED_TIME)));

    //public static final RegistryEntry<Item> BEE_LOCATOR = registerItem(ITEMS, "bee_locator", () -> new BeeLocatorItem(new Item.Properties().stacksTo(1)));

    //region Waxed Blocks
    public static final RegistryEntry<Item> HONEY_GLASS = registerBlockItem(ITEMS, "honey_glass", ModBlocks.HONEY_GLASS, Item.Properties::new);
    public static final RegistryEntry<Item> HONEY_GLASS_PLAYER = registerBlockItem(ITEMS, "honey_glass_player", ModBlocks.HONEY_GLASS_PLAYER, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_PLANKS = registerBlockItem(ITEMS, "waxed_planks", ModBlocks.WAXED_PLANKS, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_STAIRS = registerBlockItem(ITEMS, "waxed_stairs", ModBlocks.WAXED_STAIRS, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_SLAB = registerBlockItem(ITEMS, "waxed_slab", ModBlocks.WAXED_SLAB, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_FENCE = registerBlockItem(ITEMS, "waxed_fence", ModBlocks.WAXED_FENCE, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_FENCE_GATE = registerBlockItem(ITEMS, "waxed_fence_gate", ModBlocks.WAXED_FENCE_GATE, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_BUTTON = registerBlockItem(ITEMS, "waxed_button", ModBlocks.WAXED_BUTTON, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_PRESSURE_PLATE = registerBlockItem(ITEMS, "waxed_pressure_plate", ModBlocks.WAXED_PRESSURE_PLATE, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_DOOR = registerBlockItem(ITEMS, "waxed_door", ModBlocks.WAXED_DOOR, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_TRAPDOOR = registerBlockItem(ITEMS, "waxed_trapdoor", ModBlocks.WAXED_TRAPDOOR, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_SIGN = registerItem(ITEMS, "waxed_sign", properties -> new SignItem(ModBlocks.WAXED_SIGN.get(), ModBlocks.WAXED_WALL_SIGN.get(), properties), Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_HANGING_SIGN = registerItem(ITEMS, "waxed_hanging_sign", properties -> new HangingSignItem(ModBlocks.WAXED_HANGING_SIGN.get(), ModBlocks.WAXED_WALL_HANGING_SIGN.get(), properties), Item.Properties::new);
    public static final RegistryEntry<Item> TRIMMED_WAXED_PLANKS = registerBlockItem(ITEMS, "trimmed_waxed_planks", ModBlocks.TRIMMED_WAXED_PLANKS, Item.Properties::new);
    public static final RegistryEntry<Item> WAXED_MACHINE_BLOCK = registerBlockItem(ITEMS, "waxed_machine_block", ModBlocks.WAXED_MACHINE_BLOCK, Item.Properties::new);
    public static final RegistryEntry<Item> HONEY_CAP_UPGRADE = ModItems.registerItem(ITEMS, "honey_cap_upgrade", Item::new, () -> new Item.Properties().stacksTo(HoneyGenConfig.upgradeStackLimit).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.HONEY_CAPACITY)));
    public static final RegistryEntry<Item> ENERGY_CAP_UPGRADE = ModItems.registerItem(ITEMS, "energy_cap_upgrade", Item::new, () -> new Item.Properties().stacksTo(HoneyGenConfig.upgradeStackLimit).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.ENERGY_CAPACITY)));
    public static final RegistryEntry<Item> ENERGY_XFER_UPGRADE = ModItems.registerItem(ITEMS, "energy_xfer_upgrade", Item::new, () -> new Item.Properties().stacksTo(HoneyGenConfig.upgradeStackLimit).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.ENERGY_TRANSFER)));
    public static final RegistryEntry<Item> ENERGY_FILL_UPGRADE = ModItems.registerItem(ITEMS, "energy_fill_upgrade", Item::new, () -> new Item.Properties().stacksTo(HoneyGenConfig.upgradeStackLimit).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.ENERGY_FILL)));
    //endregion

    //region Machines

//    public static final RegistryEntry<Item> FLOW_HIVE = NEST_ITEMS.register("flow_hive", () -> new BlockItem(ModBlocks.FLOW_HIVE.get(), new Item.Properties()));
//    public static final RegistryEntry<Item> ENDER_BEECON_ITEM = ITEMS.register("ender_beecon", () -> new BlockItem(ModBlocks.ENDER_BEECON.get(), new Item.Properties()));
//    public static final RegistryEntry<Item> HONEY_POT_ITEM = ITEMS.register("honey_pot", () -> new BlockItem(ModBlocks.HONEY_POT.get(), new Item.Properties()));
//    public static final RegistryEntry<Item> SOLIDIFICATION_CHAMBER_ITEM = ITEMS.register("solidification_chamber", () -> new BlockItem(ModBlocks.SOLIDIFICATION_CHAMBER.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_CRANK = registerItem(ITEMS, "centrifuge_crank", properties -> new CrankItem(ModBlocks.CENTRIFUGE_CRANK.get(), properties), Item.Properties::new);
    public static final RegistryEntry<Item> CENTRIFUGE = registerItem(ITEMS, "centrifuge", properties -> new CentrifugeItem(ModBlocks.BASIC_CENTRIFUGE.get(), properties), Item.Properties::new);
//    public static final RegistryEntry<Item> HONEY_GENERATOR_ITEM = ITEMS.register("honey_generator", () -> new BlockItem(ModBlocks.HONEY_GENERATOR.get(), new Item.Properties()));
    //endregion

    //todo needs texture
    public static final RegistryEntry<Item> HONEY_BUCKET = registerItem(ITEMS, "honey_bucket", properties -> new BucketItem(ModFluids.HONEY_FLUID_TYPE.get().still().get(), properties), () -> new Item.Properties().stacksTo(1));

    //region Special Items
    public static final FoodProperties OREO_FOOD_PROPERTIES = new FoodProperties(8, 2f, true);

    public static final Consumable OREO_CONSUMABLE = Consumable.builder().onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
            new MobEffectInstance(MobEffects.REGENERATION, 600, 1),
            new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3),
            new MobEffectInstance(MobEffects.SATURATION, 2400, 1),
            new MobEffectInstance(MobEffects.LUCK, 600, 1),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 1),
            new MobEffectInstance(MobEffects.RESISTANCE, 6000, 1),
            new MobEffectInstance(MobEffects.WATER_BREATHING, 6000, 1),
            new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 1)
    ))).build();

    public static final RegistryEntry<Item> OREO_COOKIE = ITEMS.register("oreo_cookie", Item::new, () -> new Item.Properties().rarity(Rarity.EPIC).food(OREO_FOOD_PROPERTIES, OREO_CONSUMABLE));

    public static final FoodProperties STRAWBEERRY_MILKSHAKE_PROPERTIES = new FoodProperties(6, 1.5f, true);

    public static final Consumable STRAWBEERRY_MILKSHAKE_CONSUMABLE = Consumable.builder().onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
            new MobEffectInstance(MobEffects.REGENERATION, 1200, 1),
            new MobEffectInstance(MobEffects.LUCK, 1200, 1),
            new MobEffectInstance(MobEffects.JUMP_BOOST, 1200, 1)
    ))).build();

    public static final RegistryEntry<Item> STRAWBEERRY_MILKSHAKE = ITEMS.register("strawbeerry_milkshake", Item::new,
            () -> new Item.Properties().rarity(Rarity.EPIC).food(STRAWBEERRY_MILKSHAKE_PROPERTIES,
                    STRAWBEERRY_MILKSHAKE_CONSUMABLE));
}
