package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.tileentity.CentrifugeTileEntity;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CentrifugeRecipeCategory extends BaseCategory<CentrifugeRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "centrifuge");
    protected final IDrawableAnimated arrow;
    private final IDrawable fluidHider;
    private final IDrawable multiblock;

    private static final ResourceLocation BACKGROUND_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/centrifuge.png");

    public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("gui.resourcefulbees.jei.category.centrifuge"),
                guiHelper.createDrawable(BACKGROUND_IMAGE, 0, 0, 133, 65),
                guiHelper.createDrawableIngredient(new ItemStack(ModItems.CENTRIFUGE_ITEM.get())),
                CentrifugeRecipe.class);

        this.fluidHider = guiHelper.createDrawable(BACKGROUND_IMAGE, 9, 41, 18, 18);
        this.arrow = guiHelper.drawableBuilder(new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/centrifuge.png"), 0, 66, 73, 30)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        this.multiblock = guiHelper.createDrawable(new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png"), 25, 0, 16, 16);
    }

    @Override
    public void setIngredients(@NotNull CentrifugeRecipe recipe, @NotNull IIngredients iIngredients) {
/*        List<ItemOutput> outputs = recipe.getItemOutputs();
        List<FluidOutput> fluidOutputs = recipe.getFluidOutputs();

        iIngredients.setInputIngredients(Lists.newArrayList(recipe.getIngredient()));

        iIngredients.setOutputs(VanillaTypes.ITEM, outputs.stream().map(ItemOutput::getItemStack).collect(Collectors.toList()));
        iIngredients.setOutputs(VanillaTypes.FLUID, fluidOutputs.stream().map(FluidOutput::getFluidStack).collect(Collectors.toList()));*/
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull CentrifugeRecipe centrifugeRecipe, @NotNull IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = iRecipeLayout.getFluidStacks();

        guiItemStacks.init(CentrifugeTileEntity.HONEYCOMB_SLOT, true, 9, 5);
        guiItemStacks.set(CentrifugeTileEntity.HONEYCOMB_SLOT, iIngredients.getInputs(VanillaTypes.ITEM).get(0));


        if (!centrifugeRecipe.getItemOutputs().isEmpty()) {
            guiItemStacks.init(CentrifugeTileEntity.OUTPUT1, false, 108, 5);
            guiItemStacks.set(CentrifugeTileEntity.OUTPUT1, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
        }

        if (!centrifugeRecipe.getFluidOutputs().isEmpty()) {
            guiFluidStacks.init(CentrifugeTileEntity.OUTPUT1, false, 109, 6, 16, 16, iIngredients.getOutputs(VanillaTypes.FLUID).get(0).get(0).getAmount(), true, null);
            guiFluidStacks.set(CentrifugeTileEntity.OUTPUT1, iIngredients.getOutputs(VanillaTypes.FLUID).get(0));
        }

        guiItemStacks.init(CentrifugeTileEntity.OUTPUT2, false, 108, 23);
        guiItemStacks.set(CentrifugeTileEntity.OUTPUT2, iIngredients.getOutputs(VanillaTypes.ITEM).get(1));

    }

    @Override
    public void draw(@NotNull CentrifugeRecipe recipe, @NotNull MatrixStack matrix, double mouseX, double mouseY) {
        this.arrow.draw(matrix, 31, 14);

        //final double output1 = recipe.getItemOutputs().get(0).getChance();
        //final double output2 = recipe.getItemOutputs().get(1).getChance();
        //final double output3 = recipe.getItemOutputs().size() < 3 ? recipe.getFluidOutputs().get(1).getChance() : recipe.getItemOutputs().get(2).getChance();
        //final double fluid = recipe.getFluidOutputs().get(0).getChance();
//
//        DecimalFormat decimalFormat = new DecimalFormat("##%");
//        String honeyBottleString = decimalFormat.format(output3);
//        String beeOutputString = decimalFormat.format(output1);
//        String beeswaxString = decimalFormat.format(output2);
//        String fluidString = decimalFormat.format(fluid);
//
//        Minecraft minecraft = Minecraft.getInstance();
//        Font fontRenderer = minecraft.font;
//        int honeyBottleOffset = fontRenderer.width(honeyBottleString) / 2;
//        int beeOutputOffset = fontRenderer.width(beeOutputString) / 2;
//        int beeswaxOffset = fontRenderer.width(beeswaxString) / 2;
//        int fluidOffset = fontRenderer.width(fluidString) / 2;
//
//        if (output1 < 1.0)
//            fontRenderer.draw(matrix, beeOutputString, (float) 95 - beeOutputOffset, (float) 10, 0xff808080);
//        if (output2 < 1.0) fontRenderer.draw(matrix, beeswaxString, (float) 95 - beeswaxOffset, (float) 30, 0xff808080);
//        if (fluid < 1.0 && !(recipe.getItemOutputs().get(0).getItemStack().isEmpty()))
//            fontRenderer.draw(matrix, fluidString, (float) 95 - fluidOffset, (float) 46, 0xff808080);
//        if (output3 < 1.0)
//            fontRenderer.draw(matrix, honeyBottleString, (float) 45 - honeyBottleOffset, (float) 49, 0xff808080);
//        if (recipe.isMultiblock()) {
//            multiblock.draw(matrix, 10, 45);
//        }
//        if (recipe.getItemOutputs().get(0).getItemStack().isEmpty()) {
//            this.fluidHider.draw(matrix, 108, 41);
//        }
    }

    @Override
    public @NotNull List<ITextComponent> getTooltipStrings(@NotNull CentrifugeRecipe recipe, double mouseX, double mouseY) {
        if (mouseX >= 10 && mouseX <= 26 && mouseY >= 45 && mouseY <= 61) {
            return Collections.singletonList(new StringTextComponent("Multiblock only recipe."));
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }
}
