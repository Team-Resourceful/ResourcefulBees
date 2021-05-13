package com.resourcefulbees.resourcefulbees.compat.jei;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.ApiaryOutputs;
import com.resourcefulbees.resourcefulbees.lib.HoneycombTypes;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HiveCategory extends BaseCategory<HiveCategory.Recipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beehive.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "hive");

    public static final List<ItemStack> NESTS = Arrays.asList(
            ModItems.OAK_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.ACACIA_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.GRASS_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.JUNGLE_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.NETHER_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.PRISMARINE_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.PURPUR_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.BIRCH_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.WITHER_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.BROWN_MUSHROOM_NEST_ITEM.get().getDefaultInstance(),
            ModItems.CRIMSON_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.CRIMSON_NYLIUM_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.DARK_OAK_NEST_ITEM.get().getDefaultInstance(),
            ModItems.RED_MUSHROOM_NEST_ITEM.get().getDefaultInstance(),
            ModItems.SPRUCE_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.WARPED_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.WARPED_NYLIUM_BEE_NEST_ITEM.get().getDefaultInstance()
    );


    public HiveCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.hive"),
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 26).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get())),
                HiveCategory.Recipe.class);
    }

    public static List<Recipe> getHoneycombRecipes() {

        final int[] outputQuantities = {
                Config.T1_APIARY_QUANTITY.get(),
                Config.T2_APIARY_QUANTITY.get(),
                Config.T3_APIARY_QUANTITY.get(),
                Config.T4_APIARY_QUANTITY.get()
        };
        final Item[] apiaryTiers = {
                ModItems.T1_APIARY_ITEM.get(),
                ModItems.T2_APIARY_ITEM.get(),
                ModItems.T3_APIARY_ITEM.get(),
                ModItems.T4_APIARY_ITEM.get()
        };

        List<Recipe> recipes = new ArrayList<>();
        BeeRegistry.getRegistry().getBees().forEach(((s, customBeeData) -> {
            List<ApiaryOutputs> outputs = customBeeData.getHoneycombData().getApiaryOutputTypes();
            List<Integer> customAmounts = customBeeData.getHoneycombData().getApiaryOutputAmounts();

            if (customBeeData.getHoneycombData().getHoneycombType() != HoneycombTypes.NONE) {
                recipes.add(new Recipe(customBeeData.getHoneycombData().getHoneycomb().getDefaultInstance(), NESTS, customBeeData));
                for (int i = 0; i < 4; i++){
                    Item outputItem = outputs.get(i).equals(ApiaryOutputs.COMB)
                            ? customBeeData.getHoneycombData().getHoneycomb()
                            : customBeeData.getHoneycombData().getHoneycombBlock();

                    int amount = customAmounts != null && customAmounts.get(i) > 0 ? customAmounts.get(i) :  outputQuantities[i];
                    ItemStack outputStack = new ItemStack(outputItem, amount);


                    recipes.add(new Recipe(outputStack, apiaryTiers[i].getDefaultInstance(), customBeeData));
                }
            }

        }));
        return recipes;
    }


    @Override
    public void setIngredients(@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.comb);
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.hives));
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(0, false, 138, 4);
        itemStacks.init(1, true, 62, 4);
        itemStacks.set(ingredients);

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 10, 2);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    public static class Recipe {
        private final ItemStack comb;
        private final CustomBeeData beeType;
        private final List<ItemStack> hives;

        public Recipe(ItemStack comb, List<ItemStack> hives, CustomBeeData beeType) {
            this.comb = comb;
            this.beeType = beeType;
            this.hives = hives;
        }

        public Recipe(ItemStack comb, ItemStack hive, CustomBeeData beeType) {
            this(comb,Collections.singletonList(hive), beeType);
        }
    }

}
