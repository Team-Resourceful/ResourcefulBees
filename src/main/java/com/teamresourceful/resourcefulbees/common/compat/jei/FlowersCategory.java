package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FlowersCategory extends BaseCategory<FlowersCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beeentityflowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "bee_pollination_flowers");

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
                Set<FluidStack> fluids = new HashSet<>();

                beeData.getCoreData().getBlockFlowers().forEach(block -> {
                    if (block instanceof FlowingFluidBlock){
                        fluids.add(new FluidStack(((FlowingFluidBlock) block).getFluid().getSource(), 1000 ));
                    }else if (block.asItem() != Items.AIR){
                        stacks.add(block.asItem().getDefaultInstance());
                    }else {
                        stacks.add(getErrorItem(block));
                    }
                });

                if (!stacks.isEmpty()){
                    recipes.add(Recipe.getItemRecipe(beeData, stacks));
                }else if (!fluids.isEmpty()){
                    recipes.add(Recipe.getFluidRecipe(beeData, fluids));
                }

            } else if (beeData.getCoreData().getEntityFlower().isPresent()){
                recipes.add(Recipe.getEntityRecipe(beeData, beeData.getCoreData().getEntityFlower().get()));
            }
        }));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeData.getEntityType(), 45.0f));
        recipe.getItemStacks().ifPresent(items -> ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(new ArrayList<>(items))));
        recipe.getFluidStacks().ifPresent(fluids -> ingredients.setInputLists(VanillaTypes.FLUID, Collections.singletonList(new ArrayList<>(fluids))));
        recipe.getEntityType().ifPresent(entity -> ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(entity, 45.0f)));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        IGuiIngredientGroup<EntityIngredient> entityIngredients = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        entityIngredients.init(0, true, 41, 10);
        entityIngredients.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));

        recipe.getFluidStacks().ifPresent(f -> {
            IGuiFluidStackGroup fluidIngredients = iRecipeLayout.getFluidStacks();
            fluidIngredients.init(1,true,41,55);
            fluidIngredients.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        });

        recipe.getItemStacks().ifPresent(i -> {
            IGuiItemStackGroup itemIngredients = iRecipeLayout.getItemStacks();
            itemIngredients.init(1, true, 41, 55);
            itemIngredients.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        });

        recipe.getEntityType().ifPresent(e -> {
            entityIngredients.init(1, true, 42, 56);
            entityIngredients.set(1, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(1));
        });
    }

    static class Recipe {
        private Set<FluidStack> fluids;
        private Set<ItemStack> items;
        private EntityType<?> entityType;
        private final CustomBeeData beeData;

        public Recipe(CustomBeeData beeData){
            this.beeData = beeData;
        }

        public static Recipe getFluidRecipe(CustomBeeData beeData, Set<FluidStack> fluids){
            Recipe recipe = new Recipe(beeData);
            recipe.fluids = fluids;
            return recipe;
        }

        public static Recipe getItemRecipe(CustomBeeData beeData, Set<ItemStack> items){
            Recipe recipe = new Recipe(beeData);
            recipe.items = items;
            return recipe;
        }

        public static Recipe getEntityRecipe(CustomBeeData beeData, EntityType<?> entityType){
            Recipe recipe = new Recipe(beeData);
            recipe.entityType = entityType;
            return recipe;
        }

        public Optional<Set<FluidStack>> getFluidStacks() {
            return Optional.ofNullable(fluids);
        }

        public Optional<Set<ItemStack>> getItemStacks() {
            return Optional.ofNullable(items);
        }

        public Optional<EntityType<?>> getEntityType() {
            return Optional.ofNullable(entityType);
        }
    }
}
