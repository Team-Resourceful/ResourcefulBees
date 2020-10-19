package com.resourcefulbees.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EntityFlowerCategory implements IRecipeCategory<EntityFlowerCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beeentityflowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "bee_pollination_entity_flowers");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public EntityFlowerCategory(IGuiHelper guiHelper){
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 100, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.GOLD_FLOWER.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.bee_pollination_entity_flowers");
    }

    public static List<EntityFlowerCategory.Recipe> getFlowersRecipes(IIngredientManager ingredientManager) {
        List<EntityFlowerCategory.Recipe> recipes = new ArrayList<>();
        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            String flower = beeData.getFlower();
            if (ValidatorUtils.ENTITY_RESOURCE_PATTERN.matcher(flower).matches()) {
                flower = flower.replace(BeeConstants.ENTITY_PREFIX, "");
                EntityType<?> entityType = BeeInfoUtils.getEntityType(flower);
                if (entityType != null)
                    recipes.add(new EntityFlowerCategory.Recipe(beeData.getName(), entityType));
            }
        }));
        return recipes;
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<? extends EntityFlowerCategory.Recipe> getRecipeClass() {
        return EntityFlowerCategory.Recipe.class;
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
    public void setIngredients(EntityFlowerCategory.Recipe recipe, IIngredients ingredients) {
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 41, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @Override
    public void draw(EntityFlowerCategory.Recipe recipe, @Nonnull MatrixStack matrix, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontRenderer = minecraft.fontRenderer;
        fontRenderer.draw(matrix, I18n.format(recipe.entityType.getTranslationKey()), (100 - fontRenderer.getStringWidth(I18n.format(recipe.entityType.getTranslationKey()))) / 2f, 75 -  (fontRenderer.FONT_HEIGHT + 5), 0xff808080);
    }

    public static class Recipe {
        private final String beeType;
        private final EntityType<?> entityType;

        public Recipe(String beeType, EntityType<?> entityType) {
            this.beeType = beeType;
            this.entityType = entityType;
        }
    }
}
