package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FlowersCategory extends BaseCategory<FlowersCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/flowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "bee_pollination_flowers");

    public FlowersCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
            TranslationConstants.Jei.FLOWERS,
            guiHelper.drawableBuilder(GUI_BACK, 0, 0, 100, 75).addPadding(0, 0, 0, 0).build(),
            guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.GOLD_FLOWER.get())),
            FlowersCategory.Recipe.class);
    }

    private static ItemStack getErrorItem(Block block){
        return new ItemStack(Items.BARRIER).setHoverName(new TextComponent(block.getRegistryName() != null ? block.getRegistryName().toString() : "Unknown Block ID"));
    }

    public static List<Recipe> getFlowersRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            if (beeData.getCoreData().getBlockFlowers().size() > 0) {
                Set<ItemStack> stacks = new HashSet<>();
                Set<FluidStack> fluids = new HashSet<>();

                beeData.getCoreData().getBlockFlowers().forEach(block -> {
                    if (block instanceof LiquidBlock liquidBlock){
                        fluids.add(new FluidStack(liquidBlock.getFluid().getSource(), 1000 ));
                    }else if (block.value().asItem() != Items.AIR){
                        stacks.add(block.value().asItem().getDefaultInstance());
                    }else {
                        stacks.add(getErrorItem(block.value()));
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
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);

        builder.addSlot(RecipeIngredientRole.INPUT, 41, 10)
                .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeData.getEntityType(), 45.0f))
                .setSlotName("bee");

        IRecipeSlotBuilder flower = builder.addSlot(RecipeIngredientRole.INPUT, 41, 55).setSlotName("flower");

        recipe.getFluidStacks().ifPresent(stacks -> flower.addIngredients(VanillaTypes.FLUID, new ArrayList<>(stacks)));
        recipe.getItemStacks().ifPresent(stacks -> flower.addIngredients(VanillaTypes.ITEM, new ArrayList<>(stacks)));
        recipe.getEntityType().ifPresent(entity -> flower.addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(entity, 45.0f)));
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
