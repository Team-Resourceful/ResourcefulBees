package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dungeonderps.resourcefulbees.lib.BeeConstants.*;

public class FlowersCategory implements IRecipeCategory<FlowersCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beeflowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "bee_pollination_flowers");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public FlowersCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 24, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.GOLD_FLOWER.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.bee_pollination_flowers");
    }

    public static List<Recipe> getFlowersRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()){
            if (!bee.getValue().getName().equals(DEFAULT_BEE_TYPE)) {
                if (!bee.getValue().getFlower().isEmpty()) {
                    String flower = bee.getValue().getFlower();
                    if (BeeInfoUtils.TAG_RESOURCE_PATTERN.matcher(flower).matches()) {
                        flower = flower.replace(BeeConstants.TAG_PREFIX, "");

                        Tag<Item> itemTag = BeeInfoUtils.getItemTag(flower);
                        if (itemTag != null)
                            recipes.add(new Recipe(itemTag, bee.getKey(), true));
                    } else if (flower.equals(FLOWER_TAG_ALL)) {
                        Tag<Item> itemTag = ItemTags.FLOWERS;
                        if (itemTag != null)
                            recipes.add(new Recipe(itemTag, bee.getKey(), true));
                    } else if (flower.equals(FLOWER_TAG_SMALL)) {
                        Tag<Item> itemTag = ItemTags.SMALL_FLOWERS;
                        if (itemTag != null)
                            recipes.add(new Recipe(itemTag, bee.getKey(), true));
                    } else if (flower.equals(FLOWER_TAG_TALL)) {
                        Tag<Item> itemTag = ItemTags.TALL_FLOWERS;
                        if (itemTag != null)
                            recipes.add(new Recipe(itemTag, bee.getKey(), true));
                    } else {
                        Item itemIn = BeeInfoUtils.getItem(flower);
                        if (BeeInfoUtils.isValidItem(itemIn))
                            recipes.add(new Recipe(new ItemStack(itemIn), bee.getKey(), false));
                    }
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
    public void setIngredients(Recipe recipe, @Nonnull IIngredients ingredients) {
        if (recipe.isAcceptsAny()) {
                List<Ingredient> list = new ArrayList<>();
                list.add(Ingredient.fromTag(recipe.tag));
                ingredients.setInputIngredients(list);
        }
        else ingredients.setInput(VanillaTypes.ITEM, recipe.itemIn);
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, 135.0F));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(1, true, 3, 57);
        itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 4, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    public static class Recipe {
        private final ItemStack itemIn;
        private final String beeType;

        private final boolean acceptsAny;
        private final Tag<Item> tag;


        public Recipe(ItemStack flower, String beeType, boolean acceptsAny) {
            this.itemIn = flower;
            this.beeType = beeType;
            this.acceptsAny = acceptsAny;
            this.tag = null;
        }

        //TAGS!!!
        public Recipe(Tag<Item> flower, String beeType, boolean acceptsAny) {
            this.itemIn = null;
            this.beeType = beeType;
            this.acceptsAny = acceptsAny;
            this.tag = flower;
        }

        public boolean isAcceptsAny() { return acceptsAny; }
        public Tag<?> getTag() { return tag; }
        public String getBeeType() { return this.beeType; }
    }
}
