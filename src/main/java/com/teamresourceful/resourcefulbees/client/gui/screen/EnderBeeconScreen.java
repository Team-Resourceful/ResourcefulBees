package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.container.EnderBeeconContainer;
import com.teamresourceful.resourcefulbees.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.network.packets.UpdateBeeconMessage;
import com.teamresourceful.resourcefulbees.tileentity.EnderBeeconTileEntity;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class EnderBeeconScreen extends ContainerScreen<EnderBeeconContainer> {

    private final EnderBeeconTileEntity tileEntity;

    public EnderBeeconScreen(EnderBeeconContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
        this.tileEntity = screenContainer.getEnderBeeconTileEntity();
        preInit();
    }

    private static final ITextComponent primaryLabel = new TranslationTextComponent("block.resourcefulbees.ender_beecon.primary");
    private static final ITextComponent drainLabel = new TranslationTextComponent("block.resourcefulbees.ender_beecon.drain");
    private static final ITextComponent rangeLabel = new TranslationTextComponent("block.resourcefulbees.ender_beecon.range");
    private static final ITextComponent activeLabel = new TranslationTextComponent("block.resourcefulbees.ender_beecon.is_active");
    private static final ResourceLocation BUTTON_CALMING = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/calming_button.png");

    final List<PowerButton> powerButtons = new LinkedList<>();

    private void preInit() {
        this.imageWidth = 230;
        this.imageHeight = 200;
        this.inventoryLabelX = 36;
        this.inventoryLabelY = 106;
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/ender_beecon.png");
        if (minecraft != null && tileEntity != null) {
            minecraft.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
        drawFluidTank(matrix);
        drawButtons();
    }

    private void drawButtons() {
        int i = this.leftPos;
        int j = this.topPos;
        int buttonX = i + 24;
        int buttonY = j + 36;
        int buttonWidth = 22;
        int padding = 16;
        int buttonStartX = buttonX;
        powerButtons.clear();
        for (EnderBeeconTileEntity.BeeconEffect effect : tileEntity.getEffects()) {
            PowerButton button = this.addButton(new PowerButton(buttonStartX, buttonY, effect.getEffect(), BUTTON_CALMING));
            button.active = true;
            button.setSelected(effect.isActive());
            powerButtons.add(button);
            buttonStartX += buttonWidth + padding;
        }
    }

    private void drawFluidTank(MatrixStack matrix) {
        if (!tileEntity.getFluidTank().isEmpty()) {
            //init stuff
            int tankPosX = this.leftPos + 207;
            int tankPosY = this.topPos + 30;
            int tankHeight = 62;
            int tankWidth = 14;
            RenderUtils.renderFluid(matrix, tileEntity.getFluidTank(), tankPosX, tankPosY, tankWidth, tankHeight, getBlitOffset());
        }
    }

    @Override
    protected void renderLabels(@NotNull MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        int buttonX = 24 + 11;
        int buttonY = 36 + 25;
        int buttonWidth = 22;
        int padding = 16;
        int buttonStartX = buttonX;
        drawCenteredString(matrixStack, this.font, primaryLabel, 92, 24, 14737632);
        this.font.draw(matrixStack, drainLabel, 13, 90, 14737632);
        this.font.draw(matrixStack, tileEntity.getDrain() + " mb/t", 44, 90, 16751628);
        this.font.draw(matrixStack, rangeLabel, 90, 90, 14737632);
        this.font.draw(matrixStack, tileEntity.getRange() + " blocks", 125, 90, 34815);
        this.font.draw(matrixStack, activeLabel, 13, 78, 14737632);
        if (tileEntity.doEffects()) {
            this.font.draw(matrixStack, "Yes", 63, 78, 47104);
        } else {
            this.font.draw(matrixStack, "No", 63, 78, 12320768);
        }

        for (PowerButton widget : this.powerButtons) {
            EnderBeeconTileEntity.BeeconEffect effect = tileEntity.getEffect(widget.effect);
            if (true) { //TODO UMM DAWN??? - epic
                drawCenteredString(matrixStack, this.font, new StringTextComponent("x" + effect.getValue()), buttonStartX, buttonY, 14737632);
            } else {
                drawCenteredString(matrixStack, this.font, new StringTextComponent("+" + effect.getValue()), buttonStartX, buttonY, 14737632);
            }
            buttonStartX += buttonWidth + padding;
            if (widget.isHovered()) widget.renderToolTip(matrixStack, mouseX - this.leftPos, mouseY - this.topPos);
        }
        StringTextComponent fluidCount;
        DecimalFormat decimalFormat = new DecimalFormat("##0.0");
        if (tileEntity.getFluidTank().getFluidAmount() < 1000f || Screen.hasShiftDown()) {
            fluidCount = new StringTextComponent(decimalFormat.format(tileEntity.getFluidTank().getFluidAmount()) + " mb");
        } else {
            fluidCount = new StringTextComponent(decimalFormat.format(tileEntity.getFluidTank().getFluidAmount() / 1000f) + " B");
        }
        if (mouseX >= this.leftPos + 207 && mouseX <= this.leftPos + 221 && mouseY >= this.topPos + 30 && mouseY <= this.topPos + 92) {
            renderTooltip(matrixStack, fluidCount, mouseX - this.leftPos, mouseY - this.topPos);
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class Button extends AbstractButton {
        private boolean selected;
        private final ResourceLocation texture;

        protected Button(int xPos, int yPos, ResourceLocation texture) {
            super(xPos, yPos, 22, 22, StringTextComponent.EMPTY);
            this.texture = texture;
        }

        @Override
        public void renderButton(@NotNull MatrixStack matrixStack, int xPos, int yPos, float v) {
            Minecraft.getInstance().getTextureManager().bind(texture);
            //noinspection deprecation
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = 0;
            int j = 0;
            if (!this.active) {
                j += this.width * 2;
            } else if (this.selected) {
                j += this.width;
            } else if (this.isHovered()) {
                j += this.width * 3;
            }

            blit(matrixStack, this.x, this.y, j, i, this.width, this.height, 22 * 4, 22);
            this.renderExtra(matrixStack);
        }

        protected abstract void renderExtra(MatrixStack matrixStack);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class PowerButton extends EnderBeeconScreen.Button {
        private final Effect effect;
        private final TextureAtlasSprite effectSprite;
        private final ITextComponent textComponent;

        public PowerButton(int xPos, int yPos, Effect effect, ResourceLocation texture) {
            super(xPos, yPos, texture);
            this.effect = effect;
            this.effectSprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
            this.textComponent = this.getTooltip(effect);
        }

        private ITextComponent getTooltip(Effect effect) {
            return new TranslationTextComponent(effect.getDescriptionId());
        }

        public void onPress() {
            NetPacketHandler.sendToServer(new UpdateBeeconMessage(effect.getRegistryName(), !this.isSelected(), tileEntity.getBlockPos()));
            this.setSelected(!this.isSelected());

            EnderBeeconScreen.this.tick();
            EnderBeeconScreen.this.init();
        }

        @Override
        public void renderToolTip(@NotNull MatrixStack matrixStack, int xPos, int yPos) {
            EnderBeeconScreen.this.renderTooltip(matrixStack, this.textComponent, xPos, yPos);
        }

        protected void renderExtra(MatrixStack matrixStack) {
            Minecraft.getInstance().getTextureManager().bind(this.effectSprite.atlas().location());
            blit(matrixStack, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.effectSprite);
        }
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }
}
