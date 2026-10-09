package com.teamresourceful.resourcefulbees.client.screen.beecon;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.client.component.BeeconEffectWidget;
import com.teamresourceful.resourcefulbees.client.component.BeeconToggleWidget;
import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.EnderBeeconBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconSettingPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EnderBeeconScreen extends AbstractContainerScreen<EnderBeeconMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("ender_beecon/background");

    private static final int SPRITE_SIZE = 256;
    private static final int EFFECT_X = 41;
    private static final int EFFECT_TOP = 20;
    private static final int EFFECT_BOTTOM = 89;
    private static final int EFFECT_ROW_HEIGHT = 24;
    private static final double EFFECT_SCROLL_SPEED = 12.0;

    private static final int SCROLLBAR_Y = 22;
    private static final int SCROLLBAR_X = 69;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_HEIGHT = 4;

    // Sprite location within your existing 256x256 texture
    private static final int SCROLLBAR_U = 178;
    private static final int SCROLLBAR_V = 108;

    // Current scroll position, in pixels
    private double effectScroll = 0.0;

    private final EnderBeeconBlockEntity tileEntity;
    private final List<BeeconEffectWidget> effectWidgets = new ArrayList<>();

    public EnderBeeconScreen(EnderBeeconMenu screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn, 180, 199);
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

        addRenderableWidget(TankWidget.single(leftPos+17, topPos+27, 14, 56, tileEntity::tankData));

        addRenderableWidget(new BeeconToggleWidget(leftPos+80, topPos+EFFECT_TOP +1, 180, 0, state.hasProperty(EnderBeeconBlock.SOUND) && !state.getValue(EnderBeeconBlock.SOUND), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.SOUND, selected ? 0 : 1, menu.getEntity().getBlockPos()));
            }
        });
        addRenderableWidget(new BeeconToggleWidget(leftPos+95, topPos+EFFECT_TOP +1, 194, 0, state.hasProperty(EnderBeeconBlock.BEAM) && !state.getValue(EnderBeeconBlock.BEAM), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.BEAM, selected ? 0 : 1, menu.getEntity().getBlockPos()));
            }
        });

        addRenderableWidget(new BeeconToggleWidget(leftPos+110, topPos+EFFECT_TOP+1, 209, 0, tileEntity.target() == EnderBeeconBlockEntity.Target.BEE, BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
            NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.TARGET, selected ? 0 : 1, menu.getEntity().getBlockPos()));
            }
        });
        addRenderableWidget(new RangeSlider(leftPos + 80, topPos + 75, (menu.getEntity().getRange() - 10f) / 40f));
        addEffectButtons();

        addRenderableWidget(new BeeconScrollbarWidget(
                leftPos + SCROLLBAR_X,
                topPos + SCROLLBAR_Y,
                SCROLLBAR_WIDTH,
                EFFECT_BOTTOM - SCROLLBAR_Y-2,
                SCROLLBAR_HEIGHT,
                SCROLLBAR_U,
                SCROLLBAR_V,
                BACKGROUND,
                () -> effectScroll,
                this::getMaxEffectScroll,
                this::setEffectScroll
        ));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        if (tileEntity != null) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, SPRITE_SIZE, SPRITE_SIZE, 0, 0, this.leftPos, this.topPos, 180, 199);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, SPRITE_SIZE, SPRITE_SIZE, 180, 57, leftPos+124, topPos+40, 42, 47);
        }
    }

    private void setEffectScroll(double scroll) {
        double clamped = Math.clamp(
                scroll,
                0.0,
                getMaxEffectScroll()
        );

        if (effectScroll != clamped) {
            effectScroll = clamped;
            updateEffectPositions();
        }
    }

    private int getEffectViewportHeight() {
        return EFFECT_BOTTOM - EFFECT_TOP;
    }

    private int getEffectContentHeight() {
        return effectWidgets.size() * EFFECT_ROW_HEIGHT;
    }

    private double getMaxEffectScroll() {
        return Math.max(0, getEffectContentHeight() - getEffectViewportHeight());
    }

    private void addEffectButtons() {
        effectWidgets.clear();

        for (Pair<Holder<MobEffect>, Float> effect : tileEntity.availableEffects()) {
            BeeconEffectWidget button = new BeeconEffectWidget(
                    leftPos + EFFECT_X,
                    topPos + EFFECT_TOP,
                    effect,
                    menu.getEntity(),
                    leftPos + EFFECT_X,
                    topPos + EFFECT_TOP,
                    leftPos + 100, //todo chnage this to be narrower in scope
                    topPos + EFFECT_BOTTOM
            );

            button.setSelected(tileEntity.isEffectActive(effect));

            effectWidgets.add(button);
            addRenderableWidget(button);
        }

        updateEffectPositions();
    }

    private void updateEffectPositions() {
        int top = topPos + EFFECT_TOP;
        int bottom = topPos + EFFECT_BOTTOM;

        for (int i = 0; i < effectWidgets.size(); i++) {
            BeeconEffectWidget widget = effectWidgets.get(i);

            int y = top + i * EFFECT_ROW_HEIGHT - (int) Math.round(effectScroll);

            widget.setY(y);

            // A widget is visible if ANY part intersects the viewport.
            boolean visible = y < bottom
                    && y + widget.getHeight() > top;

            widget.visible = visible;
            widget.active = visible;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int left = leftPos + EFFECT_X;
        int right = leftPos + 100;
        int top = topPos + EFFECT_TOP;
        int bottom = topPos + EFFECT_BOTTOM;

        if (mouseX >= left && mouseX < right
                && mouseY >= top && mouseY < bottom) {

            setEffectScroll(effectScroll - scrollY * EFFECT_SCROLL_SPEED);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public class RangeSlider extends AbstractSliderButton {

        public RangeSlider(int pX, int pY, double pValue) {
            super(pX, pY, 42, 11, Component.empty(), pValue);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(String.valueOf((int)((value * 40)+10))));
            //setMessage(Component.translatable(BeeconTranslations.EFFECT_RANGE, (int)((value * 40)+10)));
        }

        @Override
        protected void applyValue() {
            int range = (int)(value * 40)+10;
            NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.RANGE, range, EnderBeeconScreen.this.menu.getEntity().getBlockPos()));
        }

        @Override
        public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 180, 45, this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND,256, 256, 222, 45, this.getX() + (int)(this.value * (double)(this.width - 3)), this.getY()+2, 3, 7, ARGB.white(this.alpha));
            graphics.text(Minecraft.getInstance().font, getMessage(), getX() + 45, getY() +2, 0xFF683E36, false);
            //this.extractScrollingStringOverContents(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE), this.getMessage(), 20);
            this.handleCursor(graphics);
        }


    }
}
