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
        RecipeHelper.createBoxed(Ingredient.of(ItemTags.PLANKS), Ingredient.of(Items.GRASS), ModItems.T1_NEST_UPGRADE.get())
                .unlockedBy(hasPlanks).save(recipes);
        RecipeHelper.createCornerWithMid(Ingredient.of(ItemTags.PLANKS), Ingredient.of(ModTags.Items.HONEYCOMB), Ingredient.of(ModTags.Items.WAX), ModItems.T2_NEST_UPGRADE.get())
                .unlockedBy(hasPlanks).save(recipes);
        RecipeHelper.createCornerWithMid(Ingredient.of(ItemTags.PLANKS), Ingredient.of(ModTags.Items.HONEYCOMB_BLOCK), Ingredient.of(ModTags.Items.WAX_BLOCK), ModItems.T3_NEST_UPGRADE.get())
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
        AdvancedShapedRecipeBuilder.shaped(ModItems.ENDER_BEECON_ITEM)
                .pattern("PPP","GEG","POP")
                .define('E', Ingredient.of(Tags.Items.ENDER_PEARLS))
                .define('P', Ingredient.of(Items.PURPUR_BLOCK))
                .define('G', Ingredient.of(Tags.Items.GLASS))
                .define('O', Ingredient.of(Tags.Items.OBSIDIAN))
                .unlockedBy("has_obsidian", RecipeProvider.has(Tags.Items.OBSIDIAN))
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
        //endregion
    }





}
