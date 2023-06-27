package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeItems;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.*;

public class CentrifugeCategory extends BaseCategory<CentrifugeCategory.CentrifugeRecipeAdapter> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/jei/centrifuge.png");
    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "centrifuge");
    public static final RecipeType<CentrifugeRecipeAdapter> RECIPE = new RecipeType<>(ID, CentrifugeRecipeAdapter.class);

    private final IJeiHelpers helpers;

    public CentrifugeCategory(IJeiHelpers helpers) {
        super(helpers.getGuiHelper(), RECIPE,
                JeiTranslations.CENTRIFUGE,
                helpers.getGuiHelper().drawableBuilder(GUI_BACK, 0, 0, 134, 66).addPadding(0, 0, 0, 0).build(),
                helpers.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, CentrifugeItems.CENTRIFUGE_ADVANCED_TERMINAL.get().getDefaultInstance()));
        this.helpers = helpers;
    }

    public static List<CentrifugeRecipeAdapter> getRecipes(Collection<CentrifugeRecipe> recipes) {
        List<CentrifugeRecipeAdapter> newRecipes = new ArrayList<>();
        for (CentrifugeRecipe recipe : recipes) {
            newRecipes.add(new CentrifugeRecipeAdapter(recipe));
        }
        return newRecipes;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CentrifugeRecipeAdapter recipe, @NotNull IFocusGroup focuses) {
        List<List<RecipeFluid>> fluids = recipe.recipe.fluidOutputs().stream()
                .map(output -> output.pool().stream().map(FluidOutput::fluid).toList()).toList();
        List<List<ItemStack>> itemStacks = recipe.recipe.itemOutputs().stream()
                .map(output -> output.pool().stream().map(ItemOutput::itemStack).toList()).toList();

        ItemStack ingredient = recipe.recipe.ingredient().getItems()[0];
        ingredient.setCount(recipe.recipe.inputAmount());

        builder.addSlot(RecipeIngredientRole.INPUT,10, 25).addItemStack(ingredient).setSlotName("input");
        for (int i = 0; i < 3; i++) {
            if (i < itemStacks.size())
                builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 7 + (i * 18))
                        .addIngredients(VanillaTypes.ITEM_STACK, itemStacks.get(i))
                        .setSlotName("item_output_"+i);

            if (i < fluids.size())
                addFluids(
                        builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 7 + (i * 18)),
                        helpers.getPlatformFluidHelper(),
                        fluids.get(i)
                ).setFluidRenderer(1, false, 16, 16)
                        .setSlotName("fluid_output_"+i);
        }
    }

    private static <T> IRecipeSlotBuilder addFluids(IRecipeSlotBuilder builder, IPlatformFluidHelper<T> helper, List<RecipeFluid> input) {
        List<T> fluids = new ArrayList<>();
        for (RecipeFluid fluid : input) {
            fluids.add(helper.create(fluid.fluid(), fluid.amount(), fluid.tag()));
        }
        return builder.addIngredients(helper.getFluidIngredientType(), fluids);
    }

    @Override
    public void draw(@NotNull CentrifugeRecipeAdapter recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, view, graphics, mouseX, mouseY);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            view.findSlotByName("item_output_"+i).ifPresent(slot -> drawWeightAndChance(graphics, 61, mouseX, mouseY, finalI));
            view.findSlotByName("fluid_output_"+i).ifPresent(slot -> drawWeightAndChance(graphics, 97, mouseX, mouseY, finalI));
        }
    }

    private static void drawWeightAndChance(GuiGraphics graphics, int start, double mouseX, double mouseY, int i){
        boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, start, start+9) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
        graphics.blit(GUI_BACK, start, 6 + (18*i), 134, (inBounds ? 18 : 0), 9,9);
        inBounds = MathUtils.inRangeInclusive((int) mouseX, start, start+9) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
        graphics.blit(GUI_BACK, start, 15 + (18*i), 134, 9 + (inBounds ? 18 : 0), 9,9);
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull CentrifugeRecipeAdapter recipe, @NotNull IRecipeSlotsView view, double mouseX, double mouseY) {
        for (int i = 0; i < 3; i++) {
            if (recipe.getRecipe().itemOutputs().size() > i) {
                String id = "item_output_"+i;
                Optional<ItemStack> itemStack = view.findSlotByName(id).flatMap(slot -> slot.getDisplayedIngredient(VanillaTypes.ITEM_STACK));
                if (itemStack.isPresent()) {
                    Double itemWeight = recipe.getItemWeight(id, itemStack.get());
                    double itemChance = recipe.getRecipe().itemOutputs().get(i).chance();
                    List<Component> itemTooltip = drawTooltip(itemStack.get().getHoverName(), itemWeight, itemChance, mouseX, mouseY, i, recipe.getRecipe().itemOutputs().size(), 61, 70);
                    if (!itemTooltip.isEmpty()) return itemTooltip;
                }
            }

            if (recipe.getRecipe().fluidOutputs().size() > i) {
                String id = "fluid_output_"+i;
                Optional<FluidStack> fluidStack = view.findSlotByName(id).flatMap(slot -> slot.getDisplayedIngredient(ForgeTypes.FLUID_STACK));
                if (fluidStack.isPresent()) {
                    Double itemWeight = recipe.getFluidWeight(id, fluidStack.get());
                    double itemChance = recipe.getRecipe().fluidOutputs().get(i).chance();
                    List<Component> fluidTooltip = drawTooltip(fluidStack.get().getDisplayName(), itemWeight, itemChance, mouseX, mouseY, i, recipe.getRecipe().fluidOutputs().size(), 97, 106);
                    if (!fluidTooltip.isEmpty()) return fluidTooltip;
                }
            }
        }
        return super.getTooltipStrings(recipe, view, mouseX, mouseY);
    }

    private static List<Component> drawTooltip(Component displayName, Double weight, double chance, double mouseX, double mouseY, int i, int outputSize, int min, int max) {
        boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, min, max) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
        if (inBounds) {
            List<Component> tooltip = new ArrayList<>();
            if (displayName != null) tooltip.add(displayName);
            if (weight != null) {
                tooltip.add(Component.translatable(JeiTranslations.CENTRIFUGE_WEIGHT, NumberFormat.getPercentInstance().format(weight)));
            } else {
                tooltip.add(JeiTranslations.CENTRIFUGE_WEIGHT_EMPTY);
            }
            return tooltip;
        }
        inBounds = MathUtils.inRangeInclusive((int) mouseX, min, max) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
        if (inBounds) {
            if (outputSize > i)
                return Collections.singletonList(Component.translatable(JeiTranslations.CENTRIFUGE_CHANCE, NumberFormat.getPercentInstance().format(chance)));
            else {
                return Collections.singletonList(JeiTranslations.CENTRIFUGE_CHANCE_EMPTY);
            }
        }
        return Collections.emptyList();
    }

    protected static class CentrifugeRecipeAdapter {

        private final CentrifugeRecipe recipe;

        private final HashMap<String, Object2DoubleMap<ItemStack>> itemWeights = new HashMap<>();
        private final HashMap<String, Object2DoubleMap<RecipeFluid>> fluidWeights = new HashMap<>();

        public CentrifugeRecipeAdapter(CentrifugeRecipe recipe) {
            this.recipe = recipe;

            List<CentrifugeRecipe.Output<ItemOutput, ItemStack>> itemOutputList = this.recipe.itemOutputs();

            for (int i = 0; i < itemOutputList.size(); i++) {
                Object2DoubleMap<ItemStack> weights = new Object2DoubleOpenHashMap<>();
                itemOutputList.get(i).pool().forEachWithSelf((pool, output) -> weights.put(output.itemStack(), pool.getAdjustedWeight(output.weight())));
                itemWeights.put("item_output_"+i, weights);
            }

            List<CentrifugeRecipe.Output<FluidOutput, RecipeFluid>> fluidOutputList = this.recipe.fluidOutputs();

            for (int i = 0; i < fluidOutputList.size(); i++) {
                Object2DoubleMap<RecipeFluid> weights = new Object2DoubleOpenHashMap<>();
                fluidOutputList.get(i).pool().forEachWithSelf((pool, output) -> weights.put(output.fluid(), pool.getAdjustedWeight(output.weight())));
                fluidWeights.put("fluid_output_"+i, weights);
            }
        }

        public Double getItemWeight(String slot, ItemStack displayedStack) {
            Map<ItemStack, Double> weightMap = itemWeights.get(slot);
            if (weightMap == null) return null;
            for (Map.Entry<ItemStack, Double> entry : weightMap.entrySet()) {
                if (ItemStack.matches(entry.getKey(), displayedStack)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        public Double getFluidWeight(String slot, FluidStack displayedStack) {
            Map<RecipeFluid, Double> weightMap = fluidWeights.get(slot);
            if (weightMap == null) return null;
            for (Map.Entry<RecipeFluid, Double> entry : weightMap.entrySet()) {
                if (entry.getKey().matches(displayedStack.getFluid(), displayedStack.getAmount(), displayedStack.getTag())) return entry.getValue();
            }
            return null;
        }

        public CentrifugeRecipe getRecipe() {
            return recipe;
        }
    }
}
