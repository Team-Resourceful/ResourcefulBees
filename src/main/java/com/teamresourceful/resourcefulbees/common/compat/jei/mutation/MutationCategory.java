package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.display.IEntityRender;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.display.IFluidRender;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.display.IItemRender;
import com.teamresourceful.resourcefulbees.common.compat.jei.BaseCategory;
import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MutationCategory extends BaseCategory<MutationRecipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/mutation.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "mutation");

    public MutationCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                TranslationConstants.Jei.MUTATIONS,
                guiHelper.drawableBuilder(GUI_BACK, -12, 0, 117, 75).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModItems.MUTATION_ENTITY_ICON.get())),
                MutationRecipe.class);
    }

    public static List<MutationRecipe> getMutationRecipes() {
        List<MutationRecipe> recipes = new ArrayList<>();
        BeeRegistry.getRegistry().getBees().values().forEach((beeData ->
                beeData.getMutationData().mutations().forEach((input, outputs) ->
                    outputs.forEach(output ->
                            recipes.add(new MutationRecipe(beeData.getEntityType(), input, output, outputs))))));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull MutationRecipe recipe, @NotNull IIngredients ingredients) {
        //TODO when switch pattern matches release switch to it.
        if (recipe.input() instanceof IItemRender itemRender) {
            ingredients.setInput(VanillaTypes.ITEM, itemRender.itemRender());
        }else if (recipe.input() instanceof IFluidRender fluidRender) {
            ingredients.setInput(VanillaTypes.FLUID, fluidRender.fluidRender());
        } else if (recipe.input() instanceof IEntityRender entityRender) {
            ingredients.setInputs(JEICompat.ENTITY_INGREDIENT, List.of(new EntityIngredient(recipe.bee(), 45f), new EntityIngredient(entityRender.entityRender(), 45f, recipe.input().tag())));
        }

        if (ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).isEmpty()) {
            ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.bee(), 45f));
        }

        if (recipe.output() instanceof IItemRender itemRender) {
            ingredients.setOutput(VanillaTypes.ITEM, itemRender.itemRender());
        }else if (recipe.output() instanceof IFluidRender fluidRender) {
            ingredients.setOutput(VanillaTypes.FLUID, fluidRender.fluidRender());
        } else if (recipe.output() instanceof IEntityRender entityRender) {
            ingredients.setOutput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(entityRender.entityRender(), -45f));
        }
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull MutationRecipe recipe, @NotNull IIngredients ingredients) {
        var entityIngredients = recipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        var itemIngredients = recipeLayout.getIngredientsGroup(VanillaTypes.ITEM);
        var fluidIngredients = recipeLayout.getIngredientsGroup(VanillaTypes.FLUID);

        entityIngredients.init(0, true, 16, 7);

        if (recipe.input() instanceof IItemRender) {
            itemIngredients.init(1, true, 15, 52);
        }else if (recipe.input() instanceof IFluidRender) {
            fluidIngredients.init(1, true, 16, 53);
        } else if (recipe.input() instanceof IEntityRender) {
            entityIngredients.init(1, true, 15, 52);
        }

        if (recipe.output() instanceof IItemRender) {
            itemIngredients.init(2, false, 89, 47);
        }else if (recipe.output() instanceof IFluidRender) {
            fluidIngredients.init(2, false, 90, 48);
        } else if (recipe.output() instanceof IEntityRender) {
            entityIngredients.init(2, false, 89, 47);
        }

        entityIngredients.set(ingredients);
        itemIngredients.set(ingredients);
        fluidIngredients.set(ingredients);

        entityIngredients.addTooltipCallback(getToolTip(recipe));
        itemIngredients.addTooltipCallback(getToolTip(recipe));
        fluidIngredients.addTooltipCallback(getToolTip(recipe));
    }

    private static <T> ITooltipCallback<T> getToolTip(MutationRecipe recipe) {
        return (slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 1) setTagToolTip(recipe.input().tag(), tooltip);
            if (slotIndex == 2) setTagToolTip(recipe.output().tag(), tooltip);
        };
    }

    private static void setTagToolTip(Optional<CompoundTag> tag, List<Component> tooltip) {
        tag.ifPresent(nbt -> {
            if (Screen.hasShiftDown()) Arrays.stream(NbtUtils.prettyPrint(nbt).split("\n"))
                    .map(TextComponent::new).map(c -> c.withStyle(ChatFormatting.DARK_PURPLE)).forEach(tooltip::add);
            else tooltip.add(TranslationConstants.Jei.NBT.withStyle(ChatFormatting.DARK_PURPLE));
        });
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull MutationRecipe recipe, double mouseX, double mouseY) {
        if (mouseX >= 63 && mouseX <= 72 && mouseY >= 8 && mouseY <= 17) {
            return Collections.singletonList(TranslationConstants.Jei.MUTATION_INFO);
        }
        double outputWeightChance = recipe.pool().getAdjustedWeight(recipe.output().weight()) * recipe.output().chance();
        if (mouseX >= 54 && mouseX <= 63 && mouseY >= 34 && mouseY <= 43 && outputWeightChance < 1) {
            List<Component> tooltip = Lists.newArrayList(TranslationConstants.Jei.MUTATION_WEIGHT_CHANCE_INFO);
            tooltip.add(TextComponent.EMPTY);
            tooltip.add(new TextComponent("Weight: " + ModConstants.PERCENT_FORMAT.format(recipe.pool().getAdjustedWeight(recipe.output().weight()))));
            tooltip.add(new TextComponent("Chance: " + ModConstants.PERCENT_FORMAT.format(recipe.output().chance())));
            return tooltip;
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    public void draw(@NotNull MutationRecipe recipe, @NotNull PoseStack stack, double mouseX, double mouseY) {
        beeHive.draw(stack, 65, 10);
        info.draw(stack, 63, 8);
        Font fontRenderer = Minecraft.getInstance().font;

        double outputWeightChance = recipe.pool().getAdjustedWeight(recipe.output().weight()) * recipe.output().chance();

        if (outputWeightChance < 1) {
            String chanceString = ModConstants.PERCENT_FORMAT.format(outputWeightChance);
            int padding = fontRenderer.width(chanceString) / 2;
            info.draw(stack, 54, 34);
            fontRenderer.draw(stack, chanceString, 76F - padding, 35, 0xff808080);
        }
        if (recipe.input().chance() < 1) {
            String chanceString = ModConstants.PERCENT_FORMAT.format(recipe.input().chance());
            int padding = fontRenderer.width(chanceString) / 2;
            fontRenderer.draw(stack, chanceString, 48F - padding, 66, 0xff808080);
        }
        if (!(recipe.input() instanceof IEntityRender)) this.slot.draw(stack, 15, 52);
        if (!(recipe.output() instanceof IEntityRender)) this.outputSlot.draw(stack, 85, 43);
    }
}
