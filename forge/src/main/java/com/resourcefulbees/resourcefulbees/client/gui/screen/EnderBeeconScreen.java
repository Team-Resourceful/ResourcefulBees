package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ModImageButton;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ToolTip;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.EnderBeeconContainer;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.UpdateBeeconMessage;
import com.resourcefulbees.resourcefulbees.network.packets.UpdateBeeconRangeMessage;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import javafx.scene.control.TextFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class EnderBeeconScreen extends AbstractContainerScreen<EnderBeeconContainer> {

    private final EnderBeeconTileEntity tileEntity;

    public EnderBeeconScreen(EnderBeeconContainer screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn);
        this.tileEntity = screenContainer.getEnderBeeconTileEntity();
        preInit();
    }

    private static final Component primaryLabel = new TranslatableComponent("block.resourcefulbees.ender_beecon.primary");
    private static final Component drainLabel = new TranslatableComponent("block.resourcefulbees.ender_beecon.drain");
    private static final Component rangeLabel = new TranslatableComponent("block.resourcefulbees.ender_beecon.range");
    private static final Component activeLabel = new TranslatableComponent("block.resourcefulbees.ender_beecon.is_active");
    private static final ResourceLocation BUTTON_CALMING = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/calming_button.png");
    public final ResourceLocation arrowImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/arrows.png");

    ModImageButton leftArrow;
    ModImageButton rightArrow;

    List<ToolTip> toolTips = new LinkedList<>();
    List<PowerButton> powerButtons = new LinkedList<>();

    int localRange;

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
        powerButtons.clear();
        toolTips.clear();
        int xPos = this.leftPos;
        int yPos = this.topPos;
        int buttonX = xPos + 24;
        int buttonY = yPos + 36;
        int buttonWidth = 22;
        int padding = 16;
        int buttonStartX = buttonX;

        leftArrow = new ModImageButton(xPos + 90, yPos + 88, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> {
            remRange();
        });
        rightArrow = new ModImageButton(xPos + 160, yPos + 88, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> {
            addRange();
        });
        localRange = tileEntity.getRange();
        addButton(rightArrow);
        addButton(leftArrow);

        for (EnderBeeconTileEntity.BeeconEffect effect : tileEntity.getEffects()) {
            PowerButton button = this.addButton(new PowerButton(buttonStartX, buttonY, effect.getEffect(), BUTTON_CALMING));
            button.active = true;
            button.setSelected(effect.isActive());
            powerButtons.add(button);
            toolTips.add(new ToolTip(buttonStartX, buttonY, 22, 22, () -> new TranslatableComponent(effect.getEffect().getDescriptionId())));
            buttonStartX += buttonWidth + padding;
        }

        // fluid tank tooltip
        toolTips.add(new ToolTip(xPos + 207, yPos + 30, 14, 62, () -> {
            DecimalFormat decimalFormat = new DecimalFormat("##0.0");
            if (tileEntity.getFluidTank().getFluidAmount() < 1000f || Screen.hasShiftDown()) {
                return new TextComponent(decimalFormat.format(tileEntity.getFluidTank().getFluidAmount()) + " mb");
            } else {
                return new TextComponent(decimalFormat.format(tileEntity.getFluidTank().getFluidAmount() / 1000f) + " B");
            }
        }));
    }

    private void addRange() {
        if (rightArrow == null) return;
        localRange++;
        if (localRange > 5) localRange = 5;
        NetPacketHandler.sendToServer(new UpdateBeeconRangeMessage(localRange, tileEntity.getBlockPos()));
    }

    private void remRange() {
        if (leftArrow == null) return;
        localRange--;
        if (localRange < 1) localRange = 1;
        NetPacketHandler.sendToServer(new UpdateBeeconRangeMessage(localRange, tileEntity.getBlockPos()));
    }


    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        for (ToolTip toolTip : toolTips) {
            toolTip.draw(this, matrix, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/ender_beecon.png");
        if (minecraft != null && tileEntity != null) {
            minecraft.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
        rightArrow.active = localRange < 5;
        leftArrow.active = localRange > 1;
        drawFluidTank(matrix);
    }

    private void drawFluidTank(PoseStack matrix) {
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
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        int buttonX = 24 + 11;
        int buttonY = 36 + 25;
        int buttonWidth = 22;
        int padding = 16;
        int buttonStartX = buttonX;
        drawCenteredString(matrixStack, this.font, primaryLabel, 92, 24, 14737632);
        this.font.draw(matrixStack, drainLabel, 13, 90, 14737632);
        this.font.draw(matrixStack, tileEntity.getDrain() + " mb/t", 44, 90, 16751628);
        int rangeLabelPadding = font.width(rangeLabel) / 2;
        this.font.draw(matrixStack, rangeLabel, ((float) 129) - rangeLabelPadding, 78, 14737632);
        MutableComponent textComponent = new TextComponent(tileEntity.getAdjustedRange() + " ").append(new TranslatableComponent("block.resourcefulbees.ender_beecon.blocks"));
        int rangePadding = font.width(textComponent) / 2;
        this.font.draw(matrixStack, textComponent, ((float) 129) - rangePadding, 90, 5592575);
        this.font.draw(matrixStack, activeLabel, 13, 78, 14737632);
        if (tileEntity.doEffects()) {
            this.font.draw(matrixStack, "Yes", 63, 78, 47104);
        } else {
            this.font.draw(matrixStack, "No", 63, 78, 12320768);
        }

        for (PowerButton widget : this.powerButtons) {
            EnderBeeconTileEntity.BeeconEffect effect = tileEntity.getEffect(widget.effect);
            drawCenteredString(matrixStack, this.font, new TextComponent("+" + effect.getValue()), buttonStartX, buttonY, 14737632);
            buttonStartX += buttonWidth + padding;
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class Button extends AbstractButton {
        private boolean selected;

        private final ResourceLocation texture;

        protected Button(int xPos, int yPos, ResourceLocation texture) {
            super(xPos, yPos, 22, 22, TextComponent.EMPTY);
            this.texture = texture;
        }

        @Override
        public void renderButton(@NotNull PoseStack matrixStack, int xPos, int yPos, float v) {
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

        protected abstract void renderExtra(PoseStack matrixStack);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

    }

    @OnlyIn(Dist.CLIENT)
    class PowerButton extends EnderBeeconScreen.Button {
        private final MobEffect effect;

        private final TextureAtlasSprite effectSprite;

        public PowerButton(int xPos, int yPos, MobEffect effect, ResourceLocation texture) {
            super(xPos, yPos, texture);
            this.effect = effect;
            this.effectSprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
        }

        public void onPress() {
            NetPacketHandler.sendToServer(new UpdateBeeconMessage(effect.getRegistryName(), !this.isSelected(), tileEntity.getBlockPos()));
            this.setSelected(!this.isSelected());
        }

        protected void renderExtra(PoseStack matrixStack) {
            Minecraft.getInstance().getTextureManager().bind(this.effectSprite.atlas().location());
            blit(matrixStack, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.effectSprite);
        }

    }
}
