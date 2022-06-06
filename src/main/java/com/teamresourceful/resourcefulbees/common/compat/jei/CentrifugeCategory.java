package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CentrifugeCategory extends BaseCategory<CentrifugeCategory.CentrifugeRecipeAdapter> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/centrifuge.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "centrifuge");
    public static final RecipeType<CentrifugeCategory.CentrifugeRecipeAdapter> RECIPE = new RecipeType<>(ID, CentrifugeCategory.CentrifugeRecipeAdapter.class);


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
        super.setRecipe(builder, recipe, focuses);
        List<List<FluidStack>> fluidStacks = recipe.recipe.fluidOutputs().stream()
                .map(output -> output.pool().stream().map(FluidOutput::getFluidStack).collect(Collectors.toList())).toList();
        List<List<ItemStack>> itemStacks = recipe.recipe.itemOutputs().stream()
                .map(output -> output.pool().stream().map(ItemOutput::getItemStack).collect(Collectors.toList())).toList();

        builder.addSlot(RecipeIngredientRole.INPUT,10, 25).addIngredients(recipe.recipe.ingredient()).setSlotName("input");
        for (int i = 0; i < 3; i++) {
            if (i < itemStacks.size())
                builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 7 + (i * 18))
                        .addIngredients(VanillaTypes.ITEM_STACK, itemStacks.get(i))
                        .setSlotName("item_output_"+i);

            if (i < fluidStacks.size())
                builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 7 + (i * 18))
                        .addIngredients(ForgeTypes.FLUID_STACK, fluidStacks.get(i))
                        .setSlotName("fluid_output_"+i);
        }
    }

    @Override
    public void draw(@NotNull CentrifugeRecipeAdapter recipe, @NotNull IRecipeSlotsView view, @NotNull PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, view, stack, mouseX, mouseY);
        RenderUtils.bindTexture(GUI_BACK);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            view.findSlotByName("item_output_"+i).ifPresent(slot -> drawWeightAndChance(stack, 61, mouseX, mouseY, finalI));
            view.findSlotByName("fluid_output_"+i).ifPresent(slot -> drawWeightAndChance(stack, 97, mouseX, mouseY, finalI));
        }
    }

    private static void drawWeightAndChance(PoseStack matrixStack, int start, double mouseX, double mouseY, int i){
        boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, start, start+9) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
        GuiUtils.drawTexturedModalRect(matrixStack, start, 6 + (18*i), 134, (inBounds ? 18 : 0), 9,9, 100);
        inBounds = MathUtils.inRangeInclusive((int) mouseX, start, start+9) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
        GuiUtils.drawTexturedModalRect(matrixStack, start, 15 + (18*i), 134, 9 + (inBounds ? 18 : 0), 9,9, 100);
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
                    List<Component> itemTooltip = drawTooltip(itemStack.get().getDisplayName(), itemWeight, itemChance, mouseX, mouseY, i, recipe.getRecipe().itemOutputs().size(), 61, 70);
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

    private static List<Component> drawTooltip(Component displayname, Double weight, double chance, double mouseX, double mouseY, int i, int outputSize, int min, int max) {
        boolean inBounds = MathUtils.inRangeInclusive((int) mouseX, min, max) && MathUtils.inRangeInclusive((int) mouseY, 6 + (18*i), 6 + (18*i) + 9);
        if (inBounds) {
            List<Component> tooltip = new ArrayList<>();
            if (displayname != null) tooltip.add(displayname);
            if (weight != null) {
                tooltip.add(new TranslatableComponent(TranslationConstants.Jei.CENTRIFUGE_WEIGHT, ModConstants.DECIMAL_PERCENT_FORMAT.format(weight)));
            } else {
                tooltip.add(TranslationConstants.Jei.CENTRIFUGE_WEIGHT_EMPTY);
            }
            return tooltip;
        }
        inBounds = MathUtils.inRangeInclusive((int) mouseX, min, max) && MathUtils.inRangeInclusive((int) mouseY, 15 + (18*i), 15 + (18*i) + 9);
        if (inBounds) {
            if (outputSize > i)
                return Collections.singletonList(new TranslatableComponent(TranslationConstants.Jei.CENTRIFUGE_CHANCE, ModConstants.DECIMAL_PERCENT_FORMAT.format(chance)));
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

            List<CentrifugeRecipe.Output<ItemOutput>> itemOutputList = this.recipe.itemOutputs();

            for (int i = 0; i < itemOutputList.size(); i++) {
                Object2DoubleMap<ItemStack> weights = new Object2DoubleOpenHashMap<>();
                itemOutputList.get(i).pool().forEachWithSelf((pool, output) -> weights.put(output.getItemStack(), pool.getAdjustedWeight(output.getWeight())));
                itemWeights.put("item_output_"+i, weights);
            }

            List<CentrifugeRecipe.Output<FluidOutput>> fluidOutputList = this.recipe.fluidOutputs();

            for (int i = 0; i < fluidOutputList.size(); i++) {
                Object2DoubleMap<FluidStack> weights = new Object2DoubleOpenHashMap<>();
                fluidOutputList.get(i).pool().forEachWithSelf((pool, output) -> weights.put(output.getFluidStack(), pool.getAdjustedWeight(output.getWeight())));
                fluidWeights.put("fluid_output_"+i, weights);
            }
        }

        public Double getItemWeight(String slot, ItemStack displayedStack) {
            Map<ItemStack, Double> weightMap = itemWeights.get(slot);
            if (weightMap == null) return null;
            for (Map.Entry<ItemStack, Double> entry : weightMap.entrySet()) {
                if (ItemStack.isSameItemSameTags(entry.getKey(), displayedStack)) return entry.getValue();
            }
            return null;
        }

        public Double getFluidWeight(String slot, FluidStack displayedStack) {
            Map<FluidStack, Double> weightMap = fluidWeights.get(slot);
            if (weightMap == null) return null;
            for (Map.Entry<FluidStack, Double> entry : weightMap.entrySet()) {
                if (entry.getKey().isFluidEqual(displayedStack)) return entry.getValue();
            }
            return null;
        }

        public CentrifugeRecipe getRecipe() {
            return recipe;
        }
    }
}
