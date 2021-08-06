package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.widget.TabToggleImageButton;
import com.teamresourceful.resourcefulbees.common.container.CentrifugeContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.DrainCentrifugeTankMessage;
import com.teamresourceful.resourcefulbees.common.network.packets.UpdateRedstoneReqMessage;
import com.teamresourceful.resourcefulbees.common.tileentity.CentrifugeTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
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
import org.jetbrains.annotations.NotNull;

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

    protected final int bgRpt; //Calculated Background filler repeat
    protected final int numInputs;
    protected int inputStartX;
    protected int outputStartX;
    protected int rBdrCor; //Right border correction amount - used to have even padding all the way around
    protected final int screenWidth;

    private TabToggleImageButton redstoneButton;
    private TabToggleImageButton fluidDispButton;

    public CentrifugeScreen(CentrifugeContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
        numInputs = screenContainer.getCentrifugeTileEntity().getNumberOfInputs();
        initializeData();
        this.screenWidth =  Math.max(numInputs * 36 + 70, 178) ;
        this.imageWidth = screenWidth + TAB_BG_WIDTH;
        this.imageHeight = 186;
        bgRpt = MathUtils.clamp((imageWidth - L_BDR_WD - R_BDR_WD - TAB_BG_WIDTH) / BG_FILL_WD, 41, 68);
    }

    @Override
    protected void init() {
        super.init();
        int buttonX = this.leftPos + imageWidth - TAB_BG_WIDTH;
        int top = this.topPos + 6;

        redstoneButton = this.addButton(new TabToggleImageButton(buttonX, top + 4, SLOT_WD, SLOT_HT, 25, 220, 18, 18,
                this.menu.getRequiresRedstone(), BACKGROUND, new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE), onPress -> this.setRedstoneControl()) {

            @Override
            public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
                TranslationTextComponent s = new TranslationTextComponent("gui.resourcefulbees.centrifuge.button.redstone." + stateTriggered);
                CentrifugeScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });
        fluidDispButton = this.addButton(new TabToggleImageButton(buttonX, top + 24, SLOT_WD, SLOT_HT, 25, 220, 0, 18,
                this.menu.shouldDisplayFluids(), BACKGROUND, new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.HONEYCOMB), onPress -> this.displayFluids()) {

            @Override
            public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
                TranslationTextComponent s = new TranslationTextComponent("gui.resourcefulbees.centrifuge.button.fluid_display." + stateTriggered);
                CentrifugeScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });
        this.addButton(new ImageButton(buttonX - 1, top + 44, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button -> {}));
    }

    private void displayFluids() {
        fluidDispButton.setStateTriggered(!fluidDispButton.isStateTriggered());
        this.menu.setDisplayFluids(fluidDispButton.isStateTriggered());
        this.menu.setupSlots();
    }

    private void setRedstoneControl() {
        redstoneButton.setStateTriggered(!redstoneButton.isStateTriggered());
        NetPacketHandler.sendToServer(new UpdateRedstoneReqMessage(this.menu.getCentrifugeTileEntity().getBlockPos()));
    }

    protected void initializeData() {
        inputStartX = 88;
        outputStartX = 79;
        rBdrCor = -2;
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bind(BACKGROUND);
            int left = this.leftPos;
            int top = this.topPos;

            drawBackground(matrix, left, top);
            drawInputSlots(matrix, left, top);
            if (this.menu.shouldDisplayFluids()) {
                drawFluidTanks(matrix, left, top);
            } else {
                drawOutputSlots(matrix, left, top);
            }
            drawPowerBar(matrix, left, top);
            drawInventorySlots(matrix, left, top);
        }
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.menu.getCentrifugeTileEntity() != null) {
            this.renderBackground(matrix);
            this.redstoneButton.render(matrix, mouseX, mouseY, partialTicks);
            this.fluidDispButton.render(matrix, mouseX, mouseY, partialTicks);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderTooltip(matrix, mouseX, mouseY);
            //TODO see if the rest of this method can be condensed a bit further
            int x = this.leftPos + 10;
            int y = this.topPos + 38;
            if (MathUtils.inRangeInclusive(mouseX, x, x + 12) && MathUtils.inRangeInclusive(mouseY, y, y + 58)) {
                if (Screen.hasShiftDown() || this.menu.getEnergy() < 500)
                    this.renderTooltip(matrix, new StringTextComponent(this.menu.getEnergy() + " RF"), mouseX, mouseY);
                else
                    this.renderTooltip(matrix, new StringTextComponent(ModConstants.DECIMAL_FORMAT.format(this.menu.getEnergy() / 1000) + " kRF"), mouseX, mouseY);
            }

            if (this.menu.shouldDisplayFluids()) {
                displayFluids(matrix, mouseX, mouseY);
            }
        }
    }

    private void displayFluids(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
        int y;
        int x;
        x = this.leftPos + L_BDR_WD + SLOT_WD;
        y = this.topPos + TOP_PAD + DBL_SLOT_HT;
        if (MathUtils.inRangeInclusive(mouseX, x, x + SLOT_WD) && MathUtils.inRangeInclusive(mouseY, y, y + 54)) {
            renderFluidToolTip(matrix, mouseX, mouseY, this.menu.getFluidInTank(CentrifugeTileEntity.BOTTLE_SLOT));
        } else {
            for (int i = 0; i < numInputs; i++) {
                x = this.leftPos + outputStartX + SLOT_WD + 9 + (i * DBL_SLOT_WD);
                if (MathUtils.inRangeInclusive(mouseX, x, x + SLOT_WD) && MathUtils.inRangeInclusive(mouseY, y, y + 54)) {
                    renderFluidToolTip(matrix, mouseX, mouseY, this.menu.getFluidInTank(i + 1));
                }
            }
        }
    }

    @Override
    public void renderLabels(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
        for (Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(matrix, mouseX - this.leftPos, mouseY - this.topPos);
                break;
            }
        }
    }

    private void drawBackground(MatrixStack matrix, int left, int top) {
        this.blit(matrix, left, top, 0, 0, L_BDR_WD, imageHeight);
        for (int i = 0; i < bgRpt; i++) {
            this.blit(matrix, left + L_BDR_WD + (i * BG_FILL_WD), top, L_BDR_WD, 0, BG_FILL_WD, imageHeight);
        }
        int x = left + L_BDR_WD + (BG_FILL_WD * bgRpt) + rBdrCor;
        this.blit(matrix, x, top, L_BDR_WD + BG_FILL_WD, 0, R_BDR_WD, imageHeight);
        this.blit(matrix, x + L_BDR_WD, top + 5, 0, 188, 25, 68);
    }

    private void drawInputSlots(MatrixStack matrix, int left, int top) {
        this.blit(matrix, left + L_BDR_WD + SLOT_WD, top + TOP_PAD, 62, 0, 18, 18); //draw bottle slot
        for (int i = 0; i < numInputs; i++) {
            int x = left + inputStartX + SLOT_WD + (i * DBL_SLOT_WD);
            this.blit(matrix, x, top + TOP_PAD, 62, SLOT_HT, SLOT_WD, SLOT_HT); //draw input slot
            this.blit(matrix, x, top + TOP_PAD + SLOT_HT, 80, 0, SLOT_WD, SLOT_HT); // draw arrow
            // Draw Scaled Progress Here.....
            int scaledProgress = 16 * this.menu.getTime(i) / this.menu.getTotalTime(i);
            this.blit(matrix, x, top + TOP_PAD + SLOT_HT, 80, 18, 18, scaledProgress);
        }
    }

    private void drawOutputSlots(MatrixStack matrix, int left, int top) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < numInputs * 2; j++) {
                int x = left + outputStartX + SLOT_WD + (j * SLOT_WD);
                int y = top + TOP_PAD + DBL_SLOT_HT + (i * SLOT_HT);
                this.blit(matrix, x, y, 62, SLOT_HT, SLOT_WD, SLOT_HT);
            }
        }
    }

    private void drawPowerBar(MatrixStack matrix, int left, int top) {
        this.blit(matrix, left + 9, top + 37, L_BDR_WD + BG_FILL_WD + R_BDR_WD, 0, 14, 60);
        int scaledRF = 58 * this.menu.getEnergy() / this.menu.getMaxEnergy();
        this.blit(matrix, left + 10, top + 38 + (58 - scaledRF), 32, 58 - scaledRF, 12, scaledRF);
    }

    private void drawInventorySlots(MatrixStack matrix, int left, int top) {
        int xStart = left + (int) (screenWidth * 0.5) - 82;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.blit(matrix, xStart + (j * SLOT_WD), top + INV_START_Y + (i * SLOT_HT), 62, SLOT_HT, SLOT_WD, SLOT_HT);
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.blit(matrix, xStart + (k * SLOT_WD), top + INV_START_Y + 58, 62, SLOT_HT, SLOT_WD, SLOT_HT);
        }
    }

    private void drawFluidTanks(MatrixStack matrix, int left, int top) {
        if (minecraft != null) {
            int x = left + L_BDR_WD + SLOT_WD;
            int y = top + TOP_PAD + DBL_SLOT_HT;
            drawFluid(0, x, y, matrix, minecraft);

            for (int i = 0; i < numInputs; i++) {
                x = left + outputStartX + SLOT_WD + 9 + (i * DBL_SLOT_WD);
                drawFluid(i + 1, x, y, matrix, minecraft);
            }
        }
    }

    private void drawFluid(int tank, int x, int y, MatrixStack matrix, Minecraft client) {
        blit(matrix, x, y, 44, 0, 18, 54);
        FluidStack fluidStack = this.menu.getFluidInTank(tank);
        if (fluidStack != FluidStack.EMPTY) {
            TextureAtlasSprite fluidSprite = RenderUtils.getStillFluidTexture(fluidStack);
            int scale = getScaledFluidAmount(tank);
            Color color = new Color(fluidStack.getFluid().getAttributes().getColor(fluidStack));
            //noinspection deprecation
            RenderSystem.color4f(color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), color.getFloatAlpha());
            RenderUtils.drawTiledSprite(matrix, x + 1, y + 1, 52, 16, scale, fluidSprite, 16, 16, getBlitOffset());
            RenderUtils.resetColor();
        }
        client.getTextureManager().bind(BACKGROUND);
    }

    private int getScaledFluidAmount(int tank) {
        float scale = (float) this.menu.getFluidAmountInTank(tank) / (float) this.menu.getMaxTankCapacity();
        return Math.round(scale * (52));
    }

    //TODO is overriding this method the "proper" way to handle this?
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (Screen.hasControlDown() && this.menu.shouldDisplayFluids()) {
            double x = (double) this.leftPos + L_BDR_WD + SLOT_WD;
            double y = (double) this.topPos + TOP_PAD + DBL_SLOT_HT;

            if (MathUtils.inRangeInclusive(mouseX, x, x + SLOT_WD) && MathUtils.inRangeInclusive(mouseY, y, y + 54)) {
                NetPacketHandler.sendToServer(new DrainCentrifugeTankMessage(this.menu.getCentrifugeTileEntity().getBlockPos(), CentrifugeTileEntity.BOTTLE_SLOT));
                return true;
            } else {
                for (int i = 0; i < numInputs; i++) {
                    x = this.leftPos + outputStartX + SLOT_WD + 9D + (i * DBL_SLOT_WD);
                    if (MathUtils.inRangeInclusive(mouseX, x, x + SLOT_WD) && MathUtils.inRangeInclusive(mouseY, y, y + 54)) {
                        NetPacketHandler.sendToServer(new DrainCentrifugeTankMessage(this.menu.getCentrifugeTileEntity().getBlockPos(), i + 1));
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
            this.renderComponentTooltip(matrix, componentList, mouseX, mouseY);
        }
    }

    public String formatFluidAmount(int amount) {
        return Screen.hasShiftDown() || amount < 500f ? amount + "mb" : ModConstants.DECIMAL_FORMAT.format(amount / 1000f) + "B";
    }

    public String getFluidNamespace(@NotNull Fluid fluid) {
        //noinspection deprecation
        return WordUtils.capitalize(Objects.requireNonNull(fluid.getRegistryName()).getNamespace());
    }
}
