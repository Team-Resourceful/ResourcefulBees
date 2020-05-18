package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.CentrifugeBlockEntity;
import com.google.common.collect.Lists;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CentrifugeRecipeCategory implements IRecipeCategory<CentrifugeRecipe> {

  public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "centrifuge");

  private final IDrawable icon;
  private final IDrawable background;
  private final String localizedName;
  protected final IDrawableAnimated arrow;

  public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
    this.background = guiHelper.createDrawable(new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/centrifuge.png"), 0, 0, 133, 65);
    this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.CENTRIFUGE_ITEM.get()));
    this.localizedName = I18n.format("gui.resourcefulbees.jei.category.centrifuge");
    this.arrow = guiHelper.drawableBuilder(new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/centrifuge.png"), 0, 66, 73, 30)
            .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
  }

  @Override
  public ResourceLocation getUid() {
    return ID;
  }

  @Override
  public Class<? extends CentrifugeRecipe> getRecipeClass() {
    return CentrifugeRecipe.class;
  }

  @Override
  public String getTitle() {
    return localizedName;
  }

  @Override
  public IDrawable getBackground() {
    return background;
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public void setIngredients(CentrifugeRecipe recipe, IIngredients iIngredients) {
    iIngredients.setInputIngredients(Lists.newArrayList(recipe.ingredient, Ingredient.fromItems(Items.GLASS_BOTTLE)));
    List<Pair<ItemStack,Double>> outputs = recipe.outputs;
    List<ItemStack> stacks = new ArrayList<>();
    stacks.add(new ItemStack(Items.HONEY_BOTTLE));
    stacks.addAll(outputs.stream().map(Pair::getLeft).collect(Collectors.toList()));
      iIngredients.setOutputs(VanillaTypes.ITEM, stacks);
  }

  @Override
  public void setRecipe(IRecipeLayout iRecipeLayout, CentrifugeRecipe centrifugeRecipe, IIngredients iIngredients) {
    IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();
    guiItemStacks.init(CentrifugeBlockEntity.HONEYCOMB_SLOT, true, 9, 5);
    guiItemStacks.init(CentrifugeBlockEntity.BOTTLE_SLOT, true, 9, 23);
    guiItemStacks.init(CentrifugeBlockEntity.HONEY_BOTTLE, false, 59, 44);
    guiItemStacks.init(CentrifugeBlockEntity.OUTPUT1, false, 108, 5);
    guiItemStacks.init(CentrifugeBlockEntity.OUTPUT2, false, 108, 23);
    guiItemStacks.set(iIngredients);
  }

  @Override
  public void draw(CentrifugeRecipe recipe, double mouseX, double mouseY) {
    this.arrow.draw(31, 14);

    final double beeOutput = recipe.outputs.get(0).getRight();
    final double beeswax = recipe.outputs.get(1).getRight();
    final double honeyBottle = recipe.outputs.get(2).getRight();

    DecimalFormat decimalFormat = new DecimalFormat("##%");

    String honeyBottleString = decimalFormat.format(honeyBottle);
    String beeOutputString = decimalFormat.format(beeOutput);
    String beeswaxString = decimalFormat.format(beeswax);

    Minecraft minecraft = Minecraft.getInstance();
    FontRenderer fontRenderer = minecraft.fontRenderer;
    if (beeOutput < 1.0) fontRenderer.drawString(beeOutputString, 80, 10, 0xff808080);
    if (honeyBottle < 1.0) fontRenderer.drawString(honeyBottleString, 80, 50, 0xff808080);
    if (beeswax < 1.0) fontRenderer.drawString(beeswaxString, 80, 30, 0xff808080);
  }
}
