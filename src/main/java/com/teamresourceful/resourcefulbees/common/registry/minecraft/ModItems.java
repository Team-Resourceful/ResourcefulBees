package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.item.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.utils.TooltipBuilder;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class ModItems {

    private ModItems() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);

    public static final DeferredRegister<Item> NESTS_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);

    public static final DeferredRegister<Item> SPAWN_EGG_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<Item> HONEYCOMB_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<Item> HONEYCOMB_BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);

    public static final DeferredRegister<Item> HONEY_BOTTLE_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<Item> HONEY_BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<Item> HONEY_BUCKET_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);



    private static Item.Properties getItemProperties() {
        return new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES);
    }

    private static Item.Properties getNestProperties() {
        return new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HIVES);
    }

    public static void initializeRegistries(IEventBus bus) {
        ITEMS.register(bus);
        NESTS_ITEMS.register(bus);
        SPAWN_EGG_ITEMS.register(bus);
        HONEYCOMB_ITEMS.register(bus);
        HONEYCOMB_BLOCK_ITEMS.register(bus);
        HONEY_BOTTLE_ITEMS.register(bus);
        HONEY_BLOCK_ITEMS.register(bus);
        HONEY_BUCKET_ITEMS.register(bus);
    }

    public static final RegistryObject<Item> OREO_COOKIE = ITEMS.register("oreo_cookie", () -> new Item(getItemProperties().food(new Food.Builder()
            .effect(() -> new EffectInstance(Effects.REGENERATION, 600, 1), 1)
            .effect(() -> new EffectInstance(Effects.ABSORPTION, 2400, 3), 1)
            .effect(() -> new EffectInstance(Effects.SATURATION, 2400, 1), 1)
            .effect(() -> new EffectInstance(Effects.LUCK, 600, 3), 1)
            .effect(() -> new EffectInstance(Effects.FIRE_RESISTANCE, 6000, 0), 1)
            .effect(() -> new EffectInstance(Effects.DAMAGE_RESISTANCE, 6000, 0), 1)
            .effect(() -> new EffectInstance(Effects.WATER_BREATHING, 6000, 0), 1)
            .effect(() -> new EffectInstance(Effects.NIGHT_VISION, 1200, 0), 1)
            .nutrition(8)
            .saturationMod(2)
            .alwaysEat()
            .build())
            .rarity(Rarity.EPIC)));


    public static final RegistryObject<Item> CRAFTING_BEE_BOX = ITEMS.register("crafting_bee_box", () -> new BeeBox(getItemProperties().stacksTo(1), true));
    public static final RegistryObject<Item> BEE_BOX = ITEMS.register("bee_box", () -> new BeeBox(getItemProperties().stacksTo(1), false));
    public static final RegistryObject<Item> BEEPEDIA = ITEMS.register("beepedia", () -> new Beepedia(getItemProperties().stacksTo(1)));
    public static final RegistryObject<Item> HONEY_DIPPER = ITEMS.register("honey_dipper", () -> new HoneyDipper(getItemProperties().stacksTo(1)));

    public static final RegistryObject<Item> SCRAPER = ITEMS.register("scraper", () -> new Item(getItemProperties().stacksTo(1)) {

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
            tooltip.add(new TranslationTextComponent("item.resourcefulbees.scraper.tooltip").withStyle(TextFormatting.GOLD));
            tooltip.add(new TranslationTextComponent("item.resourcefulbees.scraper.tooltip.1").withStyle(TextFormatting.GOLD));
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    });

    public static final RegistryObject<Item> SMOKER = ITEMS.register("smoker", () -> new Smoker(getItemProperties().setNoRepair().durability(CommonConfig.SMOKER_DURABILITY.get())));
    public static final RegistryObject<Item> BELLOW = ITEMS.register("bellow", () -> new Item(getItemProperties()));
    public static final RegistryObject<Item> SMOKERCAN = ITEMS.register("smoker_can", () -> new Item(getItemProperties()));

    public static final RegistryObject<Item> WAX = ITEMS.register("wax", () -> new Item(getItemProperties()) {
        @Override
        public int getBurnTime(ItemStack itemStack, IRecipeType<?> recipeType) {
            return 400;
        }
    });

    public static final RegistryObject<Item> CENTRIFUGE_ITEM = ITEMS.register("centrifuge", () -> new BlockItem(ModBlocks.CENTRIFUGE.get(), getItemProperties()));
    public static final RegistryObject<Item> MECHANICAL_CENTRIFUGE_ITEM = ITEMS.register("mechanical_centrifuge", () -> new BlockItem(ModBlocks.MECHANICAL_CENTRIFUGE.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_CONTROLLER_ITEM = ITEMS.register("centrifuge_controller", () -> new BlockItem(ModBlocks.CENTRIFUGE_CONTROLLER.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_CASING_ITEM = ITEMS.register("centrifuge_casing", () -> new BlockItem(ModBlocks.CENTRIFUGE_CASING.get(), getItemProperties()));
    public static final RegistryObject<Item> ELITE_CENTRIFUGE_CONTROLLER_ITEM = ITEMS.register("elite_centrifuge_controller", () -> new BlockItem(ModBlocks.ELITE_CENTRIFUGE_CONTROLLER.get(), getItemProperties()));
    public static final RegistryObject<Item> ELITE_CENTRIFUGE_CASING_ITEM = ITEMS.register("elite_centrifuge_casing", () -> new BlockItem(ModBlocks.ELITE_CENTRIFUGE_CASING.get(), getItemProperties()));
    public static final RegistryObject<Item> HONEY_GENERATOR_ITEM = ITEMS.register("honey_generator", () -> new BlockItem(ModBlocks.HONEY_GENERATOR.get(), getItemProperties()));

    public static final RegistryObject<Item> WAX_BLOCK_ITEM = ITEMS.register("wax_block", () -> new BlockItem(ModBlocks.WAX_BLOCK.get(), getItemProperties()) {
        @Override
        public int getBurnTime(ItemStack itemStack, IRecipeType<?> recipeType) {
            return 4000;
        }
    });

    public static final RegistryObject<Item> GOLD_FLOWER_ITEM = ITEMS.register("gold_flower", () -> new BlockItem(ModBlocks.GOLD_FLOWER.get(), getItemProperties()));
    public static final RegistryObject<Item> BEE_JAR = ITEMS.register("bee_jar", () -> new BeeJar(getItemProperties().durability(0).stacksTo(16)));
    public static final RegistryObject<Item> OAK_BEE_NEST_ITEM = NESTS_ITEMS.register("bee_nest", () -> new TieredBeehiveItem(ModBlocks.OAK_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> ACACIA_BEE_NEST_ITEM = NESTS_ITEMS.register("acacia_bee_nest", () -> new TieredBeehiveItem(ModBlocks.ACACIA_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> GRASS_BEE_NEST_ITEM = NESTS_ITEMS.register("grass_bee_nest", () -> new TieredBeehiveItem(ModBlocks.GRASS_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> JUNGLE_BEE_NEST_ITEM = NESTS_ITEMS.register("jungle_bee_nest", () -> new TieredBeehiveItem(ModBlocks.JUNGLE_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> NETHER_BEE_NEST_ITEM = NESTS_ITEMS.register("nether_bee_nest", () -> new TieredBeehiveItem(ModBlocks.NETHER_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> PRISMARINE_BEE_NEST_ITEM = NESTS_ITEMS.register("prismarine_bee_nest", () -> new TieredBeehiveItem(ModBlocks.PRISMARINE_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> PURPUR_BEE_NEST_ITEM = NESTS_ITEMS.register("purpur_bee_nest", () -> new TieredBeehiveItem(ModBlocks.PURPUR_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> BIRCH_BEE_NEST_ITEM = NESTS_ITEMS.register("birch_bee_nest", () -> new TieredBeehiveItem(ModBlocks.BIRCH_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> WITHER_BEE_NEST_ITEM = NESTS_ITEMS.register("wither_bee_nest", () -> new TieredBeehiveItem(ModBlocks.WITHER_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> BROWN_MUSHROOM_NEST_ITEM = NESTS_ITEMS.register("brown_mushroom_bee_nest", () -> new TieredBeehiveItem(ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> CRIMSON_BEE_NEST_ITEM = NESTS_ITEMS.register("crimson_bee_nest", () -> new TieredBeehiveItem(ModBlocks.CRIMSON_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> CRIMSON_NYLIUM_BEE_NEST_ITEM = NESTS_ITEMS.register("crimson_nylium_bee_nest", () -> new TieredBeehiveItem(ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> DARK_OAK_NEST_ITEM = NESTS_ITEMS.register("dark_oak_bee_nest", () -> new TieredBeehiveItem(ModBlocks.DARK_OAK_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> RED_MUSHROOM_NEST_ITEM = NESTS_ITEMS.register("red_mushroom_bee_nest", () -> new TieredBeehiveItem(ModBlocks.RED_MUSHROOM_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> SPRUCE_BEE_NEST_ITEM = NESTS_ITEMS.register("spruce_bee_nest", () -> new TieredBeehiveItem(ModBlocks.SPRUCE_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> WARPED_BEE_NEST_ITEM = NESTS_ITEMS.register("warped_bee_nest", () -> new TieredBeehiveItem(ModBlocks.WARPED_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> WARPED_NYLIUM_BEE_NEST_ITEM = NESTS_ITEMS.register("warped_nylium_bee_nest", () -> new BlockItem(ModBlocks.WARPED_NYLIUM_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_APIARY_ITEM = ITEMS.register("t1_apiary", () -> new BlockItem(ModBlocks.T1_APIARY_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_APIARY_ITEM = ITEMS.register("t2_apiary", () -> new BlockItem(ModBlocks.T2_APIARY_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_APIARY_ITEM = ITEMS.register("t3_apiary", () -> new BlockItem(ModBlocks.T3_APIARY_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> T4_APIARY_ITEM = ITEMS.register("t4_apiary", () -> new BlockItem(ModBlocks.T4_APIARY_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> APIARY_STORAGE_ITEM = ITEMS.register("apiary_storage", () -> new BlockItem(ModBlocks.APIARY_STORAGE_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> APIARY_BREEDER_ITEM = ITEMS.register("apiary_breeder", () -> new BlockItem(ModBlocks.APIARY_BREEDER_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> ENDER_BEECON_ITEM = ITEMS.register("ender_beecon", () -> new BlockItem(ModBlocks.ENDER_BEECON.get(), getItemProperties()));
    public static final RegistryObject<Item> PURPUR_HONEY_TANK_ITEM = ITEMS.register("purpur_honey_tank", () -> new BlockItem(ModBlocks.PURPUR_HONEY_TANK.get(), getItemProperties()));
    public static final RegistryObject<Item> NETHER_HONEY_TANK_ITEM = ITEMS.register("nether_honey_tank", () -> new BlockItem(ModBlocks.NETHER_HONEY_TANK.get(), getItemProperties()));
    public static final RegistryObject<Item> WOODEN_HONEY_TANK_ITEM = ITEMS.register("wooden_honey_tank", () -> new BlockItem(ModBlocks.WOODEN_HONEY_TANK.get(), getItemProperties()));
    public static final RegistryObject<Item> HONEY_CONGEALER_ITEM = ITEMS.register("honey_congealer", () -> new BlockItem(ModBlocks.HONEY_CONGEALER.get(), getItemProperties()));
//    public static final RegistryObject<Item> HONEY_PIPE_ITEM = ITEMS.register("honey_pipe", () -> new BlockItem(ModBlocks.HONEY_PIPE.get(), getItemProperties()));
    public static final RegistryObject<Item> BOTTOMLESS_HONEY_POT = ITEMS.register("bottomless_honey_pot", () -> new BlockItem(ModBlocks.BOTTOMLESS_HONEY_POT.get(), getItemProperties()));

    public static final RegistryObject<Item> IRON_STORAGE_UPGRADE = ITEMS.register("iron_storage_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_STORAGE_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_SLOT_UPGRADE, 27F)
                    .build()));

    public static final RegistryObject<Item> GOLD_STORAGE_UPGRADE = ITEMS.register("gold_storage_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_STORAGE_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_SLOT_UPGRADE, 54F)
                    .build()));

    public static final RegistryObject<Item> DIAMOND_STORAGE_UPGRADE = ITEMS.register("diamond_storage_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_STORAGE_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_SLOT_UPGRADE, 81F)
                    .build()));

    public static final RegistryObject<Item> EMERALD_STORAGE_UPGRADE = ITEMS.register("emerald_storage_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_STORAGE_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_SLOT_UPGRADE, 108F)
                    .build()));

    public static final RegistryObject<Item> APIARY_BREEDER_UPGRADE = ITEMS.register("apiary_breeder_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_BREEDER_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_BREEDER_COUNT, 1)
                    .build()) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTranslatableTip("item.resourcefulbees.apiary_breeder_upgrade.tooltip.info", TextFormatting.GOLD)
                    .build());
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    });

    public static final RegistryObject<Item> APIARY_BREED_TIME_UPGRADE = ITEMS.register("apiary_breed_time_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_BREEDER_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_BREED_TIME, 300)
                    .build()) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTranslatableTip("item.resourcefulbees.apiary_breed_time_upgrade.tooltip.info", TextFormatting.GOLD)
                    .build());
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    });

    public static final RegistryObject<Item> HONEY_FLUID_BUCKET = ITEMS.register("honey_fluid_bucket", () -> new BucketItem(ModFluids.HONEY_STILL, getItemProperties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> T1_HIVE_UPGRADE = ITEMS.register("t1_hive_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_HIVE_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_TIER, 1f)
                    .upgradeModification(NBTConstants.NBT_TIER_MODIFIER, 1f)
                    .build()) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTranslatableTip(ModConstants.HIVE_UPGRADE_TOOLTIP, TextFormatting.GOLD)
                    .build());
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    });

    public static final RegistryObject<Item> T2_HIVE_UPGRADE = ITEMS.register("t2_hive_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_HIVE_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_TIER, 2f)
                    .upgradeModification(NBTConstants.NBT_TIER_MODIFIER, 1.5f)
                    .build()) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTranslatableTip(ModConstants.HIVE_UPGRADE_TOOLTIP, TextFormatting.GOLD)
                    .build());
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    });

    public static final RegistryObject<Item> T3_HIVE_UPGRADE = ITEMS.register("t3_hive_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_HIVE_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_TIER, 3f)
                    .upgradeModification(NBTConstants.NBT_TIER_MODIFIER, 2f)
                    .build()) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTranslatableTip(ModConstants.HIVE_UPGRADE_TOOLTIP, TextFormatting.GOLD)
                    .build());
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    });

    public static final RegistryObject<Item> T4_HIVE_UPGRADE = ITEMS.register("t4_hive_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_HIVE_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_TIER, 4f)
                    .upgradeModification(NBTConstants.NBT_TIER_MODIFIER, 4f)
                    .build()) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTranslatableTip(ModConstants.HIVE_UPGRADE_TOOLTIP, TextFormatting.GOLD)
                    .build());
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    });

    // HIDDEN ITEMS
    public static final RegistryObject<Item> MUTATION_ICON = ITEMS.register("mutation_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MUTATION_BLOCK_ICON = ITEMS.register("mutation_block_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MUTATION_ITEM_ICON = ITEMS.register("mutation_item_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MUTATION_ENTITY_ICON = ITEMS.register("mutation_entity_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TRAIT_ICON = ITEMS.register("trait_icon", () -> new Item(new Item.Properties()));
}
