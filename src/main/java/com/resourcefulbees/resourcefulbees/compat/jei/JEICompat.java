package com.resourcefulbees.resourcefulbees.compat.jei;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.CentrifugeMultiblockScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.CentrifugeScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.MechanicalCentrifugeScreen;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredientFactory;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredientHelper;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityRenderer;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import static com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE;

@mezz.jei.api.JeiPlugin
public class JEICompat implements IModPlugin {

    public static final IIngredientType<EntityIngredient> ENTITY_INGREDIENT = () -> EntityIngredient.class;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new BeeHiveCategory(helper));
        registration.addRecipeCategories(new BeeBreedingCategory(helper));
        registration.addRecipeCategories(new FlowersCategory(helper));
        registration.addRecipeCategories(new EntityFlowerCategory(helper));
        registration.addRecipeCategories(new CentrifugeRecipeCategory(helper));
        registration.addRecipeCategories(new FluidToFluid(helper));
        registration.addRecipeCategories(new BlockToFluid(helper));
        registration.addRecipeCategories(new FluidToBlock(helper));
        registration.addRecipeCategories(new BlockToBlock(helper));
        registration.addRecipeCategories(new ApiaryCategory(helper));
    }

    @Nonnull
    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation( ResourcefulBees.MOD_ID, "jei" );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.T1_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T2_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T3_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T4_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T1_APIARY_ITEM.get()), ApiaryCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T2_APIARY_ITEM.get()), ApiaryCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T3_APIARY_ITEM.get()), ApiaryCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T4_APIARY_ITEM.get()), ApiaryCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.CENTRIFUGE_ITEM.get()), CentrifugeRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MECHANICAL_CENTRIFUGE_ITEM.get()), CentrifugeRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.CENTRIFUGE_CONTROLLER_ITEM.get()), CentrifugeRecipeCategory.ID);
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        World clientWorld= Minecraft.getInstance().world;
        if (clientWorld != null) {
            RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();
            registration.addRecipes(BeeHiveCategory.getHoneycombRecipes(registration.getIngredientManager()), BeeHiveCategory.ID);
            registration.addRecipes(recipeManager.getRecipes(CENTRIFUGE_RECIPE_TYPE).values(), CentrifugeRecipeCategory.ID);
            registration.addRecipes(BeeBreedingCategory.getBreedingRecipes(registration.getIngredientManager()), BeeBreedingCategory.ID);
            registration.addRecipes(FluidToFluid.getMutationRecipes(registration.getIngredientManager()), FluidToFluid.ID);
            registration.addRecipes(BlockToFluid.getMutationRecipes(registration.getIngredientManager()), BlockToFluid.ID);
            registration.addRecipes(FluidToBlock.getMutationRecipes(registration.getIngredientManager()), FluidToBlock.ID);
            registration.addRecipes(BlockToBlock.getMutationRecipes(registration.getIngredientManager()), BlockToBlock.ID);
            registration.addRecipes(ApiaryCategory.getHoneycombRecipes(registration.getIngredientManager()), ApiaryCategory.ID);
            registration.addRecipes(FlowersCategory.getFlowersRecipes(registration.getIngredientManager()), FlowersCategory.ID);
            registration.addRecipes(EntityFlowerCategory.getFlowersRecipes(registration.getIngredientManager()), EntityFlowerCategory.ID);
            registerInfoDesc(registration);
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CentrifugeScreen.class, 80, 30, 18, 18, CentrifugeRecipeCategory.ID);
        registration.addRecipeClickArea(MechanicalCentrifugeScreen.class, 80, 30, 18, 18, CentrifugeRecipeCategory.ID);
        registration.addRecipeClickArea(CentrifugeMultiblockScreen.class, 88, 26, 18, 18, CentrifugeRecipeCategory.ID);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        List<EntityIngredient> entityIngredients = EntityIngredientFactory.create();
        registration.register(ENTITY_INGREDIENT, entityIngredients, new EntityIngredientHelper<>(), new EntityRenderer());
    }

    public void registerInfoDesc(IRecipeRegistration registration){
        for (EntityIngredient bee : EntityIngredientFactory.create()) {
            CustomBeeData beeData = BeeRegistry.getRegistry().getBeeData(bee.getBeeType());

            StringBuilder stats = new StringBuilder();

            stats.append("\u00A73 Attack Damage: \u00A75").append(beeData.getCombatData().getAttackDamage()).append("\n");
            stats.append("\u00A73 Has Honeycomb: \u00A75").append(StringUtils.capitalize(String.valueOf(beeData.hasHoneycomb()))).append("\n");
            stats.append("\u00A73 Max Time in Hive: \u00A75").append(beeData.getMaxTimeInHive()).append(" ticks\n");

            stats.append("\u00A73 Has Mutation: \u00A75").append(StringUtils.capitalize(String.valueOf(beeData.getMutationData().hasMutation()))).append("\n");
            if (beeData.getMutationData().hasMutation()) {
                stats.append("\u00A73 Mutation Count: \u00A75").append(StringUtils.capitalize(String.valueOf(beeData.getMutationData().getMutationCount()))).append("\n");
            }

            stats.append("\u00A73 Is Breedable: \u00A75").append(StringUtils.capitalize(String.valueOf(beeData.getBreedData().isBreedable()))).append("\n");
            if (beeData.getBreedData().isBreedable() && beeData.getBreedData().hasParents()) {
                stats.append("\u00A73 Parents: \u00A75").append(StringUtils.capitalize(beeData.getBreedData().getParent1())).append(" Bee, ")
                        .append(StringUtils.capitalize(beeData.getBreedData().getParent2())).append(" Bee\n");
            }

            if (beeData.hasTraitNames()) {
                StringJoiner traits = new StringJoiner(", ");
                Arrays.stream(beeData.getTraitNames()).forEach(trait -> traits.add(WordUtils.capitalize(trait.replaceAll("_"," "))));
                stats.append("\u00A73 Traits: \u00A75").append(traits.toString()).append("\n");
            }

            stats.append("\u00A73 Spawns in World: \u00A75").append(StringUtils.capitalize(String.valueOf(beeData.getSpawnData().canSpawnInWorld()))).append("\n");
            if (beeData.getSpawnData().canSpawnInWorld()) {
                stats.append("\u00A73 Light Level: \u00A75").append(beeData.getSpawnData().getLightLevel()).append("\n");
                stats.append("\u00A73 Min Group Size: \u00A75").append(beeData.getSpawnData().getMinGroupSize()).append("\n");
                stats.append("\u00A73 Max Group Size: \u00A75").append(beeData.getSpawnData().getMaxGroupSize()).append("\n");
                stats.append("\u00A73 Biomes: \u00A75").append(BiomeParser.parseBiomes(beeData));
            }

            registration.addIngredientInfo(bee, ENTITY_INGREDIENT, stats.toString());
        }
    }
}
