package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeFluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeItemOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.gui.GuiUtils;
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
                I18n.get("gui.resourcefulbees.jei.category.centrifuge"),
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 134, 66).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(ModItems.APIARY_BREEDER_ITEM.get().getDefaultInstance()),
                CentrifugeRecipeAdapter.class);
    }

    public static List<CentrifugeRecipeAdapter> getRecipes(Collection<IRecipe<IInventory>> recipes) {
        List<CentrifugeRecipeAdapter> newRecipes = new ArrayList<>();
        for (IRecipe<IInventory> recipe : recipes) {
            if (recipe instanceof CentrifugeRecipe){
                newRecipes.add(new CentrifugeRecipeAdapter(((CentrifugeRecipe) recipe)));
            }
        }
        return newRecipes;
    }

    @Override
    public void setIngredients(@NotNull CentrifugeRecipeAdapter recipe, @NotNull IIngredients ingredients) {
        ingredients.setInputIngredients(Lists.newArrayList(recipe.recipe.getIngredient()));
        ingredients.setOutputLists(VanillaTypes.ITEM, recipe.recipe.getItemOutputs().stream()
                .map(output -> output.getPool().stream().map(ItemOutput::getItemStack).collect(Collectors.toList()))
                .collect(Collectors.toList()));
        ingredients.setOutputLists(VanillaTypes.FLUID, recipe.recipe.getFluidOutputs().stream()
                .map(output -> output.getPool().stream().map(FluidOutput::getFluidStack).collect(Collectors.toList()))
                .collect(Collectors.toList()));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull CentrifugeRecipeAdapter recipe, @NotNull IIngredients ingredients) {
        IGuiIngredientGroup<ItemStack> guiItemStacks = recipeLayout.getIngredientsGroup(VanillaTypes.ITEM);
        guiItemStacks.init(0, true, 9, 24);
        for (int i = 0; i < 3; i++) {
            guiItemStacks.init(i+1, false, 71, 6+(i*18));
        }
        guiItemStacks.set(ingredients);
        IGuiIngredientGroup<FluidStack> guiFluidStacks = recipeLayout.getIngredientsGroup(VanillaTypes.FLUID);
        for (int i = 0; i < 3; i++) {
            guiFluidStacks.init(i, false, 107, 6+(i*18));
        }
        guiFluidStacks.set(ingredients);
        recipe.setIngredients(recipeLayout.getItemStacks().getGuiIngredients(), recipeLayout.getFluidStacks().getGuiIngredients());
    }

    @Override
    public void draw(@NotNull CentrifugeRecipeAdapter recipe, @NotNull MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);

        Minecraft.getInstance().getTextureManager().bind(GUI_BACK);
        for (int i = 0; i < 3; i++) {
            IGuiIngredient<ItemStack> ingredinet = recipe.items.get(i+1);
            if (ingredinet == null) continue;
            boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, 61, 70) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
            GuiUtils.drawTexturedModalRect(matrixStack, 61, 6 + (18*i), 134, (inBounds ? 18 : 0), 9,9, 100);
            inBounds = MathUtils.inRangeInclusive((int) mouseX, 61, 70) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
            GuiUtils.drawTexturedModalRect(matrixStack, 61, 15 + (18*i), 134, 9 + (inBounds ? 18 : 0), 9,9, 100);
        }

        for (int i = 0; i < 3; i++) {
            IGuiIngredient<FluidStack> ingredinet = recipe.fluids.get(i);
            if (ingredinet == null) continue;
            boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, 97, 106) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
            GuiUtils.drawTexturedModalRect(matrixStack, 97, 6 + (18*i), 134, (inBounds ? 18 : 0), 9,9, 100);
            inBounds = MathUtils.inRangeInclusive((int) mouseX, 97, 106) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
            GuiUtils.drawTexturedModalRect(matrixStack, 97, 15 + (18*i), 134, 9 + (inBounds ? 18 : 0), 9,9, 100);
        }

    }

    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(@NotNull CentrifugeRecipeAdapter recipe, double mouseX, double mouseY) {
        for (int i = 0; i < 3; i++) {
            Double weight = recipe.getItemWeight(i+1);
            boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, 61, 70) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18 * i), 6 + (18 * i) + 9);
            if (weight != null && inBounds) {
                ItemStack ingredient = recipe.items.get(i+1).getDisplayedIngredient();
                List<ITextComponent> tooltip = new ArrayList<>();
                if (ingredient != null) {
                    tooltip.add(ingredient.getDisplayName());
                }
                tooltip.add(new StringTextComponent("Weight: " + DECIMAL_FORMAT.format(weight)));
                return tooltip;
            }
            inBounds = MathUtils.inRangeInclusive((int) mouseX, 61, 70) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
            if (inBounds) {
                if (recipe.getRecipe().getItemOutputs().size() > i) {
                    return Collections.singletonList(new StringTextComponent("Pool Chance: " + DECIMAL_FORMAT.format(recipe.getRecipe().getItemOutputs().get(i).getChance())));
                } else {
                    return Collections.singletonList(new StringTextComponent("Pool Chance: POOL EMPTY"));
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            Double weight = recipe.getFluidWeight(i);
            boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, 97, 106) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
            if (weight != null && inBounds) {
                FluidStack ingredient = recipe.fluids.get(i).getDisplayedIngredient();
                List<ITextComponent> tooltip = new ArrayList<>();
                if (ingredient != null) {
                    tooltip.add(ingredient.getDisplayName());
                }
                tooltip.add(new StringTextComponent("Weight: " + DECIMAL_FORMAT.format(weight)));
                return tooltip;
            }
            inBounds = MathUtils.inRangeInclusive((int) mouseX, 97, 106) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
            if (inBounds) {
                if (recipe.getRecipe().getFluidOutputs().size() > i)
                    return Collections.singletonList(new StringTextComponent("Pool Chance: " + DECIMAL_FORMAT.format(recipe.getRecipe().getFluidOutputs().get(i).getChance())));
                else {
                    return Collections.singletonList(new StringTextComponent("Pool Chance: POOL EMPTY"));
                }
            }
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    protected static class CentrifugeRecipeAdapter {

        private final CentrifugeRecipe recipe;

        private final Map<Integer, Map<ItemStack, Double>> itemWeights = new HashMap<>();
        private final Map<Integer, Map<FluidStack, Double>> fluidWeights = new HashMap<>();

        private Map<Integer, ? extends IGuiIngredient<ItemStack>> items = null;
        private Map<Integer, ? extends IGuiIngredient<FluidStack>> fluids = null;

        public CentrifugeRecipeAdapter(CentrifugeRecipe recipe) {
            this.recipe = recipe;

            List<CentrifugeItemOutput> itemOutputList = this.recipe.getItemOutputs();

            for (int i = 0; i < itemOutputList.size(); i++) {
                RandomCollection<ItemOutput> pool = itemOutputList.get(i).getPool();
                Map<ItemStack, Double> weights = new HashMap<>();
                pool.forEach(output -> weights.put(output.getItemStack(), pool.getAdjustedWeight(output.getWeight())));
                itemWeights.put(i+1, weights);
            }

            List<CentrifugeFluidOutput> fluidOutputList = this.recipe.getFluidOutputs();

            for (int i = 0; i < fluidOutputList.size(); i++) {
                RandomCollection<FluidOutput> pool = fluidOutputList.get(i).getPool();
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
                if (Container.consideredTheSameItem(entry.getKey(), displayedIngredient)) return entry.getValue();
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
