package com.resourcefulbees.resourcefulbees.compat.jei;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BaseCategory<T> implements IRecipeCategory<T> {

    public static final ResourceLocation ICONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");

    private final ResourceLocation categoryId;
    private final String localizedName;
    private final IDrawable background;
    private final IDrawable icon;
    private final Class<? extends T> recipeClass;

    public final IDrawable info;
    public final IDrawable beeHive;


    public BaseCategory(IGuiHelper guiHelper, ResourceLocation categoryId, String localizedName, IDrawable background, IDrawable icon, Class<? extends T> recipeClass) {
        this.categoryId = categoryId;
        this.localizedName = localizedName;
        this.background = background;
        this.icon = icon;
        this.recipeClass = recipeClass;

        this.beeHive = guiHelper.createDrawableIngredient(new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get()));
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
    }

    @Override
    public @NotNull ResourceLocation getUid() {
        return categoryId;
    }

    @Override
    public @NotNull Class<? extends T> getRecipeClass() {
        return recipeClass;
    }

    @Override
    public @NotNull String getTitle() {
        return localizedName;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(@NotNull T t, @NotNull IIngredients iIngredients) {
        //required for the implementation and needed for the children.
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull T t, @NotNull IIngredients iIngredients) {
        //required for the implementation and needed for the children.
    }
}
