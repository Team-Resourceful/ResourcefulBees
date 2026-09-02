package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.blocks.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.components.BeehiveUpgrade;
import com.teamresourceful.resourcefulbees.common.components.DipperEntity;
import com.teamresourceful.resourcefulbees.common.components.Upgrade;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.config.HoneyGenConfig;
import com.teamresourceful.resourcefulbees.common.items.*;
import com.teamresourceful.resourcefulbees.common.items.locator.BeeLocatorItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.HolderRegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.builtin.ResourcefulItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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

    private static HolderRegistryEntry<Item> registerBasicBlockItem(ResourcefulItemRegistry registry, String id, RegistryEntry<Block> block, Supplier<Item.Properties> getter) {
        return registerItem(registry, id, properties -> new BlockItem(block.get(), properties), getter);
    }

    private static HolderRegistryEntry<Item> registerItem(ResourcefulItemRegistry registry, String id, Function<Item.Properties, Item> factory, Supplier<Item.Properties> getter) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ModIdentifier.of(id));
        return registry.registerHolder(id, () -> factory.apply(getter.get().setId(key)));
    }

    public static void registerHiveItem(ResourcefulItemRegistry registry, String id, RegistryEntry<Block> block) {
        registerBasicBlockItem(registry, id, block, Item.Properties::new);
    }

    public static final HolderRegistryEntry<Item> T1_APIARY_ITEM = registerItem(ITEMS, "t1_apiary", properties -> new ApiaryBlockItem((ApiaryBlock) ModBlocks.T1_APIARY_BLOCK.get(), properties), Item.Properties::new);
    public static final HolderRegistryEntry<Item> T2_APIARY_ITEM = registerItem(ITEMS,"t2_apiary", properties -> new ApiaryBlockItem((ApiaryBlock) ModBlocks.T2_APIARY_BLOCK.get(), properties), Item.Properties::new);
    public static final HolderRegistryEntry<Item> T3_APIARY_ITEM = registerItem(ITEMS,"t3_apiary", properties -> new ApiaryBlockItem((ApiaryBlock) ModBlocks.T3_APIARY_BLOCK.get(), properties), Item.Properties::new);
    public static final HolderRegistryEntry<Item> T4_APIARY_ITEM = registerItem(ITEMS,"t4_apiary", properties -> new ApiaryBlockItem((ApiaryBlock) ModBlocks.T4_APIARY_BLOCK.get(), properties), Item.Properties::new);


    public static final HolderRegistryEntry<Item> WAX = registerItem(ITEMS, "wax", WaxItem::new, Item.Properties::new);
public static final HolderRegistryEntry<Item> WAX_BLOCK_ITEM = registerBasicBlockItem(ITEMS, "wax_block", ModBlocks.WAX_BLOCK, Item.Properties::new);

    public static final HolderRegistryEntry<Item> SCRAPER = registerItem(ITEMS,"scraper", ScraperItem::new, () -> new Item.Properties().stacksTo(1));

    public static final HolderRegistryEntry<Item> SMOKER = registerItem(ITEMS, "smoker", SmokerItem::new, () -> new Item.Properties().durability(GeneralConfig.smokerDurability));
    public static final HolderRegistryEntry<Item> BELLOW = registerItem(ITEMS, "bellow", Item::new, Item.Properties::new);
    public static final HolderRegistryEntry<Item> SMOKER_CAN = registerItem(ITEMS, "smoker_can", Item::new, Item.Properties::new);

    public static final HolderRegistryEntry<Item> BEE_BOX_TEMP = registerItem(ITEMS, "bee_box_temp", properties -> BeeBoxItem.temp(ModBlocks.BEE_BOX_TEMP.get(), properties), () -> new Item.Properties().stacksTo(1));
    public static final HolderRegistryEntry<Item> BEE_BOX = registerItem(ITEMS, "bee_box", properties -> BeeBoxItem.of(ModBlocks.BEE_BOX.get(), properties), () -> new Item.Properties().stacksTo(1));
    //public static final HolderRegistryEntry<Item> BEEPEDIA = registerItem(ITEMS, "beepedia", () -> new BeepediaItem(new Item.Properties().stacksTo(1)));
    public static final HolderRegistryEntry<Item> HONEY_DIPPER = registerItem(ITEMS, "honey_dipper", HoneyDipperItem::new, () -> new Item.Properties().stacksTo(1).component(ModDataComponents.DIPPER_ENTITY, DipperEntity.EMPTY));

    public static final HolderRegistryEntry<Item> BEE_JAR = registerItem(ITEMS, "bee_jar", BeeJarItem::new, () -> new Item.Properties().stacksTo(16));
//    public static final HolderRegistryEntry<Item> POLLEN_SPREADER_FAN = registerItem(ITEMS, "pollen_spreader_fan", () -> new BlockItem(ModBlocks.POLLEN_SPREADER_FAN.get(), new Item.Properties()));
//    public static final HolderRegistryEntry<Item> POLLEN_SPREADER = registerItem(ITEMS, "pollen_spreader", () -> new BlockItem(ModBlocks.POLLEN_SPREADER.get(), new Item.Properties()));
    //public static final HolderRegistryEntry<Item> MUTATED_POLLEN = registerItem(ITEMS, "mutated_pollen", () -> new MutatedPollenItem(new Item.Properties()));
//    public static final HolderRegistryEntry<Item> FAKE_FLOWER = registerItem(ITEMS, "fake_flower", () -> new BlockItem(ModBlocks.FAKE_FLOWER.get(), new Item.Properties()));

    public static final HolderRegistryEntry<Item> GOLD_FLOWER_ITEM = registerBasicBlockItem(ITEMS, "gold_flower", ModBlocks.GOLD_FLOWER, Item.Properties::new);

    public static final HolderRegistryEntry<Item> BREEDER_ITEM = registerBasicBlockItem(ITEMS, "breeder", ModBlocks.BREEDER_BLOCK, Item.Properties::new);

    public static final HolderRegistryEntry<Item> T2_NEST_UPGRADE = registerItem(ITEMS, "t2_nest_upgrade", Item::new, () -> new Item.Properties().stacksTo(16).component(ModDataComponents.BEEHIVE_UPGRADE, BeehiveUpgrade.create(BeehiveUpgrade.Tier.T1_TO_T2)));
    public static final HolderRegistryEntry<Item> T3_NEST_UPGRADE = registerItem(ITEMS, "t3_nest_upgrade", Item::new, () -> new Item.Properties().stacksTo(16).component(ModDataComponents.BEEHIVE_UPGRADE, BeehiveUpgrade.create(BeehiveUpgrade.Tier.T2_TO_T3)));
    public static final HolderRegistryEntry<Item> T4_NEST_UPGRADE = registerItem(ITEMS, "t4_nest_upgrade", Item::new, () -> new Item.Properties().stacksTo(16).component(ModDataComponents.BEEHIVE_UPGRADE, BeehiveUpgrade.create(BeehiveUpgrade.Tier.T3_TO_T4)));
    public static final HolderRegistryEntry<Item> BREED_TIME_UPGRADE = registerItem(ITEMS, "breed_time_upgrade", Item::new, () -> new Item.Properties().stacksTo(8).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.BREED_TIME)));

    public static final HolderRegistryEntry<Item> BEE_LOCATOR = registerItem(ITEMS, "bee_locator", BeeLocatorItem::new, () -> new Item.Properties().stacksTo(1));

    //region Waxed Blocks
    public static final HolderRegistryEntry<Item> HONEY_GLASS = registerBasicBlockItem(ITEMS, "honey_glass", ModBlocks.HONEY_GLASS, Item.Properties::new);
    public static final HolderRegistryEntry<Item> HONEY_GLASS_PLAYER = registerBasicBlockItem(ITEMS, "honey_glass_player", ModBlocks.HONEY_GLASS_PLAYER, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_PLANKS = registerBasicBlockItem(ITEMS, "waxed_planks", ModBlocks.WAXED_PLANKS, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_STAIRS = registerBasicBlockItem(ITEMS, "waxed_stairs", ModBlocks.WAXED_STAIRS, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_SLAB = registerBasicBlockItem(ITEMS, "waxed_slab", ModBlocks.WAXED_SLAB, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_FENCE = registerBasicBlockItem(ITEMS, "waxed_fence", ModBlocks.WAXED_FENCE, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_FENCE_GATE = registerBasicBlockItem(ITEMS, "waxed_fence_gate", ModBlocks.WAXED_FENCE_GATE, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_BUTTON = registerBasicBlockItem(ITEMS, "waxed_button", ModBlocks.WAXED_BUTTON, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_PRESSURE_PLATE = registerBasicBlockItem(ITEMS, "waxed_pressure_plate", ModBlocks.WAXED_PRESSURE_PLATE, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_DOOR = registerBasicBlockItem(ITEMS, "waxed_door", ModBlocks.WAXED_DOOR, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_TRAPDOOR = registerBasicBlockItem(ITEMS, "waxed_trapdoor", ModBlocks.WAXED_TRAPDOOR, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_SIGN = registerItem(ITEMS, "waxed_sign", properties -> new SignItem(ModBlocks.WAXED_SIGN.get(), ModBlocks.WAXED_WALL_SIGN.get(), properties), Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_HANGING_SIGN = registerItem(ITEMS, "waxed_hanging_sign", properties -> new HangingSignItem(ModBlocks.WAXED_HANGING_SIGN.get(), ModBlocks.WAXED_WALL_HANGING_SIGN.get(), properties), Item.Properties::new);
    public static final HolderRegistryEntry<Item> TRIMMED_WAXED_PLANKS = registerBasicBlockItem(ITEMS, "trimmed_waxed_planks", ModBlocks.TRIMMED_WAXED_PLANKS, Item.Properties::new);
    public static final HolderRegistryEntry<Item> WAXED_MACHINE_BLOCK = registerBasicBlockItem(ITEMS, "waxed_machine_block", ModBlocks.WAXED_MACHINE_BLOCK, Item.Properties::new);
    public static final HolderRegistryEntry<Item> HONEY_CAP_UPGRADE = ModItems.registerItem(ITEMS, "honey_cap_upgrade", Item::new, () -> new Item.Properties().stacksTo(HoneyGenConfig.upgradeStackLimit).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.HONEY_CAPACITY)));
    public static final HolderRegistryEntry<Item> ENERGY_CAP_UPGRADE = ModItems.registerItem(ITEMS, "energy_cap_upgrade", Item::new, () -> new Item.Properties().stacksTo(HoneyGenConfig.upgradeStackLimit).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.ENERGY_CAPACITY)));
    public static final HolderRegistryEntry<Item> ENERGY_XFER_UPGRADE = ModItems.registerItem(ITEMS, "energy_xfer_upgrade", Item::new, () -> new Item.Properties().stacksTo(HoneyGenConfig.upgradeStackLimit).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.ENERGY_TRANSFER)));
    public static final HolderRegistryEntry<Item> ENERGY_FILL_UPGRADE = ModItems.registerItem(ITEMS, "energy_fill_upgrade", Item::new, () -> new Item.Properties().stacksTo(HoneyGenConfig.upgradeStackLimit).component(ModDataComponents.UPGRADE, Upgrade.create(Upgrade.Type.ENERGY_FILL)));
    //endregion

    //region Machines

    public static final HolderRegistryEntry<Item> FLOW_HIVE = registerBasicBlockItem(ITEMS, "flow_hive", ModBlocks.FLOW_HIVE, Item.Properties::new);
    public static final HolderRegistryEntry<Item> ENDER_BEECON_ITEM = registerBasicBlockItem(ITEMS, "ender_beecon", ModBlocks.ENDER_BEECON, Item.Properties::new);
    public static final HolderRegistryEntry<Item> HONEY_POT_ITEM = registerBasicBlockItem(ITEMS, "honey_pot", ModBlocks.HONEY_POT, Item.Properties::new);
    public static final HolderRegistryEntry<Item> SOLIDIFICATION_CHAMBER_ITEM = registerBasicBlockItem(ITEMS, "solidification_chamber", ModBlocks.SOLIDIFICATION_CHAMBER, Item.Properties::new);
    public static final HolderRegistryEntry<Item> CENTRIFUGE_CRANK = registerItem(ITEMS, "centrifuge_crank", properties -> new CrankItem(ModBlocks.CENTRIFUGE_CRANK.get(), properties), Item.Properties::new);
    public static final HolderRegistryEntry<Item> CENTRIFUGE = registerItem(ITEMS, "centrifuge", properties -> new CentrifugeItem(ModBlocks.BASIC_CENTRIFUGE.get(), properties), Item.Properties::new);
    public static final HolderRegistryEntry<Item> HONEY_GENERATOR_ITEM = registerBasicBlockItem(ITEMS, "honey_generator", ModBlocks.HONEY_GENERATOR, Item.Properties::new);
    //endregion

    //todo needs texture
    public static final HolderRegistryEntry<Item> HONEY_BUCKET = registerItem(HONEY_BUCKET_ITEMS, "honey_bucket", properties -> new BucketItem(ModFluids.HONEY_FLUID_TYPE.get().still().get(), properties), () -> new Item.Properties().stacksTo(1));

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

    public static final ResourceKey<Item> OREO_KEY = ResourceKey.create(Registries.ITEM, ModIdentifier.of("oreo_cookie"));

    public static final HolderRegistryEntry<Item> OREO_COOKIE = ITEMS.registerHolder("oreo_cookie",() -> new Item(new Item.Properties().rarity(Rarity.EPIC).food(OREO_FOOD_PROPERTIES, OREO_CONSUMABLE).setId(OREO_KEY)));

    public static final FoodProperties STRAWBEERRY_MILKSHAKE_PROPERTIES = new FoodProperties(6, 1.5f, true);

    public static final Consumable STRAWBEERRY_MILKSHAKE_CONSUMABLE = Consumable.builder().onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
            new MobEffectInstance(MobEffects.REGENERATION, 1200, 1),
            new MobEffectInstance(MobEffects.LUCK, 1200, 1),
            new MobEffectInstance(MobEffects.JUMP_BOOST, 1200, 1)
    ))).build();

    public static final ResourceKey<Item> STRAWBEERRY_MILKSHAKE_KEY = ResourceKey.create(Registries.ITEM, ModIdentifier.of("strawbeerry_milkshake"));

    public static final HolderRegistryEntry<Item> STRAWBEERRY_MILKSHAKE = ITEMS.registerHolder("strawbeerry_milkshake", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).food(STRAWBEERRY_MILKSHAKE_PROPERTIES, STRAWBEERRY_MILKSHAKE_CONSUMABLE).setId(STRAWBEERRY_MILKSHAKE_KEY)));
}
