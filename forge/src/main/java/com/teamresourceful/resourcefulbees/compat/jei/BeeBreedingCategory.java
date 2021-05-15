package com.teamresourceful.resourcefulbees.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

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

                        Iterator<String> parent1 = beeData.getBreedData().getParent1().iterator();
                        Iterator<String> parent2 = beeData.getBreedData().getParent2().iterator();

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
        return new Recipe(beeData);
    }

    private static Recipe getParentsRecipe(CustomBeeData beeData, Iterator<String> parent1, Iterator<String> parent2) {
        String p1 = parent1.next();
        String p2 = parent2.next();

        if (beeRegistry.getBees().containsKey(p1) && beeRegistry.getBees().containsKey(p2)) {
            CustomBeeData p1Data = beeRegistry.getBeeData(p1);
            CustomBeeData p2Data = beeRegistry.getBeeData(p2);

            return new Recipe(p1Data, p2Data, beeData);
        }

        return null;
    }

    @Override
    public void setIngredients(@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        List<List<ItemStack>> list = new ArrayList<>();

        if (!recipe.parent1.getBreedData().getFeedItems().isEmpty()) {
            list.add(recipe.getParent1FeedItems());
        }
        if (!recipe.parent2.getBreedData().getFeedItems().isEmpty()) {
            list.add(recipe.getParent2FeedItems());
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
    public void draw(Recipe recipe, @Nonnull PoseStack matrix, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        fontRenderer.draw(matrix, decimalFormat.format(beeRegistry.getAdjustedWeightForChild(recipe.child, recipe.parent1, recipe.parent2)), 90, 35, 0xff808080);
        if (recipe.child.getBreedData().getBreedChance() < 1) {
            fontRenderer.draw(matrix, decimalFormat.format(recipe.child.getBreedData().getBreedChance()), 130, 40, 0xff808080);
            info.draw(matrix, 115, 40);
        }
    }

    @NotNull
    @Override
    public List<Component> getTooltipStrings(@NotNull Recipe recipe, double mouseX, double mouseY) {
        double infoX = 115D;
        double infoY = 40D;
        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D && recipe.child.getBreedData().getBreedChance() < 1) {
            return Collections.singletonList(new TextComponent(I18n.get("gui." + ResourcefulBees.MOD_ID + ".jei.category.breed_chance.info")));
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    public static class Recipe {
        private final CustomBeeData parent1;
        private final CustomBeeData parent2;
        private final CustomBeeData child;

        public Recipe(CustomBeeData data){
            this(data, data, data);
        }

        public Recipe(CustomBeeData parent1, CustomBeeData parent2, CustomBeeData child) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.child = child;
        }

        private List<ItemStack> getParentFeedItems(CustomBeeData parent) {
            return parent.getBreedData().getFeedItems().stream().map(item -> new ItemStack(item, parent.getBreedData().getFeedAmount())).collect(Collectors.toList());
        }

        public List<ItemStack> getParent1FeedItems() {
            return getParentFeedItems(parent1);
        }

        public List<ItemStack> getParent2FeedItems() {
            return getParentFeedItems(parent2);
        }
    }
}
