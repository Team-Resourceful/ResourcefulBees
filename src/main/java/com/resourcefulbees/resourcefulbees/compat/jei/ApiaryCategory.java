package com.resourcefulbees.resourcefulbees.compat.jei;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.config.BeeInfo;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.data.BeeData;
import com.resourcefulbees.resourcefulbees.lib.ApiaryOutput;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.utils.NBTHelper;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ApiaryCategory implements IRecipeCategory<ApiaryCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beehive.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "apiary");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public ApiaryCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 26).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.T1_APIARY_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.apiary");
    }

    public static List<Recipe> getHoneycombRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();
        final List<ApiaryOutput> outputs = new ArrayList<>(Arrays.asList(Config.T1_APIARY_OUTPUT.get(), Config.T2_APIARY_OUTPUT.get(), Config.T3_APIARY_OUTPUT.get(), Config.T4_APIARY_OUTPUT.get()));
        final int[] outputQuantities = {Config.T1_APIARY_QUANTITY.get(), Config.T2_APIARY_QUANTITY.get(), Config.T3_APIARY_QUANTITY.get(), Config.T4_APIARY_QUANTITY.get()};
        final List<Item> apiaryTiers = new ArrayList<>(Arrays.asList(RegistryHandler.T1_APIARY_ITEM.get(), RegistryHandler.T2_APIARY_ITEM.get(), RegistryHandler.T3_APIARY_ITEM.get(), RegistryHandler.T4_APIARY_ITEM.get()));

        for (Map.Entry<String, BeeData> bee : BeeInfo.getBees().entrySet()){
            if (!bee.getKey().equals(BeeConstants.DEFAULT_BEE_TYPE) && bee.getValue().getHoneycombColor() != null && !bee.getValue().getHoneycombColor().isEmpty()) {
                for (int i = 0; i < 4; i++){
                    Item outputItem = outputs.get(i).equals(ApiaryOutput.COMB) ? RegistryHandler.RESOURCEFUL_HONEYCOMB.get() : RegistryHandler.HONEYCOMB_BLOCK_ITEM.get();
                    ItemStack outputStack = new ItemStack(outputItem, outputQuantities[i]);
                    outputStack.setTag(NBTHelper.createHoneycombItemTag(bee.getKey()));
                    recipes.add(new Recipe(outputStack, bee.getKey(), new ItemStack(apiaryTiers.get(i))));
                }
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
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getComb());
        ingredients.setInput(VanillaTypes.ITEM, recipe.apiary);
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
        //private final int outputQuantity;
        private final ItemStack apiary;

        public Recipe(ItemStack comb, String beeType, ItemStack apiary) {
            this.comb = comb;
            this.beeType = beeType;
            //this.outputQuantity = outputQuantity;
            this.apiary = apiary;
        }

        public ItemStack getComb() {
            return this.comb;
        }
        public String getBeeType() {
            return this.beeType;
        }
        //public int getOutputQuantity() { return this.outputQuantity; }
        public ItemStack getApiary() { return this.apiary; }
    }
}