package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.client.config.ClientConfig;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredientHelper;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityRenderer;
import com.teamresourceful.resourcefulbees.common.compat.jei.mutation.MutationCategory;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.item.Beepedia;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.mixin.RecipeManagerAccessorInvoker;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;
import java.util.stream.Collectors;

@JeiPlugin
public class JEICompat implements IModPlugin {

    public static final IIngredientType<EntityIngredient> ENTITY_INGREDIENT = () -> EntityIngredient.class;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new HiveCategory(helper));
        registration.addRecipeCategories(new BeeBreedingCategory(helper));
        registration.addRecipeCategories(new FlowersCategory(helper));
        registration.addRecipeCategories(new MutationCategory(helper));
        registration.addRecipeCategories(new CentrifugeCategory(helper));
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
        registration.addRecipeCatalyst(new ItemStack(ModItems.APIARY_BREEDER_ITEM.get()), BeeBreedingCategory.ID);
        for (ItemStack stack:HiveCategory.NESTS_0) registration.addRecipeCatalyst(stack, HiveCategory.ID);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        World clientWorld = Minecraft.getInstance().level;
        if (clientWorld != null) {
            RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
            registration.addRecipes(HiveCategory.getHoneycombRecipes(), HiveCategory.ID);
            registration.addRecipes(BeeBreedingCategory.getBreedingRecipes(), BeeBreedingCategory.ID);
            registration.addRecipes(MutationCategory.getMutationRecipes(), MutationCategory.ID);
            registration.addRecipes(FlowersCategory.getFlowersRecipes(), FlowersCategory.ID);
            registration.addRecipes(CentrifugeCategory.getRecipes(((RecipeManagerAccessorInvoker)recipeManager).callByType(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE).values()), CentrifugeCategory.ID);
            registerInfoDesc(registration);
        }
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(
                ENTITY_INGREDIENT,
                BeeRegistry.getRegistry().getBees().values().stream().map(b -> new EntityIngredient(b.getEntityType(), -45.0f)).collect(Collectors.toList()),
                new EntityIngredientHelper(),
                new EntityRenderer()
        );
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.BEEPEDIA.get(), (ingredient, context) -> ingredient.hasTag() && ingredient.getTag() != null && ingredient.getTag().contains(Beepedia.CREATIVE_TAG) ? "creative.beepedia" : "");

        if (Boolean.TRUE.equals(ClientConfig.SHOW_TIERS_IN_JEI.get())) {
            for (RegistryObject<Item> nest : ModItems.NESTS_ITEMS.getEntries()) {
                registration.registerSubtypeInterpreter(nest.get(), (stack, context) -> {
                    if (!stack.hasTag() || stack.getTag() == null) return IIngredientSubtypeInterpreter.NONE;
                    if (!stack.getTag().contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) return IIngredientSubtypeInterpreter.NONE;
                    CompoundNBT blockTag = stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
                    if (!blockTag.contains(NBTConstants.NBT_TIER)) return IIngredientSubtypeInterpreter.NONE;
                    return "tier." + blockTag.getInt(NBTConstants.NBT_TIER) + "." + nest.getId().getPath();
                });
            }
        }
    }

    public void registerInfoDesc(IRecipeRegistration registration) {
        for (EntityIngredient ingredient : registration.getIngredientManager().getAllIngredients(ENTITY_INGREDIENT)) {
            if (ingredient.getEntity() != null && ingredient.getEntity() instanceof CustomBeeEntity) {
                CustomBeeEntity customBee = ((CustomBeeEntity) ingredient.getEntity());

                StringBuilder stats = new StringBuilder();
                String aqua = TextFormatting.DARK_AQUA.toString();
                String purple = TextFormatting.DARK_PURPLE.toString();


                stats.append(aqua).append(" Base Health: ").append(purple).append(customBee.getCombatData().getBaseHealth()).append("\n");
                stats.append(aqua).append(" Attack Damage: ").append(purple).append(customBee.getCombatData().getAttackDamage()).append("\n");
                stats.append(aqua).append(" Max Time in Hive: ").append(purple).append(customBee.getCoreData().getMaxTimeInHive()).append(" ticks\n");

                stats.append(aqua).append(" Has Mutation: ").append(purple).append(StringUtils.capitalize(String.valueOf(customBee.getMutationData().hasMutation()))).append("\n");
                if (customBee.getMutationData().hasMutation()) {
                    stats.append(aqua).append(" Mutation Count: ").append(purple).append(StringUtils.capitalize(String.valueOf(customBee.getMutationData().getMutationCount()))).append("\n");
                }

                stats.append(aqua).append(" Is Breedable: ").append(purple).append(StringUtils.capitalize(String.valueOf(customBee.getBreedData().hasParents()))).append("\n");
                if (customBee.getBreedData().hasParents()) {
                    stats.append(aqua).append(" Parents: ").append(purple);
                    for (BeeFamily family : customBee.getBreedData().getFamilies()) {
                        stats.append(StringUtils.capitalize(family.getParent1())).append(" Bee, ")
                                .append(StringUtils.capitalize(family.getParent2())).append(" Bee\n");
                    }
                }

                if (customBee.getTraitData().hasTraits()) {
                    StringJoiner traits = new StringJoiner(", ");
                    //noinspection deprecation
                    customBee.getTraitData().getTraits().forEach(trait -> traits.add(WordUtils.capitalize(trait.replace("_", " "))));
                    stats.append(aqua).append(" Traits: ").append(purple).append(traits).append("\n");
                }

                stats.append(aqua).append(" Spawns in World: ").append(purple).append(StringUtils.capitalize(String.valueOf(customBee.getSpawnData().canSpawnInWorld()))).append("\n");
                if (customBee.getSpawnData().canSpawnInWorld()) {
                    stats.append(aqua).append(" Light Level: ").append(purple).append(customBee.getSpawnData().getLightLevel()).append("\n");
                    stats.append(aqua).append(" Min Y Level: ").append(purple).append(customBee.getSpawnData().getMinYLevel()).append("\n");
                    stats.append(aqua).append(" Max Y Level: ").append(purple).append(customBee.getSpawnData().getMaxYLevel()).append("\n");
                    stats.append(aqua).append(" Min Group Size: ").append(purple).append(customBee.getSpawnData().getMinGroupSize()).append("\n");
                    stats.append(aqua).append(" Max Group Size: ").append(purple).append(customBee.getSpawnData().getMaxGroupSize()).append("\n");
                    stats.append(aqua).append(" Biomes: ").append(purple).append(customBee.getSpawnData().getSpawnableBiomesAsString());
                }

                registration.addIngredientInfo(ingredient, ENTITY_INGREDIENT, new StringTextComponent(stats.toString()));
            }
        }
    }


}
