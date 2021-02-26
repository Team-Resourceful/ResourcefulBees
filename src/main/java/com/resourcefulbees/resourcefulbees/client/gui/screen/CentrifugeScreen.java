package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabToggleImageButton;
import com.resourcefulbees.resourcefulbees.container.CentrifugeContainer;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.DrainCentrifugeTankMessage;
import com.resourcefulbees.resourcefulbees.network.packets.UpdateRedstoneReqMessage;
import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import com.resourcefulbees.resourcefulbees.utils.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//TODO can aspects of this class be extracted and put into a super class that other screens can pull from?
public class CentrifugeScreen extends ContainerScreen<CentrifugeContainer> {

    // drawTexture(matrix, x, y, textureX, textureY, textWidth, textHeight)
    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/centrifuges/centrifuge_gui.png");
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    protected static final int L_BDR_WD = 7;  //Left border width
    protected static final int BG_FILL_WD = 4; //Background filler width
    protected static final int R_BDR_WD = 7; //Right Border Width
    protected static final int TOP_PAD = 7; //Top border padding
    protected static final int SLOT_WD = 18; //Slot width
    protected static final int SLOT_HT = 18; //Slot Height
    protected static final int DBL_SLOT_WD = SLOT_WD * 2; // Double slot width
    protected static final int DBL_SLOT_HT = SLOT_HT * 2; // double slot height
    protected static final int INV_START_Y = 103; //player inventory height from top
    protected static final int TAB_BG_WIDTH = 25; //Width of the tab background

    protected int bgRpt; //Calculated Background filler repeat
    protected int numInputs;
    protected int inputStartX;
    protected int outputStartX;
    protected int rBdrCor; //Right border correction amount - used to have even padding all the way around
    protected int screenWidth;

    private TabToggleImageButton redstoneButton;
    private TabToggleImageButton fluidDispButton;

    public CentrifugeScreen(CentrifugeContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
        numInputs = screenContainer.getCentrifugeTileEntity().getNumberOfInputs();
        initializeData();
        this.screenWidth =  Math.max(numInputs * 36 + 70, 178) ;
        this.xSize = screenWidth + TAB_BG_WIDTH;
        this.ySize = 186;
        bgRpt = MathUtils.clamp((xSize - L_BDR_WD - R_BDR_WD - TAB_BG_WIDTH) / BG_FILL_WD, 41, 68);
    }

    @Override
    protected void init() {
        super.init();
        int buttonX = this.guiLeft + xSize - TAB_BG_WIDTH;
        int top = this.guiTop + 6;

        redstoneButton = this.addButton(new TabToggleImageButton(buttonX, top + 4, SLOT_WD, SLOT_HT, 25, 220, 18, 18,
                this.container.getRequiresRedstone(), BACKGROUND, new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE), onPress -> this.setRedstoneControl()) {
            public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
                TranslationTextComponent s = new TranslationTextComponent("gui.resourcefulbees.centrifuge.button.redstone." + stateTriggered);
                CentrifugeScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });
        fluidDispButton = this.addButton(new TabToggleImageButton(buttonX, top + 24, SLOT_WD, SLOT_HT, 25, 220, 0, 18,
                this.container.shouldDisplayFluids(), BACKGROUND, new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.HONEYCOMB), onPress -> this.displayFluids()) {
            public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
                TranslationTextComponent s = new TranslationTextComponent("gui.resourcefulbees.centrifuge.button.fluid_display." + stateTriggered);
                CentrifugeScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });
        this.addButton(new ImageButton(buttonX - 1, top + 44, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {}));
    }

    private void displayFluids() {
        fluidDispButton.setStateTriggered(!fluidDispButton.isStateTriggered());
        this.container.setDisplayFluids(fluidDispButton.isStateTriggered());
        this.container.setupSlots();
    }

    private void setRedstoneControl() {
        redstoneButton.setStateTriggered(!redstoneButton.isStateTriggered());
        NetPacketHandler.sendToServer(new UpdateRedstoneReqMessage(this.container.getCentrifugeTileEntity().getPos()));
    }

    protected void initializeData() {
        inputStartX = 88;
        outputStartX = 79;
        rBdrCor = -2;
    }

    @Override
    protected void drawBackground(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.client;
        if (client != null) {
            client.getTextureManager().bindTexture(BACKGROUND);
            int left = this.guiLeft;
            int top = this.guiTop;

            drawBackground(matrix, left, top);
            drawInputSlots(matrix, left, top);
            if (this.container.shouldDisplayFluids()) {
                drawFluidTanks(matrix, left, top);
            } else {
                drawOutputSlots(matrix, left, top);
            }
            drawPowerBar(matrix, left, top);
            drawInventorySlots(matrix, left, top);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        this.redstoneButton.render(matrix, mouseX, mouseY, partialTicks);
        this.fluidDispButton.render(matrix, mouseX, mouseY, partialTicks);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(matrix, mouseX, mouseY);
        //TODO see if the rest of this method can be condensed a bit further
        int x = this.guiLeft + 10;
        int y = this.guiTop + 38;
        if (MathUtils.inRangeInclusive(mouseX, x, x + 12) && MathUtils.inRangeInclusive(mouseY, y, y + 58)){
            if (Screen.hasShiftDown() || this.container.getEnergy() < 500) this.renderTooltip(matrix, new StringTextComponent(this.container.getEnergy() + " RF"), mouseX, mouseY);
            else this.renderTooltip(matrix, new StringTextComponent(ModConstants.DECIMAL_FORMAT.format(this.container.getEnergy() / 1000) + " kRF"), mouseX, mouseY);
        }

        if (this.container.shouldDisplayFluids()) {
            x = this.guiLeft + L_BDR_WD + SLOT_WD;
            y = this.guiTop + TOP_PAD + DBL_SLOT_HT;
            if (MathUtils.inRangeInclusive(mouseX, x, x + SLOT_WD) && MathUtils.inRangeInclusive(mouseY, y, y + 54)) {
                renderFluidToolTip(matrix, mouseX, mouseY, this.container.getFluidInTank(CentrifugeTileEntity.BOTTLE_SLOT));
            } else {
                for (int i = 0; i < numInputs; i++) {
                    x = this.guiLeft + outputStartX + SLOT_WD + 9 + (i * DBL_SLOT_WD);
                    if (MathUtils.inRangeInclusive(mouseX, x, x + SLOT_WD) && MathUtils.inRangeInclusive(mouseY, y, y + 54)) {
                        renderFluidToolTip(matrix, mouseX, mouseY, this.container.getFluidInTank(i + 1));
                    }
                }
            }
        }
    }

    @Override
    public void drawForeground(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        for (Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(matrix, mouseX - this.guiLeft, mouseY - this.guiTop);
                break;
            }
        }
    }

    private void drawBackground(MatrixStack matrix, int left, int top) {
        this.drawTexture(matrix, left, top, 0, 0, L_BDR_WD, ySize);
        for (int i = 0; i < bgRpt; i++) {
            this.drawTexture(matrix, left + L_BDR_WD + (i * BG_FILL_WD), top, L_BDR_WD, 0, BG_FILL_WD, ySize);
        }
        int x = left + L_BDR_WD + (BG_FILL_WD * bgRpt) + rBdrCor;
        this.drawTexture(matrix, x, top, L_BDR_WD + BG_FILL_WD, 0, R_BDR_WD, ySize);
        this.drawTexture(matrix, x + L_BDR_WD, top + 5, 0, 188, 25, 68);
    }

    private void drawInputSlots(MatrixStack matrix, int left, int top) {
        this.drawTexture(matrix, left + L_BDR_WD + SLOT_WD, top + TOP_PAD, 62, 0, 18, 18); //draw bottle slot
        for (int i = 0; i < numInputs; i++) {
            int x = left + inputStartX + SLOT_WD + (i * DBL_SLOT_WD);
            this.drawTexture(matrix, x, top + TOP_PAD, 62, SLOT_HT, SLOT_WD, SLOT_HT); //draw input slot
            this.drawTexture(matrix, x, top + TOP_PAD + SLOT_HT, 80, 0, SLOT_WD, SLOT_HT); // draw arrow
            // Draw Scaled Progress Here.....
            int scaledProgress = 16 * this.container.getTime(i) / this.container.getTotalTime(i);
            this.drawTexture(matrix, x, top + TOP_PAD + SLOT_HT, 80, 18, 18, scaledProgress);
        }
    }

    private void drawOutputSlots(MatrixStack matrix, int left, int top) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < numInputs * 2; j++) {
                int x = left + outputStartX + SLOT_WD + (j * SLOT_WD);
                int y = top + TOP_PAD + DBL_SLOT_HT + (i * SLOT_HT);
                this.drawTexture(matrix, x, y, 62, SLOT_HT, SLOT_WD, SLOT_HT);
            }
        }
    }

    private void drawPowerBar(MatrixStack matrix, int left, int top) {
        this.drawTexture(matrix, left + 9, top + 37, L_BDR_WD + BG_FILL_WD + R_BDR_WD, 0, 14, 60);
        int scaledRF = 58 * this.container.getEnergy() / this.container.getMaxEnergy();
        this.drawTexture(matrix, left + 10, top + 38 + (58 - scaledRF), 32, 58 - scaledRF, 12, scaledRF);
    }

    private void drawInventorySlots(MatrixStack matrix, int left, int top) {
        int xStart = left + (int) (screenWidth * 0.5) - 82;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.drawTexture(matrix, xStart + (j * SLOT_WD), top + INV_START_Y + (i * SLOT_HT), 62, SLOT_HT, SLOT_WD, SLOT_HT);
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.drawTexture(matrix, xStart + (k * SLOT_WD), top + INV_START_Y + 58, 62, SLOT_HT, SLOT_WD, SLOT_HT);
        }
    }

    private void drawFluidTanks(MatrixStack matrix, int left, int top) {
        if (client != null) {
            int x = left + L_BDR_WD + SLOT_WD;
            int y = top + TOP_PAD + DBL_SLOT_HT;
            drawFluid(0, x, y, matrix, client);

            for (int i = 0; i < numInputs; i++) {
                x = left + outputStartX + SLOT_WD + 9 + (i * DBL_SLOT_WD);
                drawFluid(i + 1, x, y, matrix, client);
            }
        }
    }

    private void drawFluid(int tank, int x, int y, MatrixStack matrix, Minecraft client) {
        drawTexture(matrix, x, y, 44, 0, 18, 54);
        FluidStack fluidStack = this.container.getFluidInTank(tank);
        if (fluidStack != FluidStack.EMPTY) {
            TextureAtlasSprite fluidSprite = RenderUtils.getStillFluidTexture(fluidStack);
            int scale = getScaledFluidAmount(tank);
            Color color = new Color(fluidStack.getFluid().getAttributes().getColor(fluidStack));
            //noinspection deprecation
            RenderSystem.color4f(color.getR(), color.getG(), color.getB(), color.getAlpha());
            RenderUtils.drawTiledSprite(matrix, x + 1, y + 1, 52, 16, scale, fluidSprite, 16, 16, getZOffset());
            RenderUtils.resetColor();
        }
        client.getTextureManager().bindTexture(BACKGROUND);
    }

    private int getScaledFluidAmount(int tank) {
        float scale = (float) this.container.getFluidAmountInTank(tank) / (float) this.container.getMaxTankCapacity();
        return Math.round(scale * (52));
    }

    //TODO is overriding this method the "proper" way to handle this?
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (Screen.hasControlDown() && this.container.shouldDisplayFluids()) {
            int x = this.guiLeft + L_BDR_WD + SLOT_WD;
            int y = this.guiTop + TOP_PAD + DBL_SLOT_HT;

            if (MathUtils.inRangeInclusive(mouseX, x, x + SLOT_WD) && MathUtils.inRangeInclusive(mouseY, y, y + 54)) {
                NetPacketHandler.sendToServer(new DrainCentrifugeTankMessage(this.container.getCentrifugeTileEntity().getPos(), CentrifugeTileEntity.BOTTLE_SLOT));
                return true;
            } else {
                for (int i = 0; i < numInputs; i++) {
                    x = this.guiLeft + outputStartX + SLOT_WD + 9 + (i * DBL_SLOT_WD);
                    if (MathUtils.inRangeInclusive(mouseX, x, x + SLOT_WD) && MathUtils.inRangeInclusive(mouseY, y, y + 54)) {
                        NetPacketHandler.sendToServer(new DrainCentrifugeTankMessage(this.container.getCentrifugeTileEntity().getPos(), i + 1));
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    //TODO methods below can probably be converted to utility methods and used elsewhere.
    public void renderFluidToolTip(MatrixStack matrix, int mouseX, int mouseY, FluidStack fluidStack) {
        if (!fluidStack.isEmpty()) {
            List<ITextComponent> componentList = new ArrayList<>();
            componentList.add(new StringTextComponent(formatFluidAmount(fluidStack.getAmount())));
            componentList.add(fluidStack.getDisplayName());
            componentList.add(new StringTextComponent(ModConstants.NAMESPACE_FORMATTING + getFluidNamespace(fluidStack.getFluid())));
            this.renderTooltip(matrix, componentList, mouseX, mouseY);
        }
    }

    public String formatFluidAmount(int amount) {
        return Screen.hasShiftDown() || amount < 500 ? amount + "mb" : ModConstants.DECIMAL_FORMAT.format(amount / 1000) + "B";
    }

    public String getFluidNamespace(@Nonnull Fluid fluid) {
        return WordUtils.capitalize(Objects.requireNonNull(fluid.getRegistryName()).getNamespace());
    }
}
