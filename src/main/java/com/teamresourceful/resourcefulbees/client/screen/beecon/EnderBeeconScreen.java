package com.teamresourceful.resourcefulbees.client.screen.beecon;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.client.component.BeeconEffectWidget;
import com.teamresourceful.resourcefulbees.client.component.BeeconToggleWidget;
import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.client.screen.BaseScreen;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.EnderBeeconBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconSettingPacket;
import earth.terrarium.olympus.client.components.base.ListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.layouts.FrameLayout;
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

public class EnderBeeconScreen extends BaseScreen<EnderBeeconMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("ender_beecon/background");

    //  RID: Removed all the constants as I needed more control over a lot of the numbers.
    // Current scroll position, in pixels
    private double effectScroll = 0.0;

    private final EnderBeeconBlockEntity tileEntity;
    private final List<BeeconEffectWidget> effectWidgets = new ArrayList<>();

    public EnderBeeconScreen(EnderBeeconMenu screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn);
        this.tileEntity = screenContainer.getEntity();
        this.titleLabelX = 7;
        this.inventoryLabelX = 7;
        this.inventoryLabelY = 103;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();

        var state = menu.getEntity().getBlockState();

        addRenderableWidget(TankWidget.single(leftPos + 20, topPos + 27, 12, 54, tileEntity::tankData));

        addRenderableWidget(new BeeconToggleWidget(leftPos + 76, topPos + 21, 180, 0, 16, 19, state.hasProperty(EnderBeeconBlock.SOUND) && !state.getValue(EnderBeeconBlock.SOUND), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.SOUND, selected ? 0 : 1, menu.getEntity().getBlockPos()));
            }
        });
        addRenderableWidget(new BeeconToggleWidget(leftPos + 93, topPos + 21, 196, 0, 16, 19, state.hasProperty(EnderBeeconBlock.BEAM) && !state.getValue(EnderBeeconBlock.BEAM), BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.BEAM, selected ? 0 : 1, menu.getEntity().getBlockPos()));
            }
        });

        addRenderableWidget(new BeeconToggleWidget(leftPos + 110, topPos + 21, 212, 0, 16, 19, tileEntity.target() == EnderBeeconBlockEntity.Target.BEE, BACKGROUND) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
            NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.TARGET, selected ? 0 : 1, menu.getEntity().getBlockPos()));
            }
        });
        addRenderableWidget(new RangeSlider(leftPos + 78, topPos + 68, (menu.getEntity().getRange() - 10f) / 40f));
        addEffectButtons();

        addRenderableWidget(new BeeconScrollbarWidget(
                leftPos + 69,
                topPos + 27,
                5,
                55,
                10,
                228,
                0,
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
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, this.leftPos, this.topPos, 180, 200);
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
        return 50;
    }

    private int getEffectContentHeight() {
        return effectWidgets.size() * 24;
    }

    private double getMaxEffectScroll() {
        return Math.max(0, getEffectContentHeight() - getEffectViewportHeight());
    }

    private void addEffectButtons() {
        effectWidgets.clear();

        for (Pair<Holder<MobEffect>, Float> effect : tileEntity.availableEffects()) {
            BeeconEffectWidget button = new BeeconEffectWidget(
                    leftPos + 43,
                    topPos + 26,
                    effect,
                    menu.getEntity(),
                    leftPos + 43,
                    topPos + 26,
                    leftPos + 67, //todo chnage this to be narrower in scope
                    topPos + 82
            );

            button.setSelected(tileEntity.isEffectActive(effect));

            effectWidgets.add(button);
            addRenderableWidget(button);
        }

        updateEffectPositions();
    }

    private void updateEffectPositions() {
        int top = topPos + 26;
        int bottom = topPos + 82;

        for (int i = 0; i < effectWidgets.size(); i++) {
            BeeconEffectWidget widget = effectWidgets.get(i);

            int y = top + i * 25 - (int) Math.round(effectScroll);

            widget.setY(y);

            // A widget is visible if ANY part intersects the viewport.
            boolean visible = y < bottom && y + widget.getHeight() > top;

            widget.visible = visible;
            widget.active = visible;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int left = leftPos + 43;
        int right = leftPos + 74;
        int top = topPos + 26;
        int bottom = topPos + 82;

        if (mouseX >= left && mouseX < right
                && mouseY >= top && mouseY < bottom) {

            setEffectScroll(effectScroll - scrollY * 12.0);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public class RangeSlider extends AbstractSliderButton {

        public RangeSlider(int pX, int pY, double pValue) {
            super(pX, pY-1, 29, 8, Component.empty(), pValue);
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
            // RID: Removed unnecessary blits and added some logic for the 3D Beecon
            // Range Slider
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND,256, 256, 233, 0, this.getX() + (int)(this.value * (double)(this.width - 3)), this.getY(), 3, 8, ARGB.white(this.alpha));
            graphics.text(Minecraft.getInstance().font, getMessage(), getX() + 18, getY() + 11, 0xFF995b35, false);
            graphics.text(Minecraft.getInstance().font, getMessage(), getX() + 18, getY() + 10, 0xFF683e36, false);

            // RID: Blit-ing an empty tank sprite on top of the rendered fluid to keep the shine in the foreground.
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 228, 10, leftPos + 19, topPos + 26, 12, 54);

            var state = menu.getEntity().getBlockState();

            if(state.getValue(EnderBeeconBlock.SOUND)) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 228, 66, leftPos + 139, topPos + 47, 17, 6);
            }
            if(state.getValue(EnderBeeconBlock.BEAM)) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 242, 0, leftPos + 143, topPos + 21, 9, 36);
            }
            if(tileEntity.target() == EnderBeeconBlockEntity.Target.BEE) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 228, 72, leftPos + 153, topPos + 63, 10, 14);
            }
            if(tileEntity.target() == EnderBeeconBlockEntity.Target.PLAYER) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 238, 72, leftPos + 132, topPos + 63, 10, 14);
            }
            this.handleCursor(graphics);
        }
    }
}
