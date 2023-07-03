package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FlowersCategory extends BaseCategory<FlowersCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/jei/flowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "bee_pollination_flowers");
    public static final RecipeType<Recipe> RECIPE = new RecipeType<>(ID, Recipe.class);

    public FlowersCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE,
            JeiTranslations.FLOWERS,
            guiHelper.drawableBuilder(GUI_BACK, 0, 0, 100, 75).addPadding(0, 0, 0, 0).build(),
            guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.GOLD_FLOWER.get())));
    }

    private static ItemStack getErrorItem(Block block){
        return new ItemStack(Items.BARRIER).setHoverName(Component.translatable(block.getDescriptionId()));
    }

    public static <T> List<Recipe> getFlowersRecipes(IPlatformFluidHelper<T> fluidHelper) {
        List<Recipe> recipes = new ArrayList<>();
        BeeRegistry.get().getBees().forEach((s, beeData) -> {
            BeeCoreData coreData = beeData.getCoreData();
            if (coreData.hasFlowers()) {
                Set<ItemStack> stacks = new HashSet<>();
                Set<Fluid> fluids = new HashSet<>();

                coreData
                    .flowers()
                    .stream()
                    .filter(Holder::isBound)
                    .map(Holder::value)
                    .forEach(value -> {
                        if (value instanceof LiquidBlock liquid) {
                            fluids.add(ModUtils.getFluid(liquid));
                        } else {
                            Item item = value.asItem();
                            stacks.add(item != Items.AIR ? new ItemStack(value.asItem()) : getErrorItem(value));
                        }
                    });

                if (!stacks.isEmpty()){
                    recipes.add(Recipe.getItemRecipe(beeData, stacks));
                }else if (!fluids.isEmpty()){
                    recipes.add(Recipe.getFluidRecipe(fluidHelper, beeData, fluids));
                }

            } else if (coreData.hasEntityFlower()){
                recipes.add(Recipe.getEntityRecipe(beeData, coreData.entityFlowers()));
            }
        });
        return recipes;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 41, 10)
                .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeData.entityType(), 45.0f))
                .setSlotName("bee");

        recipe.builder.accept(builder.addSlot(RecipeIngredientRole.INPUT, 41, 55).setSlotName("flower"));
    }

    record Recipe(CustomBeeData beeData, Consumer<IRecipeSlotBuilder> builder) {

        private static <T> Recipe getFluidRecipe(IPlatformFluidHelper<T> fluidHelper, CustomBeeData data, Set<Fluid> fluids){
            List<T> fluidStacks = new ArrayList<>();
            fluids.forEach(fluid -> fluidStacks.add(fluidHelper.create(fluid, fluidHelper.bucketVolume())));
            return new Recipe(data, slot -> slot.addIngredients(fluidHelper.getFluidIngredientType(), fluidStacks));
        }

        public static Recipe getItemRecipe(CustomBeeData beeData, Set<ItemStack> items){
            return new Recipe(beeData, slot -> slot.addIngredients(VanillaTypes.ITEM_STACK, new ArrayList<>(items)));
        }

        public static Recipe getEntityRecipe(CustomBeeData beeData, HolderSet<EntityType<?>> entityType){
            var entityTypes = entityType.stream().filter(Holder::isBound).map(Holder::value).map(e -> new EntityIngredient(e, 45f)).collect(Collectors.toSet());
            return new Recipe(beeData, slot -> slot.addIngredients(JEICompat.ENTITY_INGREDIENT, new ArrayList<>(entityTypes)));
        }
    }
}
