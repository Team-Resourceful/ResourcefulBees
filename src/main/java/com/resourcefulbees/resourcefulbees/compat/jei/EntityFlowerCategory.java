package com.resourcefulbees.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;;
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
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityFlowerCategory implements IRecipeCategory<EntityFlowerCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beeentityflowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "bee_pollination_entity_flowers");
    public static final ResourceLocation ICONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable nonRegisteredEgg;
    private final String localizedName;

    public EntityFlowerCategory(IGuiHelper guiHelper){
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 100, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.GOLD_FLOWER.get()));
        this.nonRegisteredEgg = guiHelper.createDrawable(ICONS, 41, 0, 16, 16);
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.bee_pollination_entity_flowers");
    }

    public static List<EntityFlowerCategory.Recipe> getFlowersRecipes(IIngredientManager ingredientManager) {
        List<EntityFlowerCategory.Recipe> recipes = new ArrayList<>();
        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            if (beeData.hasEntityFlower()) {
                EntityType<?> entityType = BeeInfoUtils.getEntityType(beeData.getEntityFlower());
                if (entityType != null) {
                    Item spawnEggItem = SpawnEggItem.getEgg(entityType);
                    recipes.add(new Recipe(beeData.getName(), entityType, spawnEggItem != null ? new ItemStack(spawnEggItem) : null));
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
    public void setIngredients(Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.spawnEgg != null) ingredients.setInput(VanillaTypes.ITEM, recipe.spawnEgg);
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        if (recipe.spawnEgg != null){
            itemStacks.init(0, false, 41, 55);
            itemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
            itemStacks.addTooltipCallback((slotIndex, isInputStack, stack, tooltip) -> {
                if (slotIndex == 0) {
                    tooltip.clear();
                    tooltip.add(recipe.entityType.getName().copy());
                    if (recipe.entityType.getRegistryName() != null) {
                        tooltip.add(new StringTextComponent(recipe.entityType.getRegistryName().toString()).formatted(TextFormatting.GRAY));
                    }
                }
            });
        }

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 41, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(@NotNull Recipe recipe, double mouseX, double mouseY) {
        if (recipe.spawnEgg == null && mouseX > 41 && mouseX < 57 && mouseY > 55 && mouseY < 71){
            List<ITextComponent> tooltip = new ArrayList<>();
            tooltip.add(recipe.entityType.getName().copy());
            if (recipe.entityType.getRegistryName() != null) {
                tooltip.add(new StringTextComponent(recipe.entityType.getRegistryName().toString()).formatted(TextFormatting.GRAY));
            }
            return tooltip;
        }
        return IRecipeCategory.super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull MatrixStack stack, double mouseX, double mouseY) {
        if (recipe.spawnEgg == null){
            nonRegisteredEgg.draw(stack, 42, 56);
        }
    }

    public static class Recipe {
        private final String beeType;
        private final EntityType<?> entityType;
        private final ItemStack spawnEgg;

        public Recipe(String beeType, EntityType<?> entityType, ItemStack spawnEgg) {
            this.beeType = beeType;
            this.entityType = entityType;
            this.spawnEgg = spawnEgg;
        }
    }
}
