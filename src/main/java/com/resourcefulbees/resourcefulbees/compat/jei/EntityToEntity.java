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
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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
    private final IDrawable nonRegisteredEgg;
    private final String localizedName;

    private static final String DARK_PURPLE = "dark_purple";

    public EntityToEntity(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawable(ICONS, 0, 0, 16, 16);
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.nonRegisteredEgg = guiHelper.createDrawable(ICONS, 41, 0, 16, 16);
        this.beeHive = guiHelper.createDrawableIngredient(new ItemStack(ModItems.T1_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.get("gui.resourcefulbees.jei.category.entity_to_entity_mutation");
    }

    public static List<Recipe> getMutationRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            MutationData mutationData = beeData.getMutationData();
            if (mutationData.hasMutation() && mutationData.hasEntityMutations()) {
                beeData.getMutationData().getEntityMutations().forEach((entityType, doubleRandomCollectionPair) -> {
                    SpawnEggItem inputSpawnEgg = SpawnEggItem.byId(entityType);
                    ItemStack inputEgg = inputSpawnEgg != null ? new ItemStack(inputSpawnEgg) : null;
                    doubleRandomCollectionPair.getRight().forEach(entityOutput -> {
                        SpawnEggItem outputSpawnEgg = SpawnEggItem.byId(entityOutput.getEntityType());
                        ItemStack outputEgg = outputSpawnEgg != null ? new ItemStack(outputSpawnEgg) : null;

                        double effectiveWeight = RecipeUtils.getEffectiveWeight(doubleRandomCollectionPair.getRight(), entityOutput.getWeight());
                        recipes.add(new Recipe(entityType, entityOutput.getEntityType(), inputEgg, outputEgg, entityOutput.getCompoundNBT(), beeData.getName(), effectiveWeight, doubleRandomCollectionPair.getLeft()));
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
    public void setIngredients(Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.input != null)
            ingredients.setInput(VanillaTypes.ITEM, recipe.input);
        if (recipe.output != null)
            ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        //HIVE INFO
        List<ITextComponent> list = RecipeUtils.getTooltipStrings(mouseX, mouseY, recipe.chance);
        if (!list.isEmpty()) {
            return list;
        }
        //OUTPUT
        if (recipe.output == null && mouseX > 64 && mouseX < 84 && mouseY > 47 && mouseY < 66){
            if (recipe.outputNBT.isEmpty()) {
                return Collections.singletonList(recipe.outputEntity.getDescription().plainCopy());
            } else {
                List<ITextComponent> tooltip = new ArrayList<>();
                tooltip.add(recipe.outputEntity.getDescription().plainCopy());
                if (recipe.outputEntity.getRegistryName() != null) {
                    tooltip.add(new StringTextComponent(recipe.outputEntity.getRegistryName().toString()).withStyle(TextFormatting.GRAY));
                }
                if (!recipe.outputNBT.isEmpty()) {
                    if (BeeInfoUtils.isShiftPressed()) {
                        List<String> lore = BeeInfoUtils.getLoreLines(recipe.outputNBT);
                        lore.forEach(l -> tooltip.add(new StringTextComponent(l).withStyle(Style.EMPTY.withColor(Color.parseColor(DARK_PURPLE)))));
                    } else {
                        tooltip.add(new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt").withStyle(Style.EMPTY.withColor(Color.parseColor(DARK_PURPLE))));
                    }
                }
                return tooltip;
            }
        }

        //INPUT
        if (recipe.input == null && mouseX > 15 && mouseX < 32 && mouseY > 57 && mouseY < 74){
            List<ITextComponent> tooltip = new ArrayList<>();
            tooltip.add(recipe.inputEntity.getDescription().plainCopy());
            if (recipe.outputEntity.getRegistryName() != null) {
                tooltip.add(new StringTextComponent(recipe.outputEntity.getRegistryName().toString()).withStyle(TextFormatting.GRAY));
            }
            return tooltip;
        }

        return IRecipeCategory.super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        if (recipe.output != null) {
            itemStacks.init(0, false, 65, 48);
            itemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
            itemStacks.addTooltipCallback((slotIndex, isInputStack, stack, tooltip) -> {
                if (slotIndex == 0) {
                    tooltip.clear();
                    tooltip.add(recipe.outputEntity.getDescription().plainCopy());
                    if (recipe.outputEntity.getRegistryName() != null) {
                        tooltip.add(new StringTextComponent(recipe.outputEntity.getRegistryName().toString()).withStyle(TextFormatting.GRAY));
                    }
                    if (!recipe.outputNBT.isEmpty()) {
                        if (BeeInfoUtils.isShiftPressed()) {
                            List<String> lore = BeeInfoUtils.getLoreLines(recipe.outputNBT);
                            lore.forEach(l -> tooltip.add(new StringTextComponent(l).withStyle(Style.EMPTY.withColor(Color.parseColor(DARK_PURPLE)))));
                        } else {
                            tooltip.add(new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt").withStyle(Style.EMPTY.withColor(Color.parseColor(DARK_PURPLE))));
                        }
                    }
                }
            });
        }
        if (recipe.input != null) {
            itemStacks.init(1, true, 15, 57);
            itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
            itemStacks.addTooltipCallback((slotIndex, isInputStack, stack, tooltip) -> {
                if (slotIndex == 1) {
                    tooltip.clear();
                    tooltip.add(recipe.inputEntity.getDescription().plainCopy());
                    if (recipe.inputEntity.getRegistryName() != null) {
                        tooltip.add(new StringTextComponent(recipe.inputEntity.getRegistryName().toString()).withStyle(TextFormatting.GRAY));
                    }
                }
            });
        }
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull MatrixStack stack, double mouseX, double mouseY) {
        RecipeUtils.drawMutationScreen(stack, this.beeHive, this.info, recipe.weight, recipe.chance);
        if (recipe.output == null){ this.nonRegisteredEgg.draw(stack, 66, 49); }
        if (recipe.input == null){ this.nonRegisteredEgg.draw(stack, 15, 57); }
    }

    public static class Recipe {
        private final EntityType<?> inputEntity;
        private final EntityType<?> outputEntity;
        private final ItemStack input;
        private final ItemStack output;
        private final String beeType;

        private final double weight;
        private final double chance;

        public final CompoundNBT outputNBT;

        public Recipe(EntityType<?> inputEntity, EntityType<?> outputEntity, ItemStack inputEgg, ItemStack outputEgg, CompoundNBT outputNBT, String beeType, double weight, double chance) {
            this.inputEntity = inputEntity;
            this.outputEntity = outputEntity;
            this.input = inputEgg;
            this.output = outputEgg;
            this.outputNBT = outputNBT;
            this.beeType = beeType;
            this.weight = weight;
            this.chance = chance;
        }
    }
}
