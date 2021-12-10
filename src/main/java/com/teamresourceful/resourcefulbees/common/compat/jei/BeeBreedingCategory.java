package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeeBreedingCategory extends BaseCategory<BeeFamily> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/breeding.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "breeding");

    public BeeBreedingCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                TranslationConstants.Jei.BREEDING,
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 60).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(ModItems.APIARY_BREEDER_ITEM.get().getDefaultInstance()),
                BeeFamily.class);
    }

    public static List<BeeFamily> getBreedingRecipes() {
        List<BeeFamily> recipes = new ArrayList<>();
        BEE_REGISTRY.getFamilyTree().values().forEach(children -> children.forEach(recipes::add));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull BeeFamily beeFamily, @NotNull IIngredients ingredients) {
        List<List<ItemStack>> list = new ArrayList<>();

        if (!beeFamily.getParent1FeedItemStacks().isEmpty()) {
            list.add(new ArrayList<>(beeFamily.getParent1FeedItemStacks()));
        }
        if (!beeFamily.getParent2FeedItemStacks().isEmpty()) {
            list.add(new ArrayList<>(beeFamily.getParent2FeedItemStacks()));
        }

        ingredients.setInputLists(VanillaTypes.ITEM, list);

        List<EntityIngredient> entities = new ArrayList<>();
        entities.add(new EntityIngredient(beeFamily.getParent1Data().getEntityType(), 45.0f));
        entities.add(new EntityIngredient(beeFamily.getParent2Data().getEntityType(), -45.0F));

        ingredients.setInputs(JEICompat.ENTITY_INGREDIENT, entities);
        ingredients.setOutput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(beeFamily.getChildData().getEntityType(), -45.0f));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull BeeFamily beeFamily, @NotNull IIngredients ingredients) {
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
    public void draw(@NotNull BeeFamily beeFamily, @NotNull PoseStack matrix, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        if (beeFamily.getChance() < 1) {
            info.draw(matrix, 115, 40);
            fontRenderer.draw(matrix, decimalFormat.format(beeFamily.getChance()), 130, 40, 0xff808080);
        }
        fontRenderer.draw(matrix, decimalFormat.format(BEE_REGISTRY.getAdjustedWeightForChild(beeFamily)), 90, 35, 0xff808080);
    }

    @NotNull
    @Override
    public List<Component> getTooltipStrings(@NotNull BeeFamily beeFamily, double mouseX, double mouseY) {
        double infoX = 115D;
        double infoY = 40D;
        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D && beeFamily.getChance() < 1) {
            return Collections.singletonList(TranslationConstants.Jei.BREED_CHANCE_INFO);
        }
        return super.getTooltipStrings(beeFamily, mouseX, mouseY);
    }
}
