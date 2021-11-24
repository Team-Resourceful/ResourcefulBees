package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCategory<T> implements IRecipeCategory<T> {

    protected static final BeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private static final ResourceLocation ICONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");

    private final ResourceLocation categoryId;
    private final String localizedName;
    private final IDrawable background;
    private final IDrawable icon;
    private final Class<? extends T> recipeClass;

    public final IDrawable slot;
    public final IDrawable info;
    public final IDrawable beeHive;
    public final IDrawable outputSlot;

    protected BaseCategory(IGuiHelper guiHelper, ResourceLocation categoryId, String localizedName, IDrawable background, IDrawable icon, Class<? extends T> recipeClass) {
        this.categoryId = categoryId;
        this.localizedName = localizedName;
        this.background = background;
        this.icon = icon;
        this.recipeClass = recipeClass;

        this.beeHive = guiHelper.createDrawableIngredient(new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get()));
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.outputSlot = guiHelper.createDrawable(ICONS, 25, 0, 26, 26);
        this.slot = guiHelper.getSlotDrawable();
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
}
