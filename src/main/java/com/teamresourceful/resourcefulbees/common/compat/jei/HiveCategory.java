package com.teamresourceful.resourcefulbees.common.compat.jei;

//import com.mojang.blaze3d.vertex.PoseStack;
//import com.teamresourceful.resourcefulbees.ResourcefulBees;
//import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
//import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
//import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
//import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
//import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
//import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
//import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.RecipeType;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraftforge.registries.DeferredRegister;
//import net.minecraftforge.registries.RegistryObject;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class HiveCategory extends BaseCategory<HiveCategory.Recipe> {
//
//    public static final ResourceLocation HIVE_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/honeycomb.png");
//    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "hive");
//    public static final RecipeType<HiveCategory.Recipe> RECIPE = new RecipeType<>(ID, HiveCategory.Recipe.class);
//
//    protected static final List<ItemStack> NESTS_0 = getStacksFromRegister(ModItems.T1_NEST_ITEMS);
//
//    private final IDrawable hiveBackground;
//    private final IDrawable apiaryBackground;
//
//    public HiveCategory(IGuiHelper guiHelper) {
//        super(guiHelper, RECIPE,
//                TranslationConstants.Jei.HIVE,
//                guiHelper.createBlankDrawable(160, 26),
//                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get())));
//
//        hiveBackground = guiHelper.drawableBuilder(HIVE_BACK, 0, 0, 160, 26).addPadding(0, 0, 0, 0).build();
//        apiaryBackground = guiHelper.drawableBuilder(HIVE_BACK, 0, 26, 160, 26).addPadding(0, 0, 0, 0).build();
//    }
//
//    public static List<Recipe> getHoneycombRecipes() {
//        List<Recipe> recipes = BeeRegistry.getRegistry().getBees().values().stream().flatMap(HiveCategory::createRecipes).collect(Collectors.toList());
//        for (BeehiveTier tier : BeehiveTier.values()) {
//            recipes.add(new Recipe(Items.HONEYCOMB.getDefaultInstance(), getStacksFromRegister(tier.getDisplayItems()), EntityType.BEE, false));
//        }
//        for (ApiaryTier tier : ApiaryTier.values()) {
//            ItemStack output = new ItemStack(tier.getOutputType().isComb() ? Items.HONEYCOMB : Items.HONEYCOMB_BLOCK);
//            recipes.add(new Recipe(output, tier.getItem().getDefaultInstance(), EntityType.BEE, true));
//        }
//        return recipes;
//    }
//
//    private static Stream<Recipe> createRecipes(CustomBeeData beeData){
//        List<Recipe> recipes = new ArrayList<>();
//        beeData.coreData().getHoneycombData().ifPresent(honeycombData -> {
//            for (BeehiveTier tier : BeehiveTier.values()) {
//                recipes.add(new Recipe(honeycombData.getHiveOutput(tier), getStacksFromRegister(tier.getDisplayItems()), beeData.getEntityType(), false));
//            }
//            for (ApiaryTier tier : ApiaryTier.values()) {
//                recipes.add(new Recipe(honeycombData.getApiaryOutput(tier), tier.getItem().getDefaultInstance(), beeData.getEntityType(), true));
//            }
//        });
//        return recipes.stream();
//    }
//
//    private static List<ItemStack> getStacksFromRegister(DeferredRegister<Item> register) {
//        return register.getEntries().stream().filter(RegistryObject::isPresent).map(RegistryObject::get).map(Item::getDefaultInstance).collect(Collectors.toList());
//    }
//
//    @Override
//    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses) {
//        super.setRecipe(builder, recipe, focuses);
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 5)
//                .addIngredient(VanillaTypes.ITEM_STACK, recipe.comb)
//                .setSlotName("comb");
//        builder.addSlot(RecipeIngredientRole.INPUT, 63, 5)
//                .addIngredients(VanillaTypes.ITEM_STACK, recipe.hives)
//                .setSlotName("hive");
//        builder.addSlot(RecipeIngredientRole.INPUT, 11, 3)
//                .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.entityType, 45.0f))
//                .setSlotName("bee");
//    }
//
//    @Override
//    public void draw(@NotNull Recipe recipe, @NotNull IRecipeSlotsView view, @NotNull PoseStack stack, double mouseX, double mouseY) {
//        super.draw(recipe, view, stack, mouseX, mouseY);
//        if (recipe.isApiary) this.apiaryBackground.draw(stack);
//        else this.hiveBackground.draw(stack);
//    }
//
//    public static class Recipe {
//        private final ItemStack comb;
//        private final EntityType<?> entityType;
//        private final List<ItemStack> hives;
//        private final boolean isApiary;
//
//        public Recipe(ItemStack comb, List<ItemStack> hives, EntityType<?> entityType, boolean isApiary) {
//            this.comb = comb;
//            this.entityType = entityType;
//            this.hives = hives;
//            this.isApiary = isApiary;
//        }
//
//        public Recipe(ItemStack comb, ItemStack hive, EntityType<?> entityType, boolean isApiary) {
//            this(comb,Collections.singletonList(hive), entityType, isApiary);
//        }
//    }
//
//}
