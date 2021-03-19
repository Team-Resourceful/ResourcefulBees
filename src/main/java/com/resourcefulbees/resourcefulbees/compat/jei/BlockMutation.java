package com.resourcefulbees.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.BlockOutput;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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
                guiHelper.createDrawable(ICONS, 0, 0, 16, 16),
                BlockMutation.Recipe.class);
    }

    public static List<Recipe> getMutationRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            MutationData mutationData = beeData.getMutationData();
            if (mutationData.hasMutation()) {
                if (mutationData.hasBlockTagMutations()) {
                    mutationData.getJeiBlockTagMutations()
                            .forEach((iTag, doubleRandomCollectionPair) -> doubleRandomCollectionPair.getRight()
                                    .forEach(blockOutput -> recipes.add(new Recipe(null, iTag, blockOutput, doubleRandomCollectionPair.getLeft(), RecipeUtils.getEffectiveWeight(doubleRandomCollectionPair.getRight(), blockOutput.getWeight()), s))));
                }
                if (mutationData.hasJeiBlockMutations()) {
                    mutationData.getJeiBlockMutations()
                            .forEach((block, doubleRandomCollectionPair) -> doubleRandomCollectionPair.getRight()
                                    .forEach(blockOutput -> recipes.add(new Recipe(block, null, blockOutput, doubleRandomCollectionPair.getLeft(), RecipeUtils.getEffectiveWeight(doubleRandomCollectionPair.getRight(), blockOutput.getWeight()), s))));
                }
            }
        }));
        return recipes;
    }

    @Override
    public void setIngredients(Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.blockInput instanceof FlowingFluidBlock || (recipe.tagInput != null && recipe.tagInput.getValues().get(0) instanceof Fluid)) {
            RecipeUtils.setFluidInput(ingredients, recipe.tagInput, recipe.blockInput);
        } else {
            RecipeUtils.setBlockInput(ingredients, recipe.tagInput, recipe.blockInput);
        }

        if (recipe.blockOutput.getBlock() instanceof FlowingFluidBlock) {
            RecipeUtils.setFluidOutput(recipe.blockOutput.getBlock(), recipe.blockOutput.getCompoundNBT(), ingredients);
        } else {
            RecipeUtils.setBlockOutput(recipe.blockOutput.getBlock(), recipe.blockOutput.getCompoundNBT(), ingredients);
        }

        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        List<ITextComponent> list = RecipeUtils.getTooltipStrings(mouseX, mouseY, recipe.chance);
        return list.isEmpty() ? super.getTooltipStrings(recipe, mouseX, mouseY) : list;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.blockInput instanceof FlowingFluidBlock || (recipe.tagInput != null && recipe.tagInput.getValues().get(0) instanceof Fluid)) {
            if (recipe.blockOutput.getBlock() instanceof FlowingFluidBlock) {
                setFluidToFluidRecipe(iRecipeLayout, recipe, ingredients);
            } else {
                setFluidToBlockRecipe(iRecipeLayout, recipe, ingredients);
            }
        } else {
            if (recipe.blockOutput.getBlock() instanceof FlowingFluidBlock) {
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
    public void draw(Recipe recipe, @NotNull MatrixStack stack, double mouseX, double mouseY) {
        RecipeUtils.drawMutationScreen(stack, this.beeHive, this.info, recipe.weight, recipe.chance);
    }

    protected static class Recipe {
        private final @Nullable Block blockInput;
        private final @Nullable ITag<?> tagInput;
        private final @NotNull BlockOutput blockOutput;
        private final double chance;
        private final double weight;
        private final @NotNull String beeType;

        public Recipe(@Nullable Block blockInput, @Nullable ITag<?> tagInput, @NotNull BlockOutput blockOutput, double chance, double weight, @NotNull String beeType) {
            this.blockInput = blockInput;
            this.tagInput = tagInput;
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
