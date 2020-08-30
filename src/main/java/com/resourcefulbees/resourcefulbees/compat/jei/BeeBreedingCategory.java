package com.resourcefulbees.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.config.BeeInfo;
import com.resourcefulbees.resourcefulbees.data.BeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.BeeValidator;
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
import java.util.List;
import java.util.Map;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.*;

public class BeeBreedingCategory implements IRecipeCategory<BeeBreedingCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/breeding.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "breeding");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public BeeBreedingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 60).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.GOLD_FLOWER.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.breeding");
    }

    public static List<Recipe> getBreedingRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> entry : BeeInfo.getBees().entrySet()){
            BeeData bee = entry.getValue();
            if (bee.isBreedable()){
                if (BeeInfo.getBees().containsKey(bee.getParent1()) && BeeInfo.getBees().containsKey(bee.getParent2())) {
                    String p1_feedItemS = BeeInfo.getInfo(bee.getParent1()).getFeedItem();
                    String p2_feedItemS = BeeInfo.getInfo(bee.getParent2()).getFeedItem();

                    int p1_feedAmount = BeeInfo.getInfo(bee.getParent1()).getFeedAmount();
                    int p2_feedAmount = BeeInfo.getInfo(bee.getParent2()).getFeedAmount();

                    p1_feedItemS = finalizeFeedItem(p1_feedItemS);
                    p2_feedItemS = finalizeFeedItem(p2_feedItemS);

                    ITag<Item> p1_feedTag = BeeInfoUtils.getItemTag(p1_feedItemS);
                    ITag<Item> p2_feedTag = BeeInfoUtils.getItemTag(p2_feedItemS);
                    Item p1_feedItem = BeeInfoUtils.getItem(p1_feedItemS);
                    Item p2_feedItem = BeeInfoUtils.getItem(p2_feedItemS);

                    recipes.add(new Recipe(bee.getParent1(), p1_feedTag, p1_feedItem, p1_feedAmount, bee.getParent2(), p2_feedTag, p2_feedItem, p2_feedAmount, entry.getKey()));
                }
            }
        }
        return recipes;
    }

    private static String finalizeFeedItem(String feedItem) {
        if (BeeValidator.TAG_RESOURCE_PATTERN.matcher(feedItem).matches()) {
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
        ItemStack parent1SpawnEgg = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());
        ItemStack parent2SpawnEgg = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());
        ItemStack childSpawnEgg = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());

        parent1SpawnEgg.setTag(NBTHelper.createSpawnEggTag(recipe.parent1));
        parent2SpawnEgg.setTag(NBTHelper.createSpawnEggTag(recipe.parent2));
        childSpawnEgg.setTag(NBTHelper.createSpawnEggTag(recipe.child));

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
            for (Item item : recipe.p1_feedTag.getAllElements()) {
                stackList.add(new ItemStack(item, recipe.p1_feedAmount));
            }
            list.add(stackList);
        }
        if (recipe.p2_feedTag != null) {
            List<ItemStack> stackList = new ArrayList<>();
            for (Item item : recipe.p2_feedTag.getAllElements()) {
                stackList.add(new ItemStack(item, recipe.p2_feedAmount));
            }
            list.add(stackList);
        }

        list.add(Arrays.asList(parent1SpawnEgg, parent2SpawnEgg));
        ingredients.setInputLists(VanillaTypes.ITEM, list);
        ingredients.setOutput(VanillaTypes.ITEM, childSpawnEgg);

        List<EntityIngredient> entities = new ArrayList<>();
        entities.add(new EntityIngredient(recipe.parent1, -45.0f));
        entities.add(new EntityIngredient(recipe.parent2, 45.0F));

        ingredients.setInputs(JEICompat.ENTITY_INGREDIENT, entities);
        ingredients.setOutput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.child, -45.0f));
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 6, 6);
        ingredientStacks.init(1, true, 60, 6);
        ingredientStacks.init(2, false, 130, 18);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
        ingredientStacks.set(1, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(1));
        ingredientStacks.set(2, ingredients.getOutputs(JEICompat.ENTITY_INGREDIENT).get(0));

        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
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
        fontRenderer.drawString(matrix, decimalFormat.format(BeeInfo.getAdjustedWeightForChild(BeeInfo.getInfo(recipe.getChild()))), 90, 35, 0xff808080);
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

        public String getParent1() {
            return this.parent1;
        }
        public String getParent2() {
            return this.parent2;
        }
        public String getChild() {
            return this.child;
        }
    }
}
