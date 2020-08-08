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
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
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
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("NullableProblems")
public class BlockToFluid implements IRecipeCategory<BlockToFluid.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ICONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "block_to_fluid_mutation");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable info;
    private final IDrawable beeHive;
    private final String localizedName;
    private final CustomBeeEntity bee;

    public BlockToFluid(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawable(ICONS, 0,0,16,16);
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.beeHive = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.T1_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.block_to_fluid_mutation");
        assert Minecraft.getInstance().world != null;
        bee = RegistryHandler.CUSTOM_BEE.get().create(Minecraft.getInstance().world);
    }

    public static List<BlockToFluid.Recipe> getMutationRecipes(IIngredientManager ingredientManager) {
        List<BlockToFluid.Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()){
            if (bee.getValue().hasMutation()) {

                String mutationIn = bee.getValue().getMutationInput();
                String mutationOut = bee.getValue().getMutationOutput();

                if (BeeInfoUtils.TAG_RESOURCE_PATTERN.matcher(mutationIn).matches()) {
                    mutationIn = mutationIn.replace(BeeConstants.TAG_PREFIX, "");

                    Tag<Item> itemTag = BeeInfoUtils.getItemTag(mutationIn);
                    if (itemTag !=null) {
                        Fluid fluidOut = BeeInfoUtils.getFluid(mutationOut);
                        if (BeeInfoUtils.isValidFluid(fluidOut)){
                            recipes.add(new Recipe(itemTag, new FluidStack(fluidOut,1000), bee.getKey(), MutationTypes.BLOCK_TO_FLUID, true));
                        }
                    }
                } else {
                    Enum<MutationTypes> mutationType = bee.getValue().getMutationType();

                    if (MutationTypes.BLOCK_TO_FLUID.equals(mutationType)) {
                        Item itemIn = BeeInfoUtils.getItem(mutationIn);
                        Fluid fluidOut = BeeInfoUtils.getFluid(mutationOut);
                        if (BeeInfoUtils.isValidItem(itemIn) && BeeInfoUtils.isValidFluid(fluidOut))
                            recipes.add( new Recipe( new ItemStack(itemIn), new FluidStack(fluidOut, 1000), bee.getKey(), mutationType, false));
                    }
                } //END INDIVIDUAL CHECKS
            }
        }
        return recipes;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends BlockToFluid.Recipe> getRecipeClass() {
        return BlockToFluid.Recipe.class;
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
    public void setIngredients(BlockToFluid.Recipe recipe, IIngredients ingredients) {
        if (recipe.isAcceptsAny()) {
            if (MutationTypes.BLOCK_TO_FLUID.equals(recipe.mutationType)) {
                List<Ingredient> list = new ArrayList<>();
                Ingredient ing = Ingredient.fromTag(recipe.tag);
                list.add(ing);
                ingredients.setInputIngredients(list);
                ingredients.setOutput(VanillaTypes.FLUID, recipe.fluidOut);
            }
        }
        else {
            if (MutationTypes.BLOCK_TO_FLUID.equals(recipe.mutationType)) {
                ingredients.setInput(VanillaTypes.ITEM, recipe.itemIn);
                ingredients.setOutput(VanillaTypes.FLUID, recipe.fluidOut);
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
    public void setRecipe(IRecipeLayout iRecipeLayout, BlockToFluid.Recipe recipe, IIngredients ingredients) {
        if (MutationTypes.BLOCK_TO_FLUID.equals(recipe.mutationType)) {
            List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
            IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
            itemStacks.init(0, true, 15, 57);
            itemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

            IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
            fluidStacks.init(0,false,66,49);
            fluidStacks.set(0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));

            IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
            ingredientStacks.init(0, true, 16, 10);
            ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
        }
    }

    @Override
    public void draw(BlockToFluid.Recipe recipe, double mouseX, double mouseY) {
        this.beeHive.draw(65, 10);
        this.info.draw(63, 8);
    }

    public static class Recipe {
        private final FluidStack fluidOut;
        private final ItemStack itemIn;
        private final String beeType;

        private final boolean acceptsAny;
        private final Tag<Item> tag;

        private final Enum<MutationTypes> mutationType;

        public Recipe(ItemStack baseBlock, FluidStack mutationBlock, String beeType, Enum<MutationTypes> type, boolean acceptsAny) {
            this.fluidOut = mutationBlock;
            this.itemIn = baseBlock;
            this.beeType = beeType;
            this.mutationType = type;
            this.acceptsAny = acceptsAny;
            this.tag = null;
        }

        //TAGS!!!
        public Recipe(Tag<Item> baseBlock, FluidStack mutationBlock, String beeType, Enum<MutationTypes> type, boolean acceptsAny) {
            this.fluidOut = mutationBlock;
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
