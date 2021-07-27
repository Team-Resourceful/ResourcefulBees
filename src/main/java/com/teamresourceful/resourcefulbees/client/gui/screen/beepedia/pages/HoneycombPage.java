package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeFluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeItemOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HoneycombPage extends BeeDataPage {

    final Button prevTab;
    final Button nextTab;
    private int counter;
    private final int max;

    private final ItemStack hiveOutput;

    final List<RecipeObject> recipes = new ArrayList<>();

    private static final List<Item> hives = BeeInfoUtils.getItemTag("minecraft:beehives").getValues();

    private static final Item[] APIARIES = {ModItems.T1_APIARY_ITEM.get(), ModItems.T2_APIARY_ITEM.get(), ModItems.T3_APIARY_ITEM.get(), ModItems.T4_APIARY_ITEM.get()};

    private static final ResourceLocation honeycombsImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/honeycombs.png");
    private static final ResourceLocation centrifugeImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/centrifuge.png");
    private static final ResourceLocation multiblockOnlyImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/multiblock_only.png");

    private final Button leftArrow;
    private final Button rightArrow;

    private int activePage = 0;


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

        hiveOutput = beeData.getHoneycombData().getHoneycomb().getDefaultInstance();

        ClientWorld world = beepedia.getMinecraft().level;
        recipes.add(new RecipeObject(false, beeData, world));
        recipes.add(new RecipeObject(true, beeData, world));
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
            manager.bind(honeycombsImage);
            AbstractGui.blit(matrix, xPos, yPos + 22, 0, 0, 169, 84, 169, 84);
            beepedia.drawSlot(matrix, hives.get(counter), xPos + 14, yPos + 23);
            drawApiaries(matrix);
            beepedia.drawSlot(matrix, hiveOutput, xPos + 14, yPos + 82);
            drawApiaryOutputs(matrix);
        }
    }

    private void drawApiaries(MatrixStack matrix) {
        for (int i = 0; i < 4; i++) {
            beepedia.drawSlot(matrix, APIARIES[i], xPos + 43 + (i * 29), yPos + 23);
        }
    }

    private void drawApiaryOutputs(MatrixStack matrix) {
        for (int i = 0; i < 4; i++) {
            beepedia.drawSlot(matrix, beeData.getHoneycombData().createApiaryOutput(i), xPos + 43 + (i * 29), yPos + 82);
        }
    }

    @Override
    public void addSearch() {
//        for (RecipeObject recipe : recipes) {
//            for (CentrifugeFluidOutput outputFluid : recipe.outputFluids) {
//                // TODO This is broke!!!! ------------------v
//                parent.addSearchItem(outputFluid.getPool().next().getFluid().toString()); //Not sure if this is correct for this
//            }
//            for (CentrifugeItemOutput outputItem : recipe.outputItems) {
//                // TODO This is broke!!!! ------------------v
//                parent.addSearchItem(outputItem.getPool().next().getItem().toString()); //Not sure if this is correct for this
//            }
//        }
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !Screen.hasShiftDown()) {
            counter++;
            if (counter >= max) counter = 0;
        }
        if (recipes.isEmpty()) return;
        recipes.get(activePage).tick(ticksActive);
    }

    @Override
    public void drawTooltips(MatrixStack matrix, int mouseX, int mouseY) {
        if (!recipes.isEmpty()) recipes.get(activePage).drawTooltip(matrix, mouseX, mouseY);
    }

    private class RecipeObject {
        final boolean isBlock;
        final ItemStack inputItem;
        Map<CentrifugeItemOutput, Integer> outputItems = new LinkedHashMap<>();
        Map<CentrifugeFluidOutput, Integer> outputFluids = new LinkedHashMap<>();
        final CentrifugeRecipe recipe;
        final CustomBeeData beeData;

        public RecipeObject(boolean isBlock, CustomBeeData beeData, ClientWorld world) {
            this.isBlock = isBlock;
            this.beeData = beeData;

            if (isBlock) {
                inputItem = beeData.getHoneycombData().getHoneycombBlock().getDefaultInstance();
            } else {
                inputItem = beeData.getHoneycombData().getHoneycomb().getDefaultInstance();
            }
            Inventory inventory = new Inventory(inputItem);
            recipe = world.getRecipeManager().getRecipeFor(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, inventory, world).orElse(null);
            if (recipe != null) {
                recipe.getItemOutputs().forEach(c -> outputItems.put(c, 0));
                recipe.getFluidOutputs().forEach(c -> outputFluids.put(c, 0));
            }
        }

        public void draw(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
            TextureManager manager = Minecraft.getInstance().getTextureManager();

            // TODO Gravy have a look at the following to see if it can be further optimised
            manager.bind(centrifugeImage);
            AbstractGui.blit(matrix, xPos, yPos + 9, 0, 0, 169, 84, 169, 84);

            beepedia.drawSlot(matrix, inputItem, xPos + 25, yPos + 22);

            int totalItemsOffset = outputItems.size() * 21;
            int startItems = 32 + yPos - totalItemsOffset / 2;
            // draw items
            final int[] k = {0};
            outputItems.forEach((c, i) -> {
                ItemStack item = new ItemStack(c.getPool().get(i).getItem(), c.getPool().get(i).getCount() * (recipe.isMultiblock() ? 9 : 1));
                drawItem(matrix, c.getPool().get(i), item, xPos + 124, startItems + k[0] * 21);
                k[0]++;
            });
            k[0] = 0;
            outputFluids.forEach((c, i) -> {
                FluidStack fluid = new FluidStack(c.getPool().get(i).getFluid(), c.getPool().get(i).getAmount() * (recipe.isMultiblock() ? 9 : 1));
                drawFluid(matrix, c.getPool().get(i), fluid, xPos + 10 + (40 * k[0]), yPos + 10);
                k[0]++;
            });

            if (isBlock || Config.MULTIBLOCK_RECIPES_ONLY.get()) {
                Minecraft.getInstance().getTextureManager().bind(multiblockOnlyImage);
                AbstractGui.blit(matrix, xPos + 28, yPos + 45, 0, 0, 16, 16, 16, 16);
            }
        }

        private void drawFluid(MatrixStack matrix, FluidOutput output, FluidStack fluid, int xPos, int yPos) {
            beepedia.drawFluidSlot(matrix, fluid, xPos, yPos, true);
            drawChance(matrix, output.getChance(), xPos + 30, yPos + 7);
            if (fluid.getFluid() instanceof CustomHoneyFluid) {
                CustomHoneyFluid honey = (CustomHoneyFluid) fluid.getFluid();
                beepedia.registerInteraction(xPos , yPos, () -> {
                    BeepediaScreen.saveScreenState();
                    beepedia.setActive(BeepediaScreen.PageType.HONEY, honey.getHoneyData().getName());
                    return true;
                });
            }
        }

        private void drawItem(MatrixStack matrix, ItemOutput output, ItemStack item, int xPos, int yPos) {
            beepedia.drawSlot(matrix, item, xPos, yPos);
            drawChance(matrix, output.getChance(), xPos + 30, yPos + 7);
            if (item.getItem() instanceof CustomHoneyBottleItem) {
                CustomHoneyBottleItem honey = (CustomHoneyBottleItem) item.getItem();
                beepedia.registerInteraction(xPos, yPos, () -> {
                    BeepediaScreen.saveScreenState();
                    beepedia.setActive(BeepediaScreen.PageType.HONEY, honey.getHoneyData().getName());
                    return true;
                });
            } else if (item.getItem() instanceof BeeSpawnEggItem) {
                BeeSpawnEggItem egg = (BeeSpawnEggItem) item.getItem();
                beepedia.registerInteraction(xPos, yPos, () -> {
                    BeepediaScreen.saveScreenState();
                    beepedia.setActive(BeepediaScreen.PageType.BEE, egg.getBeeData().getCoreData().getName());
                    return true;
                });
            }
        }

        private void drawChance(MatrixStack matrix, double right, int xPos, int yPos) {
            if (right == 1) return;
            FontRenderer font = Minecraft.getInstance().font;
            DecimalFormat decimalFormat = new DecimalFormat("##%");
            StringTextComponent text = new StringTextComponent(decimalFormat.format(right));
            int padding = font.width(text) / 2;
            font.draw(matrix, text.withStyle(TextFormatting.GRAY), (float) xPos - padding, yPos, -1);
        }

        public void drawTooltip(MatrixStack matrix, int mouseX, int mouseY) {
            if (BeepediaScreen.mouseHovering((float) xPos + 28, (float) yPos + 67, 20, 20, mouseX, mouseY)) {
                beepedia.renderTooltip(matrix, new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.centrifuge.requires_multiblock"), mouseX, mouseY);
            }
        }

        public void tick(int ticksActive) {
            if (ticksActive % 20 == 0 && !Screen.hasShiftDown()) {
                outputItems.forEach((c, i) -> {
                    i++;
                    if (i == c.getPool().getSize()) i = 0;
                    outputItems.put(c, i);
                });
                outputFluids.forEach((c, i) -> {
                    i++;
                    if (i == c.getPool().getSize()) i = 0;
                    outputFluids.put(c, i);
                });
            }
        }
    }
}
