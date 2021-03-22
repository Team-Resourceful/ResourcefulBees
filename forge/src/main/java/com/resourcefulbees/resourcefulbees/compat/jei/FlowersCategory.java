package com.resourcefulbees.resourcefulbees.compat.jei;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FlowersCategory extends BaseCategory<FlowersCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beeflowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "bee_pollination_flowers");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    public FlowersCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
            I18n.get("gui.resourcefulbees.jei.category.bee_pollination_flowers"),
            guiHelper.drawableBuilder(GUI_BACK, 0, 0, 24, 75).addPadding(0, 0, 0, 0).build(),
            guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.GOLD_FLOWER.get())),
            FlowersCategory.Recipe.class);
    }

    public static List<Recipe> getFlowersRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            if (beeData.hasBlockFlowers()) {
                if (beeData.getBlockFlowers().stream().allMatch(FlowingFluidBlock.class::isInstance)){
                    Set<Fluid> fluids = beeData.getBlockFlowers().stream().map(b -> ((FlowingFluidBlock) b).getFluid().getSource()).collect(Collectors.toSet());
                    recipes.add(new Recipe(fluids, null, beeData.getName()));
                }else {
                    List<ItemStack> items = beeData.getBlockFlowers().stream()
                            .filter(b -> b.asItem() != Items.AIR)
                            .map(b ->new ItemStack(b.asItem())).collect(Collectors.toList());
                    recipes.add(new Recipe(null, items, beeData.getName()));
                }
            }
        }));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull Recipe recipe, @Nonnull @NotNull IIngredients ingredients) {
        if (recipe.flowerItems != null){
            List<List<ItemStack>> items = new ArrayList<>();
            items.add(recipe.flowerItems);
            ingredients.setInputLists(VanillaTypes.ITEM, items);
        }else if (recipe.flowerFluids != null) {
            List<FluidStack> fluids = new ArrayList<>();
            for (Fluid element: recipe.flowerFluids ) {
                FluidStack fluid = new FluidStack(element, 1000);
                fluids.add(fluid);
            }
            List<List<FluidStack>> fluidFluids = new ArrayList<>();
            fluidFluids.add(fluids);
            ingredients.setInputLists(VanillaTypes.FLUID, fluidFluids);
        }
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public void setRecipe(@Nonnull @NotNull IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
        if (recipe.flowerFluids != null){
            IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
            fluidStacks.init(1,true,4,58);
            fluidStacks.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        } else {
            IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
            itemStacks.init(1, true, 3, 57);
            itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        }

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 4, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    public static class Recipe {
        private final Set<Fluid> flowerFluids;
        private final List<ItemStack> flowerItems;

        private final String beeType;

        public Recipe(@Nullable Set<Fluid> flowerFluids, @Nullable List<ItemStack> flowerItems, String beeType){
            this.flowerFluids = flowerFluids;
            this.flowerItems = flowerItems;
            this.beeType = beeType;
        }


        public String getBeeType() { return this.beeType; }
    }
}
