package com.resourcefulbees.resourcefulbees.compat.jei;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.lib.HoneycombTypes;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeeHiveCategory extends BaseCategory<BeeHiveCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beehive.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "hive");

    public BeeHiveCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.hive"),
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 26).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get())),
                BeeHiveCategory.Recipe.class);
    }

    public static List<Recipe> getHoneycombRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        BeeRegistry.getRegistry().getBees().forEach(((s, customBeeData) -> {
            if (customBeeData.getHoneycombData().getHoneycombType() != HoneycombTypes.NONE) {
                ItemStack honeyCombItemStack = customBeeData.getHoneycombData().getHoneycomb().getDefaultInstance();
                recipes.add(new Recipe(honeyCombItemStack, customBeeData));
            }
        }));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        List<Ingredient> list = new ArrayList<>(Collections.singletonList(Ingredient.of(Blocks.STONE)));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getComb());
        ingredients.setInputIngredients(list);
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(0, false, 138, 4);
        itemStacks.init(1, true, 62, 4);
        itemStacks.set(ingredients);

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 10, 2);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    public static class Recipe {
        private final ItemStack comb;
        private final CustomBeeData beeType;

        public Recipe(ItemStack comb, CustomBeeData beeType) {
            this.comb = comb;
            this.beeType = beeType;
        }

        public ItemStack getComb() {
            return this.comb;
        }
        public CustomBeeData getBeeType() {
            return this.beeType;
        }
    }
}