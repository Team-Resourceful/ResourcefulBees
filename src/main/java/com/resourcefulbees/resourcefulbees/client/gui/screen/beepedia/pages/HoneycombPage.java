package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.item.CustomHoneyBottleItem;
import com.resourcefulbees.resourcefulbees.lib.ApiaryOutput;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
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
import java.util.function.Supplier;


public class HoneycombPage extends BeeDataPage {

    Button prevTab;
    Button nextTab;
    private int counter;
    private final int max;

    private final ItemStack hiveOutput;
    private final ItemStack apiary1Output;
    private final ItemStack apiary2Output;
    private final ItemStack apiary3Output;
    private final ItemStack apiary4Output;

    List<RecipeObject> recipes = new ArrayList<>();

    List<Item> hives = BeeInfoUtils.getItemTag("minecraft:beehives").getValues();
    ItemStack apiary1 = new ItemStack(ModItems.T1_APIARY_ITEM.get());
    ItemStack apiary2 = new ItemStack(ModItems.T2_APIARY_ITEM.get());
    ItemStack apiary3 = new ItemStack(ModItems.T3_APIARY_ITEM.get());
    ItemStack apiary4 = new ItemStack(ModItems.T4_APIARY_ITEM.get());

    public static final ResourceLocation HONEYCOMBS_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/honeycombs.png");
    public static final ResourceLocation CENTRIFUGE_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/centrifuge.png");
    public static final ResourceLocation MULTIBLOCK_ONLY_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/multiblock_only.png");

    Button leftArrow;
    Button rightArrow;

    int activePage = 0;


    public HoneycombPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        prevTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 48, yPos + 6, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> toggleTab());
        nextTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 40, yPos + 6, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> toggleTab());
        beepedia.addButton(nextTab);
        beepedia.addButton(prevTab);
        nextTab.visible = false;
        prevTab.visible = false;
        counter = 0;
        max = hives.size();

        int[] apiaryAmounts = beeData.getApiaryOutputAmounts();
        if (apiaryAmounts == null)
            apiaryAmounts = new int[]{Config.T1_APIARY_QUANTITY.get(), Config.T2_APIARY_QUANTITY.get(), Config.T3_APIARY_QUANTITY.get(), Config.T4_APIARY_QUANTITY.get()};
        hiveOutput = new ItemStack(beeData.getCombRegistryObject().get(), 1);
        apiary1Output = new ItemStack(beeData.getApiaryOutputsTypes()[0] == ApiaryOutput.COMB ? beeData.getCombRegistryObject().get() : beeData.getCombBlockItemRegistryObject().get(), apiaryAmounts[0]);
        apiary2Output = new ItemStack(beeData.getApiaryOutputsTypes()[1] == ApiaryOutput.COMB ? beeData.getCombRegistryObject().get() : beeData.getCombBlockItemRegistryObject().get(), apiaryAmounts[1]);
        apiary3Output = new ItemStack(beeData.getApiaryOutputsTypes()[2] == ApiaryOutput.COMB ? beeData.getCombRegistryObject().get() : beeData.getCombBlockItemRegistryObject().get(), apiaryAmounts[2]);
        apiary4Output = new ItemStack(beeData.getApiaryOutputsTypes()[3] == ApiaryOutput.COMB ? beeData.getCombRegistryObject().get() : beeData.getCombBlockItemRegistryObject().get(), apiaryAmounts[3]);

        ClientWorld world = beepedia.getMinecraft().level;
        recipes.add(new RecipeObject(false, true, beeData, world, beepedia));
        recipes.add(new RecipeObject(false, false, beeData, world, beepedia));
        recipes.add(new RecipeObject(true, true, beeData, world, beepedia));
        recipes.add(new RecipeObject(true, false, beeData, world, beepedia));
        recipes.removeIf(b -> b.recipe == null);

        leftArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 28, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevPage());
        rightArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 20, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextPage());
        beepedia.addButton(leftArrow);
        beepedia.addButton(rightArrow);
        leftArrow.visible = false;
        rightArrow.visible = false;
    }

    private void toggleTab() {
        BeepediaScreen.currScreenState.setCentrifugeOpen(!BeepediaScreen.currScreenState.isCentrifugeOpen());
        updateButtonVisibility();
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
        nextTab.visible = !recipes.isEmpty();
        prevTab.visible = !recipes.isEmpty();
        rightArrow.visible = BeepediaScreen.currScreenState.isCentrifugeOpen() && !recipes.isEmpty();
        leftArrow.visible = BeepediaScreen.currScreenState.isCentrifugeOpen() && !recipes.isEmpty();
    }

    @Override
    public void closePage() {
        super.closePage();
        nextTab.visible = false;
        prevTab.visible = false;
        rightArrow.visible = false;
        leftArrow.visible = false;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().font;
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        TranslationTextComponent title = new TranslationTextComponent(BeepediaScreen.currScreenState.isCentrifugeOpen() ? "gui.resourcefulbees.beepedia.bee_subtab.centrifuge" : "gui.resourcefulbees.beepedia.bee_subtab.honeycombs");
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
        if (BeepediaScreen.currScreenState.isCentrifugeOpen() && !recipes.isEmpty()) {
            recipes.get(activePage).draw(matrix, xPos, yPos + 22, mouseX, mouseY);
            if (recipes.size() > 1) {
                StringTextComponent page = new StringTextComponent(String.format("%d / %d", activePage + 1, recipes.size()));
                padding = font.width(page) / 2;
                font.draw(matrix, page.withStyle(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + SUB_PAGE_HEIGHT - 14, -1);
            }
        } else {
            manager.bind(HONEYCOMBS_IMAGE);
            AbstractGui.blit(matrix, xPos, yPos + 22, 0, 0, 169, 84, 169, 84);
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
    public String getSearch() {
        String search = "";
        for (RecipeObject recipe : recipes) {
            for (Pair<FluidStack, Float> outputFluid : recipe.outputFluids) {
                search = String.format("%s %s", search, outputFluid.getLeft().getDisplayName().getString());
            }
            for (Pair<ItemStack, Float> outputItem : recipe.outputItems) {
                search = String.format("%s %s", search, outputItem.getLeft().getDisplayName().getString());
            }
        }
        return search;
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !BeeInfoUtils.isShiftPressed()) {
            counter++;
            if (counter >= max) counter = 0;
        }
    }

    @Override
    public void drawTooltips(MatrixStack matrix, int mouseX, int mouseY) {
        if (!recipes.isEmpty()) recipes.get(activePage).drawTooltip(matrix, xPos, yPos, mouseX, mouseY);
    }



    public static class RecipeObject {
        public boolean isBlock;
        public boolean hasBottle;
        public ItemStack inputItem;
        public ItemStack bottleItem = ItemStack.EMPTY;
        public List<Pair<ItemStack, Float>> outputItems;
        public List<Pair<FluidStack, Float>> outputFluids;
        public CentrifugeRecipe recipe;
        public CustomBeeData beeData;
        public BeepediaScreen beepedia;

        public RecipeObject(boolean isBlock, boolean hasBottle, CustomBeeData beeData, ClientWorld world, BeepediaScreen beepedia) {
            this.isBlock = isBlock;
            this.hasBottle = hasBottle;
            this.beeData = beeData;
            this.beepedia = beepedia;
            setBottles();
            if (isBlock) {
                inputItem = beeData.getCombBlockItemStack();
            } else {
                inputItem = beeData.getCombStack();
            }
            Inventory inventory = new Inventory(inputItem, bottleItem);
            recipe = world.getRecipeManager().getRecipeFor(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, inventory, world).orElse(null);
            if (recipe != null) {
                outputItems = recipe.itemOutputs;
                outputFluids = recipe.fluidOutput;
            }
        }

        public RecipeObject(CentrifugeRecipe recipe, BeepediaScreen beepedia) {
            this.beepedia = beepedia;
            this.recipe = recipe;
            inputItem = recipe.ingredient.getItems()[0];
            outputItems = recipe.itemOutputs;
            outputFluids = recipe.fluidOutput;
            isBlock = recipe.multiblock;
            hasBottle = !recipe.noBottleInput;
            setBottles();
        }

        private void setBottles() {
            if (hasBottle) {
                bottleItem = isBlock ? new ItemStack(Items.GLASS_BOTTLE, 9) : new ItemStack(Items.GLASS_BOTTLE);
            }
        }

        public void draw(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY) {

            // draw background
            Minecraft.getInstance().textureManager.bind(CENTRIFUGE_IMAGE);
            AbstractGui.blit(matrix, xPos, yPos, 0, 0, 169, 84, 169, 84);

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
            beepedia.drawSlot(matrix, outputItems.get(1).getLeft(), xPos + 124, yPos + 23);
            drawWeight(matrix, outputItems.get(1).getRight(), xPos + 112, yPos + 30);
            if (isBlock || Config.MULTIBLOCK_RECIPES_ONLY.get()) {
                Minecraft.getInstance().getTextureManager().bind(MULTIBLOCK_ONLY_IMAGE);
                AbstractGui.blit(matrix, xPos + 28, yPos + 45, 0, 0, 16, 16, 16, 16);
            }
            if (hasBottle) {
                drawHoneyBottle(matrix, xPos, yPos, mouseX, mouseY);
            } else {
                drawHoneyFluid(matrix, xPos, yPos, mouseX, mouseY);
            }
        }

        private void drawHoneyBottle(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
            Item bottle = outputItems.get(2).getLeft().getItem();
            String pageID = null;
            if (bottle == Items.HONEY_BOTTLE) {
                pageID = "honey";
            } else if (bottle instanceof CustomHoneyBottleItem) {
                pageID = ((CustomHoneyBottleItem) bottle).honeyBottleData.getName();
            }

            if (pageID != null) {
                String finalPageID = pageID;
                Supplier<Boolean> supplier = () -> {
                    BeepediaScreen.saveScreenState();
                    beepedia.setActive(BeepediaScreen.PageType.HONEY, finalPageID);
                    return true;
                };
                beepedia.drawInteractiveSlot(matrix, outputItems.get(2).getLeft(), xPos + 75, yPos + 43, mouseX, mouseY, supplier);
            } else {
                beepedia.drawSlot(matrix, outputItems.get(2).getLeft(), xPos + 75, yPos + 43);
            }
            drawWeight(matrix, outputItems.get(2).getRight(), xPos + 64, yPos + 49);
        }

        private void drawHoneyFluid(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
            FluidStack fluidStack = outputFluids.get(1).getLeft();
            String pageID = null;
            if (fluidStack.getFluid() == ModFluids.HONEY_STILL.get()) {
                pageID = "honey";
            } else if (fluidStack.getFluid() instanceof HoneyFlowingFluid) {
                HoneyFlowingFluid fluid = (HoneyFlowingFluid) fluidStack.getFluid();
                pageID = fluid.getHoneyData().getName();
            }

            if (pageID != null) {
                String finalPageID = pageID;
                Supplier<Boolean> supplier = () -> {
                    BeepediaScreen.saveScreenState();
                    beepedia.setActive(BeepediaScreen.PageType.HONEY, finalPageID);
                    return true;
                };
                beepedia.drawInteractiveFluidSlot(matrix, outputFluids.get(1).getLeft(), xPos + 75, yPos + 43, mouseX, mouseY, supplier);
            } else {
                beepedia.drawFluidSlot(matrix, outputFluids.get(1).getLeft(), xPos + 75, yPos + 43);
            }
            drawWeight(matrix, outputFluids.get(1).getRight(), xPos + 64, yPos + 49);
        }

        private void drawWeight(MatrixStack matrix, Float right, int xPos, int yPos) {
            if (right == 1) return;
            FontRenderer font = Minecraft.getInstance().font;
            DecimalFormat decimalFormat = new DecimalFormat("##%");
            StringTextComponent text = new StringTextComponent(decimalFormat.format(right));
            int padding = font.width(text) / 2;
            font.draw(matrix, text.withStyle(TextFormatting.GRAY), (float) xPos - padding, yPos, -1);
        }

        public void drawTooltip(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
            if (BeepediaScreen.mouseHovering((float) xPos + 28, (float) yPos + 67, 20, 20, mouseX, mouseY)) {
                beepedia.renderTooltip(matrix, new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.centrifuge.requires_multiblock"), mouseX, mouseY);
            }
        }

        public boolean mouseClick(int xPos, int i, int mouseX, int mouseY) {
            // already handled via interactive slot
            return false;
        }

        public void tick(int ticksActive) {
            // recipes don't tick yet
        }
    }
}
