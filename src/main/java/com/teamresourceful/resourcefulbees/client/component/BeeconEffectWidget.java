package com.teamresourceful.resourcefulbees.client.component;

import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeconTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconEffect;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconEffectPacket;
import com.teamresourceful.resourcefulbees.common.lib.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class BeeconEffectWidget extends AbstractWidget {

    private static final Identifier BACKGROUND = ModIdentifier.of("textures/gui/ender_beecon/ender_beecon.png");

    private static final Tooltip ACTIVE_TOOLTIP =
            Tooltip.create(BeeconTranslations.EFFECT_ACTIVE);

    private static final Tooltip INACTIVE_TOOLTIP =
            Tooltip.create(BeeconTranslations.EFFECT_INACTIVE);

    private final EnderBeeconBlockEntity tile;
    private final BeeconEffect effect;
    private boolean selected;
    private final Identifier effectSprite;
    private final Tooltip effectTooltip;

    public BeeconEffectWidget(int x, int y, BeeconEffect effect, EnderBeeconBlockEntity tile) {
        super(x, y, 88, 22, BeeconTranslations.BEECON_EFFECT_BUTTON);
        this.tile = tile;
        this.effect = effect;
        this.effectSprite = Hud.getMobEffectSprite(effect.effectHolder());
        this.effectTooltip = Tooltip.create(effect.effect().getDisplayName());
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Minecraft mc = Minecraft.getInstance();

        boolean buttonHover = inBounds(mouseX, mouseY);
        boolean spriteHover = inSpriteBounds(mouseX, mouseY);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, getX() + 59, getY() + 3, selected ? 0 : 26, buttonHover ? 216 : 200, 26, 16, 256, 256);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, effectSprite, getX() + 2, getY() + 2, 18, 18);
        graphics.text(mc.font, Component.literal("+" + effect.drainAmount()), this.getX() + 24, this.getY() + 6, 0xFFE0E0E0);

        if (spriteHover) {
            setTooltip(effectTooltip);
        } else if (buttonHover) {
            setTooltip(selected ? ACTIVE_TOOLTIP : INACTIVE_TOOLTIP);
        } else {
            setTooltip(null);
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (inBounds(event.x(), event.y())) {
            selected = !selected;
            NetworkHandler.NETWORK.sendToServer(new BeeconEffectPacket(
                    selected ? BeeconPacketOption.EFFECT_ON : BeeconPacketOption.EFFECT_OFF,
                    effect,
                    tile.getBlockPos()
            ));
        }
    }

    private boolean inBounds(double x, double y) {
        return MathUtils.inRangeInclusive(x, getX() + 59d, getX() + 84d)
                && MathUtils.inRangeInclusive(y, getY() + 3d, getY() + 18d);
    }

    private boolean inSpriteBounds(double x, double y) {
        return MathUtils.inRangeInclusive(x, getX() + 2d, getX() + 19d)
                && MathUtils.inRangeInclusive(y, getY() + 2d, getY() + 19d);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        // document why this method is empty
    }
}
