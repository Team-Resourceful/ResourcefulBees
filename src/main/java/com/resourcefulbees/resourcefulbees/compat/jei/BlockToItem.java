package com.resourcefulbees.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.ItemOutput;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockToItem implements IRecipeCategory<BlockToItem.Recipe> {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ICONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "block_to_item_mutation");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable info;
    private final IDrawable beeHive;
    private final String localizedName;

    public BlockToItem(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawable(ICONS, 0, 0, 16, 16);
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.beeHive = guiHelper.createDrawableIngredient(new ItemStack(ModItems.T1_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.block_to_item_mutation");
    }

    public static List<Recipe> getMutationRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            MutationData mutationData = beeData.getMutationData();
            if (mutationData.hasMutation()) {
                if (mutationData.hasBlockTagItemMutations()) {
                    mutationData.getJeiBlockTagItemMutations()
                            .forEach((iTag, doubleRandomCollectionPair) -> doubleRandomCollectionPair.getRight()
                                    .forEach(itemOutput -> recipes.add(new Recipe(null, iTag, itemOutput, doubleRandomCollectionPair.getLeft(), RecipeUtils.getEffectiveWeight(doubleRandomCollectionPair.getRight(), itemOutput.getWeight()), s)))
                            );
                }
                if (mutationData.hasJeiItemMutations()) {
                    mutationData.getJeiItemMutations()
                            .forEach((block, doubleRandomCollectionPair) -> doubleRandomCollectionPair.getRight()
                                    .forEach(itemOutput -> recipes.add(new Recipe(block, null, itemOutput, doubleRandomCollectionPair.getLeft(), RecipeUtils.getEffectiveWeight(doubleRandomCollectionPair.getRight(), itemOutput.getWeight()), s)))
                            );
                }
            }
        }));
        return recipes;
    }

    @Override
    public @NotNull ResourceLocation getUid() {
        return ID;
    }

    @Override
    public @NotNull Class<? extends Recipe> getRecipeClass() {
        return Recipe.class;
    }

    @Override
    public @NotNull String getTitle() {
        return this.localizedName;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(BlockToItem.Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.tagInput != null && recipe.tagInput.values().get(0) instanceof Fluid) {
            RecipeUtils.setFluidInput(ingredients, recipe.tagInput, recipe.blockInput);
        } else {
            RecipeUtils.setBlockInput(ingredients, recipe.tagInput, recipe.blockInput);
        }

        ItemStack itemStack = new ItemStack(recipe.itemOutput.getItem());
        if (!recipe.itemOutput.getCompoundNBT().isEmpty()) {
            itemStack.setTag(recipe.itemOutput.getCompoundNBT());
        }
        ingredients.setOutput(VanillaTypes.ITEM, itemStack);
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        List<ITextComponent> list = RecipeUtils.getTooltipStrings(mouseX, mouseY, recipe.chance);
        return list.isEmpty() ? IRecipeCategory.super.getTooltipStrings(recipe, mouseX, mouseY) : list;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, BlockToItem.@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        RecipeUtils.setGuiItemStacksGroup(iRecipeLayout, ingredients);

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
        private final @NotNull ItemOutput itemOutput;
        private final double chance;
        private final double weight;
        private final @NotNull String beeType;

        public Recipe(@Nullable Block blockInput, @Nullable ITag<?> tagInput, @NotNull ItemOutput itemOutput, double chance, double weight, @NotNull String beeType) {
            this.blockInput = blockInput;
            this.tagInput = tagInput;
            this.itemOutput = itemOutput;
            this.chance = chance;
            this.weight = weight;
            this.beeType = beeType;
        }
    }
}
