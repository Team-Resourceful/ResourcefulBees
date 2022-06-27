package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FlowersCategory extends BaseCategory<FlowersCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/flowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "bee_pollination_flowers");
    public static final RecipeType<FlowersCategory.Recipe> RECIPE = new RecipeType<>(ID, FlowersCategory.Recipe.class);

    public FlowersCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE,
            TranslationConstants.Jei.FLOWERS,
            guiHelper.drawableBuilder(GUI_BACK, 0, 0, 100, 75).addPadding(0, 0, 0, 0).build(),
            guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.GOLD_FLOWER.get())));
    }

    private static ItemStack getErrorItem(Block block){
        return new ItemStack(Items.BARRIER).setHoverName(Component.translatable(block.getDescriptionId()));
    }

    public static List<Recipe> getFlowersRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            if (beeData.coreData().blockFlowers().size() > 0) {
                Set<ItemStack> stacks = new HashSet<>();
                Set<FluidStack> fluids = new HashSet<>();

                beeData.coreData().blockFlowers().forEach(block -> {
                    if (block.isBound()) {
                        if (block.value() instanceof LiquidBlock liquidBlock) {
                            fluids.add(new FluidStack(liquidBlock.getFluid().getSource(), 1000));
                        } else if (block.value().asItem() != Items.AIR) {
                            stacks.add(block.value().asItem().getDefaultInstance());
                        } else {
                            stacks.add(getErrorItem(block.value()));
                        }
                    }
                });

                if (!stacks.isEmpty()){
                    recipes.add(Recipe.getItemRecipe(beeData, stacks));
                }else if (!fluids.isEmpty()){
                    recipes.add(Recipe.getFluidRecipe(beeData, fluids));
                }

            } else if (beeData.coreData().isEntityPresent()){
                recipes.add(Recipe.getEntityRecipe(beeData, beeData.coreData().entityFlower()));
            }
        }));
        return recipes;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 41, 10)
                .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeData.getEntityType(), 45.0f))
                .setSlotName("bee");

        IRecipeSlotBuilder flower = builder
                .addSlot(RecipeIngredientRole.INPUT, 41, 55)
                .setSlotName("flower");

        recipe.getFluidStacks().ifPresent(stacks -> flower.addIngredients(ForgeTypes.FLUID_STACK, new ArrayList<>(stacks)));
        recipe.getItemStacks().ifPresent(stacks -> flower.addIngredients(VanillaTypes.ITEM_STACK, new ArrayList<>(stacks)));
        recipe.getEntityType().ifPresent(entity -> flower.addIngredients(JEICompat.ENTITY_INGREDIENT, new ArrayList<>(entity)));
    }

    static class Recipe {
        private Set<FluidStack> fluids;
        private Set<ItemStack> items;
        private Set<EntityIngredient> entityType;
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

        public static Recipe getEntityRecipe(CustomBeeData beeData, HolderSet<EntityType<?>> entityType){
            Recipe recipe = new Recipe(beeData);
            recipe.entityType = entityType.stream().filter(Holder::isBound).map(Holder::value).map(e -> new EntityIngredient(e, 45f)).collect(Collectors.toSet());
            return recipe;
        }

        public Optional<Set<FluidStack>> getFluidStacks() {
            return Optional.ofNullable(fluids);
        }

        public Optional<Set<ItemStack>> getItemStacks() {
            return Optional.ofNullable(items);
        }

        public Optional<Set<EntityIngredient>> getEntityType() {
            return Optional.ofNullable(entityType);
        }
    }
}
