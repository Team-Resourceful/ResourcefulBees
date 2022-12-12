package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.util.displays.EntityDisplay;
import com.teamresourceful.resourcefulbees.client.util.displays.FluidDisplay;
import com.teamresourceful.resourcefulbees.client.util.displays.ItemDisplay;
import com.teamresourceful.resourcefulbees.common.compat.jei.BaseCategory;
import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.*;

public class MutationCategory extends BaseCategory<MutationRecipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/mutation.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "mutation");
    public static final RecipeType<MutationRecipe> RECIPE = new RecipeType<>(ID, MutationRecipe.class);

    public MutationCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE,
                TranslationConstants.Jei.MUTATIONS,
                guiHelper.drawableBuilder(GUI_BACK, -12, 0, 117, 75).build(),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.BEE_BOX.get())));
    }

    public static List<MutationRecipe> getMutationRecipes(Level level) {
        List<MutationRecipe> recipes = new ArrayList<>();
        BeeRegistry.get()
            .getSetOfBees()
            .forEach((beeData -> {
                beeData.getMutationData().mutations(level).forEach((input, outputs) -> {
                    outputs.forEach(output -> recipes.add(new MutationRecipe(beeData.entityType(), input, output, outputs)));
                });
            }));
        return recipes;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull MutationRecipe recipe, @NotNull IFocusGroup focuses) {
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 16, 53)
                .setSlotName("input")
                .addTooltipCallback(getToolTip(recipe));

        if (recipe.input() instanceof ItemDisplay itemRender) input.addItemStack(itemRender.displayedItem());
        if (recipe.input() instanceof FluidDisplay fluidRender) input.addFluidStack(fluidRender.displayedFluid(), 1000);
        if (recipe.input() instanceof EntityDisplay entityRender) input.addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(entityRender.displayedEntity(), 45f, recipe.input().tag()));

        builder.addSlot(RecipeIngredientRole.INPUT, 17, 8)
                .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.bee(), 45f))
                .setSlotName("bee");

        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 48)
                .setSlotName("output")
                .addTooltipCallback(getToolTip(recipe));

        if (recipe.output() instanceof ItemDisplay itemRender) output.addItemStack(itemRender.displayedItem());
        if (recipe.output() instanceof FluidDisplay fluidRender) output.addFluidStack(fluidRender.displayedFluid(), 1000);
        if (recipe.output() instanceof EntityDisplay entityRender) output.addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(entityRender.displayedEntity(), -45f, recipe.output().tag()));
    }

    private static IRecipeSlotTooltipCallback getToolTip(MutationRecipe recipe) {
        return (view, tooltip) -> view.getSlotName().ifPresent(name -> {
            if ("input".equals(name)) setTagToolTip(recipe.input().tag(), tooltip);
            if ("output".equals(name)) setTagToolTip(recipe.output().tag(), tooltip);
        });
    }

    private static void setTagToolTip(Optional<CompoundTag> tag, List<Component> tooltip) {
        tag.ifPresent(nbt -> {
            if (Screen.hasShiftDown()) Arrays.stream(NbtUtils.prettyPrint(nbt).split("\n"))
                    .map(Component::literal)
                    .map(c -> c.withStyle(ChatFormatting.DARK_PURPLE))
                    .forEach(tooltip::add);
            else tooltip.add(TranslationConstants.Jei.NBT.withStyle(ChatFormatting.DARK_PURPLE));
        });
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull MutationRecipe recipe, @NotNull IRecipeSlotsView view, double mouseX, double mouseY) {
        if (mouseX >= 63 && mouseX <= 72 && mouseY >= 8 && mouseY <= 17) {
            return Collections.singletonList(TranslationConstants.Jei.MUTATION_INFO);
        }
        double outputWeightChance = recipe.pool().getAdjustedWeight(recipe.output().weight()) * recipe.output().chance();
        if (mouseX >= 54 && mouseX <= 63 && mouseY >= 34 && mouseY <= 43 && outputWeightChance < 1) {
            List<Component> tooltip = Lists.newArrayList(TranslationConstants.Jei.MUTATION_WEIGHT_CHANCE_INFO);
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Weight: " + NumberFormat.getPercentInstance().format(recipe.pool().getAdjustedWeight(recipe.output().weight()))));
            tooltip.add(Component.literal("Chance: " + NumberFormat.getPercentInstance().format(recipe.output().chance())));
            return tooltip;
        }
        return super.getTooltipStrings(recipe, view, mouseX, mouseY);
    }

    @Override
    public void draw(@NotNull MutationRecipe recipe, @NotNull IRecipeSlotsView view, @NotNull PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, view, stack, mouseX, mouseY);
        beeHive.draw(stack, 65, 10);
        info.draw(stack, 63, 8);
        Font fontRenderer = Minecraft.getInstance().font;

        double outputWeightChance = recipe.pool().getAdjustedWeight(recipe.output().weight()) * recipe.output().chance();

        if (outputWeightChance < 1) {
            String chanceString = NumberFormat.getPercentInstance().format(outputWeightChance);
            int padding = fontRenderer.width(chanceString) / 2;
            info.draw(stack, 54, 34);
            fontRenderer.draw(stack, chanceString, 76F - padding, 35, 0xff808080);
        }
        if (recipe.input().chance() < 1) {
            String chanceString = NumberFormat.getPercentInstance().format(recipe.input().chance());
            int padding = fontRenderer.width(chanceString) / 2;
            fontRenderer.draw(stack, chanceString, 48F - padding, 66, 0xff808080);
        }
        if (!(recipe.input() instanceof EntityDisplay)) this.slot.draw(stack, 15, 52);
        if (!(recipe.output() instanceof EntityDisplay)) this.outputSlot.draw(stack, 85, 43);
    }
}
