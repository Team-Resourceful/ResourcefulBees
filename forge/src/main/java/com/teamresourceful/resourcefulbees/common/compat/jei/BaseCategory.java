package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCategory<T> implements IRecipeCategory<T> {

    private static final ResourceLocation ICONS = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/jei/icons.png");

    private final RecipeType<T> recipeType;
    private final Component localizedName;
    private final IDrawable background;
    private final IDrawable icon;

    public final IDrawable slot;
    public final IDrawable info;
    public final IDrawable beeHive;
    public final IDrawable outputSlot;

    protected BaseCategory(IGuiHelper guiHelper, RecipeType<T> recipeType, Component localizedName, IDrawable background, IDrawable icon) {
        this.recipeType = recipeType;
        this.localizedName = localizedName;
        this.background = background;
        this.icon = icon;

        this.beeHive = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get()));
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.outputSlot = guiHelper.createDrawable(ICONS, 25, 0, 26, 26);
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType<T> getRecipeType() {
        return this.recipeType;
    }

    @Override
    public @NotNull Component getTitle() {
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
}
