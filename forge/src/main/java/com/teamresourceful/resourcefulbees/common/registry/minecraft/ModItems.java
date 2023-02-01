package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.item.HoneyDipperItem;
import com.teamresourceful.resourcefulbees.common.item.MutatedPollenItem;
import com.teamresourceful.resourcefulbees.common.item.centrifuge.CrankItem;
import com.teamresourceful.resourcefulbees.common.item.centrifuge.ManualCentrifugeItem;
import com.teamresourceful.resourcefulbees.common.items.*;
import com.teamresourceful.resourcefulbees.common.items.locator.BeeLocatorItem;
import com.teamresourceful.resourcefulbees.common.items.upgrade.BreederTimeUpgradeItem;
import com.teamresourceful.resourcefulbees.common.items.upgrade.nestupgrade.BeehiveUpgrade;
import com.teamresourceful.resourcefulbees.common.items.upgrade.nestupgrade.NestUpgradeItem;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
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

    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.ITEMS);

    public static final ResourcefulRegistry<Item> NEST_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> SPAWN_EGG_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEYCOMB_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEYCOMB_BLOCK_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEY_BOTTLE_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEY_BLOCK_ITEMS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> HONEY_BUCKET_ITEMS = ResourcefulRegistries.create(ITEMS);

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


    public static final RegistryEntry<Item> BEE_BOX_TEMP = ITEMS.register("bee_box_temp", () -> BeeBoxItem.temp(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.BEE_BOX_TEMP.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> BEE_BOX = ITEMS.register("bee_box", () -> BeeBoxItem.of(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.BEE_BOX.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> BEEPEDIA = ITEMS.register("beepedia", () -> new BeepediaItem(new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> HONEY_DIPPER = ITEMS.register("honey_dipper", () -> new HoneyDipperItem(new Item.Properties().stacksTo(1)));

    public static final RegistryEntry<Item> SCRAPER = ITEMS.register("scraper", () -> new ScraperItem(new Item.Properties().stacksTo(1)));

    public static final RegistryEntry<Item> SMOKER = ITEMS.register("smoker", () -> new SmokerItem(new Item.Properties().setNoRepair().durability(GeneralConfig.smokerDurability)));
    public static final RegistryEntry<Item> BELLOW = ITEMS.register("bellow", () -> new Item(new Item.Properties()));
    public static final RegistryEntry<Item> SMOKERCAN = ITEMS.register("smoker_can", () -> new Item(new Item.Properties()));

    public static final RegistryEntry<Item> WAX = ITEMS.register("wax", () -> new WaxItem(new Item.Properties()) {
        @Override
        public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
            return 400;
        }
    });

    public static final RegistryEntry<Item> MUTATED_POLLEN = ITEMS.register("mutated_pollen", () -> new MutatedPollenItem(new Item.Properties()));

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

    public static final RegistryEntry<Item> HONEY_FLUID_BUCKET = HONEY_BUCKET_ITEMS.register("honey_bucket", () -> new BucketItem(ModFluids.HONEY_STILL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryEntry<Item> T2_NEST_UPGRADE = ITEMS.register("t2_nest_upgrade", () -> new NestUpgradeItem(BeehiveUpgrade.T1_TO_T2, new Item.Properties().stacksTo(16)));
    public static final RegistryEntry<Item> T3_NEST_UPGRADE = ITEMS.register("t3_nest_upgrade", () -> new NestUpgradeItem(BeehiveUpgrade.T2_TO_T3, new Item.Properties().stacksTo(16)));
    public static final RegistryEntry<Item> T4_NEST_UPGRADE = ITEMS.register("t4_nest_upgrade", () -> new NestUpgradeItem(BeehiveUpgrade.T3_TO_T4, new Item.Properties().stacksTo(16)));
    public static final RegistryEntry<Item> BEE_LOCATOR = ITEMS.register("bee_locator", () -> new BeeLocatorItem(new Item.Properties().stacksTo(1)));

    public static final RegistryEntry<Item> CENTRIFUGE_CRANK = ITEMS.register("centrifuge_crank", () -> new CrankItem(ModBlocks.CENTRIFUGE_CRANK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE = ITEMS.register("centrifuge", () -> new ManualCentrifugeItem(ModBlocks.BASIC_CENTRIFUGE.get(), new Item.Properties()));

    //region Waxed Blocks
    public static final RegistryEntry<Item> HONEY_GLASS = ITEMS.register("honey_glass", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.HONEY_GLASS.get(), new Item.Properties()));
    public static final RegistryEntry<Item> HONEY_GLASS_PLAYER = ITEMS.register("honey_glass_player", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.HONEY_GLASS_PLAYER.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_PLANKS = ITEMS.register("waxed_planks", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_PLANKS.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_STAIRS = ITEMS.register("waxed_stairs", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_STAIRS.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_SLAB = ITEMS.register("waxed_slab", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_SLAB.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_FENCE = ITEMS.register("waxed_fence", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_FENCE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_FENCE_GATE = ITEMS.register("waxed_fence_gate", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_FENCE_GATE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_BUTTON = ITEMS.register("waxed_button", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_BUTTON.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_PRESSURE_PLATE = ITEMS.register("waxed_pressure_plate", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_PRESSURE_PLATE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_DOOR = ITEMS.register("waxed_door", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_DOOR.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_TRAPDOOR = ITEMS.register("waxed_trapdoor", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_TRAPDOOR.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_SIGN = ITEMS.register("waxed_sign", () -> new SignItem(new Item.Properties(), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_SIGN.get(), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_WALL_SIGN.get()));
    public static final RegistryEntry<Item> TRIMMED_WAXED_PLANKS = ITEMS.register("trimmed_waxed_planks", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.TRIMMED_WAXED_PLANKS.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WAXED_MACHINE_BLOCK = ITEMS.register("waxed_machine_block", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_MACHINE_BLOCK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> FAKE_FLOWER = ITEMS.register("fake_flower", () -> new BlockItem(ModBlocks.FAKE_FLOWER.get(), new Item.Properties()));
    //endregion
}
