package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeeconEffectWidget;
import com.teamresourceful.resourcefulbees.client.gui.widget.ToggleImageButton;
import com.teamresourceful.resourcefulbees.common.block.EnderBeecon;
import com.teamresourceful.resourcefulbees.common.inventory.containers.EnderBeeconContainer;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.BeeconChangeMessage;
import com.teamresourceful.resourcefulbees.common.tileentity.EnderBeeconTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class EnderBeeconScreen extends ContainerScreen<EnderBeeconContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/ender_beecon.png");

    private static final ITextComponent PRIMARY_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.primary");
    private static final ITextComponent DRAIN_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.drain");
    private static final ITextComponent RANGE_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.range");
    private static final ITextComponent ACTIVE_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.is_active");
    private static final ITextComponent FLUID_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.fluid");
    private static final ITextComponent FLUID_AMOUNT_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.fluid_amount");
    private static final ITextComponent NO_FLUID_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.no_fluid");

    private final EnderBeeconTileEntity tileEntity;

    public EnderBeeconScreen(EnderBeeconContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
        this.tileEntity = screenContainer.getEnderBeeconTileEntity();
        this.imageWidth = 230;
        this.imageHeight = 200;
        this.inventoryLabelX = 36;
        this.inventoryLabelY = 107;
        this.titleLabelX = 110;
    }

    private final List<BeeconEffectWidget> powerButtons = new LinkedList<>();
    private ToggleImageButton soundButton;
    private ToggleImageButton beamButton;

    @Override
    protected void init() {
        super.init();
        buttons.clear();

        BlockState state = menu.getEnderBeeconTileEntity().getBlockState();

        soundButton = addButton(new ToggleImageButton(  leftPos+109, topPos+84, 52, 200, state.hasProperty(EnderBeecon.SOUND) && !state.getValue(EnderBeecon.SOUND), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetPacketHandler.sendToServer(new BeeconChangeMessage(BeeconChangeMessage.Option.SOUND, !selected, menu.getEnderBeeconTileEntity().getBlockPos()));
            }
        });
        beamButton = addButton(new ToggleImageButton(leftPos+132, topPos+84, 92, 200, state.hasProperty(EnderBeecon.BEAM) && !state.getValue(EnderBeecon.BEAM), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetPacketHandler.sendToServer(new BeeconChangeMessage(BeeconChangeMessage.Option.BEAM, !selected, menu.getEnderBeeconTileEntity().getBlockPos()));
            }
        });
        addButton(new RangeSlider(leftPos+155, topPos+84, menu.getEnderBeeconTileEntity().getRange()/50f));
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrix);
        if (minecraft != null && tileEntity != null) {
            minecraft.getTextureManager().bind(BACKGROUND);
            this.blit(matrix, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
            this.blit(matrix, this.leftPos+100, this.topPos+17, 138, 200, 6, 27);
        }
        drawButtons();
    }

    private void drawButtons() {
        int buttonStartY = this.topPos + 17;
        powerButtons.clear();
        for (Effect allowedEffect : EnderBeeconTileEntity.ALLOWED_EFFECTS) {
            BeeconEffectWidget button = this.addButton(new BeeconEffectWidget(this.leftPos + 9, buttonStartY, allowedEffect, menu.getEnderBeeconTileEntity()));
            button.active = true;
            button.setSelected(tileEntity.hasEffect(allowedEffect));
            powerButtons.add(button);
            buttonStartY += 22;
        }
    }

    @Override
    protected void renderLabels(@NotNull MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        this.font.draw(matrixStack, PRIMARY_LABEL, 10, 6, 4210752);

        drawString(matrixStack, font, ACTIVE_LABEL, 110, 20, 14737632);
        drawString(matrixStack, font, tileEntity.doEffects() ? "Yes" : "No", 160, 20, tileEntity.doEffects() ? 47104 : 12320768);
        drawString(matrixStack, font, DRAIN_LABEL, 110, 32, 14737632);
        drawString(matrixStack, font, tileEntity.getDrain() + " mb/t", 141, 32, 16751628);
        drawString(matrixStack, font, RANGE_LABEL, 110, 44, 14737632);
        drawString(matrixStack, font, tileEntity.getRange() + " blocks", 145, 44, 34815);

        FluidStack fluidStack = menu.getEnderBeeconTileEntity().getTank().getFluid();

        drawString(matrixStack, font, FLUID_LABEL, 110, 56, 14737632);
        drawString(matrixStack, font, fluidStack.isEmpty() ? NO_FLUID_LABEL : fluidStack.getDisplayName(), 137, 56, 16751628);
        drawString(matrixStack, font, FLUID_AMOUNT_LABEL, 110, 68, 14737632);
        drawString(matrixStack, font, fluidStack.getAmount()+"mB", 148, 68, 47104);
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
        for (BeeconEffectWidget widget : this.powerButtons) if (widget.isHovered()) widget.renderToolTip(matrix, mouseX, mouseY);
        if (soundButton.isHovered()) soundButton.renderToolTip(matrix, mouseX, mouseY);
        if (beamButton.isHovered()) beamButton.renderToolTip(matrix, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return this.getFocused() != null && this.isDragging() && pButton == 0 ? this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) : super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    public class RangeSlider extends AbstractSlider {

        public RangeSlider(int pX, int pY, double pValue) {
            super(pX, pY, 66, 20, StringTextComponent.EMPTY, pValue);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(new StringTextComponent("Range: " + (int)((value * 40)+10)));
        }

        @Override
        protected void applyValue() {
            int range = (int)(value * 40)+10;
            NetPacketHandler.sendToServer(new BeeconChangeMessage(BeeconChangeMessage.Option.RANGE, range, EnderBeeconScreen.this.menu.getEnderBeeconTileEntity().getBlockPos()));
        }
    }
}
