package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeeHiveCategory implements IRecipeCategory<BeeHiveCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beehive.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "hive");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public BeeHiveCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 46).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.IRON_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.hive");
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends Recipe> getRecipeClass() {
        return Recipe.class;
    }

    @Override
    public String getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(Recipe recipe, IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getComb());
        ingredients.setInput(VanillaTypes.ITEM, new ItemStack(RegistryHandler.IRON_BEEHIVE_ITEM.get()));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, Recipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(0, false, 138, 4);
        itemStacks.init(1, true, 62, 4);
        itemStacks.set(ingredients);
    }

    @Override
    public void draw(Recipe recipe, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.fontRenderer.drawString("Honeycombs depend on the", 8, 28,0x000000);
        minecraft.fontRenderer.drawString("bee type that go in the hive!", 8, 36,0x000000);
    }

    public static List<Recipe> getHoneycombRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeInfo> beeType : BeeInfo.BEE_INFO.entrySet()){
            if (beeType.getKey() == "Default")
                continue;
            else {
                ItemStack honeyCombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
                final CompoundNBT honeyCombItemStackTag = honeyCombItemStack.getOrCreateChildTag(BeeConst.NBT_ROOT);
                honeyCombItemStackTag.putString(BeeConst.NBT_COLOR, beeType.getValue().getColor());
                honeyCombItemStackTag.putString(BeeConst.NBT_BEE_TYPE, beeType.getKey());
                recipes.add(new Recipe(honeyCombItemStack));
            }
        }
        return recipes;
    }

    public static class Recipe {
        private final ItemStack comb;

        public Recipe(ItemStack comb) {
            this.comb = comb;
        }

        public ItemStack getComb() {
            return this.comb;
        }
    }
}