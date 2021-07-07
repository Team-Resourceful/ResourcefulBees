package com.teamresourceful.resourcefulbees.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.CentrifugeScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.MechanicalCentrifugeScreen;
import com.teamresourceful.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.compat.jei.ingredients.EntityIngredientFactory;
import com.teamresourceful.resourcefulbees.compat.jei.ingredients.EntityIngredientHelper;
import com.teamresourceful.resourcefulbees.compat.jei.ingredients.EntityRenderer;
import com.teamresourceful.resourcefulbees.item.Beepedia;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.teamresourceful.resourcefulbees.recipe.CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE;

@JeiPlugin
public class JEICompat implements IModPlugin {

    public static final IIngredientType<EntityIngredient> ENTITY_INGREDIENT = () -> EntityIngredient.class;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new HiveCategory(helper));
        registration.addRecipeCategories(new BeeBreedingCategory(helper));
        registration.addRecipeCategories(new FlowersCategory(helper));
        registration.addRecipeCategories(new CentrifugeRecipeCategory(helper));
        registration.addRecipeCategories(new BlockMutation(helper));
        registration.addRecipeCategories(new EntityToEntity(helper));
        registration.addRecipeCategories(new BlockToItem(helper));
    }

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ResourcefulBees.MOD_ID, "jei");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.T1_APIARY_ITEM.get()), HiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T2_APIARY_ITEM.get()), HiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T3_APIARY_ITEM.get()), HiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T4_APIARY_ITEM.get()), HiveCategory.ID);
        for (ItemStack stack:HiveCategory.NESTS) registration.addRecipeCatalyst(stack, HiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.CENTRIFUGE_ITEM.get()), CentrifugeRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MECHANICAL_CENTRIFUGE_ITEM.get()), CentrifugeRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.CENTRIFUGE_CONTROLLER_ITEM.get()), CentrifugeRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModItems.ELITE_CENTRIFUGE_CONTROLLER_ITEM.get()), CentrifugeRecipeCategory.ID);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        Level clientWorld = Minecraft.getInstance().level;
        if (clientWorld != null) {
            RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
            registration.addRecipes(HiveCategory.getHoneycombRecipes(), HiveCategory.ID);
            registration.addRecipes(recipeManager.byType(CENTRIFUGE_RECIPE_TYPE).values(), CentrifugeRecipeCategory.ID);
            registration.addRecipes(BeeBreedingCategory.getBreedingRecipes(), BeeBreedingCategory.ID);
            registration.addRecipes(BlockMutation.getMutationRecipes(), BlockMutation.ID);
            registration.addRecipes(BlockToItem.getMutationRecipes(), BlockToItem.ID);
            registration.addRecipes(EntityToEntity.getMutationRecipes(), EntityToEntity.ID);
            registration.addRecipes(FlowersCategory.getFlowersRecipes(), FlowersCategory.ID);
            registerInfoDesc(registration);
        }
        List<ItemStack> blacklistedItems = new LinkedList<>();
        blacklistedItems.add(ModItems.MUTATION_ITEM.get().getDefaultInstance());
        registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM,  blacklistedItems);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(MechanicalCentrifugeScreen.class, 80, 30, 18, 18, CentrifugeRecipeCategory.ID);

        registration.addGuiContainerHandler(CentrifugeScreen.class, new IGuiContainerHandler<CentrifugeScreen>() {

            @Override
            public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull CentrifugeScreen screen, double mouseX, double mouseY) {
                IGuiClickableArea clickableArea = IGuiClickableArea.createBasic(screen.getXSize() - 25, 50, 18, 18, CentrifugeRecipeCategory.ID);
                return Collections.singleton(clickableArea);
            }
        });
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        List<EntityIngredient> entityIngredients = EntityIngredientFactory.create();
        registration.register(ENTITY_INGREDIENT, entityIngredients, new EntityIngredientHelper(), new EntityRenderer());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.BEEPEDIA.get(), (ingredient, context) -> ingredient.hasTag() && ingredient.getTag() != null && ingredient.getTag().contains(Beepedia.CREATIVE_TAG) ? "creative.beepedia" : "");
    }


    //gravy, we could add Kube hooks for this, but the info card is a one-stop-shop for all bee data at a glance
    //I'm not entirely sure what other data should be defined here using Kube that we don't already provide
    //since we're already adding a "Lore" field and "Author" field
    public void registerInfoDesc(IRecipeRegistration registration) {
        for (EntityIngredient bee : EntityIngredientFactory.create()) {
            CustomBeeData beeData = bee.getBeeData();

            StringBuilder stats = new StringBuilder();
            String aqua = ChatFormatting.DARK_AQUA.toString();
            String purple = ChatFormatting.DARK_PURPLE.toString();


            stats.append(aqua).append(" Base Health: ").append(purple).append(beeData.getCombatData().getBaseHealth()).append("\n");
            stats.append(aqua).append(" Attack Damage: ").append(purple).append(beeData.getCombatData().getAttackDamage()).append("\n");
            stats.append(aqua).append(" Honeycomb Type: ").append(purple).append(StringUtils.capitalize(beeData.getHoneycombData().getHoneycombType().toString())).append("\n");
            stats.append(aqua).append(" Max Time in Hive: ").append(purple).append(beeData.getCoreData().getMaxTimeInHive()).append(" ticks\n");

            stats.append(aqua).append(" Has Mutation: ").append(purple).append(StringUtils.capitalize(String.valueOf(beeData.getMutationData().hasMutation()))).append("\n");
            if (beeData.getMutationData().hasMutation()) {
                stats.append(aqua).append(" Mutation Count: ").append(purple).append(StringUtils.capitalize(String.valueOf(beeData.getMutationData().getMutationCount()))).append("\n");
            }

            stats.append(aqua).append(" Is Breedable: ").append(purple).append(StringUtils.capitalize(String.valueOf(beeData.getBreedData().hasParents()))).append("\n");
            if (beeData.getBreedData().hasParents()) {
                stats.append(aqua).append(" Parents: ").append(purple);
                //NEED TO SETUP WITH NEW BeeFamily OBJECT
/*                Iterator<String> parent1 = beeData.getBreedData().getParent1().iterator();
                Iterator<String> parent2 = beeData.getBreedData().getParent2().iterator();

                while (parent1.hasNext() && parent2.hasNext()) {
                    stats.append(StringUtils.capitalize(parent1.next())).append(" Bee, ")
                            .append(StringUtils.capitalize(parent2.next())).append(" Bee\n");
                }*/
            }

            if (beeData.getTraitData().hasTraits()) {
                StringJoiner traits = new StringJoiner(", ");
                //noinspection deprecation
                beeData.getTraitData().getTraits().forEach(trait -> traits.add(WordUtils.capitalize(trait.replace("_", " "))));
                stats.append(aqua).append(" Traits: ").append(purple).append(traits).append("\n");
            }

            stats.append(aqua).append(" Spawns in World: ").append(purple).append(StringUtils.capitalize(String.valueOf(beeData.getSpawnData().canSpawnInWorld()))).append("\n");
            if (beeData.getSpawnData().canSpawnInWorld()) {
                stats.append(aqua).append(" Light Level: ").append(purple).append(beeData.getSpawnData().getLightLevel()).append("\n");
                stats.append(aqua).append(" Min Y Level: ").append(purple).append(beeData.getSpawnData().getMinYLevel()).append("\n");
                stats.append(aqua).append(" Max Y Level: ").append(purple).append(beeData.getSpawnData().getMaxYLevel()).append("\n");
                stats.append(aqua).append(" Min Group Size: ").append(purple).append(beeData.getSpawnData().getMinGroupSize()).append("\n");
                stats.append(aqua).append(" Max Group Size: ").append(purple).append(beeData.getSpawnData().getMaxGroupSize()).append("\n");
                stats.append(aqua).append(" Biomes: ").append(purple).append(beeData.getSpawnData().getSpawnableBiomesAsString());
            }

            registration.addIngredientInfo(bee, ENTITY_INGREDIENT, new TextComponent(stats.toString()));
        }
    }


}
