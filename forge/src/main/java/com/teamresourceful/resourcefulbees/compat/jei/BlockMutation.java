package com.teamresourceful.resourcefulbees.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.BlockOutput;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.ModBlocks;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockMutation extends BaseCategory<BlockMutation.Recipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "block_mutation");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    public BlockMutation(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.block_mutation"),
                guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(new ItemStack(ModItems.MUTATION_BLOCK_ICON.get())),
                BlockMutation.Recipe.class);
    }

    public static List<Recipe> getMutationRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().values().forEach((beeData ->  {
            MutationData mutationData = beeData.getMutationData();
            if (mutationData.hasMutation() && !mutationData.getBlockMutations().isEmpty()) {
                mutationData.getBlockMutations().forEach((block, collection) -> collection
                        .forEach(blockOutput -> recipes.add(new Recipe(block, blockOutput, blockOutput.getChance(), RecipeUtils.getEffectiveWeight(collection, blockOutput.getWeight()), beeData))));
            }
        }));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.blockInput instanceof LiquidBlock) {
            RecipeUtils.setFluidInput(ingredients, null, recipe.blockInput);
        } else {
            RecipeUtils.setBlockInput(ingredients, null, recipe.blockInput);
        }

        if (recipe.blockOutput.getBlock() instanceof LiquidBlock) {
            RecipeUtils.setFluidOutput(recipe.blockOutput.getBlock(), recipe.blockOutput.getCompoundNBT().orElse(new CompoundTag()), ingredients);
        } else {
            RecipeUtils.setBlockOutput(recipe.blockOutput.getBlock(), recipe.blockOutput.getCompoundNBT().orElse(new CompoundTag()), ingredients);
        }

        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        List<Component> list = RecipeUtils.getTooltipStrings(mouseX, mouseY, recipe.chance);
        return list.isEmpty() ? super.getTooltipStrings(recipe, mouseX, mouseY) : list;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.blockInput instanceof LiquidBlock) {
            if (recipe.blockOutput.getBlock() instanceof LiquidBlock) {
                setFluidToFluidRecipe(iRecipeLayout, recipe, ingredients);
            } else {
                setFluidToBlockRecipe(iRecipeLayout, recipe, ingredients);
            }
        } else {
            if (recipe.blockOutput.getBlock() instanceof LiquidBlock) {
                setBlockToFluidRecipe(iRecipeLayout, recipe, ingredients);
            } else {
                setBlockToBlockRecipe(iRecipeLayout, recipe, ingredients);
            }
        }
    }

    public void setBlockToBlockRecipe(IRecipeLayout iRecipeLayout, Recipe recipe, IIngredients ingredients) {
        RecipeUtils.addMutationToolTip(recipe, RecipeUtils.setGuiItemStacksGroup(iRecipeLayout, ingredients));
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    public void setBlockToFluidRecipe(IRecipeLayout iRecipeLayout, Recipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
        fluidStacks.init(0, true, 66, 49);
        itemStacks.init(0, false, 15, 57);
        fluidStacks.set(0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        itemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        addMutationTooltip(recipe, itemStacks, fluidStacks);
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    public void setFluidToBlockRecipe(IRecipeLayout iRecipeLayout, Recipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
        fluidStacks.init(0, false, 16, 58);
        itemStacks.init(0, true, 65, 48);
        fluidStacks.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        itemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        addMutationTooltip(recipe, itemStacks, fluidStacks);
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    private void addMutationTooltip(Recipe recipe, IGuiItemStackGroup itemStacks, IGuiFluidStackGroup fluidStacks) {
        RecipeUtils.addMutationToolTip(recipe, itemStacks);
        RecipeUtils.addMutationToolTip(recipe, fluidStacks);
    }

    public void setFluidToFluidRecipe(IRecipeLayout iRecipeLayout, Recipe recipe, IIngredients ingredients) {
        IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
        fluidStacks.init(0, false, 66, 49);
        fluidStacks.init(1, true, 16, 58);
        fluidStacks.set(0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        fluidStacks.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        RecipeUtils.addMutationToolTip(recipe, fluidStacks);
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @Override
    public void draw(Recipe recipe, @NotNull PoseStack stack, double mouseX, double mouseY) {
        RecipeUtils.drawMutationScreen(stack, this.beeHive, this.info, recipe.weight, recipe.chance);
    }

    protected static class Recipe {
        private final @Nullable Block blockInput;
        private final @NotNull BlockOutput blockOutput;
        private final double chance;
        private final double weight;
        private final @NotNull CustomBeeData beeType;

        public Recipe(@Nullable Block blockInput, @NotNull BlockOutput blockOutput, double chance, double weight, @NotNull CustomBeeData beeType) {
            this.blockInput = blockInput;
            this.blockOutput = blockOutput;
            this.chance = chance;
            this.weight = weight;
            this.beeType = beeType;
        }

        public @NotNull BlockOutput getBlockOutput() {
            return blockOutput;
        }
    }
}
