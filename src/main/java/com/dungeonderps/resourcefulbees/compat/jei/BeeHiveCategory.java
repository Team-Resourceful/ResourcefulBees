package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
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
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.dungeonderps.resourcefulbees.config.BeeInfo.BEE_INFO;

public class BeeHiveCategory implements IRecipeCategory<BeeHiveCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beehive.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "hive");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;
    private final CustomBeeEntity bee;

    public BeeHiveCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 25).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.IRON_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.hive");
        World clientWorld = Minecraft.getInstance().world;
        if (clientWorld != null)
            bee = RegistryHandler.CUSTOM_BEE.get().create(clientWorld);
        else
            bee = null;
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<? extends Recipe> getRecipeClass() {
        return Recipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return this.localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(Recipe recipe, IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getComb());
        ingredients.setInput(VanillaTypes.ITEM, new ItemStack(RegistryHandler.IRON_BEEHIVE_ITEM.get()));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(0, false, 138, 4);
        itemStacks.init(1, true, 62, 4);
        itemStacks.set(ingredients);
    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(@Nonnull Recipe recipe, double mouseX, double mouseY) {
        double beeX = 2D;
        double beeY = 2D;
        if (mouseX >= beeX && mouseX <= beeX + 30D && mouseY >= beeY && mouseY <= beeY + 30D){
            return Collections.singletonList(I18n.format("entity." + ResourcefulBees.MOD_ID + "." + recipe.beeType + "_bee"));
        }
        return IRecipeCategory.super.getTooltipStrings(recipe,mouseX, mouseY);
    }

    public void renderEntity(String beeType, Float rotation){
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
        entityrenderermanager.renderEntityStatic(bee, 1.75D, 0.1D, 0.0D, mc.getRenderPartialTicks(), 1, matrixstack, irendertypebuffer$impl, 15728880);

        irendertypebuffer$impl.finish();

        RenderSystem.popMatrix();
    }

    @Override
    public void draw(Recipe recipe, double mouseX, double mouseY) {
        renderEntity(recipe.getBeeType(), 135.0F);
    }

    public static List<Recipe> getHoneycombRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BEE_INFO.entrySet()){
            if (bee.getKey().equals(BeeConst.DEFAULT_BEE_TYPE) || bee.getKey().equals(BeeConst.DEFAULT_REMOVE)) { }
            else {
                ItemStack honeyCombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
                final CompoundNBT honeyCombItemStackTag = honeyCombItemStack.getOrCreateChildTag(BeeConst.NBT_ROOT);
                honeyCombItemStackTag.putString(BeeConst.NBT_COLOR, bee.getValue().getHoneycombColor());
                honeyCombItemStackTag.putString(BeeConst.NBT_BEE_TYPE, bee.getKey());
                recipes.add(new Recipe(honeyCombItemStack, bee.getKey()));
            }
        }
        return recipes;
    }

    public static class Recipe {
        private final ItemStack comb;
        private final String beeType;

        public Recipe(ItemStack comb, String beeType) {
            this.comb = comb;
            this.beeType = beeType;
        }

        public ItemStack getComb() {
            return this.comb;
        }
        public String getBeeType() {
            return this.beeType;
        }
    }
}