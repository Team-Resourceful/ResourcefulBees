package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.NBTHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.dungeonderps.resourcefulbees.config.BeeInfo.BEE_INFO;

public class BeeHiveCategory implements IRecipeCategory<BeeHiveCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beehive.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "hive");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public BeeHiveCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 26).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.T1_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.hive");
    }

    public static List<Recipe> getHoneycombRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BEE_INFO.entrySet()){
            if (!bee.getKey().equals(BeeConstants.DEFAULT_BEE_TYPE) && bee.getValue().getHoneycombColor() != null && !bee.getValue().getHoneycombColor().isEmpty()) {
                ItemStack honeyCombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
                honeyCombItemStack.setTag(NBTHelper.createHoneycombItemTag(bee.getKey()));
                recipes.add(new Recipe(honeyCombItemStack, bee.getKey()));
            }
        }
        return recipes;
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<? extends Recipe> getRecipeClass() {
        return Recipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return this.localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(Recipe recipe, IIngredients ingredients) {
        List<Ingredient> list = new ArrayList<>(Collections.singletonList(Ingredient.fromItems(RegistryHandler.T1_BEEHIVE_ITEM.get(), RegistryHandler.T2_BEEHIVE_ITEM.get(), RegistryHandler.T3_BEEHIVE_ITEM.get(), RegistryHandler.T4_BEEHIVE_ITEM.get())));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getComb());
        ingredients.setInputIngredients(list);
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
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
        private final String beeType;

        public Recipe(ItemStack comb, String beeType) {
            this.comb = comb;
            this.beeType = beeType;
        }

        public ItemStack getComb() {
            return this.comb;
        }
        public String getBeeType() {
            return this.beeType;
        }
    }
}