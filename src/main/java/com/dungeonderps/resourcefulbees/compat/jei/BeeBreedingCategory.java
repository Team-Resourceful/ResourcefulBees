package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BeeBreedingCategory implements IRecipeCategory<BeeBreedingCategory.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/breeding.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "breeding");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;
    private final CustomBeeEntity bee;

    public BeeBreedingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 30).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.GOLD_FLOWER.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.breeding");
        World clientWorld = Minecraft.getInstance().world;
        if (clientWorld != null)
            bee = RegistryHandler.CUSTOM_BEE.get().create(clientWorld);
        else
            bee = null;    }

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
    public void setIngredients(@Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, @Nonnull IIngredients ingredients) {
    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(@Nonnull Recipe recipe, double mouseX, double mouseY) {
        double beeX = 2D;
        double beeY = 0D;
        double bee2X = 52D;
        double bee2Y = 0D;
        double bee3X = 124D;
        double bee3Y = 0D;
        if (mouseX >= beeX && mouseX <= beeX + 30D && mouseY >= beeY && mouseY <= beeY + 30D){
            return Collections.singletonList(I18n.format("entity." + ResourcefulBees.MOD_ID + "." + recipe.parent1 + "_bee"));
        }
        if (mouseX >= bee2X && mouseX <= bee2X + 30D && mouseY >= bee2Y && mouseY <= bee2Y + 30D){
            return Collections.singletonList(I18n.format("entity." + ResourcefulBees.MOD_ID + "." + recipe.parent2 + "_bee"));
        }
        if (mouseX >= bee3X && mouseX <= bee3X + 30D && mouseY >= bee3Y && mouseY <= bee3Y + 30D){
            return Collections.singletonList(I18n.format("entity." + ResourcefulBees.MOD_ID + "." + recipe.child + "_bee"));
        }
        return IRecipeCategory.super.getTooltipStrings(recipe,mouseX, mouseY);
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
    public void draw(Recipe recipe, double mouseX, double mouseY) {
        renderEntity(recipe.getParent1(), 135.0F, 1.75D, 0.1D);
        renderEntity(recipe.getParent2(), -135.0F, 0.05D, 0.1D);
        renderEntity(recipe.getChild(), 135.0F, -2.3D, 0.1D);
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontRenderer = minecraft.fontRenderer;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        fontRenderer.drawString(decimalFormat.format(BeeInfo.getInfo(recipe.getChild()).getBreedWeight()), 90, 18, 0xff808080);
    }

    public static List<Recipe> getBreedingRecipes(IIngredientManager ingredientManager) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()){
            if (bee.getKey().equals(BeeConst.DEFAULT_BEE_TYPE) || bee.getKey().equals(BeeConst.DEFAULT_REMOVE)) { }
            else {
                if (bee.getValue().isBreedable()){
                    if (BeeInfo.BEE_INFO.containsKey(bee.getValue().getParent1()) && BeeInfo.BEE_INFO.containsKey(bee.getValue().getParent2()) && BeeInfo.BEE_INFO.containsKey(bee.getKey()))
                        recipes.add(new Recipe(bee.getValue().getParent1(), bee.getValue().getParent2(), bee.getKey()));
                }
            }
        }
        return recipes;
    }
    public static class Recipe {
        private final String parent1;
        private final String parent2;
        private final String child;

        public Recipe(String parent1, String parent2, String child) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.child = child;
        }

        public String getParent1() {
            return this.parent1;
        }

        public String getParent2() {
            return this.parent2;
        }
        public String getChild() {
            return this.child;
        }
    }
}
