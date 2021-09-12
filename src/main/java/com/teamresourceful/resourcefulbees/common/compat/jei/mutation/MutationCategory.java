package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.common.compat.jei.BaseCategory;
import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MutationCategory extends BaseCategory<IMutationRecipe> {

    public static final ResourceLocation OUTPUT_SLOT = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/output_slot.png");
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "mutation");

    private final IDrawableStatic slot;
    private final IDrawableStatic outputSlot;

    public MutationCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.mutation"),
                guiHelper.drawableBuilder(GUI_BACK, -12, 0, 117, 75).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(new ItemStack(ModItems.MUTATION_ENTITY_ICON.get())),
                IMutationRecipe.class);
        this.slot = guiHelper.getSlotDrawable();
        this.outputSlot = guiHelper.createDrawable(OUTPUT_SLOT, 0, 0, 26, 26);
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
        if (recipe.isEntityMutation() && recipe.getInputEntity().isPresent() && recipe.getOutputEntity().isPresent()) {
            ingredients.setInputs(JEICompat.ENTITY_INGREDIENT, Arrays.asList(
                    new EntityIngredient(recipe.getBeeData().getEntityType(), 45.0f),
                    new EntityIngredient(recipe.getInputEntity().get(), 45.0f)
            ));
            ingredients.setOutput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.getOutputEntity().get(), -45.0f, recipe.getNBT()));
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

        if (recipe.getInputItem().isPresent()) {
            itemIngredients.init(0, true, 15, 52);
            itemIngredients.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        }

        if (recipe.getOutputItem().isPresent()) {
            itemIngredients.init(1, false, 89, 47);
            itemIngredients.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        }

        if (recipe.getInputFluid().isPresent()) {
            fluidIngredients.init(0, true, 16, 53);
            fluidIngredients.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        }

        if (recipe.getOutputFluid().isPresent()) {
            fluidIngredients.init(1, false, 90, 48);
            fluidIngredients.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        }

        if (recipe.getInputEntity().isPresent()) {
            entityIngredients.init(1, true, 16, 53);
            entityIngredients.set(1, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(1));
        }

        if (recipe.getOutputEntity().isPresent()) {
            entityIngredients.init(2, false, 90, 48);
            entityIngredients.set(2, ingredients.getOutputs(JEICompat.ENTITY_INGREDIENT).get(0));
            recipe.getNBT().ifPresent(nbt -> {
                if (!nbt.isEmpty()) {
                    entityIngredients.addTooltipCallback((index, isInput, ingredient, tooltip) -> {
                        if (index == 2) {
                            if (Screen.hasShiftDown()) {
                                List<String> lore = BeeInfoUtils.getLoreLines(nbt);
                                lore.forEach(l -> tooltip.add(new StringTextComponent(l).withStyle(TextFormatting.DARK_PURPLE)));
                            } else {
                                tooltip.add(new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt").withStyle(TextFormatting.DARK_PURPLE));
                            }
                        }
                    });
                }
            });
        }

    }

    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(@NotNull IMutationRecipe recipe, double mouseX, double mouseY) {
        if (mouseX >= 63 && mouseX <= 72 && mouseY >= 8 && mouseY <= 17) {
            return Collections.singletonList(new TranslationTextComponent("gui." + ResourcefulBees.MOD_ID + ".jei.category.mutation.info"));
        }
        if (mouseX >= 54 && mouseX <= 63 && mouseY >= 34 && mouseY <= 43 && recipe.chance() < 1) {
            return Collections.singletonList(new TranslationTextComponent("gui." + ResourcefulBees.MOD_ID + ".jei.category.mutation_chance.info"));
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    public void draw(@NotNull IMutationRecipe recipe, @NotNull MatrixStack stack, double mouseX, double mouseY) {
        beeHive.draw(stack, 65, 10);
        info.draw(stack, 63, 8);
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        if (recipe.chance() < 1) {
            String chanceString = decimalFormat.format(recipe.chance());
            int padding2 = fontRenderer.width(chanceString) / 2;
            info.draw(stack, 54, 34);
            fontRenderer.draw(stack, chanceString, 76F - padding2, 35, 0xff808080);
        }
        if (recipe.weight() < 1) {
            String weightString = decimalFormat.format(recipe.weight());
            int padding = fontRenderer.width(weightString) / 2;
            fontRenderer.draw(stack, weightString, 48F - padding, 66, 0xff808080);
        }
        if (!recipe.getInputEntity().isPresent()) this.slot.draw(stack, 15, 52);
        if (!recipe.getOutputEntity().isPresent()) this.outputSlot.draw(stack, 85, 43);
    }
}
