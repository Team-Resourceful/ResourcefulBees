package com.teamresourceful.resourcefulbees.datagen.providers.recipes;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> recipes) {

        CentrifugeRecipeBuilder.of(Ingredient.of(Items.HONEYCOMB), new ResourceLocation(ResourcefulBees.MOD_ID, "honeycomb_centrifuge"))
                .time(200)
                .addOutput(new CentrifugeRecipeBuilder.ItemOutputBuilder(1).addOutput(Items.SUGAR.getDefaultInstance(), 10))
                .addOutput(new CentrifugeRecipeBuilder.ItemOutputBuilder(0.25).addOutput(ModItems.WAX.get().getDefaultInstance(), 10))
                .addOutput(new CentrifugeRecipeBuilder.FluidOutputBuilder(0.20).addOutput(new FluidStack(ModFluids.HONEY_STILL.get(), 250), 10))
                .rf(5)
                .save(recipes);

        CentrifugeRecipeBuilder.of(Ingredient.of(Items.HONEYCOMB_BLOCK), new ResourceLocation(ResourcefulBees.MOD_ID, "honeycomb_block_centrifuge"))
                .time(200)
                .addOutput(new CentrifugeRecipeBuilder.ItemOutputBuilder(1).addOutput(new ItemStack(Items.SUGAR, 9), 10))
                .addOutput(new CentrifugeRecipeBuilder.ItemOutputBuilder(0.25).addOutput(new ItemStack(ModItems.WAX.get(), 9), 10))
                .addOutput(new CentrifugeRecipeBuilder.FluidOutputBuilder(0.20).addOutput(new FluidStack(ModFluids.HONEY_STILL.get(), 250*9), 10))
                .rf(10)
                .save(recipes);

        RecipeCriteria hasPlanks = new RecipeCriteria("has_planks", RecipeProvider.has(ItemTags.PLANKS));
        RecipeCriteria hasHoneycombBlock = new RecipeCriteria("has_honeycomb_block", RecipeProvider.has(ModTags.Items.HONEYCOMB_BLOCK));
        RecipeCriteria hasIron = new RecipeCriteria("has_iron", RecipeProvider.has(Tags.Items.INGOTS_IRON));

        //region Hive Upgrades
        RecipeHelper.createBoxed(Ingredient.of(ItemTags.PLANKS), Ingredient.of(Items.GRASS), ModItems.T2_NEST_UPGRADE.get())
                .unlockedBy(hasPlanks).save(recipes);
        RecipeHelper.createCornerWithMid(Ingredient.of(ItemTags.PLANKS), Ingredient.of(ModTags.Items.HONEYCOMB), Ingredient.of(ModTags.Items.WAX), ModItems.T3_NEST_UPGRADE.get())
                .unlockedBy(hasPlanks).save(recipes);
        RecipeHelper.createCornerWithMid(Ingredient.of(ItemTags.PLANKS), Ingredient.of(ModTags.Items.HONEYCOMB_BLOCK), Ingredient.of(ModTags.Items.WAX_BLOCK), ModItems.T4_NEST_UPGRADE.get())
                .unlockedBy(hasPlanks).save(recipes);
        //endregion

        //region Apiary Blocks
        RecipeHelper.createCornerWithMid(Ingredient.of(Items.NETHER_STAR), Ingredient.of(ModTags.Items.HONEYCOMB_BLOCK), Ingredient.of(ModTags.Items.T3_NESTS), ModItems.T1_APIARY_ITEM.get())
                .unlockedBy(hasHoneycombBlock).save(recipes);
        RecipeHelper.createCornerWithMid(Ingredient.of(Items.NETHER_STAR), Ingredient.of(ModTags.Items.HONEYCOMB_BLOCK), Ingredient.of(ModItems.T1_APIARY_ITEM.get()), ModItems.T2_APIARY_ITEM.get())
                .unlockedBy(hasHoneycombBlock).save(recipes);
        RecipeHelper.createCornerWithMid(Ingredient.of(Items.NETHER_STAR), Ingredient.of(ModTags.Items.HONEYCOMB_BLOCK), Ingredient.of(ModItems.T2_APIARY_ITEM.get()), ModItems.T3_APIARY_ITEM.get())
                .unlockedBy(hasHoneycombBlock).save(recipes);
        RecipeHelper.createCornerWithMid(Ingredient.of(Items.NETHER_STAR), Ingredient.of(ModTags.Items.HONEYCOMB_BLOCK), Ingredient.of(ModItems.T3_APIARY_ITEM.get()), ModItems.T4_APIARY_ITEM.get())
                .unlockedBy(hasHoneycombBlock).save(recipes);
        //endregion
        //region Honey Conversion
        ShapelessRecipeBuilder.shapeless(ModItems.HONEY_FLUID_BUCKET.get()).requires(Items.HONEY_BOTTLE, 4).requires(Items.BUCKET)
                .unlockedBy("has_honey_bottle", RecipeProvider.has(Items.HONEY_BOTTLE)).save(recipes, new ResourceLocation(ResourcefulBees.MOD_ID, "honey_bottles_to_bucket"));
        ShapelessRecipeBuilder.shapeless(Items.HONEY_BOTTLE, 4).requires(Items.GLASS_BOTTLE, 4).requires(ModItems.HONEY_FLUID_BUCKET.get())
                .unlockedBy("has_honey_bottle", RecipeProvider.has(Items.HONEY_BOTTLE)).save(recipes, new ResourceLocation(ResourcefulBees.MOD_ID, "honey_bucket_to_bottles"));
        //endregion

        //region Wax
        RecipeHelper.getStorageRecipe(ModItems.WAX_BLOCK_ITEM.get(), Ingredient.of(ModItems.WAX.get()))
                .unlockedBy("has_wax", RecipeProvider.has(ModItems.WAX.get())).save(recipes);
        RecipeHelper.getStorageToItemRecipe(ModItems.WAX.get(), Ingredient.of(ModItems.WAX_BLOCK_ITEM.get()))
                .unlockedBy("has_wax_block", RecipeProvider.has(ModItems.WAX_BLOCK_ITEM.get())).save(recipes);
        //endregion
        AdvancedShapedRecipeBuilder.shaped(ModItems.HONEY_POT_ITEM)
                .pattern("HCH", "CBC", "HCH")
                .define('H', Ingredient.of(ModTags.Items.HONEY_BOTTLES))
                .define('C', Ingredient.of(ModTags.Items.HONEYCOMB))
                .define('B', Ingredient.of(Items.BUCKET))
                .unlockedBy(hasHoneycombBlock)
                .save(recipes);
        //region Waxed Blocks
        RecipeCriteria hasWax = new RecipeCriteria("has_wax", RecipeProvider.has(ModTags.Items.WAX));
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_PLANKS.get(), 4)
                .pattern("WPW", "PWP", "WPW")
                .define('W', Ingredient.of(ModTags.Items.WAX))
                .define('P', Ingredient.of(ItemTags.PLANKS))
                .unlockedBy(hasWax)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_SLAB, 6)
                .pattern("   ", "WWW", "   ")
                .define('W', Ingredient.of(ModItems.WAXED_PLANKS.get()))
                .unlockedBy(hasWax)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_STAIRS, 4)
                .pattern("W  ", "WW ", "WWW")
                .define('W', Ingredient.of(ModItems.WAXED_PLANKS.get()))
                .unlockedBy(hasWax)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_FENCE, 3)
                .pattern("   ", "WSW", "WSW")
                .define('W', Ingredient.of(ModItems.WAXED_PLANKS.get()))
                .define('S', Ingredient.of(Items.STICK))
                .unlockedBy(hasWax)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_FENCE_GATE)
                .pattern("   ", "SWS", "SWS")
                .define('W', Ingredient.of(ModItems.WAXED_PLANKS.get()))
                .define('S', Ingredient.of(Items.STICK))
                .unlockedBy(hasWax)
                .save(recipes);
        ShapelessRecipeBuilder.shapeless(ModItems.WAXED_BUTTON.get())
                .requires(ModItems.WAXED_PLANKS.get())
                .unlockedBy("has_wax", RecipeProvider.has(ModTags.Items.WAX))
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_PRESSURE_PLATE)
                .pattern("   ", "WW ", "   ")
                .define('W', Ingredient.of(ModItems.WAXED_PLANKS.get()))
                .unlockedBy(hasWax)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_DOOR, 3)
                .pattern("WW ", "WW ", "WW ")
                .define('W', Ingredient.of(ModItems.WAXED_PLANKS.get()))
                .unlockedBy(hasWax)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_TRAPDOOR, 2)
                .pattern("   ", "WWW", "WWW")
                .define('W', Ingredient.of(ModItems.WAXED_PLANKS.get()))
                .unlockedBy(hasWax)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_SIGN, 3)
                .pattern("WWW", "WWW", " S ")
                .define('W', Ingredient.of(ModItems.WAXED_PLANKS.get()))
                .define('S', Ingredient.of(Items.STICK))
                .unlockedBy(hasWax)
                .save(recipes);
        ShapelessRecipeBuilder.shapeless(ModItems.TRIMMED_WAXED_PLANKS.get(), 4)
                .requires(ModItems.WAXED_PLANKS.get(), 4)
                .unlockedBy("has_wax", RecipeProvider.has(ModTags.Items.WAX))
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.WAXED_MACHINE_BLOCK, 2)
                .pattern("RHR", "HWH", "RHR")
                .define('W', Ingredient.of(ModTags.Items.WAX_BLOCK))
                .define('H', Ingredient.of(ModTags.Items.HONEYCOMB))
                .define('R', Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(hasWax)
                .save(recipes);
        //endregion
        //region Tools
        AdvancedShapedRecipeBuilder.shaped(ModItems.SMOKERCAN)
                .pattern("II ", "I I", "ICI")
                .define('I', Ingredient.of(Tags.Items.INGOTS_IRON))
                .define('C', Ingredient.of(Items.CAMPFIRE))
                .unlockedBy(hasIron)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.SCRAPER)
                .pattern(" II", " SI", "S  ")
                .define('I', Ingredient.of(Tags.Items.INGOTS_IRON))
                .define('S', Ingredient.of(Items.STICK))
                .unlockedBy(hasIron)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.BEE_JAR)
                .pattern(" G ", "G G", "GGG")
                .define('G', Ingredient.of(Tags.Items.GLASS_PANES_COLORLESS))
                .unlockedBy("has_glass_panes", RecipeProvider.has(Tags.Items.GLASS_PANES_COLORLESS))
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.BELLOW)
                .pattern("LL ", "L L", " LL")
                .define('L', Ingredient.of(Items.LEATHER))
                .unlockedBy("has_leather", RecipeProvider.has(Items.LEATHER))
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.BEEPEDIA)
                .pattern("IRI", "IGI", "IHI")
                .define('I', Ingredient.of(Tags.Items.INGOTS_IRON))
                .define('G', Ingredient.of(Tags.Items.GLASS_PANES))
                .define('H', Ingredient.of(ModTags.Items.HONEY_BOTTLES))
                .define('R', Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(hasIron)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.BEE_BOX)
                .pattern("PPP", "WIW", "PPP")
                .define('I', Ingredient.of(Tags.Items.INGOTS_IRON))
                .define('P', Ingredient.of(ItemTags.PLANKS))
                .define('W', Ingredient.of(ModTags.Items.WAX))
                .unlockedBy(hasIron)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.HONEY_DIPPER)
                .pattern(" CW", " SC", "S  ")
                .define('C', Ingredient.of(ModTags.Items.HONEYCOMB))
                .define('S', Ingredient.of(Tags.Items.RODS_WOODEN))
                .define('W', Ingredient.of(ModTags.Items.WAX))
                .unlockedBy("has_honeycomb", RecipeProvider.has(ModTags.Items.HONEYCOMB))
                .save(recipes);
        ShapelessRecipeBuilder.shapeless(ModItems.SMOKER.get()).requires(ModItems.SMOKERCAN.get()).requires(ModItems.BELLOW.get()).unlockedBy("has_honeycomb", RecipeProvider.has(ModItems.SMOKERCAN.get())).save(recipes);
        //endregion
        //region Machines
        RecipeCriteria hasObsidian = new RecipeCriteria("has_obsidian", RecipeProvider.has(Tags.Items.OBSIDIAN));
        AdvancedShapedRecipeBuilder.shaped(ModItems.ENDER_BEECON_ITEM)
                .pattern("PPP","GEG","POP")
                .define('E', Ingredient.of(Tags.Items.ENDER_PEARLS))
                .define('P', Ingredient.of(Items.PURPUR_BLOCK))
                .define('G', Ingredient.of(Tags.Items.GLASS))
                .define('O', Ingredient.of(Tags.Items.OBSIDIAN))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.SOLIDIFICATION_CHAMBER_ITEM)
                .pattern(" G ","IGI","SWS")
                .define('I', Ingredient.of(Tags.Items.INGOTS_IRON))
                .define('G', Ingredient.of(Tags.Items.GLASS))
                .define('W', Ingredient.of(ModTags.Items.WAX))
                .define('S', Ingredient.of(Tags.Items.STONE))
                .unlockedBy(hasIron)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.HONEY_GENERATOR_ITEM)
                .pattern("I@I","SGR","IBI")
                .define('I', Ingredient.of(Tags.Items.INGOTS_IRON))
                .define('B', Ingredient.of(Items.BUCKET))
                .define('G', Ingredient.of(Tags.Items.GLASS))
                .define('S', Ingredient.of(Items.IRON_BARS))
                .define('R', Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .define('@', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON))
                .unlockedBy(hasIron)
                .save(recipes);

        //region Centrifuge
        RecipeCriteria hasNetherite = new RecipeCriteria("has_netherite", RecipeProvider.has(Tags.Items.INGOTS_NETHERITE));
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_CASING)
                .pattern("IWI","WHW","IWI")
                .define('H', Ingredient.of(ModTags.Items.HONEYCOMB_BLOCK))
                .define('I', Ingredient.of(Tags.Items.INGOTS_IRON))
                .define('W', Ingredient.of(ModItems.WAXED_MACHINE_BLOCK.get()))
                .unlockedBy(hasIron)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_PROCESSOR)
                .pattern("NCN","CLC","NCN")
                .define('L', Ingredient.of(Items.CLOCK))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('N', Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(hasNetherite)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_GEARBOX)
                .pattern("NCN","CRC","NCN")
                .define('R', Ingredient.of(Items.REPEATER))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('N', Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(hasNetherite)
                .save(recipes);
        //region Terminal
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_BASIC_TERMINAL)
                .pattern("OCO","CFC","OCO")
                .define('F', Ingredient.of(ModItems.CENTRIFUGE.get()))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('O', Ingredient.of(Tags.Items.OBSIDIAN))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ADVANCED_TERMINAL)
                .pattern("NCN","CTC","NCN")
                .define('T', Ingredient.of(ModItems.CENTRIFUGE_BASIC_TERMINAL.get()))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('N', Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(hasNetherite)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ELITE_TERMINAL)
                .pattern("NGN","GTG","NGN")
                .define('T', Ingredient.of(ModItems.CENTRIFUGE_ADVANCED_TERMINAL.get()))
                .define('G', Ingredient.of(ModItems.CENTRIFUGE_GEARBOX.get()))
                .define('N', Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(hasNetherite)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ULTIMATE_TERMINAL)
                .pattern("NPN","STS","NPN")
                .define('T', Ingredient.of(ModItems.CENTRIFUGE_ELITE_TERMINAL.get()))
                .define('P', Ingredient.of(ModItems.CENTRIFUGE_PROCESSOR.get()))
                .define('N', Ingredient.of(Tags.Items.STORAGE_BLOCKS_NETHERITE))
                .define('S', Ingredient.of(Tags.Items.NETHER_STARS))
                .unlockedBy(hasNetherite)
                .save(recipes);
        //endregion
        //region Void
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_BASIC_VOID)
                .pattern("OCO","CEC","OCO")
                .define('E', Ingredient.of(Tags.Items.CHESTS_ENDER))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('O', Ingredient.of(Tags.Items.OBSIDIAN))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ADVANCED_VOID)
                .pattern("ECE","CVC","ECE")
                .define('V', Ingredient.of(ModItems.CENTRIFUGE_BASIC_VOID.get()))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('E', Ingredient.of(Items.ENDER_EYE))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ELITE_VOID)
                .pattern("NEN","EVE","NEN")
                .define('V', Ingredient.of(ModItems.CENTRIFUGE_ADVANCED_VOID.get()))
                .define('E', Ingredient.of(Items.ENDER_EYE))
                .define('N', Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(hasNetherite)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ULTIMATE_VOID)
                .pattern("NEN","STS","NEN")
                .define('T', Ingredient.of(ModItems.CENTRIFUGE_ELITE_VOID.get()))
                .define('E', Ingredient.of(Items.END_ROD))
                .define('N', Ingredient.of(Tags.Items.STORAGE_BLOCKS_NETHERITE))
                .define('S', Ingredient.of(Tags.Items.NETHER_STARS))
                .unlockedBy(hasNetherite)
                .save(recipes);
        //endregion
        //region Input
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_BASIC_INPUT)
                .pattern("OCO","CHC","OCO")
                .define('H', Ingredient.of(Items.HOPPER))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('O', Ingredient.of(Tags.Items.OBSIDIAN))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ADVANCED_INPUT)
                .pattern("HCH","CIC","HCH")
                .define('I', Ingredient.of(ModItems.CENTRIFUGE_BASIC_INPUT.get()))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('H', Ingredient.of(Items.HOPPER))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ELITE_INPUT)
                .pattern("NHN","HIH","NHN")
                .define('I', Ingredient.of(ModItems.CENTRIFUGE_ADVANCED_INPUT.get()))
                .define('H', Ingredient.of(Items.HOPPER))
                .define('N', Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(hasNetherite)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ULTIMATE_INPUT)
                .pattern("NHN","SIS","NHN")
                .define('I', Ingredient.of(ModItems.CENTRIFUGE_ELITE_INPUT.get()))
                .define('H', Ingredient.of(Items.HOPPER))
                .define('N', Ingredient.of(Tags.Items.STORAGE_BLOCKS_NETHERITE))
                .define('S', Ingredient.of(Tags.Items.NETHER_STARS))
                .unlockedBy(hasNetherite)
                .save(recipes);
        //endregion
        //region Energy Input
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_BASIC_ENERGY_PORT)
                .pattern("OCO","CGC","OCO")
                .define('G', Ingredient.of(ModItems.HONEY_GENERATOR_ITEM.get()))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('O', Ingredient.of(Tags.Items.OBSIDIAN))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ADVANCED_ENERGY_PORT)
                .pattern("RCR","CEC","RCR")
                .define('E', Ingredient.of(ModItems.CENTRIFUGE_BASIC_ENERGY_PORT.get()))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('R', Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ELITE_ENERGY_PORT)
                .pattern("NRN","RER","NRN")
                .define('E', Ingredient.of(ModItems.CENTRIFUGE_ADVANCED_ENERGY_PORT.get()))
                .define('R', Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .define('N', Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(hasNetherite)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ULTIMATE_ENERGY_PORT)
                .pattern("NRN","SES","NRN")
                .define('E', Ingredient.of(ModItems.CENTRIFUGE_ELITE_ENERGY_PORT.get()))
                .define('R', Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .define('N', Ingredient.of(Tags.Items.STORAGE_BLOCKS_NETHERITE))
                .define('S', Ingredient.of(Tags.Items.NETHER_STARS))
                .unlockedBy(hasNetherite)
                .save(recipes);
        //endregion
        //region Item Output
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_BASIC_ITEM_OUTPUT)
                .pattern("OCO","CHC","OCO")
                .define('H', Ingredient.of(Tags.Items.CHESTS))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('O', Ingredient.of(Tags.Items.OBSIDIAN))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ADVANCED_ITEM_OUTPUT)
                .pattern("HCH","COC","HCH")
                .define('O', Ingredient.of(ModItems.CENTRIFUGE_BASIC_ITEM_OUTPUT.get()))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('H', Ingredient.of(Tags.Items.CHESTS))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ELITE_ITEM_OUTPUT)
                .pattern("NHN","HOH","NHN")
                .define('O', Ingredient.of(ModItems.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get()))
                .define('H', Ingredient.of(Tags.Items.CHESTS))
                .define('N', Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(hasNetherite)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT)
                .pattern("NHN","SOS","NHN")
                .define('O', Ingredient.of(ModItems.CENTRIFUGE_ELITE_ITEM_OUTPUT.get()))
                .define('H', Ingredient.of(Tags.Items.CHESTS))
                .define('N', Ingredient.of(Tags.Items.STORAGE_BLOCKS_NETHERITE))
                .define('S', Ingredient.of(Tags.Items.NETHER_STARS))
                .unlockedBy(hasNetherite)
                .save(recipes);
        //endregion
        //region Fluid Output
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_BASIC_FLUID_OUTPUT)
                .pattern("OCO","CHC","OCO")
                .define('H', Ingredient.of(ModItems.HONEY_POT_ITEM.get()))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('O', Ingredient.of(Tags.Items.OBSIDIAN))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ADVANCED_FLUID_OUTPUT)
                .pattern("BCB","COC","BCB")
                .define('O', Ingredient.of(ModItems.CENTRIFUGE_BASIC_FLUID_OUTPUT.get()))
                .define('C', Ingredient.of(ModItems.CENTRIFUGE_CASING.get()))
                .define('B', Ingredient.of(Items.BUCKET))
                .unlockedBy(hasObsidian)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ELITE_FLUID_OUTPUT)
                .pattern("NBN","BOB","NBN")
                .define('O', Ingredient.of(ModItems.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get()))
                .define('B', Ingredient.of(Items.BUCKET))
                .define('N', Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(hasNetherite)
                .save(recipes);
        AdvancedShapedRecipeBuilder.shaped(ModItems.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT)
                .pattern("NBN","SOS","NBN")
                .define('O', Ingredient.of(ModItems.CENTRIFUGE_ELITE_FLUID_OUTPUT.get()))
                .define('B', Ingredient.of(Items.BUCKET))
                .define('N', Ingredient.of(Tags.Items.STORAGE_BLOCKS_NETHERITE))
                .define('S', Ingredient.of(Tags.Items.NETHER_STARS))
                .unlockedBy(hasNetherite)
                .save(recipes);
        //endregion
        //endregion
        //endregion
    }

}
