package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.lib.RecipeTypes;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import com.dungeonderps.resourcefulbees.utils.BeeValidator;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dungeonderps.resourcefulbees.lib.BeeConstants.*;

public class FlowersCategory implements IRecipeCategory<FlowersCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beeflowers.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "bee_pollination_flowers");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public FlowersCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 24, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.GOLD_FLOWER.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.bee_pollination_flowers");
    }

    public static List<Recipe> getFlowersRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()){
            if (!bee.getValue().getName().equals(DEFAULT_BEE_TYPE)) {
                if (!bee.getValue().getFlower().isEmpty()) {
                    String flower = bee.getValue().getFlower();
                    if (BeeValidator.TAG_RESOURCE_PATTERN.matcher(flower).matches()) {
                        flower = flower.replace(BeeConstants.TAG_PREFIX, "");

                        Tag<Fluid> fluidTag = BeeInfoUtils.getFluidTag(flower);
                        Tag<Item> itemTag = BeeInfoUtils.getItemTag(flower);
                        Tag<Block> blockTag = BeeInfoUtils.getBlockTag(flower);
                        if (itemTag != null) {
                            recipes.add(new Recipe(itemTag, null, null, bee.getKey(), RecipeTypes.ITEM, true));
                        } else if (fluidTag != null) {
                            recipes.add(new Recipe(null, fluidTag, null, bee.getKey(), RecipeTypes.FLUID,  true));
                        } else if (blockTag != null) {
                            recipes.add(new Recipe(null, null, blockTag, bee.getKey(), RecipeTypes.BLOCK, true));
                        }
                    } else if (flower.equals(FLOWER_TAG_ALL)) {
                        Tag<Item> itemTag = ItemTags.FLOWERS;
                        if (itemTag != null)
                            recipes.add(new Recipe(itemTag, null,  null, bee.getKey(), RecipeTypes.ITEM, true));
                    } else if (flower.equals(FLOWER_TAG_SMALL)) {
                        Tag<Item> itemTag = ItemTags.SMALL_FLOWERS;
                        if (itemTag != null)
                            recipes.add(new Recipe(itemTag, null, null, bee.getKey(), RecipeTypes.ITEM, true));
                    } else if (flower.equals(FLOWER_TAG_TALL)) {
                        Tag<Item> itemTag = ItemTags.TALL_FLOWERS;
                        if (itemTag != null)
                            recipes.add(new Recipe(itemTag, null, null, bee.getKey(), RecipeTypes.ITEM, true));
                    } else {
                        Item itemIn = BeeInfoUtils.getItem(flower);
                        Fluid fluidIn = BeeInfoUtils.getFluid(flower);
                        if (BeeInfoUtils.isValidItem(itemIn))
                            recipes.add(new Recipe(new ItemStack(itemIn), null, bee.getKey(), RecipeTypes.ITEM, false));
                        else if (BeeInfoUtils.isValidFluid(fluidIn))
                            recipes.add(new Recipe(null, new FluidStack(fluidIn, 1000), bee.getKey(), RecipeTypes.FLUID, false));
                    }
                }
            }
        }
        return recipes;
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<? extends Recipe> getRecipeClass() {
        return Recipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return this.localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(Recipe recipe, @Nonnull IIngredients ingredients) {
        if (recipe.isAcceptsAny()) {
            if (recipe.recipeType == RecipeTypes.ITEM) {
                List<Ingredient> list = new ArrayList<>();
                list.add(Ingredient.fromTag(recipe.itemITag));
                ingredients.setInputIngredients(list);
            } else if (recipe.recipeType == RecipeTypes.FLUID) {
                List<FluidStack> fluids = new ArrayList<>();
                for (Fluid element: recipe.fluidITag.getAllElements() ) {
                    FluidStack fluid = new FluidStack(element, 1000);
                    fluids.add(fluid);
                }
                List<List<FluidStack>> fluid_fluids = new ArrayList<>();
                fluid_fluids.add(fluids);
                ingredients.setInputLists(VanillaTypes.FLUID, fluid_fluids);
            } else if (recipe.recipeType == RecipeTypes.BLOCK) {
                List<ItemStack> itemStacks = new ArrayList<>();
                for (Block element: recipe.blockTag.getAllElements() ) {
                    ItemStack item = new ItemStack(element.asItem());
                    itemStacks.add(item);
                }
                List<List<ItemStack>> item_items = new ArrayList<>();
                item_items.add(itemStacks);
                ingredients.setInputLists(VanillaTypes.ITEM, item_items);
            }
        } else if (recipe.itemIn != null) {
            ingredients.setInput(VanillaTypes.ITEM, recipe.itemIn);
        } else if (recipe.fluidIn != null) {
            ingredients.setInput(VanillaTypes.FLUID, recipe.fluidIn);
        }
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, 135.0F));
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
        if (recipe.recipeType == RecipeTypes.FLUID) {
            IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
            fluidStacks.init(1,true,4,58);
            fluidStacks.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        } else {
            IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
            itemStacks.init(1, true, 3, 57);
            itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        }

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 4, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    public static class Recipe {
        private final ItemStack itemIn;
        private final FluidStack fluidIn;

        private final String beeType;

        private final boolean acceptsAny;

        private final Tag<Item> itemITag;
        private final Tag<Fluid> fluidITag;
        private final Tag<Block> blockTag;

        private final RecipeTypes recipeType;

        public Recipe(@Nullable ItemStack flowerItem, @Nullable FluidStack flowerFluid, String beeType, RecipeTypes recipeType, boolean acceptsAny) {
            this.itemIn = flowerItem;
            this.fluidIn = flowerFluid;
            this.beeType = beeType;
            this.acceptsAny = acceptsAny;
            this.itemITag = null;
            this.fluidITag = null;
            this.recipeType = recipeType;
            this.blockTag = null;
        }

        //TAGS!!!
        public Recipe(@Nullable Tag<Item> flowerItemTag, @Nullable Tag<Fluid> flowerFluidTag, Tag<Block> blockTag, String beeType, RecipeTypes recipeType, boolean acceptsAny) {
            this.itemIn = null;
            this.fluidIn = null;
            this.beeType = beeType;
            this.acceptsAny = acceptsAny;
            this.itemITag = flowerItemTag;
            this.fluidITag = flowerFluidTag;
            this.recipeType = recipeType;
            this.blockTag = blockTag;
        }

        public boolean isAcceptsAny() { return acceptsAny; }
        public Tag<?> getItemITag() { return itemITag; }
        public String getBeeType() { return this.beeType; }
    }
}
