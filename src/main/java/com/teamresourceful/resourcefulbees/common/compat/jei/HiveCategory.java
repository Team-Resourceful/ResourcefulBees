package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HiveCategory extends BaseCategory<HiveCategory.Recipe> {

    public static final ResourceLocation HIVE_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/honeycomb.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "hive");

    protected static final List<ItemStack> NESTS_0 = ModItems.NESTS_ITEMS.getEntries().stream()
            .filter(RegistryObject::isPresent).map(RegistryObject::get).map(Item::getDefaultInstance).collect(Collectors.toList());

    private static final List<ItemStack> NESTS_1 = NESTS_0.stream().map(stack -> getNestWithTier(stack, 1, 1f)).collect(Collectors.toList());
    private static final List<ItemStack> NESTS_2 = NESTS_0.stream().map(stack -> getNestWithTier(stack, 2, 1.5f)).collect(Collectors.toList());
    private static final List<ItemStack> NESTS_3 = NESTS_0.stream().map(stack -> getNestWithTier(stack, 3, 2f)).collect(Collectors.toList());
    private static final List<ItemStack> NESTS_4 = NESTS_0.stream().map(stack -> getNestWithTier(stack, 4, 4f)).collect(Collectors.toList());

    private static final List<List<ItemStack>> NESTS = Lists.newArrayList(NESTS_0, NESTS_1, NESTS_2, NESTS_3, NESTS_4);
    private static final List<ItemStack> APIARIES = Lists.newArrayList(ModItems.T1_APIARY_ITEM.get().getDefaultInstance(), ModItems.T2_APIARY_ITEM.get().getDefaultInstance(), ModItems.T3_APIARY_ITEM.get().getDefaultInstance(), ModItems.T4_APIARY_ITEM.get().getDefaultInstance());


    private final IDrawable hiveBackground;
    private final IDrawable apiaryBackground;

    public HiveCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                TranslationConstants.Jei.HIVE,
                guiHelper.createBlankDrawable(160, 26),
                guiHelper.createDrawableIngredient(new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get())),
                HiveCategory.Recipe.class);

        hiveBackground = guiHelper.drawableBuilder(HIVE_BACK, 0, 0, 160, 26).addPadding(0, 0, 0, 0).build();
        apiaryBackground = guiHelper.drawableBuilder(HIVE_BACK, 0, 26, 160, 26).addPadding(0, 0, 0, 0).build();
    }

    public static List<Recipe> getHoneycombRecipes() {
        List<Recipe> recipes = BeeRegistry.getRegistry().getBees().values().stream().flatMap(HiveCategory::createRecipes).collect(Collectors.toList());
        for (int i = 0; i < 5; i++) recipes.add(new Recipe(Items.HONEYCOMB.getDefaultInstance(), NESTS.get(i), EntityType.BEE, false));
        //for (int i = 0; i < 4; i++) recipes.add(new Recipe(ApiaryStorageTileEntity.getVanillaOutput(i), APIARIES.get(i), EntityType.BEE, true));
        return recipes;
    }

    private static Stream<Recipe> createRecipes(CustomBeeData beeData){
        List<Recipe> recipes = new ArrayList<>();
        beeData.getHoneycombData().ifPresent(honeycombData -> {
            for (int i = 0; i < 5; i++) recipes.add(new Recipe(honeycombData.getHiveOutput(i), NESTS.get(i), beeData.getEntityType(), false));
            for (int i = 0; i < 4; i++) recipes.add(new Recipe(honeycombData.getApiaryOutput(i), APIARIES.get(i), beeData.getEntityType(), true));
        });
        return recipes.stream();
    }

    private static ItemStack getNestWithTier(ItemStack stack, int tier, float modifier) {
        stack = stack.copy();
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Tier", tier);
        nbt.putFloat("TierModifier", modifier);
        stack.getOrCreateTag().put(NBTConstants.NBT_BLOCK_ENTITY_TAG, nbt);
        return stack;
    }

    @Override
    public void setIngredients(@NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.comb);
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.hives));
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.entityType, 45.0f));
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
    public void draw(@NotNull Recipe recipe, @NotNull PoseStack matrixStack, double mouseX, double mouseY) {
        if (recipe.isApiary) this.apiaryBackground.draw(matrixStack);
        else this.hiveBackground.draw(matrixStack);
    }

    public static class Recipe {
        private final ItemStack comb;
        private final EntityType<?> entityType;
        private final List<ItemStack> hives;
        private final boolean isApiary;

        public Recipe(ItemStack comb, List<ItemStack> hives, EntityType<?> entityType, boolean isApiary) {
            this.comb = comb;
            this.entityType = entityType;
            this.hives = hives;
            this.isApiary = isApiary;
        }

        public Recipe(ItemStack comb, ItemStack hive, EntityType<?> entityType, boolean isApiary) {
            this(comb,Collections.singletonList(hive), entityType, isApiary);
        }
    }

}
