package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.item.*;
import com.teamresourceful.resourcefulbees.common.item.centrifuge.CrankItem;
import com.teamresourceful.resourcefulbees.common.item.centrifuge.ManualCentrifugeItem;
import com.teamresourceful.resourcefulbees.common.item.locator.BeeLocator;
import com.teamresourceful.resourcefulbees.common.item.upgrade.BreederTimeUpgradeItem;
import com.teamresourceful.resourcefulbees.common.item.upgrade.nestupgrade.BeehiveUpgrade;
import com.teamresourceful.resourcefulbees.common.item.upgrade.nestupgrade.NestUpgradeItem;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;

@SuppressWarnings("unused")
public final class ModItems {

    private ModItems() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(Registry.ITEM, ResourcefulBees.MOD_ID);

    public static final ResourcefulRegistry<Item> NEST_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> T1_NEST_ITEMS = ResourcefulRegistries.create(NEST_ITEMS);
    public static final ResourcefulRegistry<Item> T2_NEST_ITEMS = ResourcefulRegistries.create(NEST_ITEMS);
    public static final ResourcefulRegistry<Item> T3_NEST_ITEMS = ResourcefulRegistries.create(NEST_ITEMS);
    public static final ResourcefulRegistry<Item> T4_NEST_ITEMS = ResourcefulRegistries.create(NEST_ITEMS);
    public static final ResourcefulRegistry<Item> SPAWN_EGG_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEYCOMB_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEYCOMB_BLOCK_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEY_BOTTLE_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEY_BLOCK_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEY_BUCKET_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> CENTRIFUGE_ITEMS = ResourcefulRegistries.create(ITEMS);

    //region Nests
    //region Acacia
    public static final RegistryEntry<Item> ACACIA_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/acacia/1", () -> new BlockItem(ModBlocks.ACACIA_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_ACACIA_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/acacia/2", () -> new BlockItem(ModBlocks.T1_ACACIA_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_ACACIA_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/acacia/3", () -> new BlockItem(ModBlocks.T2_ACACIA_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_ACACIA_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/acacia/4", () -> new BlockItem(ModBlocks.T3_ACACIA_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Birch
    public static final RegistryEntry<Item> BIRCH_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/birch/1", () -> new BlockItem(ModBlocks.BIRCH_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_BIRCH_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/birch/2", () -> new BlockItem(ModBlocks.T1_BIRCH_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_BIRCH_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/birch/3", () -> new BlockItem(ModBlocks.T2_BIRCH_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_BIRCH_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/birch/4", () -> new BlockItem(ModBlocks.T3_BIRCH_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Brown Mushroom
    public static final RegistryEntry<Item> BROWN_MUSHROOM_NEST_ITEM = T1_NEST_ITEMS.register("nest/brown_mushroom/1", () -> new BlockItem(ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_BROWN_MUSHROOM_NEST_ITEM = T2_NEST_ITEMS.register("nest/brown_mushroom/2", () -> new BlockItem(ModBlocks.T1_BROWN_MUSHROOM_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_BROWN_MUSHROOM_NEST_ITEM = T3_NEST_ITEMS.register("nest/brown_mushroom/3", () -> new BlockItem(ModBlocks.T2_BROWN_MUSHROOM_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_BROWN_MUSHROOM_NEST_ITEM = T4_NEST_ITEMS.register("nest/brown_mushroom/4", () -> new BlockItem(ModBlocks.T3_BROWN_MUSHROOM_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Crimson
    public static final RegistryEntry<Item> CRIMSON_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/crimson/1", () -> new BlockItem(ModBlocks.CRIMSON_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_CRIMSON_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/crimson/2", () -> new BlockItem(ModBlocks.T1_CRIMSON_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_CRIMSON_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/crimson/3", () -> new BlockItem(ModBlocks.T2_CRIMSON_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_CRIMSON_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/crimson/4", () -> new BlockItem(ModBlocks.T3_CRIMSON_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Crimson Nylium
    public static final RegistryEntry<Item> CRIMSON_NYLIUM_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/crimson_nylium/1", () -> new BlockItem(ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_CRIMSON_NYLIUM_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/crimson_nylium/2", () -> new BlockItem(ModBlocks.T1_CRIMSON_NYLIUM_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_CRIMSON_NYLIUM_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/crimson_nylium/3", () -> new BlockItem(ModBlocks.T2_CRIMSON_NYLIUM_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_CRIMSON_NYLIUM_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/crimson_nylium/4", () -> new BlockItem(ModBlocks.T3_CRIMSON_NYLIUM_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Dark Oak
    public static final RegistryEntry<Item> DARK_OAK_NEST_ITEM = T1_NEST_ITEMS.register("nest/dark_oak/1", () -> new BlockItem(ModBlocks.DARK_OAK_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_DARK_OAK_NEST_ITEM = T2_NEST_ITEMS.register("nest/dark_oak/2", () -> new BlockItem(ModBlocks.T1_DARK_OAK_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_DARK_OAK_NEST_ITEM = T3_NEST_ITEMS.register("nest/dark_oak/3", () -> new BlockItem(ModBlocks.T2_DARK_OAK_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_DARK_OAK_NEST_ITEM = T4_NEST_ITEMS.register("nest/dark_oak/4", () -> new BlockItem(ModBlocks.T3_DARK_OAK_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Grass
    public static final RegistryEntry<Item> GRASS_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/grass/1", () -> new BlockItem(ModBlocks.GRASS_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_GRASS_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/grass/2", () -> new BlockItem(ModBlocks.T1_GRASS_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_GRASS_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/grass/3", () -> new BlockItem(ModBlocks.T2_GRASS_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_GRASS_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/grass/4", () -> new BlockItem(ModBlocks.T3_GRASS_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Jungle
    public static final RegistryEntry<Item> JUNGLE_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/jungle/1", () -> new BlockItem(ModBlocks.JUNGLE_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_JUNGLE_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/jungle/2", () -> new BlockItem(ModBlocks.T1_JUNGLE_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_JUNGLE_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/jungle/3", () -> new BlockItem(ModBlocks.T2_JUNGLE_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_JUNGLE_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/jungle/4", () -> new BlockItem(ModBlocks.T3_JUNGLE_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Nether
    public static final RegistryEntry<Item> NETHER_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/netherrack/1", () -> new BlockItem(ModBlocks.NETHER_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_NETHER_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/netherrack/2", () -> new BlockItem(ModBlocks.T1_NETHER_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_NETHER_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/netherrack/3", () -> new BlockItem(ModBlocks.T2_NETHER_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_NETHER_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/netherrack/4", () -> new BlockItem(ModBlocks.T3_NETHER_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Oak
    public static final RegistryEntry<Item> OAK_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/oak/1", () -> new BlockItem(ModBlocks.OAK_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_OAK_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/oak/2", () -> new BlockItem(ModBlocks.T1_OAK_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_OAK_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/oak/3", () -> new BlockItem(ModBlocks.T2_OAK_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_OAK_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/oak/4", () -> new BlockItem(ModBlocks.T3_OAK_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Prismarine
    public static final RegistryEntry<Item> PRISMARINE_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/prismarine/1", () -> new BlockItem(ModBlocks.PRISMARINE_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_PRISMARINE_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/prismarine/2", () -> new BlockItem(ModBlocks.T1_PRISMARINE_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_PRISMARINE_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/prismarine/3", () -> new BlockItem(ModBlocks.T2_PRISMARINE_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_PRISMARINE_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/prismarine/4", () -> new BlockItem(ModBlocks.T3_PRISMARINE_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Purpur
    public static final RegistryEntry<Item> PURPUR_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/chorus/1", () -> new BlockItem(ModBlocks.PURPUR_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_PURPUR_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/chorus/2", () -> new BlockItem(ModBlocks.T1_PURPUR_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_PURPUR_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/chorus/3", () -> new BlockItem(ModBlocks.T2_PURPUR_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_PURPUR_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/chorus/4", () -> new BlockItem(ModBlocks.T3_PURPUR_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Red Mushroom
    public static final RegistryEntry<Item> RED_MUSHROOM_NEST_ITEM = T1_NEST_ITEMS.register("nest/red_mushroom/1", () -> new BlockItem(ModBlocks.RED_MUSHROOM_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_RED_MUSHROOM_NEST_ITEM = T2_NEST_ITEMS.register("nest/red_mushroom/2", () -> new BlockItem(ModBlocks.T1_RED_MUSHROOM_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_RED_MUSHROOM_NEST_ITEM = T3_NEST_ITEMS.register("nest/red_mushroom/3", () -> new BlockItem(ModBlocks.T2_RED_MUSHROOM_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_RED_MUSHROOM_NEST_ITEM = T4_NEST_ITEMS.register("nest/red_mushroom/4", () -> new BlockItem(ModBlocks.T3_RED_MUSHROOM_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Spruce
    public static final RegistryEntry<Item> SPRUCE_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/spruce/1", () -> new BlockItem(ModBlocks.SPRUCE_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_SPRUCE_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/spruce/2", () -> new BlockItem(ModBlocks.T1_SPRUCE_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_SPRUCE_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/spruce/3", () -> new BlockItem(ModBlocks.T2_SPRUCE_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_SPRUCE_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/spruce/4", () -> new BlockItem(ModBlocks.T3_SPRUCE_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Warped
    public static final RegistryEntry<Item> WARPED_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/warped/1", () -> new BlockItem(ModBlocks.WARPED_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_WARPED_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/warped/2", () -> new BlockItem(ModBlocks.T1_WARPED_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_WARPED_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/warped/3", () -> new BlockItem(ModBlocks.T2_WARPED_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_WARPED_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/warped/4", () -> new BlockItem(ModBlocks.T3_WARPED_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Warped Nylium
    public static final RegistryEntry<Item> WARPED_NYLIUM_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/warped_nylium/1", () -> new BlockItem(ModBlocks.WARPED_NYLIUM_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_WARPED_NYLIUM_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/warped_nylium/2", () -> new BlockItem(ModBlocks.T1_WARPED_NYLIUM_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_WARPED_NYLIUM_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/warped_nylium/3", () -> new BlockItem(ModBlocks.T2_WARPED_NYLIUM_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_WARPED_NYLIUM_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/warped_nylium/4", () -> new BlockItem(ModBlocks.T3_WARPED_NYLIUM_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //region Wither
    public static final RegistryEntry<Item> WITHER_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/wither/1", () -> new BlockItem(ModBlocks.WITHER_BEE_NEST.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_WITHER_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/wither/2", () -> new BlockItem(ModBlocks.T1_WITHER_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_WITHER_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/wither/3", () -> new BlockItem(ModBlocks.T2_WITHER_BEEHIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_WITHER_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/wither/4", () -> new BlockItem(ModBlocks.T3_WITHER_BEEHIVE.get(), new Item.Properties()));
    //endregion
    //endregion

    public static final RegistryEntry<Item> BEE_JAR = ITEMS.register("bee_jar", () -> new BeeJar(new Item.Properties().durability(0).stacksTo(16)));

    public static final RegistryEntry<Item> OREO_COOKIE = ITEMS.register("oreo_cookie", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 1), 1)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 2400, 1), 1)
            .effect(() -> new MobEffectInstance(MobEffects.LUCK, 600, 3), 1)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), 1)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1)
            .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 6000, 0), 1)
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0), 1)
            .nutrition(8)
            .saturationMod(2)
            .alwaysEat()
            .build())
            .rarity(Rarity.EPIC))
    );


    public static final RegistryEntry<Item> BEE_BOX_TEMP = ITEMS.register("bee_box_temp", () -> BeeBoxItem.temp(ModBlocks.BEE_BOX_TEMP.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> BEE_BOX = ITEMS.register("bee_box", () -> BeeBoxItem.of(ModBlocks.BEE_BOX.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> BEEPEDIA = ITEMS.register("beepedia", () -> new Beepedia(new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> HONEY_DIPPER = ITEMS.register("honey_dipper", () -> new HoneyDipper(new Item.Properties().stacksTo(1)));

    public static final RegistryEntry<Item> SCRAPER = ITEMS.register("scraper", () -> new ScraperItem(new Item.Properties().stacksTo(1)));

    public static final RegistryEntry<Item> SMOKER = ITEMS.register("smoker", () -> new Smoker(new Item.Properties().setNoRepair().durability(GeneralConfig.smokerDurability)));
    public static final RegistryEntry<Item> BELLOW = ITEMS.register("bellow", () -> new Item(new Item.Properties()));
    public static final RegistryEntry<Item> SMOKERCAN = ITEMS.register("smoker_can", () -> new Item(new Item.Properties()));

    public static final RegistryEntry<Item> WAX = ITEMS.register("wax", () -> new WaxItem(new Item.Properties()) {
        @Override
        public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
            return 400;
        }
    });

    public static final RegistryEntry<Item> HONEY_GENERATOR_ITEM = ITEMS.register("honey_generator", () -> new BlockItem(ModBlocks.HONEY_GENERATOR.get(), new Item.Properties()));

    public static final RegistryEntry<Item> WAX_BLOCK_ITEM = ITEMS.register("wax_block", () -> new BlockItem(ModBlocks.WAX_BLOCK.get(), new Item.Properties()) {
        @Override
        public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
            return 4000;
        }
    });

    public static final RegistryEntry<Item> GOLD_FLOWER_ITEM = ITEMS.register("gold_flower", () -> new BlockItem(ModBlocks.GOLD_FLOWER.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T1_APIARY_ITEM = NEST_ITEMS.register("t1_apiary", () -> new BlockItem(ModBlocks.T1_APIARY_BLOCK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T2_APIARY_ITEM = NEST_ITEMS.register("t2_apiary", () -> new BlockItem(ModBlocks.T2_APIARY_BLOCK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T3_APIARY_ITEM = NEST_ITEMS.register("t3_apiary", () -> new BlockItem(ModBlocks.T3_APIARY_BLOCK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> T4_APIARY_ITEM = NEST_ITEMS.register("t4_apiary", () -> new BlockItem(ModBlocks.T4_APIARY_BLOCK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> BREEDER_ITEM = NEST_ITEMS.register("breeder", () -> new BlockItem(ModBlocks.BREEDER_BLOCK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> FLOW_HIVE = NEST_ITEMS.register("flow_hive", () -> new BlockItem(ModBlocks.FLOW_HIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> ENDER_BEECON_ITEM = ITEMS.register("ender_beecon", () -> new BlockItem(ModBlocks.ENDER_BEECON.get(), new Item.Properties()));
    public static final RegistryEntry<Item> SOLIDIFICATION_CHAMBER_ITEM = ITEMS.register("solidification_chamber", () -> new BlockItem(ModBlocks.SOLIDIFICATION_CHAMBER.get(), new Item.Properties()));
    public static final RegistryEntry<Item> HONEY_POT_ITEM = ITEMS.register("honey_pot", () -> new BlockItem(ModBlocks.HONEY_POT.get(), new Item.Properties()));

    public static final RegistryEntry<Item> BREED_TIME_UPGRADE = ITEMS.register("breed_time_upgrade", () ->
            new BreederTimeUpgradeItem(new Item.Properties().stacksTo(16)));

    public static final RegistryEntry<Item> HONEY_FLUID_BUCKET = ITEMS.register("honey_bucket", () -> new BucketItem(ModFluids.HONEY_STILL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryEntry<Item> T2_NEST_UPGRADE = ITEMS.register("t2_nest_upgrade", () -> new NestUpgradeItem(BeehiveUpgrade.T1_TO_T2, new Item.Properties().stacksTo(16)));
    public static final RegistryEntry<Item> T3_NEST_UPGRADE = ITEMS.register("t3_nest_upgrade", () -> new NestUpgradeItem(BeehiveUpgrade.T2_TO_T3, new Item.Properties().stacksTo(16)));
    public static final RegistryEntry<Item> T4_NEST_UPGRADE = ITEMS.register("t4_nest_upgrade", () -> new NestUpgradeItem(BeehiveUpgrade.T3_TO_T4, new Item.Properties().stacksTo(16)));
    public static final RegistryEntry<Item> BEE_LOCATOR = ITEMS.register("bee_locator", () -> new BeeLocator(new Item.Properties().stacksTo(1)));

    //region centrifuge items
    public static final RegistryEntry<Item> CENTRIFUGE_CASING = CENTRIFUGE_ITEMS.register("centrifuge/casing", () -> new BlockItem(ModBlocks.CENTRIFUGE_CASING.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_PROCESSOR = CENTRIFUGE_ITEMS.register("centrifuge/processor", () -> new BlockItem(ModBlocks.CENTRIFUGE_PROCESSOR.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_GEARBOX = CENTRIFUGE_ITEMS.register("centrifuge/gearbox", () -> new BlockItem(ModBlocks.CENTRIFUGE_GEARBOX.get(), new Item.Properties()));

    //TERMINAL
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_TERMINAL.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_TERMINAL.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_TERMINAL.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_TERMINAL.get(), new Item.Properties()));

    //VOID
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_VOID.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_VOID.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_VOID.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_VOID.get(), new Item.Properties()));

    //INPUT
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_INPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_INPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_INPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_INPUT.get(), new Item.Properties()));

    //ENERGY PORT
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_ENERGY_PORT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_ENERGY_PORT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get(), new Item.Properties()));

    //ITEM OUTPUT
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get(), new Item.Properties()));

    //FLUID OUTPUT
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get(), new Item.Properties()));
    //endregion

    public static final RegistryEntry<Item> CENTRIFUGE_CRANK = ITEMS.register("centrifuge_crank", () -> new CrankItem(ModBlocks.CENTRIFUGE_CRANK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE = ITEMS.register("centrifuge", () -> new ManualCentrifugeItem(ModBlocks.BASIC_CENTRIFUGE.get(), new Item.Properties()));

    //region Waxed Blocks
    public static final RegistryEntry<Item> HONEY_GLASS = ITEMS.register("honey_glass", () -> new BlockItem(ModBlocks.HONEY_GLASS.get(), new Item.Properties()));
    public static final RegistryEntry<Item> HONEY_GLASS_PLAYER = ITEMS.register("honey_glass_player", () -> new BlockItem(ModBlocks.HONEY_GLASS_PLAYER.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_PLANKS = ITEMS.register("waxed_planks", () -> new BlockItem(ModBlocks.WAXED_PLANKS.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_STAIRS = ITEMS.register("waxed_stairs", () -> new BlockItem(ModBlocks.WAXED_STAIRS.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_SLAB = ITEMS.register("waxed_slab", () -> new BlockItem(ModBlocks.WAXED_SLAB.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_FENCE = ITEMS.register("waxed_fence", () -> new BlockItem(ModBlocks.WAXED_FENCE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_FENCE_GATE = ITEMS.register("waxed_fence_gate", () -> new BlockItem(ModBlocks.WAXED_FENCE_GATE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_BUTTON = ITEMS.register("waxed_button", () -> new BlockItem(ModBlocks.WAXED_BUTTON.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_PRESSURE_PLATE = ITEMS.register("waxed_pressure_plate", () -> new BlockItem(ModBlocks.WAXED_PRESSURE_PLATE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_DOOR = ITEMS.register("waxed_door", () -> new BlockItem(ModBlocks.WAXED_DOOR.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_TRAPDOOR = ITEMS.register("waxed_trapdoor", () -> new BlockItem(ModBlocks.WAXED_TRAPDOOR.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_SIGN = ITEMS.register("waxed_sign", () -> new SignItem(new Item.Properties(), ModBlocks.WAXED_SIGN.get(), ModBlocks.WAXED_WALL_SIGN.get()));
    public static final RegistryEntry<Item> TRIMMED_WAXED_PLANKS = ITEMS.register("trimmed_waxed_planks", () -> new BlockItem(ModBlocks.TRIMMED_WAXED_PLANKS.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_MACHINE_BLOCK = ITEMS.register("waxed_machine_block", () -> new BlockItem(ModBlocks.WAXED_MACHINE_BLOCK.get(), new Item.Properties()));
    //endregion
}
