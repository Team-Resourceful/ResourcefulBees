package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.HoneycombData;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.lib.ApiaryOutputs;
import com.teamresourceful.resourcefulbees.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class HoneycombPage extends BeeDataPage {

    final Button prevTab;
    final Button nextTab;
    private int counter;
    private final int max;

    private final ItemStack hiveOutput;
    private final ItemStack apiary1Output;
    private final ItemStack apiary2Output;
    private final ItemStack apiary3Output;
    private final ItemStack apiary4Output;

    final List<RecipeObject> recipes = new ArrayList<>();

    final List<Item> hives = BeeInfoUtils.getItemTag("minecraft:beehives").getValues();
    final ItemStack apiary1 = new ItemStack(ModItems.T1_APIARY_ITEM.get());
    final ItemStack apiary2 = new ItemStack(ModItems.T2_APIARY_ITEM.get());
    final ItemStack apiary3 = new ItemStack(ModItems.T3_APIARY_ITEM.get());
    final ItemStack apiary4 = new ItemStack(ModItems.T4_APIARY_ITEM.get());

    final ResourceLocation honeycombsImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/honeycombs.png");
    final ResourceLocation centrifugeImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/centrifuge.png");
    ResourceLocation multiblockOnlyImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/multiblock_only.png");

    final Button leftArrow;
    final Button rightArrow;

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

        List<Integer> apiaryAmounts = beeData.getHoneycombData().getApiaryOutputAmounts();
        hiveOutput = beeData.getHoneycombData().getHoneycomb().getDefaultInstance();
        apiary1Output = getApiaryOutput(0, beeData.getHoneycombData(), apiaryAmounts);
        apiary2Output = getApiaryOutput(1, beeData.getHoneycombData(), apiaryAmounts);
        apiary3Output = getApiaryOutput(2, beeData.getHoneycombData(), apiaryAmounts);
        apiary4Output = getApiaryOutput(3, beeData.getHoneycombData(), apiaryAmounts);

        ClientLevel world = beepedia.getMinecraft().level;
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

    private ItemStack getApiaryOutput(int index, HoneycombData honeycombData, List<Integer> apiaryAmounts) {
        return new ItemStack(honeycombData.getApiaryOutputTypes().get(index) == ApiaryOutputs.COMB ? honeycombData.getHoneycomb() : honeycombData.getHoneycombBlock(), apiaryAmounts.get(index));
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
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        TranslatableComponent title = new TranslatableComponent(BeepediaScreen.currScreenState.isCentrifugeOpen() ? "gui.resourcefulbees.beepedia.bee_subtab.centrifuge" : "gui.resourcefulbees.beepedia.bee_subtab.honeycombs");
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
        if (BeepediaScreen.currScreenState.isCentrifugeOpen() && !recipes.isEmpty()) {
            recipes.get(activePage).draw(matrix, xPos, yPos + 22, mouseX, mouseY);
            if (recipes.size() > 1) {
                TextComponent page = new TextComponent(String.format("%d / %d", activePage + 1, recipes.size()));
                padding = font.width(page) / 2;
                font.draw(matrix, page.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + SUB_PAGE_HEIGHT - 14, -1);
            }
        } else {
            manager.bind(honeycombsImage);
            GuiComponent.blit(matrix, xPos, yPos + 22, 0, 0, 169, 84, 169, 84);
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
    public void addSearch() {
        for (RecipeObject recipe : recipes) {
            for (FluidOutput outputFluid : recipe.outputFluids) {
                parent.addSearchItem(outputFluid.getFluid().toString()); //Not sure if this is correct for this
            }
            for (ItemOutput outputItem : recipe.outputItems) {
                parent.addSearchItem(outputItem.getItem().toString()); //Not sure if this is correct for this
            }
        }
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !Screen.hasShiftDown()) {
            counter++;
            if (counter >= max) counter = 0;
        }
    }

    @Override
    public void drawTooltips(PoseStack matrix, int mouseX, int mouseY) {
        if (!recipes.isEmpty()) recipes.get(activePage).drawTooltip(matrix, mouseX, mouseY);
    }

    private class RecipeObject {
        final boolean isBlock;
        final ItemStack inputItem;
        ItemStack bottleItem = ItemStack.EMPTY;
        List<ItemOutput> outputItems;
        List<FluidOutput> outputFluids;
        final CentrifugeRecipe recipe;
        final CustomBeeData beeData;

        public RecipeObject(boolean isBlock, CustomBeeData beeData, ClientLevel world) {
            this.isBlock = isBlock;
            this.beeData = beeData;

            if (isBlock) {
                inputItem = beeData.getHoneycombData().getHoneycombBlock().getDefaultInstance();
            } else {
                inputItem = beeData.getHoneycombData().getHoneycomb().getDefaultInstance();
            }
            SimpleContainer inventory = new SimpleContainer(inputItem);
            recipe = world.getRecipeManager().getRecipeFor(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, inventory, world).orElse(null);
            if (recipe != null) {
                outputItems = recipe.getItemOutputs();
                outputFluids = recipe.getFluidOutputs();
            }
        }

        public void draw(PoseStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
            TextureManager manager = Minecraft.getInstance().getTextureManager();

            manager.bind(centrifugeImage);
            GuiComponent.blit(matrix, xPos, yPos + 9, 0, 0, 169, 84, 169, 84);

            beepedia.drawSlot(matrix, inputItem, xPos + 25, yPos + 22);

            List<ItemOutput> items = recipe.getItemOutputs();
            List<FluidOutput> fluids = recipe.getFluidOutputs();


            int totalItemsOffset = items.size() * 21;
            int startItems = HoneycombPage.this.yPos + 54 - totalItemsOffset / 2;
            // draw items
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = new ItemStack(items.get(i).getItem(), items.get(i).getCount() * (recipe.isMultiblock() ? 9 : 1));
                drawItem(matrix, items.get(i), item, HoneycombPage.this.xPos + 124, startItems + i * 21);
            }
            // draw fluids
            for (int i = 0; i < fluids.size(); i++) {
                FluidOutput fluid = fluids.get(i);
                beepedia.drawFluidSlot(matrix, fluid.getFluidStack(), xPos + 10 + (40 * i), yPos + 10, true);
                if (fluid.getFluid() instanceof CustomHoneyFluid) {
                    CustomHoneyFluid honey = (CustomHoneyFluid) fluid.getFluid();
                    beepedia.registerInteraction(xPos + 10 + (40 * i), yPos + 10, () -> {
                        BeepediaScreen.saveScreenState();
                        beepedia.setActive(BeepediaScreen.PageType.HONEY, honey.getHoneyData().getName());
                        return true;
                    });
                }
            }

            if (isBlock || Config.MULTIBLOCK_RECIPES_ONLY.get()) {
                Minecraft.getInstance().getTextureManager().bind(multiblockOnlyImage);
                GuiComponent.blit(matrix, xPos + 28, yPos + 45, 0, 0, 16, 16, 16, 16);
            }
        }

        private void drawItem(PoseStack matrix, ItemOutput output, ItemStack item, int xPos, int yPos) {
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

        private void drawChance(PoseStack matrix, double right, int xPos, int yPos) {
            if (right == 1) return;
            Font font = Minecraft.getInstance().font;
            DecimalFormat decimalFormat = new DecimalFormat("##%");
            TextComponent text = new TextComponent(decimalFormat.format(right));
            int padding = font.width(text) / 2;
            font.draw(matrix, text.withStyle(ChatFormatting.GRAY), (float) xPos - padding, yPos, -1);
        }

        public void drawTooltip(PoseStack matrix, int mouseX, int mouseY) {
            if (BeepediaScreen.mouseHovering((float) xPos + 28, (float) yPos + 67, 20, 20, mouseX, mouseY)) {
                beepedia.renderTooltip(matrix, new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.centrifuge.requires_multiblock"), mouseX, mouseY);
            }
        }
    }
}
