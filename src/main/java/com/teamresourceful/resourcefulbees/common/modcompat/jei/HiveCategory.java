package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultHiveTypes;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class HiveCategory extends BaseCategory<HiveCategory.Recipe> {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 26;

    private static final Identifier HIVE_BACKGROUND = ModIdentifier.of("textures/gui/jei/honeycomb.png");

    public static final Identifier ID = ModIdentifier.of("hive");

    public static final IRecipeType<Recipe> RECIPE = IRecipeType.create(ID, Recipe.class);

    private final IDrawable hiveBackground;
    private final IDrawable apiaryBackground;

    public HiveCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE, JeiTranslations.HIVE, guiHelper.createDrawableItemLike(DefaultHiveTypes.OAK.tierOneNest()), WIDTH, HEIGHT);
        this.hiveBackground = guiHelper.drawableBuilder(HIVE_BACKGROUND, 0, 0, WIDTH, HEIGHT).build();
        this.apiaryBackground = guiHelper.drawableBuilder(HIVE_BACKGROUND, 0, HEIGHT, WIDTH, HEIGHT).build();
    }

    public static List<Recipe> getHoneycombRecipes(Collection<HiveRecipe> recipes) {
        return recipes.stream()
                .flatMap(HiveCategory::createRecipes)
                .toList();
    }

    private static Stream<Recipe> createRecipes(HiveRecipe recipe) {
        List<Recipe> recipes = new ArrayList<>();

        for (BeehiveTier tier : BeehiveTier.values()) {
            ItemStack output = recipe.getHiveOutput(tier);

            if (output.isEmpty()) {
                continue;
            }

            List<ItemStack> hives = tier.getDisplayItems()
                    .stream()
                    .map(ItemStack::new)
                    .toList();

            recipe.bees()
                    .stream()
                    .filter(Holder::isBound)
                    .map(Holder::value)
                    .map(entityType -> new Recipe(output, hives, entityType, false))
                    .forEach(recipes::add);
        }

        for (ApiaryTier tier : ApiaryTier.values()) {
            ItemStack output = recipe.getApiaryOutput(tier);

            if (output.isEmpty()) {
                continue;
            }

            ItemStack apiary = new ItemStack(
                    tier.getItem()
            );

            recipe.bees()
                    .stream()
                    .filter(Holder::isBound)
                    .map(Holder::value)
                    .map(entityType -> new Recipe(output, apiary, entityType, true))
                    .forEach(recipes::add);
        }

        return recipes.stream();
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 5)
                .add(VanillaTypes.ITEM_STACK, recipe.comb())
                .setSlotName("comb");

        builder.addSlot(RecipeIngredientRole.INPUT, 63, 5)
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.hives())
                .setSlotName("hive");

        builder.addSlot(RecipeIngredientRole.INPUT, 11, 3)
                .add(JEICompat.ENTITY_INGREDIENT, EntityIngredient.of(recipe.entityType))
                .setSlotName("bee");
    }

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        if (recipe.isApiary()) {
            apiaryBackground.draw(graphics, 0, 0);
        } else {
            hiveBackground.draw(graphics, 0, 0);
        }
    }

    @Override
    public @Nullable Identifier getIdentifier(@NonNull Recipe recipe) {
        return null;
    }

    public record Recipe(
            ItemStack comb,
            List<ItemStack> hives,
            EntityType<?> entityType,
            boolean isApiary
    ) {

        public Recipe(ItemStack comb, ItemStack hive, EntityType<?> entityType, boolean isApiary) {
            this(comb, Collections.singletonList(hive), entityType, isApiary);
        }
    }
}