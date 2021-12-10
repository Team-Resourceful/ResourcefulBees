package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.common.compat.jei.BaseCategory;
import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

public class MutationCategory extends BaseCategory<IMutationRecipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/mutation.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "mutation");

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##%");

    public MutationCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                TranslationConstants.Jei.MUTATIONS,
                guiHelper.drawableBuilder(GUI_BACK, -12, 0, 117, 75).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(new ItemStack(ModItems.MUTATION_ENTITY_ICON.get())),
                IMutationRecipe.class);
    }

    public static List<IMutationRecipe> getMutationRecipes() {
        List<IMutationRecipe> recipes = new ArrayList<>();

        BeeRegistry.getRegistry().getBees().values().forEach((beeData ->  {
            MutationData mutationData = beeData.getMutationData();
            if (mutationData.hasMutation()) {
                if (!mutationData.getBlockMutations().isEmpty()){
                    mutationData.getBlockMutations().forEach((block, outputs) ->
                            outputs.forEach(output ->
                                    recipes.add(new BlockToMutationRecipe(beeData, output.getChance(), outputs.getAdjustedWeight(output.getWeight()), block, output))
                            )
                    );
                }
                if (!mutationData.getItemMutations().isEmpty()){
                    mutationData.getItemMutations().forEach((block, outputs) ->
                            outputs.forEach(output ->
                                    recipes.add(new BlockToMutationRecipe(beeData, output.getChance(), outputs.getAdjustedWeight(output.getWeight()), block, output))
                            )
                    );
                }
                if (!mutationData.getEntityMutations().isEmpty()){
                    mutationData.getEntityMutations().forEach((entity, outputs) ->
                            outputs.forEach(output ->
                                    recipes.add(new EntityMutationRecipe(beeData, output.getChance(), outputs.getAdjustedWeight(output.getWeight()), entity, output))
                            )
                    );
                }
            }
        }));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull IMutationRecipe recipe, @NotNull IIngredients ingredients) {
        Optional<EntityType<?>> inputEntity = recipe.getInputEntity();
        Optional<EntityType<?>> outputEntity = recipe.getOutputEntity();

        if (inputEntity.isPresent() && outputEntity.isPresent()) {
            ingredients.setInputs(JEICompat.ENTITY_INGREDIENT, Arrays.asList(
                    new EntityIngredient(recipe.getBeeData().getEntityType(), 45.0f),
                    new EntityIngredient(inputEntity.get(), 45.0f)
            ));
            ingredients.setOutput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(outputEntity.get(), -45.0f, recipe.getNBT()));
        }else {
            recipe.getInputItem().ifPresent(item -> ingredients.setInput(VanillaTypes.ITEM, item));
            recipe.getInputFluid().ifPresent(fluid-> ingredients.setInput(VanillaTypes.FLUID, fluid));
            recipe.getOutputItem().ifPresent(item-> ingredients.setOutput(VanillaTypes.ITEM, item));
            recipe.getOutputFluid().ifPresent(fluid-> ingredients.setOutput(VanillaTypes.FLUID, fluid));
            ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.getBeeData().getEntityType(), 45.0f));
        }
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull IMutationRecipe recipe, @NotNull IIngredients ingredients) {
        IGuiIngredientGroup<ItemStack> itemIngredients = recipeLayout.getIngredientsGroup(VanillaTypes.ITEM);
        IGuiIngredientGroup<FluidStack> fluidIngredients = recipeLayout.getIngredientsGroup(VanillaTypes.FLUID);
        IGuiIngredientGroup<EntityIngredient> entityIngredients = recipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);

        entityIngredients.init(0, true, 16, 7);
        entityIngredients.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));

        recipe.getInputItem().ifPresent(item -> {
            itemIngredients.init(0, true, 15, 52);
            itemIngredients.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        });

        recipe.getOutputItem().ifPresent(item -> {
            itemIngredients.init(1, false, 89, 47);
            itemIngredients.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
            recipe.getNBT().ifPresent(nbt -> {
                if (!nbt.isEmpty()) {
                    itemIngredients.addTooltipCallback((index, isInput, ingredient, tooltip) -> addMutationTooltip(tooltip, nbt, index, 1));
                }
            });
        });

        recipe.getInputFluid().ifPresent(fluid -> {
            fluidIngredients.init(0, true, 16, 53);
            fluidIngredients.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        });

        recipe.getOutputFluid().ifPresent(fluid -> {
            fluidIngredients.init(1, false, 90, 48);
            fluidIngredients.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        });

        recipe.getInputEntity().ifPresent(entity -> {
            entityIngredients.init(1, true, 16, 53);
            entityIngredients.set(1, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(1));
        });

        recipe.getOutputEntity().ifPresent(entity -> {
            entityIngredients.init(2, false, 90, 48);
            entityIngredients.set(2, ingredients.getOutputs(JEICompat.ENTITY_INGREDIENT).get(0));
            recipe.getNBT().ifPresent(nbt -> {
                if (!nbt.isEmpty()) {
                    entityIngredients.addTooltipCallback((index, isInput, ingredient, tooltip) -> addMutationTooltip(tooltip, nbt, index, 2));
                }
            });
        });
    }

    private static void addMutationTooltip(List<Component> tooltip, CompoundTag nbt, int index, int wantedIndex) {
        if (index == wantedIndex) {
            if (Screen.hasShiftDown()) {
                String tag = "null"; // TODO nbt.getPrettyDisplay(" ", 0).getString();
                for (String s : tag.split("\n")) {
                    tooltip.add(new TextComponent(s).withStyle(ChatFormatting.DARK_PURPLE));
                }
            } else {
                tooltip.add(TranslationConstants.Jei.NBT.withStyle(ChatFormatting.DARK_PURPLE));
            }
        }
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull IMutationRecipe recipe, double mouseX, double mouseY) {
        if (mouseX >= 63 && mouseX <= 72 && mouseY >= 8 && mouseY <= 17) {
            return Collections.singletonList(TranslationConstants.Jei.MUTATION_INFO);
        }
        if (mouseX >= 54 && mouseX <= 63 && mouseY >= 34 && mouseY <= 43 && recipe.chance() < 1) {
            return Collections.singletonList(TranslationConstants.Jei.MUTATION_CHANCE_INFO);
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    public void draw(@NotNull IMutationRecipe recipe, @NotNull PoseStack stack, double mouseX, double mouseY) {
        beeHive.draw(stack, 65, 10);
        info.draw(stack, 63, 8);
        Font fontRenderer = Minecraft.getInstance().font;
        if (recipe.chance() < 1) {
            String chanceString = DECIMAL_FORMAT.format(recipe.chance());
            int padding = fontRenderer.width(chanceString) / 2;
            info.draw(stack, 54, 34);
            fontRenderer.draw(stack, chanceString, 76F - padding, 35, 0xff808080);
        }
        if (recipe.weight() < 1) {
            String weightString = DECIMAL_FORMAT.format(recipe.weight());
            int padding = fontRenderer.width(weightString) / 2;
            fontRenderer.draw(stack, weightString, 48F - padding, 66, 0xff808080);
        }
        if (recipe.getInputEntity().isEmpty()) this.slot.draw(stack, 15, 52);
        if (recipe.getOutputEntity().isEmpty()) this.outputSlot.draw(stack, 85, 43);
    }
}
