package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class HoneycombPage extends BeeDataPage {

    Button centrifuge;
    Button honeycombs;
    private int counter;
    private int max;

    private ItemStack hiveOutput;
    private ItemStack apiary1Output;
    private ItemStack apiary2Output;
    private ItemStack apiary3Output;
    private ItemStack apiary4Output;

    List<RecipeObject> recipes = new ArrayList<>();

    List<Item> hives = BeeInfoUtils.getItemTag("minecraft:beehives").values();
    ItemStack apiary1 = new ItemStack(ModItems.T1_APIARY_ITEM.get());
    ItemStack apiary2 = new ItemStack(ModItems.T2_APIARY_ITEM.get());
    ItemStack apiary3 = new ItemStack(ModItems.T3_APIARY_ITEM.get());
    ItemStack apiary4 = new ItemStack(ModItems.T4_APIARY_ITEM.get());

    ResourceLocation honeycombsImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/honeycombs.png");
    ResourceLocation centrifugeImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/centrifuge.png");

    Button leftArrow;
    Button rightArrow;

    int activePage = 0;

    public HoneycombPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        centrifuge = new Button(xPos + subPageWidth - 74, yPos, 70, 20, new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.honeycombs.centrifuge_button"), onPress -> {
            BeepediaScreen.currScreenState.setCentrifugeOpen(true);
            updateButtonVisibility();
        });
        honeycombs = new Button(xPos + subPageWidth - 74, yPos, 70, 20, new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.honeycombs.honeycombs_button"), onPress -> {
            BeepediaScreen.currScreenState.setCentrifugeOpen(false);
            updateButtonVisibility();
        });
        beepedia.addButton(honeycombs);
        beepedia.addButton(centrifuge);
        honeycombs.visible = false;
        centrifuge.visible = false;
        counter = 0;
        max = hives.size();

        int[] apiaryAmounts = beeData.getApiaryOutputAmounts();
        if (apiaryAmounts == null)
            apiaryAmounts = new int[]{Config.T1_APIARY_QUANTITY.get(), Config.T2_APIARY_QUANTITY.get(), Config.T3_APIARY_QUANTITY.get(), Config.T4_APIARY_QUANTITY.get()};
        hiveOutput = new ItemStack(beeData.getCombRegistryObject().get(), 1);
        apiary1Output = new ItemStack(beeData.getCombRegistryObject().get(), apiaryAmounts[0]);
        apiary2Output = new ItemStack(beeData.getCombRegistryObject().get(), apiaryAmounts[1]);
        apiary3Output = new ItemStack(beeData.getCombBlockItemRegistryObject().get(), apiaryAmounts[2]);
        apiary4Output = new ItemStack(beeData.getCombBlockItemRegistryObject().get(), apiaryAmounts[3]);

        ClientWorld world = beepedia.getMinecraft().world;
        recipes.add(new RecipeObject(true, true, beeData, world));
        recipes.add(new RecipeObject(false, true, beeData, world));
        recipes.add(new RecipeObject(true, false, beeData, world));
        recipes.add(new RecipeObject(false, false, beeData, world));
        recipes.removeIf(b -> b.recipe == null);

        leftArrow = new ImageButton(xPos + (subPageWidth / 2) - 28, yPos + subPageHeight - 16, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevPage());
        rightArrow = new ImageButton(xPos + (subPageWidth / 2) + 20, yPos + subPageHeight - 16, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextPage());
        beepedia.addButton(leftArrow);
        beepedia.addButton(rightArrow);
        leftArrow.visible = false;
        rightArrow.visible = false;
    }

    private void nextPage() {
        activePage++;
        if (activePage >= recipes.size()) activePage = 0;
        BeepediaScreen.currScreenState.setCentrifugePage(activePage);
    }

    private void prevPage() {
        activePage--;
        if (activePage < 0) activePage = recipes.size() - 1;
        BeepediaScreen.currScreenState.setCentrifugePage(activePage);
    }

    @Override
    public void openPage() {
        super.openPage();
        updateButtonVisibility();
        activePage = BeepediaScreen.currScreenState.getCentrifugePage();
        if (activePage >= recipes.size()) activePage = 0;
        BeepediaScreen.currScreenState.setBreedingPage(activePage);
    }

    private void updateButtonVisibility() {
        honeycombs.visible = BeepediaScreen.currScreenState.isCentrifugeOpen() && !recipes.isEmpty();
        centrifuge.visible = !BeepediaScreen.currScreenState.isCentrifugeOpen() && !recipes.isEmpty();
        rightArrow.visible = BeepediaScreen.currScreenState.isCentrifugeOpen() && !recipes.isEmpty();
        leftArrow.visible = BeepediaScreen.currScreenState.isCentrifugeOpen() && !recipes.isEmpty();
    }

    @Override
    public void closePage() {
        super.closePage();
        honeycombs.visible = false;
        centrifuge.visible = false;
        rightArrow.visible = false;
        leftArrow.visible = false;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        String title = BeepediaScreen.currScreenState.isCentrifugeOpen() ? "gui.resourcefulbees.beepedia.bee_subtab.centrifuge" : "gui.resourcefulbees.beepedia.bee_subtab.honeycombs";
        font.draw(matrix, new TranslationTextComponent(title), xPos, (float) yPos + 8, TextFormatting.WHITE.getColor());
        if (BeepediaScreen.currScreenState.isCentrifugeOpen() && !recipes.isEmpty()) {
            manager.bindTexture(centrifugeImage);
            AbstractGui.drawTexture(matrix, xPos, yPos + 22, 0, 0, 169, 84, 169, 84);
            recipes.get(activePage).draw(matrix, xPos, yPos + 22);
            if (recipes.size() > 1) {
                StringTextComponent page = new StringTextComponent(String.format("%d / %d", activePage + 1, recipes.size()));
                int padding = font.getWidth(page) / 2;
                font.draw(matrix, page, xPos + (subPageWidth / 2) - padding, (float) yPos + subPageHeight - 14, TextFormatting.WHITE.getColor());
            }
        } else {
            manager.bindTexture(honeycombsImage);
            AbstractGui.drawTexture(matrix, xPos, yPos + 22, 0, 0, 169, 84, 169, 84);
            beepedia.drawSlot(matrix, hives.get(counter), xPos + 14, yPos + 23);
            beepedia.drawSlot(matrix, apiary1, xPos + 43, yPos + 23);
            beepedia.drawSlot(matrix, apiary2, xPos + 72, yPos + 23);
            beepedia.drawSlot(matrix, apiary3, xPos + 101, yPos + 23);
            beepedia.drawSlot(matrix, apiary4, xPos + 130, yPos + 23);

            beepedia.drawSlot(matrix, hiveOutput, xPos + 14, yPos + 82);
            beepedia.drawSlot(matrix, apiary1Output, xPos + 43, yPos + 82);
            beepedia.drawSlot(matrix, apiary2Output, xPos + 72, yPos + 82);
            beepedia.drawSlot(matrix, apiary3Output, xPos + 101, yPos + 82);
            beepedia.drawSlot(matrix, apiary4Output, xPos + 130, yPos + 82);
        }
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !BeeInfoUtils.isShiftPressed()) {
            counter++;
            if (counter >= max) counter = 0;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!recipes.isEmpty()) {
            if (recipes.get(activePage).onMouseClick(mouseX, mouseY, mouseButton)) return true;
        }
        return false;
    }

    private class RecipeObject {
        boolean isBlock;
        boolean hasBottle;
        ItemStack inputItem;
        ItemStack bottleItem = ItemStack.EMPTY;
        List<Pair<ItemStack, Float>> outputItems;
        List<Pair<FluidStack, Float>> outputFluids;
        CentrifugeRecipe recipe;
        CustomBeeData beeData;

        public RecipeObject(boolean isBlock, boolean hasBottle, CustomBeeData beeData, ClientWorld world) {
            this.isBlock = isBlock;
            this.hasBottle = hasBottle;
            this.beeData = beeData;

            if (isBlock) {
                inputItem = beeData.getCombBlockItemStack();
                if (hasBottle) {
                    bottleItem = new ItemStack(Items.GLASS_BOTTLE, 9);
                }
            } else {
                inputItem = beeData.getCombStack();
                if (hasBottle) {
                    bottleItem = new ItemStack(Items.GLASS_BOTTLE);
                }
            }
            Inventory inventory = new Inventory(inputItem, bottleItem);
            recipe = world.getRecipeManager().getRecipe(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, inventory, world).orElse(null);
            if (recipe != null) {
                outputItems = recipe.itemOutputs;
                outputFluids = recipe.fluidOutput;
            }
        }

        public void draw(MatrixStack matrix, int xPos, int yPos) {
            beepedia.drawSlot(matrix, inputItem, xPos + 25, yPos + 3);
            if (bottleItem.isEmpty()) beepedia.drawEmptySlot(matrix, xPos + 25, yPos + 23);
            else beepedia.drawSlot(matrix, bottleItem, xPos + 25, yPos + 23);
            if (recipe.hasFluidOutput) {
                if (outputItems.get(0).getLeft().isEmpty()) {
                    beepedia.drawFluidSlot(matrix, outputFluids.get(0).getLeft(), xPos + 124, yPos + 3);
                    drawWeight(matrix, outputFluids.get(0).getRight(), xPos + 112, yPos + 9);
                } else {
                    beepedia.drawFluidSlot(matrix, outputFluids.get(0).getLeft(), xPos + 124, yPos + 43);
                    drawWeight(matrix, outputFluids.get(0).getRight(), xPos + 112, yPos + 49);
                    beepedia.drawSlot(matrix, outputItems.get(0).getLeft(), xPos + 124, yPos + 3);
                    drawWeight(matrix, outputItems.get(0).getRight(), xPos + 112, yPos + 9);
                }
            } else {
                beepedia.drawSlot(matrix, outputItems.get(0).getLeft(), xPos + 124, yPos + 3);
                drawWeight(matrix, outputItems.get(0).getRight(), xPos + 112, yPos + 9);
            }
            if (hasBottle) {
                beepedia.drawSlot(matrix, outputItems.get(2).getLeft(), xPos + 75, yPos + 43);
                drawWeight(matrix, outputItems.get(2).getRight(), xPos + 64, yPos + 49);
            } else {
                beepedia.drawFluidSlot(matrix, outputFluids.get(1).getLeft(), xPos + 75, yPos + 43);
                drawWeight(matrix, outputFluids.get(1).getRight(), xPos + 64, yPos + 49);
            }
            beepedia.drawSlot(matrix, outputItems.get(1).getLeft(), xPos + 124, yPos + 23);
            drawWeight(matrix, outputItems.get(1).getRight(), xPos + 112, yPos + 30);
        }

        private void drawWeight(MatrixStack matrix, Float right, int xPos, int yPos) {
            if (right == 1) return;
            FontRenderer font = Minecraft.getInstance().fontRenderer;
            DecimalFormat decimalFormat = new DecimalFormat("##%");
            StringTextComponent text = new StringTextComponent(decimalFormat.format(right));
            int padding = font.getWidth(text) / 2;
            font.draw(matrix, text, xPos - padding, yPos, TextFormatting.GRAY.getColor());
        }

        public boolean onMouseClick(double mouseX, double mouseY, int button) {
            return false;
        }
    }
}
