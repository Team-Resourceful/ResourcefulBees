package com.teamresourceful.resourcefulbees.client.screen;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.client.component.BasicImageButton;
import com.teamresourceful.resourcefulbees.client.component.BeeconEffectWidget;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.EnderBeeconBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeconTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconSettingPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EnderBeeconScreen extends AbstractContainerScreen<EnderBeeconMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("ender_beecon/background");

    private static final int EFFECT_X = 9;
    private static final int EFFECT_TOP = 17;
    private static final int EFFECT_BOTTOM = 107;
    private static final int EFFECT_ROW_HEIGHT = 22;

    private int effectScroll;

    private final EnderBeeconBlockEntity tileEntity;
    private final List<BeeconEffectWidget> effectWidgets = new ArrayList<>();

    public EnderBeeconScreen(EnderBeeconMenu screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn, 230, 200);
        this.tileEntity = screenContainer.getEntity();
        this.inventoryLabelX = 36;
        this.inventoryLabelY = 107;
        this.titleLabelX = 110;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();

        var state = menu.getEntity().getBlockState();

        addRenderableWidget(new BasicImageButton(leftPos+109, topPos+84, 52, 200, state.hasProperty(EnderBeeconBlock.SOUND) && !state.getValue(EnderBeeconBlock.SOUND), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.SOUND, selected ? 0 : 1, menu.getEntity().getBlockPos()));
            }
        });
        addRenderableWidget(new BasicImageButton(leftPos+132, topPos+84, 92, 200, state.hasProperty(EnderBeeconBlock.BEAM) && !state.getValue(EnderBeeconBlock.BEAM), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.BEAM, selected ? 0 : 1, menu.getEntity().getBlockPos()));
            }
        });
        addRenderableWidget(new RangeSlider(leftPos + 155, topPos + 84, (menu.getEntity().getRange() - 10f) / 40f));
        addEffectButtons();
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        if (tileEntity != null) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 138, 200, this.leftPos + 100, this.topPos + 17, 6, 27);
        }
    }

    private boolean isEffectVisible(int y, int height) {
        int top = topPos + EFFECT_TOP;
        int bottom = topPos + EFFECT_BOTTOM;

        return y >= top && y + height <= bottom;
    }

    private void addEffectButtons() {
        effectWidgets.clear();

        for (Pair<Holder<MobEffect>, Float> effect : tileEntity.availableEffects()) {
            BeeconEffectWidget button = new BeeconEffectWidget(
                    leftPos + EFFECT_X,
                    topPos + EFFECT_TOP,
                    effect,
                    menu.getEntity(),
                    leftPos,
                    topPos + EFFECT_TOP,
                    leftPos + 100,
                    topPos + EFFECT_BOTTOM
            );

            button.setSelected(tileEntity.isEffectActive(effect));

            effectWidgets.add(button);
            addRenderableWidget(button);
        }

        updateEffectPositions();
    }

    private void updateEffectPositions() {
        for (int i = 0; i < effectWidgets.size(); i++) {
            BeeconEffectWidget widget = effectWidgets.get(i);

            int y = topPos + EFFECT_TOP
                    + (i - effectScroll) * EFFECT_ROW_HEIGHT;

            widget.setY(y);
            widget.active = isEffectVisible(y, widget.getHeight());
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int left = leftPos;
        int right = leftPos + 100;
        int top = topPos + EFFECT_TOP;
        int bottom = topPos + EFFECT_BOTTOM;

        if (mouseX >= left && mouseX < right
                && mouseY >= top && mouseY < bottom) {

            int visibleRows = (EFFECT_BOTTOM - EFFECT_TOP) / EFFECT_ROW_HEIGHT;
            int maxScroll = Math.max(
                    0,
                    tileEntity.availableEffects().size() - visibleRows
            );

            int oldScroll = effectScroll;

            if (scrollY < 0) {
                effectScroll = Math.min(effectScroll + 1, maxScroll);
            } else if (scrollY > 0) {
                effectScroll = Math.max(effectScroll - 1, 0);
            }

            if (effectScroll != oldScroll) {
                updateEffectPositions();
            }

            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        boolean active = tileEntity.isActive();

        graphics.text(font, BeeconTranslations.PRIMARY_LABEL, 10, 6, 0xFF404040, false);

        graphics.text(font, BeeconTranslations.ACTIVE_LABEL, 110, 20, 0xFFE0E0E0, false);
        graphics.text(font, Component.literal(active ? "Yes" : "No"), 160, 20, active ? 0xFF00B800 : 0xFFBC0000, false);

        graphics.text(font, BeeconTranslations.DRAIN_LABEL, 110, 32, 0xFFE0E0E0, false);
        graphics.text(font, Component.literal(tileEntity.drainAmount() + " mb/t"), 141, 32, 0xFFFFA00C, false);

        graphics.text(font, BeeconTranslations.RANGE_LABEL, 110, 44, 0xFFE0E0E0, false);
        graphics.text(font, Component.literal(tileEntity.getRange() + " blocks"), 145, 44, 0xFF0087FF, false);

        FluidStack fluid = menu.getEntity().clientFluid();

        graphics.text(font, BeeconTranslations.FLUID_LABEL, 110, 56, 0xFFE0E0E0);
        graphics.text(font, fluid.isEmpty() ? BeeconTranslations.NO_FLUID_LABEL : fluid.getHoverName(), 137, 56, 0xFFFF9C0C);
        graphics.text(font, BeeconTranslations.FLUID_AMOUNT_LABEL, 110, 68, 0xFFE0E0E0);
        graphics.text(font, fluid.amount() + "mB", 148, 68, 0xFF00B800);
    }

    //    @Override
//    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
//        super.renderLabels(graphics, mouseX, mouseY);
//        graphics.drawString(this.font, BeeconTranslations.PRIMARY_LABEL, 10, 6, 4210752, false);
//
//        graphics.drawString(font, BeeconTranslations.ACTIVE_LABEL, 110, 20, 14737632);
//        graphics.drawString(font, tileEntity.doEffects() ? "Yes" : "No", 160, 20, tileEntity.doEffects() ? 47104 : 12320768);
//        graphics.drawString(font, BeeconTranslations.DRAIN_LABEL, 110, 32, 14737632);
//        graphics.drawString(font, tileEntity.getDrain() + " mb/t", 141, 32, 16751628);
//        graphics.drawString(font, BeeconTranslations.RANGE_LABEL, 110, 44, 14737632);
//        graphics.drawString(font, tileEntity.getRange() + " blocks", 145, 44, 34815);
//        //TODO FLUID SHIT
//        *//*FluidHolder holder = menu.getEntity().getFluid();
//
//        graphics.drawString(font, BeeconTranslations.FLUID_LABEL, 110, 56, 14737632);
//        graphics.drawString(font, holder.isEmpty() ? BeeconTranslations.NO_FLUID_LABEL : ClientFluidHooks.getDisplayName(holder), 137, 56, 16751628);
//        graphics.drawString(font, BeeconTranslations.FLUID_AMOUNT_LABEL, 110, 68, 14737632);
//        graphics.drawString(font, FluidHooks.toMillibuckets(holder.getFluidAmount()) +"mB", 148, 68, 47104);*//*
//    }

//    @Override
//    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
//        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
//        super.render(graphics, mouseX, mouseY, partialTicks);
//        this.renderTooltip(graphics, mouseX, mouseY);
//        for (BeeconEffectWidget widget : this.powerButtons) {
//            widget.render(graphics, mouseX, mouseY, partialTicks);
//        }
//    }

//    @Override
//    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
//        return this.getFocused() != null && this.isDragging() && pButton == 0 ? this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) : super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
//    }
//
//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        for (BeeconEffectWidget powerButton : this.powerButtons) powerButton.onClick(mouseX, mouseY);
//        return super.mouseClicked(mouseX, mouseY, button);
//    }*/

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
            NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.RANGE, range, EnderBeeconScreen.this.menu.getEntity().getBlockPos()));
        }
    }
}
