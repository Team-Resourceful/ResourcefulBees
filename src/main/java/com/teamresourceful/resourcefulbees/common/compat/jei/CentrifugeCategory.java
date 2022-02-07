package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CentrifugeCategory extends BaseCategory<CentrifugeCategory.CentrifugeRecipeAdapter> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#%");

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/centrifuge.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "centrifuge");

    public CentrifugeCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                TranslationConstants.Jei.CENTRIFUGE,
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 134, 66).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM, ModItems.CENTRIFUGE_ADVANCED_TERMINAL.get().getDefaultInstance()),
                CentrifugeRecipeAdapter.class);
    }

    public static List<CentrifugeRecipeAdapter> getRecipes(Collection<Recipe<Container>> recipes) {
        List<CentrifugeRecipeAdapter> newRecipes = new ArrayList<>();
        for (Recipe<Container> recipe : recipes) {
            if (recipe instanceof CentrifugeRecipe){
                newRecipes.add(new CentrifugeRecipeAdapter(((CentrifugeRecipe) recipe)));
            }
        }
        return newRecipes;
    }

    @Override
    public void setIngredients(@NotNull CentrifugeRecipeAdapter recipe, @NotNull IIngredients ingredients) {
        ingredients.setInputIngredients(Lists.newArrayList(recipe.recipe.ingredient()));
        ingredients.setOutputLists(VanillaTypes.ITEM, recipe.recipe.itemOutputs().stream()
                .map(output -> output.pool().stream().map(ItemOutput::getItemStack).collect(Collectors.toList()))
                .collect(Collectors.toList()));
        ingredients.setOutputLists(VanillaTypes.FLUID, recipe.recipe.fluidOutputs().stream()
                .map(output -> output.pool().stream().map(FluidOutput::getFluidStack).collect(Collectors.toList()))
                .collect(Collectors.toList()));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull CentrifugeRecipeAdapter recipe, @NotNull IIngredients ingredients) {
        IGuiIngredientGroup<ItemStack> guiItemStacks = recipeLayout.getIngredientsGroup(VanillaTypes.ITEM);
        IGuiIngredientGroup<FluidStack> guiFluidStacks = recipeLayout.getIngredientsGroup(VanillaTypes.FLUID);
        guiItemStacks.init(0, true, 9, 24);
        for (int i = 0; i < 3; i++) {
            guiItemStacks.init(i + 1, false, 71, 6 + (i * 18));
            guiFluidStacks.init(i, false, 107, 6 + (i * 18));
        }
        guiFluidStacks.set(ingredients);
        guiItemStacks.set(ingredients);

        recipe.setIngredients(recipeLayout.getItemStacks().getGuiIngredients(), recipeLayout.getFluidStacks().getGuiIngredients());
    }

    @Override
    public void draw(@NotNull CentrifugeRecipeAdapter recipe, @NotNull PoseStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
        RenderUtils.bindTexture(GUI_BACK);
        for (int i = 0; i < 3; i++) {
            if (recipe.items.get(i+1) != null) drawWeightAndChance(matrixStack, 61, mouseX, mouseY, i);
            if (recipe.fluids.get(i) != null) drawWeightAndChance(matrixStack, 97, mouseX, mouseY, i);
        }
    }

    private static void drawWeightAndChance(PoseStack matrixStack, int start, double mouseX, double mouseY, int i){
        boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, start, start+9) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
        GuiUtils.drawTexturedModalRect(matrixStack, start, 6 + (18*i), 134, (inBounds ? 18 : 0), 9,9, 100);
        inBounds = MathUtils.inRangeInclusive((int) mouseX, start, start+9) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
        GuiUtils.drawTexturedModalRect(matrixStack, start, 15 + (18*i), 134, 9 + (inBounds ? 18 : 0), 9,9, 100);
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull CentrifugeRecipeAdapter recipe, double mouseX, double mouseY) {
        for (int i = 0; i < 3; i++) {
            if (recipe.getRecipe().itemOutputs().size() > i) {
                Double itemWeight = recipe.getItemWeight(i + 1);
                ItemStack item = recipe.items.get(i + 1).getDisplayedIngredient();
                double itemChance = recipe.getRecipe().itemOutputs().get(i).chance();
                List<Component> itemTooltip = drawTooltip(item == null ? null : item.getDisplayName(), itemWeight, itemChance, mouseX, mouseY, i, recipe.getRecipe().itemOutputs().size(), 61, 70);
                if (!itemTooltip.isEmpty()) return itemTooltip;
            }

            if (recipe.getRecipe().fluidOutputs().size() > i) {
                Double fluidWeight = recipe.getFluidWeight(i);
                FluidStack fluid = recipe.fluids.get(i).getDisplayedIngredient();
                double fluidChance = recipe.getRecipe().fluidOutputs().get(i).chance();
                List<Component> fluidTooltip = drawTooltip(fluid == null ? null : fluid.getDisplayName(), fluidWeight, fluidChance, mouseX, mouseY, i, recipe.getRecipe().fluidOutputs().size(), 97, 106);
                if (!fluidTooltip.isEmpty()) return fluidTooltip;
            }
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    private static List<Component> drawTooltip(Component displayname, Double weight, double chance, double mouseX, double mouseY, int i, int outputSize, int min, int max) {
        boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, min, max) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
        if (inBounds) {
            List<Component> tooltip = new ArrayList<>();
            if (displayname != null) tooltip.add(displayname);
            if (weight != null) {
                tooltip.add(new TranslatableComponent(TranslationConstants.Jei.CENTRIFUGE_WEIGHT, DECIMAL_FORMAT.format(weight)));
            } else {
                tooltip.add(TranslationConstants.Jei.CENTRIFUGE_WEIGHT_EMPTY);
            }
            return tooltip;
        }
        inBounds = MathUtils.inRangeInclusive((int) mouseX, min, max) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
        if (inBounds) {
            if (outputSize > i)
                return Collections.singletonList(new TranslatableComponent(TranslationConstants.Jei.CENTRIFUGE_CHANCE, DECIMAL_FORMAT.format(chance)));
            else {
                return Collections.singletonList(TranslationConstants.Jei.CENTRIFUGE_CHANCE_EMPTY);
            }
        }
        return Collections.emptyList();
    }

    protected static class CentrifugeRecipeAdapter {

        private final CentrifugeRecipe recipe;

        private final Map<Integer, Map<ItemStack, Double>> itemWeights = new HashMap<>();
        private final Map<Integer, Map<FluidStack, Double>> fluidWeights = new HashMap<>();

        private Map<Integer, ? extends IGuiIngredient<ItemStack>> items = null;
        private Map<Integer, ? extends IGuiIngredient<FluidStack>> fluids = null;

        public CentrifugeRecipeAdapter(CentrifugeRecipe recipe) {
            this.recipe = recipe;

            List<CentrifugeRecipe.Output<ItemOutput>> itemOutputList = this.recipe.itemOutputs();

            for (int i = 0; i < itemOutputList.size(); i++) {
                RandomCollection<ItemOutput> pool = itemOutputList.get(i).pool();
                Map<ItemStack, Double> weights = new HashMap<>();
                pool.forEach(output -> weights.put(output.getItemStack(), pool.getAdjustedWeight(output.getWeight())));
                itemWeights.put(i+1, weights);
            }

            List<CentrifugeRecipe.Output<FluidOutput>> fluidOutputList = this.recipe.fluidOutputs();

            for (int i = 0; i < fluidOutputList.size(); i++) {
                RandomCollection<FluidOutput> pool = fluidOutputList.get(i).pool();
                Map<FluidStack, Double> weights = new HashMap<>();
                pool.forEach(output -> weights.put(output.getFluidStack(), pool.getAdjustedWeight(output.getWeight())));
                fluidWeights.put(i, weights);
            }
        }

        public void setIngredients(Map<Integer, ? extends IGuiIngredient<ItemStack>> items, Map<Integer, ? extends IGuiIngredient<FluidStack>> fluids) {
            this.items = items;
            this.fluids = fluids;
        }

        public Double getItemWeight(Integer integer) {
            ItemStack displayedIngredient = items.get(integer).getDisplayedIngredient();
            if (displayedIngredient == null) return null;
            Map<ItemStack, Double> weightMap = itemWeights.get(integer);
            if (weightMap == null) return null;
            for (Map.Entry<ItemStack, Double> entry : weightMap.entrySet()) {
                if (ItemStack.isSameItemSameTags(entry.getKey(), displayedIngredient)) return entry.getValue();
            }
            return null;
        }

        public Double getFluidWeight(Integer integer) {
            FluidStack displayedIngredient = fluids.get(integer).getDisplayedIngredient();
            if (displayedIngredient == null) return null;
            Map<FluidStack, Double> weightMap = fluidWeights.get(integer);
            if (weightMap == null) return null;
            for (Map.Entry<FluidStack, Double> entry : weightMap.entrySet()) {
                if (entry.getKey().isFluidEqual(displayedIngredient)) return entry.getValue();
            }
            return null;
        }

        public CentrifugeRecipe getRecipe() {
            return recipe;
        }
    }
}
