package com.resourcefulbees.resourcefulbees.compat.jei;

import com.google.common.base.Splitter;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.*;

public class BeeBreedingCategory extends BaseCategory<BeeBreedingCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/breeding.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "breeding");
    private static final IBeeRegistry beeRegistry = BeeRegistry.getRegistry();


    public BeeBreedingCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.breeding"),
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 60).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.GOLD_FLOWER.get())),
                BeeBreedingCategory.Recipe.class);
    }

    public static List<Recipe> getBreedingRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        beeRegistry.getSetOfBees().stream()
                .filter(BeeBreedingCategory::isBreedable)
                .forEach(beeData -> {
                    if (beeHasParents(beeData)) {

                        Iterator<String> parent1 = Splitter.on(",").trimResults().split(beeData.getBreedData().getParent1()).iterator();
                        Iterator<String> parent2 = Splitter.on(",").trimResults().split(beeData.getBreedData().getParent2()).iterator();

                        while (parent1.hasNext() && parent2.hasNext()) {
                            Recipe parentsRecipe = getParentsRecipe(beeData, parent1, parent2);
                            if (parentsRecipe != null) {
                                recipes.add(parentsRecipe);
                            }
                        }
                    }


                    recipes.add(getSelfRecipe(beeData));
                });
        return recipes;
    }

    private static boolean beeHasParents(CustomBeeData beeData) {
        return beeData.getBreedData().hasParents();
    }

    private static boolean isBreedable(CustomBeeData beeData) {
        return beeData.getBreedData().isBreedable();
    }

    private static Recipe getSelfRecipe(CustomBeeData beeData) {
        int feedAmount = beeData.getBreedData().getFeedAmount();
        String feedItemS = finalizeFeedItem(beeData.getBreedData().getFeedItem());

        ITag<Item> feedTag = TagCollectionManager.getInstance().getItems().getTag(new ResourceLocation(feedItemS));
        Item feedItem = BeeInfoUtils.getItem(feedItemS);

        if (BeeInfoUtils.isTag(beeData.getBreedData().getFeedItem())) feedItem = null;

        return new Recipe(beeData.getName(), feedTag, feedItem, feedAmount, beeData.getName(), feedTag, feedItem, feedAmount, beeData.getName(), 1);
    }

    private static Recipe getParentsRecipe(CustomBeeData beeData, Iterator<String> parent1, Iterator<String> parent2) {
        String p1 = parent1.next();
        String p2 = parent2.next();

        if (beeRegistry.getBees().containsKey(p1) && beeRegistry.getBees().containsKey(p2)) {
            CustomBeeData p1Data = beeRegistry.getBeeData(p1);
            CustomBeeData p2Data = beeRegistry.getBeeData(p2);

            String p1FeedItemS = finalizeFeedItem(p1Data.getBreedData().getFeedItem());
            String p2FeedItemS = finalizeFeedItem(p2Data.getBreedData().getFeedItem());

            ITag<Item> p1FeedTag = TagCollectionManager.getInstance().getItems().getTag(new ResourceLocation(p1FeedItemS));
            ITag<Item> p2FeedTag = TagCollectionManager.getInstance().getItems().getTag(new ResourceLocation(p2FeedItemS));

            Item p1FeedItem = BeeInfoUtils.getItem(p1FeedItemS);
            Item p2FeedItem = BeeInfoUtils.getItem(p2FeedItemS);

            if (BeeInfoUtils.isTag(p1Data.getBreedData().getFeedItem())) p1FeedItem = null;
            if (BeeInfoUtils.isTag(p2Data.getBreedData().getFeedItem())) p2FeedItem = null;

            return new Recipe(
                    p1Data.getName(), p1FeedTag, p1FeedItem, p1Data.getBreedData().getFeedAmount(),
                    p2Data.getName(), p2FeedTag, p2FeedItem, p2Data.getBreedData().getFeedAmount(),
                    beeData.getName(), beeData.getBreedData().getBreedChance());
        }

        return null;
    }

    private static String finalizeFeedItem(String feedItem) {
        if (ValidatorUtils.TAG_RESOURCE_PATTERN.matcher(feedItem).matches()) {
            return feedItem.replace(BeeConstants.TAG_PREFIX, "");
        } else if (feedItem.equals(FLOWER_TAG_TALL)) {
            return "minecraft:tall_flowers";
        } else if (feedItem.equals(FLOWER_TAG_SMALL)) {
            return "minecraft:small_flowers";
        } else if (feedItem.equals(FLOWER_TAG_ALL)) {
            return "minecraft:flowers";
        }
        return feedItem;
    }

    @Override
    public void setIngredients(@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        List<List<ItemStack>> list = new ArrayList<>();

        if (recipe.p1FeedItem != null && recipe.p1FeedItem != Items.AIR) {
            List<ItemStack> stackList = new ArrayList<>();
            stackList.add(new ItemStack(recipe.p1FeedItem, recipe.p1FeedAmount));
            list.add(stackList);
        }
        if (recipe.p1FeedTag != null) {
            List<ItemStack> stackList = (List<ItemStack>) new Ingredient.TagList(recipe.p1FeedTag).getItems();
            stackList.forEach(itemStack -> itemStack.setCount(recipe.p1FeedAmount));
            list.add(stackList);
        }
        if (recipe.p2FeedItem != null && recipe.p2FeedItem != Items.AIR) {
            List<ItemStack> stackList = new ArrayList<>();
            stackList.add(new ItemStack(recipe.p2FeedItem, recipe.p2FeedAmount));
            list.add(stackList);
        }
        if (recipe.p2FeedTag != null) {
            List<ItemStack> stackList = (List<ItemStack>) new Ingredient.TagList(recipe.p2FeedTag).getItems();
            stackList.forEach(itemStack -> itemStack.setCount(recipe.p2FeedAmount));
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
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
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
        if (!ingredients.getInputs(VanillaTypes.ITEM).isEmpty()) {
            itemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        }
        if (ingredients.getInputs(VanillaTypes.ITEM).size() > 1) {
            itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        }
    }

    @Override
    public void draw(Recipe recipe, @NotNull MatrixStack matrix, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontRenderer = minecraft.font;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        fontRenderer.draw(matrix, decimalFormat.format(beeRegistry.getAdjustedWeightForChild(beeRegistry.getBeeData(recipe.child), recipe.parent1, recipe.parent2)), 90, 35, 0xff808080);
        if (recipe.chance < 1) {
            fontRenderer.draw(matrix, decimalFormat.format(recipe.chance), 130, 40, 0xff808080);
            info.draw(matrix, 115, 40);
        }
    }

    @NotNull
    @Override
    public List<ITextComponent> getTooltipStrings(@NotNull Recipe recipe, double mouseX, double mouseY) {
        double infoX = 115D;
        double infoY = 40D;
        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D && recipe.chance < 1) {
            return Collections.singletonList(new StringTextComponent(I18n.get("gui." + ResourcefulBees.MOD_ID + ".jei.category.breed_chance.info")));
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    public static class Recipe {
        private final String parent1;
        private final String parent2;
        private final String child;
        private final float chance;

        private final ITag<Item> p1FeedTag;
        private final Item p1FeedItem;

        private final ITag<Item> p2FeedTag;
        private final Item p2FeedItem;

        private final int p1FeedAmount;
        private final int p2FeedAmount;

        public Recipe(String parent1, ITag<Item> p1FeedTag, Item p1FeedItem, int p1FeedAmount, String parent2, ITag<Item> p2FeedTag, Item p2FeedItem, int p2FeedAmount, String child, float chance) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.child = child;
            this.chance = chance;
            this.p1FeedItem = p1FeedItem;
            this.p2FeedItem = p2FeedItem;
            this.p1FeedTag = p1FeedTag;
            this.p2FeedTag = p2FeedTag;
            this.p1FeedAmount = p1FeedAmount;
            this.p2FeedAmount = p2FeedAmount;
        }
    }
}
