package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.util.MathUtils;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class CentrifugeCategory extends BaseCategory<CentrifugeCategory.CentrifugeRecipeAdapter> {

    private static final int WIDTH = 134;
    private static final int HEIGHT = 66;

    private static final Identifier GUI = ModIdentifier.of("textures/gui/jei/centrifuge.png");

    public static final Identifier ID = ModIdentifier.of("centrifuge");

    public static final IRecipeType<CentrifugeRecipeAdapter> RECIPE = IRecipeType.create(ID, CentrifugeRecipeAdapter.class);

    private final IJeiHelpers helpers;
    private final IPlatformFluidHelper<FluidStack> fluidHelper;
    private final IDrawable background;

    private final IDrawable weightIcon;
    private final IDrawable weightIconHovered;
    private final IDrawable chanceIcon;
    private final IDrawable chanceIconHovered;

    public CentrifugeCategory(IJeiHelpers helpers) {
        super(helpers.getGuiHelper(), RECIPE, JeiTranslations.CENTRIFUGE, helpers.getGuiHelper().createDrawableItemLike(ModItems.CENTRIFUGE.get()), WIDTH, HEIGHT);

        this.helpers = helpers;
        this.fluidHelper = getFluidHelper(helpers);

        IGuiHelper guiHelper = helpers.getGuiHelper();

        this.background = guiHelper.createDrawable(GUI, 0, 0, WIDTH, HEIGHT);

        this.weightIcon = guiHelper.createDrawable(GUI, 134, 0, 9, 9);

        this.chanceIcon = guiHelper.createDrawable(GUI, 134, 9, 9, 9);

        this.weightIconHovered = guiHelper.createDrawable(GUI, 134, 18, 9, 9);

        this.chanceIconHovered = guiHelper.createDrawable(GUI, 134, 27, 9, 9);
    }

    @SuppressWarnings("unchecked")
    private static IPlatformFluidHelper<FluidStack> getFluidHelper(IJeiHelpers helpers) {
        return (IPlatformFluidHelper<FluidStack>) helpers.getPlatformFluidHelper();
    }

    public static List<CentrifugeRecipeAdapter> getRecipes(IPlatformFluidHelper<FluidStack> fluidHelper, Iterable<CentrifugeRecipe> recipes) {
        List<CentrifugeRecipeAdapter> result = new ArrayList<>();

        for (CentrifugeRecipe recipe : recipes) {
            result.add(new CentrifugeRecipeAdapter(fluidHelper, recipe));
        }

        return result;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CentrifugeRecipeAdapter adapter, @NotNull IFocusGroup focuses) {
        CentrifugeRecipe recipe = adapter.recipe();

        builder.addInputSlot(10, 25).add(recipe.ingredient()).setSlotName("input");

        List<List<ItemStack>> itemStacks = recipe.itemOutputs().stream().map(output -> output.pool().stream().map(ItemOutput::itemStack).toList()).toList();
        List<List<FluidStack>> fluidStacks = recipe.fluidOutputs().stream().map(output -> output.pool().stream().map(FluidOutput::fluid).map(FluidStackTemplate::create).toList()).toList();

        for (int i = 0; i < 3; i++) {
            if (i < itemStacks.size()) {
                builder.addInputSlot(72, 7 + i * 18)
                        .addIngredients(VanillaTypes.ITEM_STACK, itemStacks.get(i))
                        .setSlotName("item_output_" + i);
            }

            if (i < fluidStacks.size()) {
                addFluids(builder.addOutputSlot(108, 7 + i * 18), fluidStacks.get(i))
                        .setFluidRenderer(1, false, 16, 16)
                        .setSlotName("fluid_output_" + i);
            }
        }
    }

    private IRecipeSlotBuilder addFluids(IRecipeSlotBuilder slot, List<FluidStack> fluids) {
        return slot.addIngredients(fluidHelper.getFluidIngredientType(), fluids);
    }

    @Override
    public void draw(@NotNull CentrifugeRecipeAdapter recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);

        for (int i = 0; i < 3; i++) {
            int index = i;

            view.findSlotByName("item_output_" + i).ifPresent(slot -> drawWeightAndChance(graphics, 61, mouseX, mouseY, index));
            view.findSlotByName("fluid_output_" + i).ifPresent(slot -> drawWeightAndChance(graphics, 97, mouseX, mouseY, index));
        }
    }

    private void drawWeightAndChance(GuiGraphicsExtractor graphics, int start, double mouseX, double mouseY, int index) {
        int weightY = 6 + 18 * index;
        int chanceY = 15 + 18 * index;

        boolean weightHovered = MathUtils.inRangeInclusive((int) mouseX, start, start + 9)
                && MathUtils.inRangeInclusive((int) mouseY, weightY, weightY + 9);
        boolean chanceHovered = MathUtils.inRangeInclusive((int) mouseX, start, start + 9)
                && MathUtils.inRangeInclusive((int) mouseY, chanceY, chanceY + 9);

        IDrawable weight = weightHovered ? weightIconHovered : weightIcon;
        IDrawable chance = chanceHovered ? chanceIconHovered : chanceIcon;
        weight.draw(graphics, start, weightY);
        chance.draw(graphics, start, chanceY);
    }

    @Override
    public void getTooltip(@NotNull ITooltipBuilder tooltip, @NotNull CentrifugeRecipeAdapter adapter, @NotNull IRecipeSlotsView view, double mouseX, double mouseY) {
        CentrifugeRecipe recipe = adapter.recipe();

        for (int i = 0; i < 3; i++) {
            if (recipe.itemOutputs().size() > i) {
                String slotName = "item_output_" + i;

                Optional<ItemStack> displayed = view.findSlotByName(slotName).flatMap(slot -> slot.getDisplayedIngredient(VanillaTypes.ITEM_STACK));

                if (displayed.isPresent()) {
                    ItemStack stack = displayed.get();

                    Double weight = adapter.getItemWeight(slotName, stack);

                    double chance = recipe.itemOutputs().get(i).chance();

                    addTooltip(tooltip, stack.getHoverName(), weight, chance, mouseX, mouseY, i, recipe.itemOutputs().size(), 61, 70);

                    if (isTooltipArea(mouseX, mouseY, i, 61, 70)) {
                        return;
                    }
                }
            }

            if (recipe.fluidOutputs().size() > i) {
                String slotName = "fluid_output_" + i;

                Optional<FluidTooltip> output = adapter.getFluidWeight(helpers, view, slotName);

                if (output.isPresent()) {
                    FluidTooltip fluid = output.get();

                    double chance = recipe.fluidOutputs().get(i).chance();

                    addTooltip(tooltip, fluid.displayName(), fluid.weight(), chance, mouseX, mouseY, i, recipe.fluidOutputs().size(), 97, 106);

                    if (isTooltipArea(mouseX, mouseY, i, 97, 106)) {
                        return;
                    }
                }
            }
        }
    }

    private static boolean isTooltipArea(double mouseX, double mouseY, int index, int min, int max) {
        int weightY = 6 + 18 * index;
        int chanceY = 15 + 18 * index;

        boolean horizontal = MathUtils.inRangeInclusive((int) mouseX, min, max);

        if (!horizontal) {
            return false;
        }

        return MathUtils.inRangeInclusive((int) mouseY, weightY, weightY + 9) || MathUtils.inRangeInclusive((int) mouseY, chanceY, chanceY + 9);
    }

    private static void addTooltip(ITooltipBuilder tooltip, @Nullable Component displayName, @Nullable Double weight, double chance, double mouseX, double mouseY, int index, int outputSize, int min, int max) {
        int weightY = 6 + 18 * index;
        int chanceY = 15 + 18 * index;

        boolean weightHovered = MathUtils.inRangeInclusive((int) mouseX, min, max) && MathUtils.inRangeInclusive((int) mouseY, weightY, weightY + 9);

        if (weightHovered) {
            if (displayName != null) {
                tooltip.add(displayName);
            }

            if (weight != null) {
                tooltip.add(Component.translatable(JeiTranslations.CENTRIFUGE_WEIGHT, NumberFormat.getPercentInstance().format(weight)));
            } else {
                tooltip.add(JeiTranslations.CENTRIFUGE_WEIGHT_EMPTY);
            }

            return;
        }

        boolean chanceHovered = MathUtils.inRangeInclusive((int) mouseX, min, max) && MathUtils.inRangeInclusive((int) mouseY, chanceY, chanceY + 9);

        if (chanceHovered) {
            if (outputSize > index) {
                tooltip.add(Component.translatable(JeiTranslations.CENTRIFUGE_CHANCE, NumberFormat.getPercentInstance().format(chance)));
            } else {
                tooltip.add(JeiTranslations.CENTRIFUGE_CHANCE_EMPTY);
            }
        }
    }

    @Override
    public @Nullable Identifier getIdentifier(@NonNull CentrifugeRecipeAdapter recipe) {
        return null;
    }

    public static final class CentrifugeRecipeAdapter {

        private final CentrifugeRecipe recipe;

        private final Map<String, Object2DoubleMap<ItemStack>> itemWeights = new HashMap<>();

        private final Map<String, Object2DoubleMap<FluidStack>> fluidWeights = new HashMap<>();

        private final IPlatformFluidHelper<FluidStack> fluidHelper;

        public CentrifugeRecipeAdapter(IPlatformFluidHelper<FluidStack> fluidHelper, CentrifugeRecipe recipe) {
            this.recipe = recipe;
            this.fluidHelper = fluidHelper;

            buildItemWeights();
            buildFluidWeights();
        }

        private void buildItemWeights() {
            List<CentrifugeRecipe.Output<ItemOutput, ItemStack>> outputs = recipe.itemOutputs();

            for (int i = 0; i < outputs.size(); i++) {
                Object2DoubleMap<ItemStack> weights = new Object2DoubleOpenHashMap<>();

                outputs.get(i).pool().forEachWithSelf((pool, output) -> weights.put(output.itemStack(), pool.getAdjustedWeight(output.weight())));

                itemWeights.put("item_output_" + i, weights);
            }
        }

        private void buildFluidWeights() {
            List<CentrifugeRecipe.Output<FluidOutput, FluidStack>> outputs = recipe.fluidOutputs();

            for (int i = 0; i < outputs.size(); i++) {
                Object2DoubleMap<FluidStack> weights = new Object2DoubleOpenHashMap<>();

                outputs.get(i).pool().forEachWithSelf((pool, output) -> {
                    FluidStack fluid = output.fluid().create();

                    weights.put(fluid, pool.getAdjustedWeight(output.weight()));
                });

                fluidWeights.put("fluid_output_" + i, weights);
            }
        }

        public CentrifugeRecipe recipe() {
            return recipe;
        }

        public Double getItemWeight(String slot, ItemStack displayedStack) {
            Object2DoubleMap<ItemStack> weights = itemWeights.get(slot);

            if (weights == null) {
                return null;
            }

            for (Object2DoubleMap.Entry<ItemStack> entry : weights.object2DoubleEntrySet()) {

                if (ItemStack.matches(entry.getKey(), displayedStack)) {
                    return entry.getDoubleValue();
                }
            }

            return null;
        }

        private Optional<FluidTooltip> getFluidWeight(IJeiHelpers helpers, IRecipeSlotsView view, String slot) {
            return view.findSlotByName(slot).flatMap(recipeSlot -> recipeSlot.getDisplayedIngredient(fluidHelper.getFluidIngredientType())).map(fluid -> {
                IIngredientHelper<FluidStack> ingredientHelper = helpers.getIngredientManager().getIngredientHelper(fluid);

                return new FluidTooltip(getFluidWeight(slot, fluid), Component.literal(ingredientHelper.getDisplayName(fluid)));
            });
        }

        public Double getFluidWeight(String slot, FluidStack displayedStack) {
            Object2DoubleMap<FluidStack> weights = fluidWeights.get(slot);

            if (weights == null) {
                return null;
            }

            for (Object2DoubleMap.Entry<FluidStack> entry : weights.object2DoubleEntrySet()) {

                FluidStack expected = entry.getKey();

                if (FluidStack.isSameFluidSameComponents(expected, displayedStack)) {
                    return entry.getDoubleValue();
                }
            }

            return null;
        }
    }

    private record FluidTooltip(@Nullable Double weight, Component displayName) {
    }
}