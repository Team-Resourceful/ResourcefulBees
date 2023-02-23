package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.component.BasicImageButton;
import com.teamresourceful.resourcefulbees.client.components.BeeconEffectWidget;
import com.teamresourceful.resourcefulbees.common.block.EnderBeecon;
import com.teamresourceful.resourcefulbees.common.blockentity.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.inventory.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeconTranslations;
import com.teamresourceful.resourcefulbees.common.network.packets.client.BeeconChangePacket;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EnderBeeconScreen extends AbstractContainerScreen<EnderBeeconMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/ender_beecon/ender_beecon.png");

    private final EnderBeeconBlockEntity tileEntity;

    public EnderBeeconScreen(EnderBeeconMenu screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn);
        this.tileEntity = screenContainer.getEntity();
        this.imageWidth = 230;
        this.imageHeight = 200;
        this.inventoryLabelX = 36;
        this.inventoryLabelY = 107;
        this.titleLabelX = 110;
    }

    private final List<BeeconEffectWidget> powerButtons = new LinkedList<>();

    @Override
    protected void init() {
        super.init();
        clearWidgets();

        BlockState state = menu.getEntity().getBlockState();

        addRenderableWidget(new BasicImageButton(leftPos+109, topPos+84, 52, 200, state.hasProperty(EnderBeecon.SOUND) && !state.getValue(EnderBeecon.SOUND), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetworkHandler.CHANNEL.sendToServer(new BeeconChangePacket(BeeconChangePacket.Option.SOUND, !selected, menu.getEntity().getBlockPos()));
            }
        });
        addRenderableWidget(new BasicImageButton(leftPos+132, topPos+84, 92, 200, state.hasProperty(EnderBeecon.BEAM) && !state.getValue(EnderBeecon.BEAM), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetworkHandler.CHANNEL.sendToServer(new BeeconChangePacket(BeeconChangePacket.Option.BEAM, !selected, menu.getEntity().getBlockPos()));
            }
        });
        addRenderableWidget(new RangeSlider(leftPos + 155, topPos + 84, (menu.getEntity().getRange() - 10f) / 40f));
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        if (tileEntity != null) {
            RenderUtils.bindTexture(BACKGROUND);
            this.blit(matrix, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
            this.blit(matrix, this.leftPos+100, this.topPos+17, 138, 200, 6, 27);
        }
        drawButtons();
    }

    private void drawButtons() {
        int buttonStartY = this.topPos + 17;
        powerButtons.clear();
        for (MobEffect allowedEffect : EnderBeeconBlockEntity.ALLOWED_EFFECTS) {
            BeeconEffectWidget button = new BeeconEffectWidget(this, this.leftPos + 9, buttonStartY, allowedEffect, menu.getEntity());
            button.active = true;
            button.setSelected(tileEntity.hasEffect(allowedEffect));
            powerButtons.add(button);
            buttonStartY += 22;
        }
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        this.font.draw(matrixStack, BeeconTranslations.PRIMARY_LABEL, 10, 6, 4210752);

        drawString(matrixStack, font, BeeconTranslations.ACTIVE_LABEL, 110, 20, 14737632);
        drawString(matrixStack, font, tileEntity.doEffects() ? "Yes" : "No", 160, 20, tileEntity.doEffects() ? 47104 : 12320768);
        drawString(matrixStack, font, BeeconTranslations.DRAIN_LABEL, 110, 32, 14737632);
        drawString(matrixStack, font, tileEntity.getDrain() + " mb/t", 141, 32, 16751628);
        drawString(matrixStack, font, BeeconTranslations.RANGE_LABEL, 110, 44, 14737632);
        drawString(matrixStack, font, tileEntity.getRange() + " blocks", 145, 44, 34815);

        FluidStack fluidStack = menu.getEntity().getTank().getFluid();

        drawString(matrixStack, font, BeeconTranslations.FLUID_LABEL, 110, 56, 14737632);
        drawString(matrixStack, font, fluidStack.isEmpty() ? BeeconTranslations.NO_FLUID_LABEL : fluidStack.getDisplayName(), 137, 56, 16751628);
        drawString(matrixStack, font, BeeconTranslations.FLUID_AMOUNT_LABEL, 110, 68, 14737632);
        drawString(matrixStack, font, fluidStack.getAmount()+"mB", 148, 68, 47104);
    }

    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
        for (BeeconEffectWidget widget : this.powerButtons) {
            widget.render(matrix, mouseX, mouseY, partialTicks);
            if (widget.isMouseOver(mouseX, mouseY)) widget.renderToolTip(matrix, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return this.getFocused() != null && this.isDragging() && pButton == 0 ? this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) : super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (BeeconEffectWidget powerButton : this.powerButtons) powerButton.onClick(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public class RangeSlider extends AbstractSliderButton {

        public RangeSlider(int pX, int pY, double pValue) {
            super(pX, pY, 66, 20, Component.empty(), pValue);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable(BeeconTranslations.EFFECT_RANGE, (int)((value * 40)+10)));
        }

        @Override
        protected void applyValue() {
            int range = (int)(value * 40)+10;
            NetworkHandler.CHANNEL.sendToServer(new BeeconChangePacket(BeeconChangePacket.Option.RANGE, range, EnderBeeconScreen.this.menu.getEntity().getBlockPos()));
        }
    }
}
