package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.ChildOutput;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.ParentInput;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class BeeBreedingCategory extends BaseCategory<BeeBreedingCategory.BreedingWrapper> {

    private static final int WIDTH = 150;
    private static final int HEIGHT = 118;

    private static final Identifier GUI = ModIdentifier.of("textures/gui/jei/breeding.png");

    public static final Identifier ID = ModIdentifier.of("breeding");

    public static final IRecipeType<BreedingWrapper> RECIPE = IRecipeType.create(ID, BreedingWrapper.class);

    private final IDrawable background;

    public BeeBreedingCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE, JeiTranslations.BREEDING, guiHelper.createDrawableItemLike(ModItems.BREEDER_ITEM.get()), WIDTH, HEIGHT);

        this.background = guiHelper.createDrawable(GUI, 0, 0, WIDTH, HEIGHT);
    }

    public static List<BreedingWrapper> getRecipes(Collection<BreederRecipe> recipes) {
        return recipes.stream().flatMap(recipe -> recipe.outputs().stream().map(output -> new BreedingWrapper(recipe, output))).toList();
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull BreedingWrapper wrapper, @NotNull IFocusGroup focuses) {
        BreederRecipe recipe = wrapper.recipe();
        addParent(builder, recipe.parent1(), 1, 11, 1, 29, 22, 20, "parent_1");
        addParent(builder, recipe.parent2(), 1, 83, 1, 101, 22, 92, "parent_2");

        recipe.optionalIngredient().ifPresent(optional ->
                builder.addInputSlot(64, 56)
                        .add(optional)
                        .setSlotName("optional_input")
        );

        ChildOutput output = wrapper.output();

        output.displayEntity()
                .flatMap(id -> BeeRegistry.get().getOptionalBeeData(id))
                .ifPresent(beeData ->
                        builder.addOutputSlot(124, 33)
                                .add(JEICompat.ENTITY_INGREDIENT, EntityIngredient.of(beeData.entityType()))
                                .setSlotName("output_entity")
                );

        builder.addOutputSlot(124, 56)
                .add(output.child())
                .setSlotName("output_item");
    }

    private static void addParent(IRecipeLayoutBuilder builder, ParentInput parent, int parentX, int parentY, int feedX, int feedY, int displayX, int displayY, String name) {
        parent.displayEntity().flatMap(entityId -> BeeRegistry.get().getOptionalBeeData(entityId)).ifPresent(beeData -> {
            EntityType<?> entityType = beeData.entityType();

            int jarColor = beeData
                    .getRenderData()
                    .colorData()
                    .jarColor()
                    .getOpaqueValue();

            builder.addInputSlot(parentX, parentY)
                    .add(BeeJarItem.createFilledJar(entityType, jarColor))
                    .setSlotName(name + "_ingredient");

            builder.addInputSlot(displayX, displayY)
                    .add(JEICompat.ENTITY_INGREDIENT, EntityIngredient.of(entityType))
                    .setSlotName(name + "_entity");
        });

        builder.addInputSlot(feedX, feedY)
                .add(parent.feedItems())
                .setSlotName(name + "_feed");
    }

    private static Optional<EntityType<?>> getEntityType(Identifier id) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(id);
    }

    @Override
    public void draw(@NotNull BreedingWrapper recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
    }

    @Override
    public @Nullable Identifier getIdentifier(@NonNull BreedingWrapper recipe) {
        return null;
    }

    public record BreedingWrapper(BreederRecipe recipe, ChildOutput output) {
    }
}