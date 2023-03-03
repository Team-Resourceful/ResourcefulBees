package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.item.centrifuge.CrankItem;
import com.teamresourceful.resourcefulbees.common.item.centrifuge.ManualCentrifugeItem;
import com.teamresourceful.resourcefulbees.common.items.WaxItem;
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

    public static final RegistryEntry<Item> STRAWBEERRY_MILKSHAKE = ITEMS.register("strawbeerry_milkshake", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200, 1), 1)
            .effect(() -> new MobEffectInstance(MobEffects.LUCK, 1200, 0), 0)
            .effect(() -> new MobEffectInstance(MobEffects.JUMP, 1200, 1), 1)
            .nutrition(6)
            .saturationMod(1.5f)
            .alwaysEat()
            .build())
            .rarity(Rarity.EPIC))
    );

    public static final RegistryEntry<Item> WAX = ITEMS.register("wax", () -> new WaxItem(new Item.Properties()) {
        @Override
        public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
            return 400;
        }
    });

    public static final RegistryEntry<Item> HONEY_GENERATOR_ITEM = ITEMS.register("honey_generator", () -> new BlockItem(ModBlocks.HONEY_GENERATOR.get(), new Item.Properties()));

    public static final RegistryEntry<Item> WAX_BLOCK_ITEM = ITEMS.register("wax_block", () -> new BlockItem(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAX_BLOCK.get(), new Item.Properties()) {
        @Override
        public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
            return 4000;
        }
    });

    public static final RegistryEntry<Item> FLOW_HIVE = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.NEST_ITEMS.register("flow_hive", () -> new BlockItem(ModBlocks.FLOW_HIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> ENDER_BEECON_ITEM = ITEMS.register("ender_beecon", () -> new BlockItem(ModBlocks.ENDER_BEECON.get(), new Item.Properties()));
    public static final RegistryEntry<Item> SOLIDIFICATION_CHAMBER_ITEM = ITEMS.register("solidification_chamber", () -> new BlockItem(ModBlocks.SOLIDIFICATION_CHAMBER.get(), new Item.Properties()));
    public static final RegistryEntry<Item> HONEY_POT_ITEM = ITEMS.register("honey_pot", () -> new BlockItem(ModBlocks.HONEY_POT.get(), new Item.Properties()));

    public static final RegistryEntry<Item> HONEY_FLUID_BUCKET = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BUCKET_ITEMS.register("honey_bucket", () -> new BucketItem(ModFluids.HONEY_STILL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryEntry<Item> CENTRIFUGE_CRANK = ITEMS.register("centrifuge_crank", () -> new CrankItem(ModBlocks.CENTRIFUGE_CRANK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE = ITEMS.register("centrifuge", () -> new ManualCentrifugeItem(ModBlocks.BASIC_CENTRIFUGE.get(), new Item.Properties()));
}
