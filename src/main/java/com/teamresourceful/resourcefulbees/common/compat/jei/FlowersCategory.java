package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.ModBlocks;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FlowersCategory extends BaseCategory<FlowersCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beeentityflowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "bee_pollination_flowers");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();


    public FlowersCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
            I18n.get("gui.resourcefulbees.jei.category.bee_pollination_flowers"),
            guiHelper.drawableBuilder(GUI_BACK, 0, 0, 100, 75).addPadding(0, 0, 0, 0).build(),
            guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.GOLD_FLOWER.get())),
            FlowersCategory.Recipe.class);
    }

    private static ItemStack getErrorItem(Block block){
        return new ItemStack(Items.BARRIER).setHoverName(new StringTextComponent(block.getRegistryName() != null ? block.getRegistryName().toString() : "Unknown Block ID"));
    }

    public static List<Recipe> getFlowersRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            if (!beeData.getCoreData().getBlockFlowers().isEmpty()) {
                Set<ItemStack> stacks = new HashSet<>();
                Set<Fluid> fluids = new HashSet<>();

                for (Block block : beeData.getCoreData().getBlockFlowers()) {
                    if (block instanceof FlowingFluidBlock){
                        fluids.add(((FlowingFluidBlock) block).getFluid().getSource());
                    }else if (block.asItem() != Items.AIR){
                        stacks.add(block.asItem().getDefaultInstance());
                    }else {
                        stacks.add(getErrorItem(block));
                    }
                }

                if (!stacks.isEmpty()){
                    recipes.add(Recipe.createItemRecipe(beeData, stacks));
                }else if (!fluids.isEmpty()){
                    recipes.add(Recipe.createFluidRecipe(beeData, fluids));
                }

            } else if (beeData.getCoreData().getEntityFlower().isPresent()){
                EntityType<?> entity = beeData.getCoreData().getEntityFlower().get();
                recipes.add(Recipe.createEntityRecipe(beeData, entity));
            }
        }));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.flowerItems != null){
            List<List<ItemStack>> items = new ArrayList<>();
            items.add(new ArrayList<>(recipe.flowerItems));
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
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeData, -45.0f));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.flowerFluids != null){
            IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
            fluidStacks.init(1,true,41,55);
            fluidStacks.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        } else if (recipe.flowerItems != null) {
            IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
            itemStacks.init(1, true, 41, 55);
            itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        }

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 41, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull MatrixStack stack, double mouseX, double mouseY) {
        if (recipe.entity != null){
            RenderUtils.renderEntity(stack, recipe.entity, Minecraft.getInstance().level, 41, 55, -45, 1f);
        }
    }



    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(@NotNull Recipe recipe, double mouseX, double mouseY) {
        if (recipe.entity != null && mouseX >= 41 && mouseX <= 59 && mouseY >= 55 && mouseY <= 73){
            return Collections.singletonList(recipe.entity.getDisplayName());
        }
        return Collections.emptyList();
    }

    public static class Recipe {
        private final Set<Fluid> flowerFluids;
        private final Set<ItemStack> flowerItems;
        private final Entity entity;

        private final CustomBeeData beeData;

        public Recipe(CustomBeeData beeData, @Nullable Set<Fluid> flowerFluids, @Nullable Set<ItemStack> flowerItems, @Nullable EntityType<?> entityType){
            this.flowerFluids = flowerFluids;
            this.flowerItems = flowerItems;
            this.beeData = beeData;
            if (entityType != null) this.entity = entityType.create(Minecraft.getInstance().level);
            else entity = null;
        }

        public static Recipe createFluidRecipe(CustomBeeData beeType, Set<Fluid> flowerFluids){
            return new Recipe(beeType, flowerFluids, null, null);
        }

        public static Recipe createItemRecipe(CustomBeeData beeType, Set<ItemStack> flowerItems){
            return new Recipe(beeType, null, flowerItems, null);
        }

        public static Recipe createEntityRecipe(CustomBeeData beeType, EntityType<?> entityType){
            return new Recipe(beeType, null, null, entityType);
        }

        public CustomBeeData getBeeData() { return this.beeData; }
    }
}
