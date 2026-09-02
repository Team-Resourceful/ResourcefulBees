package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.shared.RegistryPredicate;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.util.ModUtils;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FlowersCategory extends BaseCategory<FlowersCategory.Recipe> {

    private static final int WIDTH = 100;
    private static final int HEIGHT = 75;

    public static final Identifier GUI_BACK = ModIdentifier.of("textures/gui/jei/flowers.png");
    public static final Identifier ID = ModIdentifier.of("bee_pollination_flowers");
    public static final IRecipeType<Recipe> RECIPE = IRecipeType.create(ID, Recipe.class);

    private final IDrawable back;

    public FlowersCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE, JeiTranslations.FLOWERS, guiHelper.createDrawableItemLike(ModBlocks.GOLD_FLOWER.get()), WIDTH, HEIGHT);
        this.back = guiHelper.createDrawable(GUI_BACK, 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public void draw(@NonNull Recipe recipe, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.back.draw(guiGraphics);
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    private static ItemStack getErrorItem(Block block) {
        ItemStack stack = new ItemStack(Items.BARRIER);
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable(block.getDescriptionId()));
        return stack;
    }

    private static <T> Stream<Holder<T>> resolve(RegistryPredicate<T> predicate, Registry<T> registry) {
        return predicate.unwrap().map(tag -> StreamSupport.stream(registry.getTagOrEmpty(tag).spliterator(), false), Set::stream);
    }

    public static <T> List<Recipe> getFlowersRecipes(IPlatformFluidHelper<T> fluidHelper) {
        List<Recipe> recipes = new ArrayList<>();

        BeeRegistry.get()
                .getBees()
                .forEach((id, beeData) -> {
                    BeeCoreData coreData = beeData.getCoreData();

                    if (coreData.hasBlockFlowers()) {
                        Set<ItemStack> stacks = new HashSet<>();
                        Set<Fluid> fluids = new HashSet<>();

                        resolve(coreData.blockFlowers(), BuiltInRegistries.BLOCK)
                                .map(Holder::value)
                                .forEach(block -> {
                                    if (block instanceof LiquidBlock liquid) {
                                        fluids.add(ModUtils.getFluid(liquid));
                                    } else {
                                        Item item = block.asItem();
                                        stacks.add(item != Items.AIR ? new ItemStack(item) : getErrorItem(block));
                                    }
                                });

                        if (!stacks.isEmpty()) {
                            recipes.add(Recipe.getItemRecipe(beeData, stacks));
                        }

                        if (!fluids.isEmpty()) {
                            recipes.add(Recipe.getFluidRecipe(fluidHelper, beeData, fluids));
                        }
                    } else if (coreData.hasEntityFlower()) {
                        recipes.add(Recipe.getEntityRecipe(beeData, coreData.entityFlowers()));
                    }
                });

        return recipes;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 41, 10)
                .add(JEICompat.ENTITY_INGREDIENT, EntityIngredient.of(recipe.beeData.entityType()))
                .setSlotName("bee");

        IRecipeSlotBuilder flowerSlot =
                builder.addSlot(RecipeIngredientRole.INPUT, 41, 55)
                        .setSlotName("flower");

        recipe.builder().accept(flowerSlot);
    }

    @Override
    public @Nullable Identifier getIdentifier(@NonNull Recipe recipe) {
        return null;
    }

    public record Recipe(CustomBeeData beeData, Consumer<IRecipeSlotBuilder> builder) {

        private static <T> Recipe getFluidRecipe(IPlatformFluidHelper<T> fluidHelper, CustomBeeData beeData, Set<Fluid> fluids) {
            List<T> fluidStacks = fluids.stream()
                    .map(fluid -> fluidHelper.create(BuiltInRegistries.FLUID.wrapAsHolder(fluid), fluidHelper.bucketVolume()))
                    .toList();

            return new Recipe(beeData, slot -> slot.addIngredients(fluidHelper.getFluidIngredientType(), fluidStacks));
        }

        public static Recipe getItemRecipe(CustomBeeData beeData, Set<ItemStack> items) {
            return new Recipe(beeData, slot -> slot.addIngredients(VanillaTypes.ITEM_STACK, List.copyOf(items)));
        }

        public static Recipe getEntityRecipe(CustomBeeData beeData, RegistryPredicate<EntityType<?>> entityTypes) {
            List<EntityIngredient> ingredients = resolve(entityTypes, BuiltInRegistries.ENTITY_TYPE)
                    .filter(Holder::isBound)
                    .map(Holder::value)
                    .map(EntityIngredient::of)
                    .toList();

            return new Recipe(beeData, slot -> slot.addIngredients(JEICompat.ENTITY_INGREDIENT, ingredients));
        }
    }
}