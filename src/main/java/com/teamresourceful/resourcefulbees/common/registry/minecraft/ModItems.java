package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.item.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class ModItems {

    private ModItems() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Item> ITEMS = createItemRegistry();


    public static final DeferredRegister<Item> T1_NEST_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> T2_NEST_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> T3_NEST_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> T4_NEST_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> SPAWN_EGG_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> HONEYCOMB_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> HONEYCOMB_BLOCK_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> HONEY_BOTTLE_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> HONEY_BLOCK_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> HONEY_BUCKET_ITEMS = createItemRegistry();
    public static final DeferredRegister<Item> CENTRIFUGE_ITEMS = createItemRegistry();

    private static DeferredRegister<Item> createItemRegistry() {
        return DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
    }

    private static Item.Properties getItemProperties() {
        return new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES);
    }

    private static Item.Properties getNestProperties() {
        return new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HIVES);
    }

    public static void initializeRegistries(IEventBus bus) {
        ITEMS.register(bus);
        T1_NEST_ITEMS.register(bus);
        T2_NEST_ITEMS.register(bus);
        T3_NEST_ITEMS.register(bus);
        T4_NEST_ITEMS.register(bus);
        SPAWN_EGG_ITEMS.register(bus);
        HONEYCOMB_ITEMS.register(bus);
        HONEYCOMB_BLOCK_ITEMS.register(bus);
        HONEY_BOTTLE_ITEMS.register(bus);
        HONEY_BLOCK_ITEMS.register(bus);
        HONEY_BUCKET_ITEMS.register(bus);
        CENTRIFUGE_ITEMS.register(bus);
    }

    //region Nests
    public static final RegistryObject<Item> ACACIA_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/acacia/1", () -> new BlockItem(ModBlocks.ACACIA_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> BIRCH_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/birch/1", () -> new BlockItem(ModBlocks.BIRCH_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> BROWN_MUSHROOM_NEST_ITEM = T1_NEST_ITEMS.register("nest/brown_mushroom/1", () -> new BlockItem(ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> CRIMSON_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/crimson/1", () -> new BlockItem(ModBlocks.CRIMSON_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> CRIMSON_NYLIUM_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/crimson_nylium/1", () -> new BlockItem(ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> DARK_OAK_NEST_ITEM = T1_NEST_ITEMS.register("nest/dark_oak/1", () -> new BlockItem(ModBlocks.DARK_OAK_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> GRASS_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/grass/1", () -> new BlockItem(ModBlocks.GRASS_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> JUNGLE_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/jungle/1", () -> new BlockItem(ModBlocks.JUNGLE_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> NETHER_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/netherrack/1", () -> new BlockItem(ModBlocks.NETHER_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> OAK_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/oak/1", () -> new BlockItem(ModBlocks.OAK_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> PRISMARINE_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/prismarine/1", () -> new BlockItem(ModBlocks.PRISMARINE_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> PURPUR_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/chorus/1", () -> new BlockItem(ModBlocks.PURPUR_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> RED_MUSHROOM_NEST_ITEM = T1_NEST_ITEMS.register("nest/red_mushroom/1", () -> new BlockItem(ModBlocks.RED_MUSHROOM_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> SPRUCE_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/spruce/1", () -> new BlockItem(ModBlocks.SPRUCE_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> WARPED_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/warped/1", () -> new BlockItem(ModBlocks.WARPED_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> WARPED_NYLIUM_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/warped_nylium/1", () -> new BlockItem(ModBlocks.WARPED_NYLIUM_BEE_NEST.get(), getNestProperties()));
    public static final RegistryObject<Item> WITHER_BEE_NEST_ITEM = T1_NEST_ITEMS.register("nest/wither/1", () -> new BlockItem(ModBlocks.WITHER_BEE_NEST.get(), getNestProperties()));
    //endregion

    //region T1 Hives
    public static final RegistryObject<Item> T1_ACACIA_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/acacia/2", () -> new BlockItem(ModBlocks.T1_ACACIA_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_BIRCH_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/birch/2", () -> new BlockItem(ModBlocks.T1_BIRCH_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_BROWN_MUSHROOM_NEST_ITEM = T2_NEST_ITEMS.register("nest/brown_mushroom/2", () -> new BlockItem(ModBlocks.T1_BROWN_MUSHROOM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_CRIMSON_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/crimson/2", () -> new BlockItem(ModBlocks.T1_CRIMSON_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_CRIMSON_NYLIUM_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/crimson_nylium/2", () -> new BlockItem(ModBlocks.T1_CRIMSON_NYLIUM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_DARK_OAK_NEST_ITEM = T2_NEST_ITEMS.register("nest/dark_oak/2", () -> new BlockItem(ModBlocks.T1_DARK_OAK_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_GRASS_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/grass/2", () -> new BlockItem(ModBlocks.T1_GRASS_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_JUNGLE_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/jungle/2", () -> new BlockItem(ModBlocks.T1_JUNGLE_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_NETHER_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/netherrack/2", () -> new BlockItem(ModBlocks.T1_NETHER_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_OAK_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/oak/2", () -> new BlockItem(ModBlocks.T1_OAK_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_PRISMARINE_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/prismarine/2", () -> new BlockItem(ModBlocks.T1_PRISMARINE_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_PURPUR_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/chorus/2", () -> new BlockItem(ModBlocks.T1_PURPUR_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_RED_MUSHROOM_NEST_ITEM = T2_NEST_ITEMS.register("nest/red_mushroom/2", () -> new BlockItem(ModBlocks.T1_RED_MUSHROOM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_SPRUCE_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/spruce/2", () -> new BlockItem(ModBlocks.T1_SPRUCE_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_WARPED_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/warped/2", () -> new BlockItem(ModBlocks.T1_WARPED_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_WARPED_NYLIUM_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/warped_nylium/2", () -> new BlockItem(ModBlocks.T1_WARPED_NYLIUM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T1_WITHER_BEEHIVE_ITEM = T2_NEST_ITEMS.register("nest/wither/2", () -> new BlockItem(ModBlocks.T1_WITHER_BEEHIVE.get(), getNestProperties()));
    //endregion

    //region T2 Hives
    public static final RegistryObject<Item> T2_ACACIA_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/acacia/3", () -> new BlockItem(ModBlocks.T2_ACACIA_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_BIRCH_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/birch/3", () -> new BlockItem(ModBlocks.T2_BIRCH_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_BROWN_MUSHROOM_NEST_ITEM = T3_NEST_ITEMS.register("nest/brown_mushroom/3", () -> new BlockItem(ModBlocks.T2_BROWN_MUSHROOM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_CRIMSON_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/crimson/3", () -> new BlockItem(ModBlocks.T2_CRIMSON_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_CRIMSON_NYLIUM_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/crimson_nylium/3", () -> new BlockItem(ModBlocks.T2_CRIMSON_NYLIUM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_DARK_OAK_NEST_ITEM = T3_NEST_ITEMS.register("nest/dark_oak/3", () -> new BlockItem(ModBlocks.T2_DARK_OAK_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_GRASS_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/grass/3", () -> new BlockItem(ModBlocks.T2_GRASS_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_JUNGLE_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/jungle/3", () -> new BlockItem(ModBlocks.T2_JUNGLE_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_NETHER_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/netherrack/3", () -> new BlockItem(ModBlocks.T2_NETHER_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_OAK_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/oak/3", () -> new BlockItem(ModBlocks.T2_OAK_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_PRISMARINE_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/prismarine/3", () -> new BlockItem(ModBlocks.T2_PRISMARINE_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_PURPUR_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/chorus/3", () -> new BlockItem(ModBlocks.T2_PURPUR_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_RED_MUSHROOM_NEST_ITEM = T3_NEST_ITEMS.register("nest/red_mushroom/3", () -> new BlockItem(ModBlocks.T2_RED_MUSHROOM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_SPRUCE_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/spruce/3", () -> new BlockItem(ModBlocks.T2_SPRUCE_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_WARPED_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/warped/3", () -> new BlockItem(ModBlocks.T2_WARPED_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_WARPED_NYLIUM_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/warped_nylium/3", () -> new BlockItem(ModBlocks.T2_WARPED_NYLIUM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_WITHER_BEEHIVE_ITEM = T3_NEST_ITEMS.register("nest/wither/3", () -> new BlockItem(ModBlocks.T2_WITHER_BEEHIVE.get(), getNestProperties()));
    //endregion

    //region T3 Hives
    public static final RegistryObject<Item> T3_ACACIA_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/acacia/4", () -> new BlockItem(ModBlocks.T3_ACACIA_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_BIRCH_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/birch/4", () -> new BlockItem(ModBlocks.T3_BIRCH_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_BROWN_MUSHROOM_NEST_ITEM = T4_NEST_ITEMS.register("nest/brown_mushroom/4", () -> new BlockItem(ModBlocks.T3_BROWN_MUSHROOM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_CRIMSON_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/crimson/4", () -> new BlockItem(ModBlocks.T3_CRIMSON_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_CRIMSON_NYLIUM_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/crimson_nylium/4", () -> new BlockItem(ModBlocks.T3_CRIMSON_NYLIUM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_DARK_OAK_NEST_ITEM = T4_NEST_ITEMS.register("nest/dark_oak/4", () -> new BlockItem(ModBlocks.T3_DARK_OAK_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_GRASS_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/grass/4", () -> new BlockItem(ModBlocks.T3_GRASS_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_JUNGLE_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/jungle/4", () -> new BlockItem(ModBlocks.T3_JUNGLE_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_NETHER_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/netherrack/4", () -> new BlockItem(ModBlocks.T3_NETHER_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_OAK_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/oak/4", () -> new BlockItem(ModBlocks.T3_OAK_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_PRISMARINE_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/prismarine/4", () -> new BlockItem(ModBlocks.T3_PRISMARINE_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_PURPUR_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/chorus/4", () -> new BlockItem(ModBlocks.T3_PURPUR_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_RED_MUSHROOM_NEST_ITEM = T4_NEST_ITEMS.register("nest/red_mushroom/4", () -> new BlockItem(ModBlocks.T3_RED_MUSHROOM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_SPRUCE_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/spruce/4", () -> new BlockItem(ModBlocks.T3_SPRUCE_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_WARPED_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/warped/4", () -> new BlockItem(ModBlocks.T3_WARPED_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_WARPED_NYLIUM_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/warped_nylium/4", () -> new BlockItem(ModBlocks.T3_WARPED_NYLIUM_BEEHIVE.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_WITHER_BEEHIVE_ITEM = T4_NEST_ITEMS.register("nest/wither/4", () -> new BlockItem(ModBlocks.T3_WITHER_BEEHIVE.get(), getNestProperties()));
    //endregion

    public static final RegistryObject<Item> BEE_JAR = ITEMS.register("bee_jar", () -> new BeeJar(getItemProperties().durability(0).stacksTo(16)));

    public static final RegistryObject<Item> OREO_COOKIE = ITEMS.register("oreo_cookie", () -> new Item(getItemProperties().food(new FoodProperties.Builder()
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
            .rarity(Rarity.EPIC)));


    public static final RegistryObject<Item> CRAFTING_BEE_BOX = ITEMS.register("crafting_bee_box", () -> new BeeBox(getItemProperties().stacksTo(1), true));
    public static final RegistryObject<Item> BEE_BOX = ITEMS.register("bee_box", () -> new BeeBox(getItemProperties().stacksTo(1), false));
    public static final RegistryObject<Item> BEEPEDIA = ITEMS.register("beepedia", () -> new Beepedia(getItemProperties().stacksTo(1)));
    public static final RegistryObject<Item> HONEY_DIPPER = ITEMS.register("honey_dipper", () -> new HoneyDipper(getItemProperties().stacksTo(1)));

    public static final RegistryObject<Item> SCRAPER = ITEMS.register("scraper", () -> new ScraperItem(getItemProperties().stacksTo(1)));

    public static final RegistryObject<Item> SMOKER = ITEMS.register("smoker", () -> new Smoker(getItemProperties().setNoRepair().durability(CommonConfig.SMOKER_DURABILITY.get())));
    public static final RegistryObject<Item> BELLOW = ITEMS.register("bellow", () -> new Item(getItemProperties()));
    public static final RegistryObject<Item> SMOKERCAN = ITEMS.register("smoker_can", () -> new Item(getItemProperties()));

    public static final RegistryObject<Item> WAX = ITEMS.register("wax", () -> new Item(getItemProperties()) {
        @Override
        public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
            return 400;
        }
    });

    public static final RegistryObject<Item> HONEY_GENERATOR_ITEM = ITEMS.register("honey_generator", () -> new BlockItem(ModBlocks.HONEY_GENERATOR.get(), getItemProperties()));

    public static final RegistryObject<Item> WAX_BLOCK_ITEM = ITEMS.register("wax_block", () -> new BlockItem(ModBlocks.WAX_BLOCK.get(), getItemProperties()) {
        @Override
        public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
            return 4000;
        }
    });

    public static final RegistryObject<Item> GOLD_FLOWER_ITEM = ITEMS.register("gold_flower", () -> new BlockItem(ModBlocks.GOLD_FLOWER.get(), getItemProperties()));
    public static final RegistryObject<Item> T1_APIARY_ITEM = ITEMS.register("t1_apiary", () -> new BlockItem(ModBlocks.T1_APIARY_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> T2_APIARY_ITEM = ITEMS.register("t2_apiary", () -> new BlockItem(ModBlocks.T2_APIARY_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> T3_APIARY_ITEM = ITEMS.register("t3_apiary", () -> new BlockItem(ModBlocks.T3_APIARY_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> T4_APIARY_ITEM = ITEMS.register("t4_apiary", () -> new BlockItem(ModBlocks.T4_APIARY_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> APIARY_BREEDER_ITEM = ITEMS.register("apiary_breeder", () -> new BlockItem(ModBlocks.APIARY_BREEDER_BLOCK.get(), getNestProperties()));
    public static final RegistryObject<Item> ENDER_BEECON_ITEM = ITEMS.register("ender_beecon", () -> new BlockItem(ModBlocks.ENDER_BEECON.get(), getItemProperties()));
    public static final RegistryObject<Item> SOLIDIFICATION_CHAMBER_ITEM = ITEMS.register("solidification_chamber", () -> new BlockItem(ModBlocks.SOLIDIFICATION_CHAMBER.get(), getItemProperties()));
    public static final RegistryObject<Item> HONEY_POT_ITEM = ITEMS.register("honey_pot", () -> new BlockItem(ModBlocks.HONEY_POT.get(), getItemProperties()));

    public static final RegistryObject<Item> APIARY_BREEDER_UPGRADE = ITEMS.register("apiary_breeder_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_BREEDER_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_BREEDER_COUNT, 1)
                    .build()) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            tooltip.add(TranslationConstants.Items.BREEDER_UPGRADE.withStyle(ChatFormatting.GOLD));
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    });

    public static final RegistryObject<Item> APIARY_BREED_TIME_UPGRADE = ITEMS.register("apiary_breed_time_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_BREEDER_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_BREED_TIME, 300)
                    .build()) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            tooltip.add(TranslationConstants.Items.BREED_TIME_UPGRADE.withStyle(ChatFormatting.GOLD));
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
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            tooltip.add(TranslationConstants.Items.HIVE_UPGRADE.withStyle(ChatFormatting.GOLD));
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
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            tooltip.add(TranslationConstants.Items.HIVE_UPGRADE.withStyle(ChatFormatting.GOLD));
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
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            tooltip.add(TranslationConstants.Items.HIVE_UPGRADE.withStyle(ChatFormatting.GOLD));
            super.appendHoverText(stack, level, tooltip, flagIn);
        }
    });

    public static final RegistryObject<Item> T4_HIVE_UPGRADE = ITEMS.register("t4_hive_upgrade", () -> new UpgradeItem(getItemProperties().durability(0).stacksTo(16),
            UpgradeItem.builder()
                    .upgradeType(NBTConstants.NBT_HIVE_UPGRADE)
                    .upgradeModification(NBTConstants.NBT_TIER, 4f)
                    .upgradeModification(NBTConstants.NBT_TIER_MODIFIER, 4f)
                    .build()) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            tooltip.add(TranslationConstants.Items.HIVE_UPGRADE.withStyle(ChatFormatting.GOLD));
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    });

    //region centrifuge items
    public static final RegistryObject<Item> CENTRIFUGE_CASING = CENTRIFUGE_ITEMS.register("centrifuge/casing", () -> new BlockItem(ModBlocks.CENTRIFUGE_CASING.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_PROCESSOR = CENTRIFUGE_ITEMS.register("centrifuge/processor", () -> new BlockItem(ModBlocks.CENTRIFUGE_PROCESSOR.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_GEARBOX = CENTRIFUGE_ITEMS.register("centrifuge/gearbox", () -> new BlockItem(ModBlocks.CENTRIFUGE_GEARBOX.get(), getItemProperties()));

    //TERMINAL
    public static final RegistryObject<Item> CENTRIFUGE_BASIC_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_TERMINAL.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ADVANCED_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_TERMINAL.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ELITE_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_TERMINAL.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ULTIMATE_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_TERMINAL.get(), getItemProperties()));

    //VOID
    public static final RegistryObject<Item> CENTRIFUGE_BASIC_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_VOID.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ADVANCED_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_VOID.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ELITE_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_VOID.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ULTIMATE_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_VOID.get(), getItemProperties()));

    //INPUT
    public static final RegistryObject<Item> CENTRIFUGE_BASIC_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_INPUT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ADVANCED_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_INPUT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ELITE_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_INPUT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ULTIMATE_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_INPUT.get(), getItemProperties()));

    //ENERGY PORT
    public static final RegistryObject<Item> CENTRIFUGE_BASIC_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_ENERGY_PORT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ADVANCED_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ELITE_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_ENERGY_PORT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ULTIMATE_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get(), getItemProperties()));

    //ITEM OUTPUT
    public static final RegistryObject<Item> CENTRIFUGE_BASIC_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ADVANCED_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ELITE_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get(), getItemProperties()));

    //FLUID OUTPUT
    public static final RegistryObject<Item> CENTRIFUGE_BASIC_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/basic", () -> new BlockItem(ModBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ADVANCED_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/advanced", () -> new BlockItem(ModBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ELITE_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/elite", () -> new BlockItem(ModBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT.get(), getItemProperties()));
    public static final RegistryObject<Item> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/ultimate", () -> new BlockItem(ModBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get(), getItemProperties()));
    //endregion

    // HIDDEN ITEMS
    public static final RegistryObject<Item> MUTATION_ICON = ITEMS.register("mutation_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MUTATION_BLOCK_ICON = ITEMS.register("mutation_block_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MUTATION_ITEM_ICON = ITEMS.register("mutation_item_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MUTATION_ENTITY_ICON = ITEMS.register("mutation_entity_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TRAIT_ICON = ITEMS.register("trait_icon", () -> new Item(new Item.Properties()));
}
