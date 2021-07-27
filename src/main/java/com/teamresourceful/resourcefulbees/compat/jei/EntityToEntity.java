package com.teamresourceful.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityToEntity extends BaseCategory<EntityToEntity.Recipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "entity_to_entity_mutation");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private final IDrawable nonRegisteredEgg;

    private static final String DARK_PURPLE = "dark_purple";

    public EntityToEntity(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.entity_to_entity_mutation"),
                guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(new ItemStack(ModItems.MUTATION_ENTITY_ICON.get())),
                EntityToEntity.Recipe.class);
        this.nonRegisteredEgg = guiHelper.createDrawable(ICONS, 41, 0, 16, 16);
    }

    public static List<Recipe> getMutationRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            MutationData mutationData = beeData.getMutationData();
            if (mutationData.hasMutation() && !mutationData.getEntityMutations().isEmpty()) {
                mutationData.getEntityMutations().forEach((entityType, collection) ->  {
                    SpawnEggItem inputSpawnEgg = SpawnEggItem.byId(entityType);
                    ItemStack inputEgg = inputSpawnEgg != null ? new ItemStack(inputSpawnEgg) : null;
                    collection.forEach(entityOutput -> {
                        SpawnEggItem outputSpawnEgg = SpawnEggItem.byId(entityOutput.getEntityType());
                        ItemStack outputEgg = outputSpawnEgg != null ? new ItemStack(outputSpawnEgg) : null;

                        double effectiveWeight = RecipeUtils.getEffectiveWeight(collection, entityOutput.getWeight());
                        recipes.add(new Recipe(entityType, entityOutput.getEntityType(), inputEgg, outputEgg, entityOutput.getCompoundNBT().orElse(null), beeData, effectiveWeight, entityOutput.getChance()));
                    });
                });
            }
        }));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
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
                addTooltip(recipe, tooltip);
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

        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        if (recipe.output != null) {
            itemStacks.init(0, false, 65, 48);
            itemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
            itemStacks.addTooltipCallback(getItemStackTooltipCallback(recipe));
        }
        if (recipe.input != null) {
            itemStacks.init(1, true, 15, 57);
            itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
            itemStacks.addTooltipCallback(getItemStackTooltipCallback(recipe));
        }
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @NotNull
    private ITooltipCallback<ItemStack> getItemStackTooltipCallback(@NotNull Recipe recipe) {
        return (slotIndex, isInputStack, stack, tooltip) -> {
            if (slotIndex == 0) {
                tooltip.clear();
                addTooltip(recipe, tooltip); //TODO NEEDS CONFIRMATION OF REFACTOR WORKING
            }
        };
    }

    private void addTooltip(@NotNull Recipe recipe, List<ITextComponent> tooltip) {
        tooltip.add(recipe.outputEntity.getDescription().plainCopy());
        if (recipe.outputEntity.getRegistryName() != null) {
            tooltip.add(new StringTextComponent(recipe.outputEntity.getRegistryName().toString()).withStyle(TextFormatting.GRAY));
        }
        if (!recipe.outputNBT.isEmpty()) {
            if (Screen.hasShiftDown()) {
                List<String> lore = BeeInfoUtils.getLoreLines(recipe.outputNBT);
                lore.forEach(l -> tooltip.add(new StringTextComponent(l).withStyle(Style.EMPTY.withColor(Color.parseColor(DARK_PURPLE)))));
            } else {
                tooltip.add(new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt").withStyle(Style.EMPTY.withColor(Color.parseColor(DARK_PURPLE))));
            }
        }
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
        private final CustomBeeData beeType;

        private final double weight;
        private final double chance;

        public final CompoundNBT outputNBT;

        public Recipe(EntityType<?> inputEntity, EntityType<?> outputEntity, ItemStack inputEgg, ItemStack outputEgg, CompoundNBT outputNBT, CustomBeeData beeType, double weight, double chance) {
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
