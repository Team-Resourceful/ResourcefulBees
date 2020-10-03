package com.resourcefulbees.resourcefulbees.compat.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.*;

public class BeeBreedingCategory implements IRecipeCategory<BeeBreedingCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/breeding.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "breeding");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;
    private static final IBeeRegistry beeRegistry = BeeRegistry.getRegistry();


    public BeeBreedingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 60).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.GOLD_FLOWER.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.breeding");
    }

    public static List<Recipe> getBreedingRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();

        beeRegistry.getBees().forEach(((s, beeData) -> {
            if (beeData.getBreedData().isBreedable()) {
                if (beeData.getBreedData().hasParents()){
                    if (beeRegistry.getBees().containsKey(beeData.getBreedData().getParent1()) && beeRegistry.getBees().containsKey(beeData.getBreedData().getParent2())) {
                        String parent1 = beeData.getBreedData().getParent1();
                        String parent2 = beeData.getBreedData().getParent2();

                        int p1_feedAmount = beeRegistry.getBeeData(parent1).getBreedData().getFeedAmount();
                        int p2_feedAmount = beeRegistry.getBeeData(parent2).getBreedData().getFeedAmount();

                        String p1_feedItemS = finalizeFeedItem(beeRegistry.getBeeData(parent1).getBreedData().getFeedItem());
                        String p2_feedItemS = finalizeFeedItem(beeRegistry.getBeeData(parent2).getBreedData().getFeedItem());

                        ITag<Item> p1_feedTag = BeeInfoUtils.getItemTag(p1_feedItemS);
                        ITag<Item> p2_feedTag = BeeInfoUtils.getItemTag(p2_feedItemS);
                        Item p1_feedItem = BeeInfoUtils.getItem(p1_feedItemS);
                        Item p2_feedItem = BeeInfoUtils.getItem(p2_feedItemS);

                        recipes.add(new Recipe(parent1, p1_feedTag, p1_feedItem, p1_feedAmount, parent2, p2_feedTag, p2_feedItem, p2_feedAmount, beeData.getName()));
                    }
                }
                int feedAmount = beeRegistry.getBeeData(beeData.getName()).getBreedData().getFeedAmount();
                String feedItemS = finalizeFeedItem(beeRegistry.getBeeData(beeData.getName()).getBreedData().getFeedItem());

                ITag<Item> feedTag = BeeInfoUtils.getItemTag(feedItemS);
                Item feedItem = BeeInfoUtils.getItem(feedItemS);

                recipes.add(new Recipe(beeData.getName(), feedTag, feedItem, feedAmount, beeData.getName(), feedTag, feedItem, feedAmount, beeData.getName()));
            }
        }));
        return recipes;
    }

    private static String finalizeFeedItem(String feedItem) {
        if (ValidatorUtils.TAG_RESOURCE_PATTERN.matcher(feedItem).matches()) {
            feedItem = feedItem.replace(BeeConstants.TAG_PREFIX, "");
        } else if (feedItem.equals(FLOWER_TAG_ALL)) {
            feedItem = "minecraft:flowers";
        } else if (feedItem.equals(FLOWER_TAG_TALL)) {
            feedItem = "minecraft:tall_flowers";
        } else if (feedItem.equals(FLOWER_TAG_SMALL)) {
            feedItem = "minecraft:small_flowers";
        }
        return feedItem;
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
    public void setIngredients(@Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
        List<List<ItemStack>> list = new ArrayList<>();

        if (recipe.p1_feedItem != null && recipe.p1_feedItem != Items.AIR) {
            List<ItemStack> stackList = new ArrayList<>();
            stackList.add(new ItemStack(recipe.p1_feedItem, recipe.p1_feedAmount));
            list.add(stackList);
        }
        if (recipe.p2_feedItem != null && recipe.p2_feedItem != Items.AIR) {
            List<ItemStack> stackList = new ArrayList<>();
            stackList.add(new ItemStack(recipe.p2_feedItem, recipe.p2_feedAmount));
            list.add(stackList);
        }
        if (recipe.p1_feedTag != null) {
            List<ItemStack> stackList = new ArrayList<>();
            for (Item item : recipe.p1_feedTag.values()) {
                stackList.add(new ItemStack(item, recipe.p1_feedAmount));
            }
            list.add(stackList);
        }
        if (recipe.p2_feedTag != null) {
            List<ItemStack> stackList = new ArrayList<>();
            for (Item item : recipe.p2_feedTag.values()) {
                stackList.add(new ItemStack(item, recipe.p2_feedAmount));
            }
            list.add(stackList);
        }

        ingredients.setInputLists(VanillaTypes.ITEM, list);

        List<EntityIngredient> entities = new ArrayList<>();
        entities.add(new EntityIngredient(recipe.parent1, -45.0f));
        entities.add(new EntityIngredient(recipe.parent2, 45.0F));

        ingredients.setInputs(JEICompat.ENTITY_INGREDIENT, entities);
        ingredients.setOutput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.child, -45.0f));
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
        ResourcefulBees.LOGGER.info("{} + {} + {} + {} + {} + {} + {} + {} + {}", recipe.child, recipe.parent1, recipe.p1_feedAmount, recipe.p1_feedItem, recipe.p1_feedTag, recipe.parent2, recipe.p2_feedAmount, recipe.p2_feedItem, recipe.p2_feedTag);
        //Logs all recipe info ^

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);

        System.out.println(ingredientStacks.toString());//LOGGERRRRRRR
        System.out.println(ingredients.getInputs(JEICompat.ENTITY_INGREDIENT));
        System.out.println(ingredients.getOutputs(JEICompat.ENTITY_INGREDIENT));
        System.out.println(ingredients.getInputs(VanillaTypes.ITEM));
        System.out.println(ingredients.getOutputs(VanillaTypes.ITEM));

        ingredientStacks.init(0, true, 6, 6);
        ingredientStacks.init(1, true, 60, 6);
        ingredientStacks.init(2, false, 130, 18);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
        ingredientStacks.set(1, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(1));
        ingredientStacks.set(2, ingredients.getOutputs(JEICompat.ENTITY_INGREDIENT).get(0));

        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();

        System.out.println(itemStacks.toString());//LOGGGERRRRRRR

        itemStacks.init(0, true, 6, 32);
        itemStacks.init(1, true, 60, 32);
        itemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
    }

    @Override
    public void draw(Recipe recipe, @Nonnull MatrixStack matrix, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontRenderer = minecraft.fontRenderer;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        fontRenderer.draw(matrix, decimalFormat.format(beeRegistry.getAdjustedWeightForChild(beeRegistry.getBeeData(recipe.child), recipe.parent1.equals(recipe.parent2))), 90, 35, 0xff808080);
    }

    public static class Recipe {
        private final String parent1;
        private final String parent2;
        private final String child;

        private final ITag<Item> p1_feedTag;
        private final Item p1_feedItem;

        private final ITag<Item> p2_feedTag;
        private final Item p2_feedItem;

        private final int p1_feedAmount;
        private final int p2_feedAmount;

        public Recipe(String parent1, ITag<Item> p1_feedTag, Item p1_feedItem, int p1_feedAmount, String parent2, ITag<Item> p2_feedTag, Item p2_feedItem, int p2_feedAmount, String child) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.child = child;
            this.p1_feedItem = p1_feedItem;
            this.p2_feedItem = p2_feedItem;
            this.p1_feedTag = p1_feedTag;
            this.p2_feedTag = p2_feedTag;
            this.p1_feedAmount = p1_feedAmount;
            this.p2_feedAmount = p2_feedAmount;
        }
    }
}
