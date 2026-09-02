package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.recipes.FlowHiveRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class FlowHiveCategory extends BaseCategory<FlowHiveCategory.Recipe> {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 26;

    private static final Identifier HIVE_BACKGROUND = ModIdentifier.of("textures/gui/jei/honeycomb.png");
    public static final Identifier ID = ModIdentifier.of("flow_hive");
    public static final IRecipeType<Recipe> RECIPE = IRecipeType.create(ID, Recipe.class);

    private final IPlatformFluidHelper<FluidStack> fluidHelper;
    private final IDrawable flowHiveBackground;

    public FlowHiveCategory(IGuiHelper guiHelper, IPlatformFluidHelper<FluidStack> fluidHelper) {
        super(guiHelper, RECIPE, JeiTranslations.FLOW_HIVE_OUTPUTS, guiHelper.createDrawableItemLike(ModItems.FLOW_HIVE.get()), WIDTH, HEIGHT);
        this.fluidHelper = fluidHelper;
        this.flowHiveBackground = guiHelper.drawableBuilder(HIVE_BACKGROUND, 0, 52, WIDTH, HEIGHT).build();
    }

    public static List<Recipe> getHoneycombRecipes(Collection<FlowHiveRecipe> recipes) {
        return recipes.stream()
                .flatMap(FlowHiveCategory::createRecipes)
                .toList();
    }

    private static Stream<Recipe> createRecipes(FlowHiveRecipe recipe) {
        FluidStackTemplate honey = recipe.fluid();

        return recipe.bees()
                .stream()
                .filter(Holder::isBound)
                .map(Holder::value)
                .map(entityType -> new Recipe(honey, entityType));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses) {
        addFluidOutput(builder, recipe.fluid());

        builder.addSlot(RecipeIngredientRole.INPUT, 63, 5)
                .add(VanillaTypes.ITEM_STACK, ModItems.FLOW_HIVE.get().getDefaultInstance())
                .setSlotName("hive");

        builder.addInputSlot(11, 3)
                .add(JEICompat.ENTITY_INGREDIENT, EntityIngredient.of(recipe.bee))
                .setSlotName("bee");
    }

    private void addFluidOutput(IRecipeLayoutBuilder builder, FluidStackTemplate fluid) {
        var fluidStack = fluid.create();

        builder.addOutputSlot(139, 5)
                .add(fluidHelper.getFluidIngredientType(), fluidStack)
                .setFluidRenderer(fluid.amount(), false, 16, 16)
                .setSlotName("honey");
    }

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        flowHiveBackground.draw(graphics, 0, 0);
    }

    @Override
    public @Nullable Identifier getIdentifier(@NonNull Recipe recipe) {
        return null;
    }

    public record Recipe(FluidStackTemplate fluid, EntityType<?> bee) {}
}