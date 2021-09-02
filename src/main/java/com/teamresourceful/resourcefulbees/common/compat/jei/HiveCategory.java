package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HiveCategory extends BaseCategory<HiveCategory.Recipe> {

    public static final ResourceLocation HIVE_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beehive.png");
    public static final ResourceLocation APIARY_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/apiary.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "hive");

    private final IDrawable HIVE_BACKGROUND;
    private final IDrawable APIARY_BACKGROUND;

    public static final List<ItemStack> NESTS_0 = ModItems.NESTS_ITEMS.getEntries().stream()
            .filter(RegistryObject::isPresent).map(RegistryObject::get).map(Item::getDefaultInstance).collect(Collectors.toList());

    private static final List<ItemStack> NESTS_1 = NESTS_0.stream().map(stack -> getNestWithTier(stack, 1, 1f)).collect(Collectors.toList());
    private static final List<ItemStack> NESTS_2 = NESTS_0.stream().map(stack -> getNestWithTier(stack, 2, 1.5f)).collect(Collectors.toList());
    private static final List<ItemStack> NESTS_3 = NESTS_0.stream().map(stack -> getNestWithTier(stack, 3, 2f)).collect(Collectors.toList());
    private static final List<ItemStack> NESTS_4 = NESTS_0.stream().map(stack -> getNestWithTier(stack, 4, 4f)).collect(Collectors.toList());

    private static final List<List<ItemStack>> NESTS = Lists.newArrayList(NESTS_0, NESTS_1, NESTS_2, NESTS_3, NESTS_4);
    private static final List<ItemStack> APIARIES = Lists.newArrayList(ModItems.T1_APIARY_ITEM.get().getDefaultInstance(), ModItems.T2_APIARY_ITEM.get().getDefaultInstance(), ModItems.T3_APIARY_ITEM.get().getDefaultInstance(), ModItems.T4_APIARY_ITEM.get().getDefaultInstance());

    public HiveCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.hive"),
                guiHelper.createBlankDrawable(160, 26),
                guiHelper.createDrawableIngredient(new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get())),
                HiveCategory.Recipe.class);

        HIVE_BACKGROUND = guiHelper.drawableBuilder(HIVE_BACK, 0, 0, 160, 26).addPadding(0, 0, 0, 0).build();
        APIARY_BACKGROUND = guiHelper.drawableBuilder(APIARY_BACK, 0, 0, 160, 26).addPadding(0, 0, 0, 0).build();

    }

    public static List<Recipe> getHoneycombRecipes() {
        return BeeRegistry.getRegistry().getBees().values().stream().flatMap(HiveCategory::createRecipes).collect(Collectors.toList());
    }

    private static Stream<Recipe> createRecipes(CustomBeeData beeData){
        List<Recipe> recipes = new ArrayList<>();
        if (beeData.getHoneycombData().isPresent()) {
            for (int i = 0; i < 5; i++) recipes.add(new Recipe(beeData.getHoneycombData().get().getHiveOutput(i), NESTS.get(i), beeData, false));
            for (int i = 0; i < 4; i++) recipes.add(new Recipe(beeData.getHoneycombData().get().getApiaryOutput(i), APIARIES.get(i), beeData, true));
        }
        return recipes.stream();
    }

    private static ItemStack getNestWithTier(ItemStack stack, int tier, float modifier) {
        stack = stack.copy();
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("Tier", tier);
        nbt.putFloat("TierModifier", modifier);
        stack.getOrCreateTag().put(NBTConstants.NBT_BLOCK_ENTITY_TAG, nbt);
        return stack;
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

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull MatrixStack matrixStack, double mouseX, double mouseY) {
        if (recipe.isApiary) this.APIARY_BACKGROUND.draw(matrixStack);
        else this.HIVE_BACKGROUND.draw(matrixStack);
    }

    public static class Recipe {
        private final ItemStack comb;
        private final CustomBeeData beeType;
        private final List<ItemStack> hives;
        private final boolean isApiary;

        public Recipe(ItemStack comb, List<ItemStack> hives, CustomBeeData beeType, boolean isApiary) {
            this.comb = comb;
            this.beeType = beeType;
            this.hives = hives;
            this.isApiary = isApiary;
        }

        public Recipe(ItemStack comb, ItemStack hive, CustomBeeData beeType, boolean isApiary) {
            this(comb,Collections.singletonList(hive), beeType, isApiary);
        }
    }

}
