package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.lib.MutationTypes;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("NullableProblems")
public class BlockToBlock implements IRecipeCategory<BlockToBlock.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ICONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "block_to_block_mutation");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable info;
    private final IDrawable beeHive;
    private final String localizedName;
    private final CustomBeeEntity bee;

    public BlockToBlock(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawable(ICONS, 0,0,16,16);
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.beeHive = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.T1_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.block_to_block_mutation");
        assert Minecraft.getInstance().world != null;
        bee = RegistryHandler.CUSTOM_BEE.get().create(Minecraft.getInstance().world);
    }

    public static List<BlockToBlock.Recipe> getMutationRecipes(IIngredientManager ingredientManager) {
        List<BlockToBlock.Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()){
            if (bee.getValue().hasMutation()) {

                String mutationIn = bee.getValue().getMutationInput();
                String mutationOut = bee.getValue().getMutationOutput();

                if (BeeInfoUtils.TAG_RESOURCE_PATTERN.matcher(mutationIn).matches()) {
                    mutationIn = mutationIn.replace(BeeConstants.TAG_PREFIX, "");

                    Tag<Item> itemTag = BeeInfoUtils.getItemTag(mutationIn);
                    if (itemTag !=null) {
                        Item itemOut = BeeInfoUtils.getItem(mutationOut);
                        if (BeeInfoUtils.isValidItem(itemOut)){
                            recipes.add( new Recipe(itemTag, new ItemStack(itemOut), bee.getKey(), MutationTypes.BLOCK_TO_BLOCK, true));
                        }
                    }
                } else {
                    Enum<MutationTypes> mutationType = bee.getValue().getMutationType();

                    if (MutationTypes.BLOCK_TO_BLOCK.equals(mutationType)) {
                        Item itemIn = BeeInfoUtils.getItem(mutationIn);
                        Item itemOut = BeeInfoUtils.getItem(mutationOut);
                        if (BeeInfoUtils.isValidItem(itemIn) && BeeInfoUtils.isValidItem(itemOut))
                            recipes.add( new Recipe( new ItemStack(itemIn), new ItemStack(itemOut), bee.getKey(), mutationType, false));
                    }
                }
            }
        }
        return recipes;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends BlockToBlock.Recipe> getRecipeClass() {
        return BlockToBlock.Recipe.class;
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
        if (recipe.isAcceptsAny()) {
            if (MutationTypes.BLOCK_TO_BLOCK.equals(recipe.mutationType)) {
                List<Ingredient> list = new ArrayList<>();
                Ingredient ing = Ingredient.fromTag(recipe.tag);
                list.add(ing);
                ingredients.setInputIngredients(list);
                ingredients.setOutput(VanillaTypes.ITEM, recipe.itemOut);
            }
        }
        else {
            if (MutationTypes.BLOCK_TO_BLOCK.equals(recipe.mutationType)) {
                ingredients.setInput(VanillaTypes.ITEM, recipe.itemIn);
                ingredients.setOutput(VanillaTypes.ITEM, recipe.itemOut);
            }
        }
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, 135.0F));
    }

    @Override
    public List<String> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        double infoX = 63D;
        double infoY = 8D;
        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D){
            return Collections.singletonList(I18n.format("gui." + ResourcefulBees.MOD_ID + ".jei.category.mutation.info"));
        }
        return IRecipeCategory.super.getTooltipStrings(recipe,mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, BlockToBlock.Recipe recipe, IIngredients ingredients) {
        if (MutationTypes.BLOCK_TO_BLOCK.equals(recipe.mutationType)) {
            IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
            itemStacks.init(0, false, 65, 48);
            itemStacks.init(1, true, 15, 57);
            itemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
            itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));

            IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
            ingredientStacks.init(0, true, 16, 10);
            ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
        }
    }

    @Override
    public void draw(BlockToBlock.Recipe recipe, double mouseX, double mouseY) {
        this.beeHive.draw(65, 10);
        this.info.draw(63, 8);
    }

    public static class Recipe {
        private final ItemStack itemIn;
        private final ItemStack itemOut;
        private final String beeType;

        private final boolean acceptsAny;
        private final Tag<Item> tag;

        private final Enum<MutationTypes> mutationType;

        public Recipe(ItemStack baseBlock, ItemStack mutationBlock, String beeType, Enum<MutationTypes> type, boolean acceptsAny) {
            this.itemOut = mutationBlock;
            this.itemIn = baseBlock;
            this.beeType = beeType;
            this.mutationType = type;
            this.acceptsAny = acceptsAny;
            this.tag = null;
        }

        //TAGS!!!
        public Recipe(Tag<Item> baseBlock, ItemStack mutationBlock, String beeType, Enum<MutationTypes> type, boolean acceptsAny) {
            this.itemOut = mutationBlock;
            this.itemIn = null;
            this.beeType = beeType;
            this.mutationType = type;
            this.acceptsAny = acceptsAny;
            this.tag = baseBlock;
        }

        public boolean isAcceptsAny() { return acceptsAny; }
        public Tag<?> getTag() { return tag; }
        public String getBeeType() { return this.beeType; }
    }
}
