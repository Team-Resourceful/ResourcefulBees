package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BeeBreedingCategory implements IRecipeCategory<BeeBreedingCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/breeding.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "breeding");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public BeeBreedingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 30).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.GOLD_FLOWER.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.breeding");
    }

    public static List<Recipe> getBreedingRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()){
            if (bee.getValue().isBreedable()){
                if (BeeInfo.BEE_INFO.containsKey(bee.getValue().getParent1()) && BeeInfo.BEE_INFO.containsKey(bee.getValue().getParent2()))
                    recipes.add(new Recipe(bee.getValue().getParent1(), bee.getValue().getParent2(), bee.getKey()));
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
    public void setIngredients(@Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
        ItemStack parent1SpawnEgg = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());
        ItemStack parent2SpawnEgg = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());
        ItemStack childSpawnEgg = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());

        CompoundNBT p1_type = new CompoundNBT();
        CompoundNBT p1_root = new CompoundNBT();
        p1_type.putString(BeeConstants.NBT_BEE_TYPE, recipe.parent1);
        p1_root.put(BeeConstants.NBT_ROOT, p1_type);
        p1_root.put(BeeConstants.NBT_SPAWN_EGG_DATA, p1_type);
        parent1SpawnEgg.setTag(p1_root);

        CompoundNBT p2_type = new CompoundNBT();
        CompoundNBT p2_root = new CompoundNBT();
        p2_type.putString(BeeConstants.NBT_BEE_TYPE, recipe.parent2);
        p2_root.put(BeeConstants.NBT_ROOT, p2_type);
        p2_root.put(BeeConstants.NBT_SPAWN_EGG_DATA, p2_type);
        parent2SpawnEgg.setTag(p2_root);

        CompoundNBT ch_type = new CompoundNBT();
        CompoundNBT ch_root = new CompoundNBT();
        ch_type.putString(BeeConstants.NBT_BEE_TYPE, recipe.child);
        ch_root.put(BeeConstants.NBT_ROOT, ch_type);
        ch_root.put(BeeConstants.NBT_SPAWN_EGG_DATA, ch_type);
        childSpawnEgg.setTag(ch_root);

        List<Ingredient> list = new ArrayList<>();
        Ingredient ing = Ingredient.fromStacks(parent1SpawnEgg, parent2SpawnEgg);
        list.add(ing);
        ingredients.setInputIngredients(list);
        ingredients.setOutput(VanillaTypes.ITEM, childSpawnEgg);

        List<EntityIngredient> entitys = new ArrayList<>();
        entitys.add(new EntityIngredient(recipe.parent1, 135.0F));
        entitys.add(new EntityIngredient(recipe.parent2, 45.0F));

        ingredients.setInputs(JEICompat.ENTITY_INGREDIENT, entitys);
        ingredients.setOutput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.child, 135.0F));
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 5, 2);
        ingredientStacks.init(1, true, 60, 2);
        ingredientStacks.init(2, false, 125, 2);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
        ingredientStacks.set(1, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(1));
        ingredientStacks.set(2, ingredients.getOutputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @Nonnull
    @Override
    public List<ITextComponent> getTooltipStrings(@Nonnull Recipe recipe, double mouseX, double mouseY) {
        double beeX = 2D;
        double beeY = 0D;
        double bee2X = 52D;
        double bee2Y = 0D;
        double bee3X = 124D;
        double bee3Y = 0D;
        if (mouseX >= beeX && mouseX <= beeX + 30D && mouseY >= beeY && mouseY <= beeY + 30D){
            return Collections.singletonList(new StringTextComponent(I18n.format("entity." + ResourcefulBees.MOD_ID + "." + recipe.parent1 + "_bee")));
        }
        if (mouseX >= bee2X && mouseX <= bee2X + 30D && mouseY >= bee2Y && mouseY <= bee2Y + 30D){
            return Collections.singletonList(new StringTextComponent(I18n.format("entity." + ResourcefulBees.MOD_ID + "." + recipe.parent2 + "_bee")));
        }
        if (mouseX >= bee3X && mouseX <= bee3X + 30D && mouseY >= bee3Y && mouseY <= bee3Y + 30D){
            return Collections.singletonList(new StringTextComponent(I18n.format("entity." + ResourcefulBees.MOD_ID + "." + recipe.child + "_bee")));
        }
        return IRecipeCategory.super.getTooltipStrings(recipe,mouseX, mouseY);
    }

    @Override
    public void draw(Recipe recipe, @Nonnull MatrixStack matrix, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontRenderer = minecraft.fontRenderer;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        fontRenderer.drawString(matrix, decimalFormat.format(BeeInfo.getInfo(recipe.getChild()).getBreedWeight()), 90, 18, 0xff808080);
    }

    public static class Recipe {
        private final String parent1;
        private final String parent2;
        private final String child;

        public Recipe(String parent1, String parent2, String child) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.child = child;

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
