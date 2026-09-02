package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultHiveTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class BaseCategory<T> extends AbstractRecipeCategory<T> {

    private static final Identifier ICONS = ModIdentifier.of("textures/gui/jei/icons.png");

    public final IDrawable slot;
    public final IDrawable info;
    public final IDrawable beeHive;
    public final IDrawable outputSlot;

    protected BaseCategory(IGuiHelper guiHelper, IRecipeType<T> recipeType, Component title, IDrawable icon, int width, int height) {
        super(recipeType, title, icon, width, height);
        this.beeHive = guiHelper.createDrawableItemLike(DefaultHiveTypes.OAK.tierOneNest());
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.outputSlot = guiHelper.createDrawable(ICONS, 25, 0, 26, 26);
        this.slot = guiHelper.getSlotDrawable();
    }
}