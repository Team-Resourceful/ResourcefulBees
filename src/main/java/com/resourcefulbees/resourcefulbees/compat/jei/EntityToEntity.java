package com.resourcefulbees.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityToEntity extends BaseCategory<EntityToEntity.Recipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "entity_to_entity_mutation");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private final IDrawable nonRegisteredEgg;

    private static final String DARK_PURPLE = "dark_purple";

    public EntityToEntity(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.entity_to_entity_mutation"),
                guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawable(ICONS, 0, 0, 16, 16),
                EntityToEntity.Recipe.class);
        this.nonRegisteredEgg = guiHelper.createDrawable(ICONS, 41, 0, 16, 16);
    }

    public static List<Recipe> getMutationRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            MutationData mutationData = beeData.getMutationData();
            if (mutationData.hasMutation() && mutationData.hasEntityMutations()) {
                beeData.getMutationData().getEntityMutations().forEach((entityType, doubleRandomCollectionPair) -> {
                    SpawnEggItem inputSpawnEgg = SpawnEggItem.byId(entityType);
                    ItemStack inputEgg = inputSpawnEgg != null ? new ItemStack(inputSpawnEgg) : null;
                    doubleRandomCollectionPair.getRight().forEach(entityOutput -> {
                        SpawnEggItem outputSpawnEgg = SpawnEggItem.byId(entityOutput.getEntityType());
                        ItemStack outputEgg = outputSpawnEgg != null ? new ItemStack(outputSpawnEgg) : null;

                        double effectiveWeight = RecipeUtils.getEffectiveWeight(doubleRandomCollectionPair.getRight(), entityOutput.getWeight());
                        recipes.add(new Recipe(entityType, entityOutput.getEntityType(), inputEgg, outputEgg, entityOutput.getCompoundNBT(), beeData.getName(), effectiveWeight, doubleRandomCollectionPair.getLeft()));
                    });
                });
            }
        }));
        return recipes;
    }

    @Override
    public void setIngredients(Recipe recipe, @NotNull IIngredients ingredients) {
        if (recipe.input != null)
            ingredients.setInput(VanillaTypes.ITEM, recipe.input);
        if (recipe.output != null)
            ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        //HIVE INFO
        List<ITextComponent> list = RecipeUtils.getTooltipStrings(mouseX, mouseY, recipe.chance);
        if (!list.isEmpty()) {
            return list;
        }
        //OUTPUT
        if (mouseX > 64 && mouseX < 84 && mouseY > 47 && mouseY < 66) {
            List<ITextComponent> tooltip = new ArrayList<>();
            addTooltip(recipe, tooltip);
            return tooltip;
        }

        //INPUT
        if (mouseX > 15 && mouseX < 32 && mouseY > 57 && mouseY < 74) {
            List<ITextComponent> tooltip = new ArrayList<>();
            tooltip.add(recipe.inputEntityType.getDescription().plainCopy());
            tooltip.addAll(BeeInfoUtils.getBeeLore(recipe.inputEntity));
            if (recipe.outputEntityType.getRegistryName() != null) {
                tooltip.add(new StringTextComponent(recipe.outputEntityType.getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY));
            }
            return tooltip;
        }

        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull Recipe recipe, @NotNull IIngredients ingredients) {
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
        ingredientStacks.init(1, false, 15, 57);
    }

    @NotNull
    private ITooltipCallback<ItemStack> getItemStackTooltipCallback(@NotNull Recipe recipe) {
        return (slotIndex, isInputStack, stack, tooltip) -> {
            if (slotIndex == 0) {
                tooltip.clear();
                addTooltip(recipe, tooltip); //TODO NEEDS CONFIRMATION OF REFACTOR WORKING
            }
        };
    }

    private void addTooltip(@NotNull Recipe recipe, List<ITextComponent> tooltip) {
        tooltip.add(recipe.outputEntityType.getDescription().plainCopy());
        tooltip.addAll(BeeInfoUtils.getBeeLore(recipe.ouputEntity));
        if (recipe.outputEntityType.getRegistryName() != null) {
            tooltip.add(new StringTextComponent(recipe.outputEntityType.getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY));
        }
        if (!recipe.outputNBT.isEmpty()) {
            if (BeeInfoUtils.isShiftPressed()) {
                List<String> lore = BeeInfoUtils.getLoreLines(recipe.outputNBT);
                lore.forEach(l -> tooltip.add(new StringTextComponent(l).withStyle(Style.EMPTY.withColor(Color.parseColor(DARK_PURPLE)))));
            } else {
                tooltip.add(new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt").withStyle(Style.EMPTY.withColor(Color.parseColor(DARK_PURPLE))));
            }
        }
    }

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull MatrixStack stack, double mouseX, double mouseY) {
        RecipeUtils.drawMutationScreen(stack, this.beeHive, this.info, recipe.weight, recipe.chance);
        RenderUtils.renderEntity(stack, recipe.inputEntity, Minecraft.getInstance().level, 14, 55, 45, 1);
        RenderUtils.renderEntity(stack, recipe.ouputEntity, Minecraft.getInstance().level, 64, 45, -45, 1);
    }

    public static class Recipe {
        private final EntityType<?> inputEntityType;
        private final EntityType<?> outputEntityType;
        private final ItemStack input;
        private final ItemStack output;
        private final String beeType;
        private Entity ouputEntity = null;
        private Entity inputEntity = null;


        private final double weight;
        private final double chance;

        public final CompoundNBT outputNBT;

        public Recipe(EntityType<?> inputEntityType, EntityType<?> outputEntityType, ItemStack inputEgg, ItemStack outputEgg, CompoundNBT outputNBT, String beeType, double weight, double chance) {
            this.inputEntityType = inputEntityType;
            this.outputEntityType = outputEntityType;

            this.input = inputEgg;
            this.output = outputEgg;
            this.outputNBT = outputNBT;
            this.beeType = beeType;
            this.weight = weight;
            this.chance = chance;

            this.ouputEntity = this.outputEntityType.create(Minecraft.getInstance().level);
            if (!this.outputNBT.isEmpty()) {
                CompoundNBT nbt = this.ouputEntity.saveWithoutId(new CompoundNBT());
                nbt.merge(this.outputNBT);
                this.ouputEntity.load(nbt);
            }
            this.inputEntity = this.inputEntityType.create(Minecraft.getInstance().level);
        }
    }
}
