package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.lib.MutationTypes;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

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
    }

    @Override
    public List<String> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        double infoX = 63D;
        double infoY = 8D;
        double beeX = 10D;
        double beeY = 6D;
        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D){
            return Collections.singletonList(I18n.format("gui." + ResourcefulBees.MOD_ID + ".jei.category.mutation.info"));
        }
        if (mouseX >= beeX && mouseX <= beeX + 30D && mouseY >= beeY && mouseY <= beeY + 30D){
            return Collections.singletonList(I18n.format("entity." + ResourcefulBees.MOD_ID + "." + recipe.beeType + "_bee"));
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

        }
    }

    public void renderEntity(String beeType, Float rotation, Double xPos, Double yPos){
        RenderSystem.pushMatrix();

        RenderSystem.translatef(70, 24, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);

        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale(30, 30, 30);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        matrixstack.rotate(quaternion);

        Minecraft mc = Minecraft.getInstance();
        EntityRendererManager entityrenderermanager = mc.getRenderManager();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = mc.getRenderTypeBuffers().getBufferSource();

        assert mc.player != null;
        bee.ticksExisted = mc.player.ticksExisted;
        bee.renderYawOffset = rotation;
        bee.setBeeType(beeType);
        entityrenderermanager.renderEntityStatic(bee, xPos, yPos, 0.0D, mc.getRenderPartialTicks(), 1, matrixstack, irendertypebuffer$impl, 15728880);

        irendertypebuffer$impl.finish();

        RenderSystem.popMatrix();
    }

    @Override
    public void draw(BlockToFluid.Recipe recipe, double mouseX, double mouseY) {
        this.beeHive.draw(65, 10);
        this.info.draw(63, 8);
        renderEntity(recipe.beeType, 135.0F, 1.5D, -0.2D);
    }

    public static List<BlockToFluid.Recipe> getMutationRecipes(IIngredientManager ingredientManager) {
        List<BlockToFluid.Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()){
            if (bee.getValue().hasMutation()) {

                String mutationIn = bee.getValue().getMutationInput();
                String mutationOut = bee.getValue().getMutationOutput();

                if (BeeInfoUtils.TAG_RESOURCE_PATTERN.matcher(mutationIn).matches()) {
                    mutationIn = mutationIn.replace(BeeConst.TAG_PREFIX, "");

                    Tag<Item> itemTag = ItemTags.getCollection().get(BeeInfoUtils.getResource(mutationIn));
                    if (itemTag !=null) {
                        Fluid fluidOut = ForgeRegistries.FLUIDS.getValue(BeeInfoUtils.getResource(mutationOut));
                        if (BeeInfoUtils.isValidFluid(fluidOut)){
                            recipes.add(new Recipe(itemTag, new FluidStack(fluidOut,1000), bee.getKey(), MutationTypes.BLOCK_TO_FLUID, true));
                        }
                    }
                } else {
                    Enum<MutationTypes> mutationType = bee.getValue().getMutationType();

                    if (MutationTypes.BLOCK_TO_FLUID.equals(mutationType)) {
                        Item itemIn = ForgeRegistries.ITEMS.getValue(BeeInfoUtils.getResource(mutationIn));
                        Fluid fluidOut = ForgeRegistries.FLUIDS.getValue(BeeInfoUtils.getResource(mutationOut));
                        if (BeeInfoUtils.isValidItem(itemIn) && BeeInfoUtils.isValidFluid(fluidOut))
                            recipes.add( new Recipe( new ItemStack(itemIn), new FluidStack(fluidOut, 1000), bee.getKey(), mutationType, false));
                    }
                } //END INDIVIDUAL CHECKS
            }
        }
        return recipes;
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
