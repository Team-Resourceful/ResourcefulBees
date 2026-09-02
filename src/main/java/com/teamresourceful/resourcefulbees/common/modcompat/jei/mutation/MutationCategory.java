package com.teamresourceful.resourcefulbees.common.modcompat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.util.displays.EntityDisplay;
import com.teamresourceful.resourcefulbees.client.util.displays.FluidDisplay;
import com.teamresourceful.resourcefulbees.client.util.displays.ItemDisplay;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ModTranslations;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.BaseCategory;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.JEICompat;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MutationCategory
        extends BaseCategory<MutationRecipe> {

    private static final int WIDTH = 117;
    private static final int HEIGHT = 75;

    private static final Identifier GUI = ModIdentifier.of("textures/gui/jei/mutation.png");
    public static final Identifier ID = ModIdentifier.of("mutation");

    public static final IRecipeType<MutationRecipe> RECIPE = IRecipeType.create(ID, MutationRecipe.class);

    private final IDrawable background;
    private final IPlatformFluidHelper<FluidStack> fluidHelper;

    public MutationCategory(
            IGuiHelper guiHelper,
            IPlatformFluidHelper<FluidStack> fluidHelper
    ) {
        super(
                guiHelper,
                RECIPE,
                JeiTranslations.MUTATIONS,
                guiHelper.createDrawableItemLike(
                        ModItems.BEE_BOX.get()
                ),
                WIDTH,
                HEIGHT
        );

        this.fluidHelper = fluidHelper;

        this.background = guiHelper.createDrawable(
                GUI,
                0,
                0,
                WIDTH,
                HEIGHT
        );
    }

    public static List<MutationRecipe> getMutationRecipes(Level level) {
        List<MutationRecipe> recipes = new ArrayList<>();

        BeeRegistry.get()
                .getSetOfBees()
                .forEach(beeData ->
                        beeData.getMutationData()
                                .mutations(level)
                                .forEach((input, outputs) ->
                                        outputs.forEach(output ->
                                                recipes.add(
                                                        new MutationRecipe(
                                                                beeData.entityType(),
                                                                input,
                                                                output,
                                                                outputs
                                                        )
                                                )
                                        )
                                )
                );

        return recipes;
    }

    @Override
    public void setRecipe(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull MutationRecipe recipe,
            @NotNull IFocusGroup focuses
    ) {
        IRecipeSlotBuilder input = builder
                .addInputSlot(16, 53)
                .setSlotName("input")
                .addRichTooltipCallback(getSlotTooltip(recipe));

        addMutationIngredient(input, recipe.input());

        builder.addInputSlot(17, 8)
                .add(JEICompat.ENTITY_INGREDIENT, EntityIngredient.of(recipe.bee()))
                .setSlotName("bee");

        IRecipeSlotBuilder output = builder
                .addOutputSlot(90, 48)
                .setSlotName("output")
                .addRichTooltipCallback(getSlotTooltip(recipe));

        addMutationIngredient(output, recipe.output());
    }

    private void addMutationIngredient(
            IRecipeSlotBuilder slot,
            MutationType mutation
    ) {
        if (mutation instanceof ItemDisplay itemDisplay) {
            slot.add(VanillaTypes.ITEM_STACK, itemDisplay.displayedItem());
            return;
        }

        if (mutation instanceof FluidDisplay fluidDisplay) {
            FluidStack stack = fluidDisplay.displayedFluidstack();

            slot.add(fluidHelper.getFluidIngredientType(), stack);
            return;
        }

        if (mutation instanceof EntityDisplay entityDisplay) {
            slot.add(JEICompat.ENTITY_INGREDIENT, EntityIngredient.of(entityDisplay.displayedEntity()));
        }
    }

    private static IRecipeSlotRichTooltipCallback getSlotTooltip(MutationRecipe recipe) {
        return (view, tooltip) ->
                view.getSlotName().ifPresent(name -> {
                    if ("input".equals(name)) {
                        setComponentTooltip(recipe.input().components(), tooltip);
                    }

                    if ("output".equals(name)) {
                        setComponentTooltip(recipe.output().components(), tooltip);
                    }
                });
    }

    private static void setComponentTooltip(Optional<DataComponentPatch> components, ITooltipBuilder tooltip) {
        components.ifPresent(patch -> {
            if (Minecraft.getInstance().hasShiftDown()) {
                tooltip.add(
                        Component.literal(patch.toString())
                                .withStyle(ChatFormatting.DARK_PURPLE)
                );
            } else {
                tooltip.add(
                        JeiTranslations.NBT.withStyle(
                                ChatFormatting.DARK_PURPLE
                        )
                );
            }
        });
    }

    @Override
    public void getTooltip(@NotNull ITooltipBuilder tooltip, @NotNull MutationRecipe recipe, @NotNull IRecipeSlotsView view, double mouseX, double mouseY) {
        if (mouseX >= 63 && mouseX <= 72 && mouseY >= 8 && mouseY <= 17) {
            tooltip.add(JeiTranslations.MUTATION_INFO);
            return;
        }

        double outputWeightChance = recipe.pool().getAdjustedWeight(recipe.output().weight()) * recipe.output().chance();

        if (mouseX >= 54 && mouseX <= 63 && mouseY >= 34 && mouseY <= 43 && outputWeightChance < 1.0d) {
            tooltip.add(JeiTranslations.MUTATION_WEIGHT_CHANCE_INFO);
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable(ModTranslations.WEIGHT, recipe.displayFormattedWeight()));
            tooltip.add(Component.translatable(ModTranslations.CHANCE,recipe.displayFormattedChance()));
        }
    }

    @Override
    public void draw(@NotNull MutationRecipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
        beeHive.draw(graphics, 65, 10);
        info.draw(graphics, 63, 8);

        Font font = Minecraft.getInstance().font;

        double outputWeightChance = recipe.pool().getAdjustedWeight(recipe.output().weight()) * recipe.output().chance();

        if (outputWeightChance < 1.0d) {
            String chanceString = NumberFormat.getPercentInstance().format(outputWeightChance);
            int padding = font.width(chanceString) / 2;
            info.draw(graphics, 54, 34);
            graphics.text(font, chanceString, 76 - padding, 35, 0xff808080, false);
        }

        if (recipe.input().chance() < 1.0d) {
            String chanceString = NumberFormat.getPercentInstance().format(recipe.input().chance());
            int padding = font.width(chanceString) / 2;
            graphics.text(font, chanceString, 48 - padding, 66, 0xff808080, false);
        }

        if (!(recipe.input() instanceof EntityDisplay)) {
            slot.draw(graphics, 15, 52);
        }

        if (!(recipe.output() instanceof EntityDisplay)) {
            outputSlot.draw(graphics, 85, 43);
        }
    }

    @Override
    public @Nullable Identifier getIdentifier(@NonNull MutationRecipe recipe) {
        return null;
    }
}