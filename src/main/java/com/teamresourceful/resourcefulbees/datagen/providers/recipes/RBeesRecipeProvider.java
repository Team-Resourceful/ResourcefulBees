package com.teamresourceful.resourcefulbees.datagen.providers.recipes;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.records.HiveType;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModItemTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class RBeesRecipeProvider extends RecipeProvider {

    protected RBeesRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        buildHiveUpgrades();
        buildApiaries();
        buildNestRecipes();
        buildHoneyRecipes();
        buildWaxRecipes();
        buildWaxedBlocks();
        buildTools();
        buildMachines();
    }

    private void buildHiveUpgrades() {
        shaped(RecipeCategory.MISC, ModItems.T2_NEST_UPGRADE.get())
                .pattern("SSS")
                .pattern("SMS")
                .pattern("SSS")
                .define('S', ItemTags.PLANKS)
                .define('M', Items.GRASS_BLOCK)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.T3_NEST_UPGRADE.get())
                .pattern("CSC")
                .pattern("SMS")
                .pattern("CSC")
                .define('C', ItemTags.PLANKS)
                .define('S', ModItemTags.WAX)
                .define('M', ModItemTags.HONEYCOMBS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.T4_NEST_UPGRADE.get())
                .pattern("CSC")
                .pattern("SMS")
                .pattern("CSC")
                .define('C', ItemTags.PLANKS)
                .define('S', ModItemTags.WAX_STORAGE_BLOCKS)
                .define('M', ModItemTags.HONEYCOMB_STORAGE_BLOCKS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);
    }

    private void buildApiaries() {
        shaped(RecipeCategory.MISC, ModItems.T1_APIARY_ITEM.get())
                .pattern("CSC")
                .pattern("SMS")
                .pattern("CSC")
                .define('C', ModItemTags.HONEYCOMB_STORAGE_BLOCKS)
                .define('S', ModItemTags.T4_NESTS)
                .define('M', Items.NETHER_STAR)
                .unlockedBy("has_honeycomb_block", has(ModItemTags.HONEYCOMB_STORAGE_BLOCKS))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.T2_APIARY_ITEM.get())
                .pattern("CSC")
                .pattern("SMS")
                .pattern("CSC")
                .define('C', ModItemTags.HONEYCOMB_STORAGE_BLOCKS)
                .define('S', ModItems.T1_APIARY_ITEM.get())
                .define('M', Items.NETHER_STAR)
                .unlockedBy("has_honeycomb_block", has(ModItemTags.HONEYCOMB_STORAGE_BLOCKS))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.T3_APIARY_ITEM.get())
                .pattern("CSC")
                .pattern("SMS")
                .pattern("CSC")
                .define('C', ModItemTags.HONEYCOMB_STORAGE_BLOCKS)
                .define('S', ModItems.T2_APIARY_ITEM.get())
                .define('M', Items.NETHER_STAR)
                .unlockedBy("has_honeycomb_block", has(ModItemTags.HONEYCOMB_STORAGE_BLOCKS))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.T4_APIARY_ITEM.get())
                .pattern("CSC")
                .pattern("SMS")
                .pattern("CSC")
                .define('C', ModItemTags.HONEYCOMB_STORAGE_BLOCKS)
                .define('S', ModItems.T3_APIARY_ITEM.get())
                .define('M', Items.NETHER_STAR)
                .unlockedBy("has_honeycomb_block", has(ModItemTags.HONEYCOMB_STORAGE_BLOCKS))
                .save(output);
    }

    private void buildNestRecipes() {
        for (HiveType hiveType : HiveType.values()) {
            shaped(RecipeCategory.MISC, hiveType.tierOneNest().asItem())
                    .pattern("WPW")
                    .pattern("PHP")
                    .pattern("WPW")
                    .define('W', ModItems.WAXED_PLANKS.get())
                    .define('P', hiveType.tierOneRecipeMaterialItem())
                    .define('H', Items.BEEHIVE)
                    .unlockedBy("has_waxed_planks", has(ModItems.WAXED_PLANKS.get()))
                    .save(output);
        }
    }

    private void buildHoneyRecipes() {
        shapeless(RecipeCategory.MISC, ModItems.HONEY_BUCKET.get())
                .requires(Items.HONEY_BOTTLE, 4)
                .requires(Items.BUCKET)
                .unlockedBy("has_honey_bottle", has(Items.HONEY_BOTTLE))
                .save(output);

        shapeless(RecipeCategory.MISC, Items.HONEY_BOTTLE, 4)
                .requires(Items.GLASS_BOTTLE, 4)
                .requires(ModItems.HONEY_BUCKET.get())
                .unlockedBy("has_honey_bucket", has(ModItems.HONEY_BUCKET.get()))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.HONEY_GLASS.get(), 4)
                .pattern("HG")
                .pattern("GH")
                .define('G', Tags.Items.GLASS_BLOCKS)
                .define('H', ModItemTags.HONEY_BLOCKS)
                .unlockedBy("has_honey_block", has(ModItemTags.HONEY_BLOCKS))
                .save(output);

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.HONEY_GLASS.get())
                .requires(ModItems.HONEY_GLASS_PLAYER.get())
                .unlockedBy("has_honey_glass", has(ModItems.HONEY_GLASS.get()))
                .save(output, ResourceKey.create(Registries.RECIPE, ModIdentifier.of("honey_glass_inverse")));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.HONEY_GLASS_PLAYER.get())
                .requires(ModItems.HONEY_GLASS.get())
                .unlockedBy("has_honey_glass", has(ModItems.HONEY_GLASS.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.HONEY_POT_ITEM.get())
                .pattern("HCH")
                .pattern("CBC")
                .pattern("HCH")
                .define('H', ModItemTags.HONEY_BOTTLES)
                .define('C', ModItemTags.HONEYCOMBS)
                .define('B', Items.BUCKET)
                .unlockedBy("has_honeycomb", has(ModItemTags.HONEYCOMBS))
                .save(output);
    }

    private void buildWaxRecipes() {
        nineBlockStorageRecipes(
                RecipeCategory.MISC,
                ModItems.WAX.get(),
                RecipeCategory.BUILDING_BLOCKS,
                ModItems.WAX_BLOCK_ITEM.get()
        );
    }

    private void buildWaxedBlocks() {
        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.WAXED_PLANKS.get(), 4)
                .pattern("WPW")
                .pattern("PWP")
                .pattern("WPW")
                .define('W', ModItemTags.WAX)
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.WAXED_SLAB.get(), 6)
                .pattern("WWW")
                .define('W', ModItems.WAXED_PLANKS.get())
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.WAXED_STAIRS.get(), 4)
                .pattern("W  ")
                .pattern("WW ")
                .pattern("WWW")
                .define('W', ModItems.WAXED_PLANKS.get())
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.WAXED_FENCE.get(), 3)
                .pattern("WSW")
                .pattern("WSW")
                .define('W', ModItems.WAXED_PLANKS.get())
                .define('S', Items.STICK)
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModItems.WAXED_FENCE_GATE.get())
                .pattern("SWS")
                .pattern("SWS")
                .define('W', ModItems.WAXED_PLANKS.get())
                .define('S', Items.STICK)
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shapeless(RecipeCategory.REDSTONE, ModItems.WAXED_BUTTON.get())
                .requires(ModItems.WAXED_PLANKS.get())
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModItems.WAXED_PRESSURE_PLATE.get())
                .pattern("WW")
                .define('W', ModItems.WAXED_PLANKS.get())
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModItems.WAXED_DOOR.get(), 3)
                .pattern("WW")
                .pattern("WW")
                .pattern("WW")
                .define('W', ModItems.WAXED_PLANKS.get())
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModItems.WAXED_TRAPDOOR.get(), 2)
                .pattern("WWW")
                .pattern("WWW")
                .define('W', ModItems.WAXED_PLANKS.get())
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.WAXED_SIGN.get(), 3)
                .pattern("WWW")
                .pattern("WWW")
                .pattern(" S ")
                .define('W', ModItems.WAXED_PLANKS.get())
                .define('S', Items.STICK)
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.WAXED_HANGING_SIGN.get(), 6)
                .pattern("C C")
                .pattern("WWW")
                .pattern("WWW")
                .define('W', ModItems.WAXED_PLANKS.get())
                .define('C', Tags.Items.CHAINS)
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.TRIMMED_WAXED_PLANKS.get(), 4)
                .requires(ModItems.WAXED_PLANKS.get(), 4)
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.WAXED_MACHINE_BLOCK.get(), 2)
                .pattern("RHR")
                .pattern("HWH")
                .pattern("RHR")
                .define('W', ModItems.WAXED_PLANKS.get())
                .define('H', ModItemTags.HONEYCOMBS)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy("has_wax", has(ModItemTags.WAX))
                .save(output);
    }

    private void buildTools() {
        shaped(RecipeCategory.TOOLS, ModItems.SMOKER_CAN.get())
                .pattern("II ")
                .pattern("I I")
                .pattern("ICI")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.CAMPFIRE)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.SCRAPER.get())
                .pattern(" II")
                .pattern(" SI")
                .pattern("S  ")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.BEE_JAR.get())
                .pattern(" G ")
                .pattern("G G")
                .pattern("GGG")
                .define('G', Tags.Items.GLASS_PANES_COLORLESS)
                .unlockedBy("has_glass_panes", has(Tags.Items.GLASS_PANES_COLORLESS))
                .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.BELLOW.get())
                .pattern("LL ")
                .pattern("L L")
                .pattern(" LL")
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(output);

        /*
         * Beepedia is intentionally omitted for now.
         * It is being rewritten during the 26.2 port.
         */

        shaped(RecipeCategory.TOOLS, ModItems.BEE_BOX.get())
                .pattern("PPP")
                .pattern("WIW")
                .pattern("PPP")
                .define('I', Items.IRON_INGOT)
                .define('P', ItemTags.PLANKS)
                .define('W', ModItemTags.WAX)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.HONEY_DIPPER.get())
                .pattern(" CW")
                .pattern(" SC")
                .pattern("S  ")
                .define('C', ModItemTags.HONEYCOMBS)
                .define('S', Items.STICK)
                .define('W', ModItemTags.WAX)
                .unlockedBy("has_honeycomb", has(ModItemTags.HONEYCOMBS))
                .save(output);

        shapeless(RecipeCategory.TOOLS, ModItems.SMOKER.get())
                .requires(ModItems.SMOKER_CAN.get())
                .requires(ModItems.BELLOW.get())
                .unlockedBy("has_smoker_can", has(ModItems.SMOKER_CAN.get()))
                .save(output);
    }

    private void buildMachines() {
        shaped(RecipeCategory.MISC, ModItems.ENDER_BEECON_ITEM.get())
                .pattern("PPP")
                .pattern("GEG")
                .pattern("PBP")
                .define('E', Items.ENDER_EYE)
                .define('P', Items.PURPUR_BLOCK)
                .define('G', Tags.Items.GLASS_PANES)
                .define('B', Items.BEACON)
                .unlockedBy("has_obsidian", has(Tags.Items.OBSIDIANS))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SOLIDIFICATION_CHAMBER_ITEM.get())
                .pattern(" G ")
                .pattern("IGI")
                .pattern("SWS")
                .define('I', Items.IRON_INGOT)
                .define('G', Tags.Items.GLASS_PANES)
                .define('W', ModItemTags.WAX)
                .define('S', ItemTags.STONE_TOOL_MATERIALS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.HONEY_GENERATOR_ITEM.get())
                .pattern("I@I")
                .pattern("SGR")
                .pattern("IBI")
                .define('I', Items.IRON_INGOT)
                .define('B', ModItemTags.HONEY_BUCKETS)
                .define('G', Tags.Items.GLASS_PANES)
                .define('S', Items.IRON_BARS)
                .define('R', Items.REDSTONE)
                .define('@', Items.IRON_BLOCK)
                .unlockedBy("has_honey_bucket", has(ModItemTags.HONEY_BUCKETS))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.CENTRIFUGE_CRANK.get())
                .pattern("S  ")
                .pattern("SSS")
                .pattern("  W")
                .define('S', Items.STICK)
                .define('W', ModItems.WAXED_MACHINE_BLOCK.get())
                .unlockedBy("has_waxed", has(ModItems.WAXED_MACHINE_BLOCK.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.CENTRIFUGE.get())
                .pattern("WHW")
                .pattern("HRH")
                .pattern("WHW")
                .define('W', ModItems.WAXED_MACHINE_BLOCK.get())
                .define('H', ModItemTags.HONEYCOMB_STORAGE_BLOCKS)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy("has_waxed", has(ModItems.WAXED_MACHINE_BLOCK.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.BEE_LOCATOR.get())
                .pattern(" E ")
                .pattern("ICI")
                .pattern("IRI")
                .define('E', Items.END_ROD)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('C', Items.COMPASS)
                .define('R', Items.COMPARATOR)
                .unlockedBy("has_end_rod", has(Items.END_ROD))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput output) {
            return new RBeesRecipeProvider(provider, output);
        }

        @Override
        public @NonNull String getName() {
            return "Resourceful Bees Recipe Provider";
        }
    }
}