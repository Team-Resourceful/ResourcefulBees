package com.resourcefulbees.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.ItemOutput;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockToItem extends BaseCategory<BlockToItem.Recipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "block_to_item_mutation");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    public BlockToItem(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.block_to_item_mutation"),
                guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawable(ICONS, 0, 0, 16, 16),
                BlockToItem.Recipe.class);
    }

    public static List<Recipe> getMutationRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            MutationData mutationData = beeData.getMutationData();
            if (mutationData.hasMutation()) {
                if (mutationData.hasJeiBlockTagItemMutations()) {
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
    public void setIngredients(BlockToItem.Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.tagInput != null && recipe.tagInput.getValues().get(0) instanceof Fluid) {
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
        return list.isEmpty() ? super.getTooltipStrings(recipe, mouseX, mouseY) : list;
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
