package com.resourcefulbees.resourcefulbees.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class EntitytoEntity implements IRecipeCategory<EntitytoEntity.Recipe> {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ICONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "entity_to_entity_mutation");
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable info;
    private final IDrawable beeHive;
    private final String localizedName;

    public EntitytoEntity(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawable(ICONS, 0, 0, 16, 16);
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.beeHive = guiHelper.createDrawableIngredient(new ItemStack(ModItems.T1_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.entity_to_entity_mutation");
    }

    public static List<Recipe> getMutationRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();

        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            if (beeData.getMutationData().hasMutation()) {
                beeData.getMutationData().iEntityMutations.forEach((b, m) -> {
                    EntityType input = BeeInfoUtils.getEntityType(m.mutationData.inputID.toLowerCase().replace(BeeConstants.ENTITY_PREFIX, ""));
                    ItemStack inputEgg = new ItemStack(Items.GHAST_SPAWN_EGG);
                    IFormattableTextComponent inputName = input.getName().copy();
                    inputName.setStyle(Style.EMPTY.withColor(Color.parse("gold")).withBold(true).withItalic(false));
                    inputEgg.setDisplayName(inputName);
                    if (input == null) {
                        LOGGER.warn(String.format("Entity Input: [%s] could not be found: ", m.mutationData.inputID));
                    } else {
                        RandomCollection<Pair<EntityType, MutationData.MutationOutput>> outputs = addMutations(m);
                        outputs.forEach(out -> {

                            ItemStack outputEgg = new ItemStack(Items.GHAST_SPAWN_EGG);
                            CompoundNBT outputDisplay = new CompoundNBT();
                            IFormattableTextComponent outputName = out.getKey().getName().copy();
                            outputName.setStyle(Style.EMPTY.withColor(Color.parse("gold")).withBold(true).withItalic(false));
                            outputDisplay.putString("Name", ITextComponent.Serializer.toJson(outputName));
                            CompoundNBT outputTag = new CompoundNBT();
                            outputTag.put("display", outputDisplay);
                            outputEgg.setTag(outputTag);
                            if (!out.getRight().getNbt().isEmpty()) {
                                BeeInfoUtils.addNBTLore(outputEgg, out.getRight().getNbt());
                            }

                            double effectiveWeight = outputs.getAdjustedWeight(out.getValue().getWeight());
                            recipes.add(new Recipe(input, out.getKey(), inputEgg, outputEgg, beeData.getName(), m.type, effectiveWeight, out.getValue().getChance(), false));
                        });
                    }
                });
            }
        }));
        return recipes;
    }

    private static RandomCollection<Pair<EntityType, MutationData.MutationOutput>> addMutations(MutationData.IEntityMutation m) {
        RandomCollection<Pair<EntityType, MutationData.MutationOutput>> outputs = new RandomCollection<>();
        for (MutationData.MutationOutput mutation : m.mutationData.outputs) {
            EntityType entity = BeeInfoUtils.getEntityType(mutation.outputID.toLowerCase().replace(BeeConstants.ENTITY_PREFIX, ""));
            if (entity == null) {
                LOGGER.warn("Entity Output: [%s] could not be found.", mutation.outputID);
                continue;
            }
            outputs.add(mutation.getWeight(), Pair.of(entity, mutation));
        }
        return outputs;
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
        if (MutationTypes.ENTITY_TO_ENTITY.equals(recipe.mutationType)) {
            ingredients.setInput(VanillaTypes.ITEM, recipe.eggIn);
            ingredients.setOutput(VanillaTypes.ITEM, recipe.eggOut);
        }
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, -45.0f));
    }

    @Override
    public List<ITextComponent> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        double infoX = 63D;
        double infoY = 8D;
        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D) {
            return Collections.singletonList(new StringTextComponent(I18n.format("gui." + ResourcefulBees.MOD_ID + ".jei.category.mutation.info")));
        }
        double info2X = 54;
        double info2Y = 34;
        if (mouseX >= info2X && mouseX <= info2X + 9D && mouseY >= info2Y && mouseY <= info2Y + 9D && recipe.chance < 1) {
            return Collections.singletonList(new StringTextComponent(I18n.format("gui." + ResourcefulBees.MOD_ID + ".jei.category.mutation_chance.info")));
        }
        return IRecipeCategory.super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, Recipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(0, false, 65, 48);
        itemStacks.init(1, true, 15, 57);
        itemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @Override
    public void draw(EntitytoEntity.Recipe recipe, MatrixStack stack, double mouseX, double mouseY) {
        this.beeHive.draw(stack, 65, 10);
        this.info.draw(stack, 63, 8);
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontRenderer = minecraft.fontRenderer;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        String weightString = decimalFormat.format(recipe.weight);
        int padding = fontRenderer.getStringWidth(weightString) / 2;
        fontRenderer.draw(stack, weightString, 48 - padding, 66, 0xff808080);
        if (recipe.chance < 1) {
            String chanceString = decimalFormat.format(recipe.chance);
            int padding2 = fontRenderer.getStringWidth(chanceString) / 2;
            fontRenderer.draw(stack, chanceString, 76 - padding2, 35, 0xff808080);
            info.draw(stack, 54, 34);
        }
    }

    public static class Recipe {
        private final EntityType entityIn;
        private final EntityType entityOut;
        private final ItemStack eggIn;
        private final ItemStack eggOut;
        private final String beeType;

        private final boolean acceptsAny;
        private final ITag<Item> tag;
        private final double weight;
        private final double chance;

        private final MutationTypes mutationType;

        public Recipe(EntityType baseEntity, EntityType outputEntity, ItemStack inputEgg, ItemStack outputEgg, String beeType, MutationTypes type, double weight, double chance, boolean acceptsAny) {
            this.entityIn = baseEntity;
            this.entityOut = outputEntity;
            this.eggIn = inputEgg;
            this.eggOut = outputEgg;
            this.beeType = beeType;
            this.mutationType = type;
            this.acceptsAny = acceptsAny;
            this.weight = weight;
            this.chance = chance;
            this.tag = null;
        }

        public boolean isAcceptsAny() {
            return acceptsAny;
        }

        public ITag<?> getTag() {
            return tag;
        }

        public String getBeeType() {
            return this.beeType;
        }
    }
}
