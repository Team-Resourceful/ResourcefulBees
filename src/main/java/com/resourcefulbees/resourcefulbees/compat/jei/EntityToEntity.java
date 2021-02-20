package com.resourcefulbees.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityToEntity implements IRecipeCategory<EntityToEntity.Recipe> {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ICONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "entity_to_entity_mutation");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable info;
    private final IDrawable beeHive;
    private final String localizedName;

    public EntityToEntity(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawable(ICONS, 0, 0, 16, 16);
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.beeHive = guiHelper.createDrawableIngredient(new ItemStack(ModItems.T1_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.entity_to_entity_mutation");
    }

    public static List<Recipe> getMutationRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            MutationData mutationData = beeData.getMutationData();
            if (mutationData.hasMutation() && mutationData.hasEntityMutations()) {
                beeData.getMutationData().getEntityMutations().forEach((entityType, doubleRandomCollectionPair) -> {
                    ItemStack inputEgg = new ItemStack(Items.GHAST_SPAWN_EGG);
                    IFormattableTextComponent inputName = entityType.getName().copy();
                    inputName.setStyle(Style.EMPTY.withColor(Color.parse("gold")).withItalic(false));
                    inputEgg.setDisplayName(inputName);

                    doubleRandomCollectionPair.getRight().forEach(entityOutput -> {
                        ItemStack outputEgg = new ItemStack(Items.GHAST_SPAWN_EGG);
                        CompoundNBT outputDisplay = new CompoundNBT();
                        IFormattableTextComponent outputName = entityOutput.getEntityType().getName().copy();
                        outputName.setStyle(Style.EMPTY.withColor(Color.parse("gold")).withBold(true).withItalic(false));
                        outputDisplay.putString("Name", ITextComponent.Serializer.toJson(outputName));
                        CompoundNBT outputTag = new CompoundNBT();
                        outputTag.put("display", outputDisplay);
                        outputEgg.setTag(outputTag);

                        double effectiveWeight = RecipeUtils.getEffectiveWeight(doubleRandomCollectionPair.getRight(), entityOutput.getWeight());
                        recipes.add(new Recipe(entityType.getName().copy(), entityOutput.getEntityType().getName().copy(), inputEgg, outputEgg, entityOutput.getCompoundNBT(), beeData.getName(), effectiveWeight, doubleRandomCollectionPair.getLeft()));
                    });
                });
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
    public void setIngredients(Recipe recipe, IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.input);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        List<ITextComponent> list = RecipeUtils.getTooltipStrings(mouseX, mouseY, recipe.chance);
        if (!list.isEmpty()) {
            return list;
        }
        return IRecipeCategory.super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = RecipeUtils.setGuiItemStacksGroup(iRecipeLayout, ingredients);
        itemStacks.addTooltipCallback((slotIndex, isInputStack, stack, tooltip) -> {
            if (Minecraft.getInstance().gameSettings.advancedItemTooltips) {
                if (slotIndex == 1) {
                    tooltip.add(recipe.inputRegName.formatted(TextFormatting.DARK_GRAY));
                } else {
                    tooltip.add(recipe.outputRegName.formatted(TextFormatting.DARK_GRAY));
                }
            }
            if (slotIndex == 0 && !recipe.outputNBT.isEmpty()) {
                if (BeeInfoUtils.isShiftPressed()) {
                    List<String> lore = BeeInfoUtils.getLoreLines(recipe.outputNBT);
                    lore.forEach(l -> tooltip.add(new StringTextComponent(l).fillStyle(Style.EMPTY.withColor(Color.parse("dark_purple")))));
                } else {
                    tooltip.add(new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt").fillStyle(Style.EMPTY.withColor(Color.parse("dark_purple"))));
                }
            }
        });

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull MatrixStack stack, double mouseX, double mouseY) {
        RecipeUtils.drawMutationScreen(stack, this.beeHive, this.info, recipe.weight, recipe.chance);
    }

    public static class Recipe {
        private final IFormattableTextComponent inputRegName;
        private final IFormattableTextComponent outputRegName;
        private final ItemStack input;
        private final ItemStack output;
        private final String beeType;

        private final double weight;
        private final double chance;

        public final CompoundNBT outputNBT;

        public Recipe(IFormattableTextComponent inputRegName, IFormattableTextComponent outputRegName, ItemStack inputEgg, ItemStack outputEgg, CompoundNBT outputNBT, String beeType, double weight, double chance) {
            this.inputRegName = inputRegName;
            this.outputRegName = outputRegName;
            this.input = inputEgg;
            this.output = outputEgg;
            this.outputNBT = outputNBT;
            this.beeType = beeType;
            this.weight = weight;
            this.chance = chance;
        }
    }
}
