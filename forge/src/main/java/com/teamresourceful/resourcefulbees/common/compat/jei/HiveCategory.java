package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HiveCategory extends BaseCategory<HiveCategory.Recipe> {

    public static final ResourceLocation HIVE_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/honeycomb.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "hive");
    public static final RecipeType<Recipe> RECIPE = new RecipeType<>(ID, Recipe.class);

    private final IDrawable hiveBackground;
    private final IDrawable apiaryBackground;

    public HiveCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE,
                TranslationConstants.Jei.HIVE,
                guiHelper.createBlankDrawable(160, 26),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get())));

        hiveBackground = guiHelper.drawableBuilder(HIVE_BACK, 0, 0, 160, 26).addPadding(0, 0, 0, 0).build();
        apiaryBackground = guiHelper.drawableBuilder(HIVE_BACK, 0, 26, 160, 26).addPadding(0, 0, 0, 0).build();
    }

    public static List<Recipe> getHoneycombRecipes(Collection<HiveRecipe> recipes) {
        return recipes.stream().flatMap(HiveCategory::createRecipes).collect(Collectors.toList());
    }

    private static Stream<Recipe> createRecipes(HiveRecipe recipe){
        List<Recipe> recipes = new ArrayList<>();

        for (BeehiveTier tier : BeehiveTier.values()) {
            ItemStack stack = recipe.getHiveOutput(tier);
            if (stack.isEmpty()) continue;
            List<ItemStack> hives = tier.getDisplayItems().stream().map(ItemStack::new).toList();
            recipe.bees().stream()
                .filter(Holder::isBound)
                .map(Holder::get)
                .map(entity -> new Recipe(stack, hives, entity, false))
                .forEach(recipes::add);
        }

        for (ApiaryTier tier : ApiaryTier.values()) {
            ItemStack stack = recipe.getApiaryOutput(tier);
            if (stack.isEmpty()) continue;
            recipe.bees().stream()
                .filter(Holder::isBound)
                .map(Holder::get)
                .map(entity -> new Recipe(stack, tier.getItem().getDefaultInstance(), entity, true))
                .forEach(recipes::add);
        }
        return recipes.stream();
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 5)
                .addIngredient(VanillaTypes.ITEM_STACK, recipe.comb)
                .setSlotName("comb");
        builder.addSlot(RecipeIngredientRole.INPUT, 63, 5)
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.hives)
                .setSlotName("hive");
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 3)
                .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.entityType, 45.0f))
                .setSlotName("bee");
    }

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull IRecipeSlotsView view, @NotNull PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, view, stack, mouseX, mouseY);
        if (recipe.isApiary) this.apiaryBackground.draw(stack);
        else this.hiveBackground.draw(stack);
    }

    public record Recipe(ItemStack comb, List<ItemStack> hives, EntityType<?> entityType, boolean isApiary) {

        public Recipe(ItemStack comb, ItemStack hive, EntityType<?> entityType, boolean isApiary) {
            this(comb,Collections.singletonList(hive), entityType, isApiary);
        }
    }

}
