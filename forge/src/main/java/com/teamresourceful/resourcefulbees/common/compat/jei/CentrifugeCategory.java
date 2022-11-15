package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

public class CentrifugeCategory extends BaseCategory<CentrifugeCategory.CentrifugeRecipeAdapter> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/centrifuge.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "centrifuge");
    public static final RecipeType<CentrifugeRecipeAdapter> RECIPE = new RecipeType<>(ID, CentrifugeRecipeAdapter.class);
    private static final DecimalFormat DECIMAL_PERCENT_FORMAT = new DecimalFormat("#.#%");

    public CentrifugeCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE,
                TranslationConstants.Jei.CENTRIFUGE,
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 134, 66).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModItems.CENTRIFUGE_ADVANCED_TERMINAL.get().getDefaultInstance()));
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
        List<List<FluidStack>> fluidStacks = recipe.recipe.fluidOutputs().stream()
                .map(output -> output.pool().stream().map(FluidOutput::fluid).toList()).toList();
        List<List<ItemStack>> itemStacks = recipe.recipe.itemOutputs().stream()
                .map(output -> output.pool().stream().map(ItemOutput::itemStack).toList()).toList();

        ItemStack ingredient = recipe.recipe.ingredient().getItems()[0];
        ingredient.setCount(recipe.recipe.getInputAmount());

        builder.addSlot(RecipeIngredientRole.INPUT,10, 25).addItemStack(ingredient).setSlotName("input");
        for (int i = 0; i < 3; i++) {
            if (i < itemStacks.size())
                builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 7 + (i * 18))
                        .addIngredients(VanillaTypes.ITEM_STACK, itemStacks.get(i))
                        .setSlotName("item_output_"+i);

            if (i < fluidStacks.size())
                builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 7 + (i * 18))
                        .addIngredients(ForgeTypes.FLUID_STACK, fluidStacks.get(i))
                        .setFluidRenderer(1, false, 16, 16)
                        .setSlotName("fluid_output_"+i);
        }
    }

    @Override
    public void draw(@NotNull CentrifugeRecipeAdapter recipe, @NotNull IRecipeSlotsView view, @NotNull PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, view, stack, mouseX, mouseY);
        ClientUtils.bindTexture(GUI_BACK);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            view.findSlotByName("item_output_"+i).ifPresent(slot -> drawWeightAndChance(stack, 61, mouseX, mouseY, finalI));
            view.findSlotByName("fluid_output_"+i).ifPresent(slot -> drawWeightAndChance(stack, 97, mouseX, mouseY, finalI));
        }
    }

    private static void drawWeightAndChance(PoseStack matrixStack, int start, double mouseX, double mouseY, int i){
        boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, start, start+9) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
        ScreenUtils.drawTexturedModalRect(matrixStack, start, 6 + (18*i), 134, (inBounds ? 18 : 0), 9,9, 100);
        inBounds = MathUtils.inRangeInclusive((int) mouseX, start, start+9) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
        ScreenUtils.drawTexturedModalRect(matrixStack, start, 15 + (18*i), 134, 9 + (inBounds ? 18 : 0), 9,9, 100);
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
                tooltip.add(Component.translatable(TranslationConstants.Jei.CENTRIFUGE_WEIGHT, DECIMAL_PERCENT_FORMAT.format(weight)));
            } else {
                tooltip.add(TranslationConstants.Jei.CENTRIFUGE_WEIGHT_EMPTY);
            }
            return tooltip;
        }
        inBounds = MathUtils.inRangeInclusive((int) mouseX, min, max) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
        if (inBounds) {
            if (outputSize > i)
                return Collections.singletonList(Component.translatable(TranslationConstants.Jei.CENTRIFUGE_CHANCE, DECIMAL_PERCENT_FORMAT.format(chance)));
            else {
                return Collections.singletonList(TranslationConstants.Jei.CENTRIFUGE_CHANCE_EMPTY);
            }
        }
        return Collections.emptyList();
    }

    protected static class CentrifugeRecipeAdapter {

        private final CentrifugeRecipe recipe;

        private final HashMap<String, Object2DoubleMap<ItemStack>> itemWeights = new HashMap<>();
        private final HashMap<String, Object2DoubleMap<FluidStack>> fluidWeights = new HashMap<>();

        public CentrifugeRecipeAdapter(CentrifugeRecipe recipe) {
            this.recipe = recipe;

            List<CentrifugeRecipe.Output<ItemOutput, ItemStack>> itemOutputList = this.recipe.itemOutputs();

            for (int i = 0; i < itemOutputList.size(); i++) {
                Object2DoubleMap<ItemStack> weights = new Object2DoubleOpenHashMap<>();
                itemOutputList.get(i).pool().forEachWithSelf((pool, output) -> weights.put(output.itemStack(), pool.getAdjustedWeight(output.weight())));
                itemWeights.put("item_output_"+i, weights);
            }

            List<CentrifugeRecipe.Output<FluidOutput, FluidStack>> fluidOutputList = this.recipe.fluidOutputs();

            for (int i = 0; i < fluidOutputList.size(); i++) {
                Object2DoubleMap<FluidStack> weights = new Object2DoubleOpenHashMap<>();
                fluidOutputList.get(i).pool().forEachWithSelf((pool, output) -> weights.put(output.fluid(), pool.getAdjustedWeight(output.weight())));
                fluidWeights.put("fluid_output_"+i, weights);
            }
        }

        public Double getItemWeight(String slot, ItemStack displayedStack) {
            Map<ItemStack, Double> weightMap = itemWeights.get(slot);
            if (weightMap == null) return null;
            for (Map.Entry<ItemStack, Double> entry : weightMap.entrySet()) {
                if (ModUtils.itemStackIsIdentical(entry.getKey(), displayedStack)) return entry.getValue();
            }
            return null;
        }

        public Double getFluidWeight(String slot, FluidStack displayedStack) {
            Map<FluidStack, Double> weightMap = fluidWeights.get(slot);
            if (weightMap == null) return null;
            for (Map.Entry<FluidStack, Double> entry : weightMap.entrySet()) {
                if (entry.getKey().isFluidStackIdentical(displayedStack)) return entry.getValue();
            }
            return null;
        }

        public CentrifugeRecipe getRecipe() {
            return recipe;
        }
    }
}
