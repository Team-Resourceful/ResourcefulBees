package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
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
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//TODO rewrite to be more like centrifuge category with weight and chance but also like mutation for items and entities.
public class BeeBreedingCategory extends BaseCategory<BreederRecipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/breeding.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "breeding");

    public BeeBreedingCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                TranslationConstants.Jei.BREEDING,
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 60).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(ModItems.APIARY_BREEDER_ITEM.get().getDefaultInstance()),
                BreederRecipe.class);
    }

    @Override
    public void setIngredients(@NotNull BreederRecipe beeFamily, @NotNull IIngredients ingredients) {
        ingredients.setInputIngredients(List.of(beeFamily.parent1().feedItem(), beeFamily.parent2().feedItem()));

        List<EntityIngredient> entities = new ArrayList<>();
        beeFamily.parent1().displayEntity().ifPresent(entity -> entities.add(new EntityIngredient(BeeInfoUtils.getEntityType(entity), 45.0f)));
        beeFamily.parent2().displayEntity().ifPresent(entity -> entities.add(new EntityIngredient(BeeInfoUtils.getEntityType(entity), 45.0f)));

        ingredients.setInputs(JEICompat.ENTITY_INGREDIENT, entities);
        ingredients.setOutputLists(VanillaTypes.ITEM, List.of(beeFamily.outputs().stream().map(BreederRecipe.BreederOutput::output).collect(Collectors.toList())));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull BreederRecipe beeFamily, @NotNull IIngredients ingredients) {
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);

        ingredientStacks.init(0, true, 6, 6);
        ingredientStacks.init(1, true, 60, 6);
        //ingredientStacks.init(2, false, 130, 18);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
        ingredientStacks.set(1, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(1));
        //ingredientStacks.set(2, ingredients.getOutputs(JEICompat.ENTITY_INGREDIENT).get(0));

        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();

        itemStacks.init(0, true, 6, 32);
        itemStacks.init(1, true, 60, 32);
        itemStacks.init(2, false, 130, 18);
        if (!ingredients.getInputs(VanillaTypes.ITEM).isEmpty()) {
            itemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        }
        if (ingredients.getInputs(VanillaTypes.ITEM).size() > 1) {
            itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        }
        itemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(@NotNull BreederRecipe beeFamily, @NotNull PoseStack matrix, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
//        if (beeFamily.chance() < 1) {
//            info.draw(matrix, 115, 40);
//            fontRenderer.draw(matrix, decimalFormat.format(beeFamily.chance()), 130, 40, 0xff808080);
//        }
        //fontRenderer.draw(matrix, decimalFormat.format(BEE_REGISTRY.getAdjustedWeightForChild(beeFamily)), 90, 35, 0xff808080);
    }

    @NotNull
    @Override
    public List<Component> getTooltipStrings(@NotNull BreederRecipe beeFamily, double mouseX, double mouseY) {
        double infoX = 115D;
        double infoY = 40D;
//        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D && beeFamily.chance() < 1) {
//            return Collections.singletonList(TranslationConstants.Jei.BREED_CHANCE_INFO);
//        }
        return super.getTooltipStrings(beeFamily, mouseX, mouseY);
    }
}
