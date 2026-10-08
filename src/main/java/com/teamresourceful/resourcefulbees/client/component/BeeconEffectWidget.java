package com.teamresourceful.resourcefulbees.client.component;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeconTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.lib.util.MathUtils;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconEffectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

public class BeeconEffectWidget extends AbstractWidget {

    private static final Identifier BACKGROUND = ModIdentifier.of("ender_beecon/background");

    private static final Tooltip ACTIVE_TOOLTIP =
            Tooltip.create(BeeconTranslations.EFFECT_ACTIVE);

    private static final Tooltip INACTIVE_TOOLTIP =
            Tooltip.create(BeeconTranslations.EFFECT_INACTIVE);

    private final EnderBeeconBlockEntity tile;
    private final Pair<Holder<MobEffect>, Float> effect;
    private boolean selected;
    private final Identifier effectSprite;
    private final Tooltip effectTooltip;
    private final int clipLeft;
    private final int clipTop;
    private final int clipRight;
    private final int clipBottom;

    public BeeconEffectWidget(int x, int y, Pair<Holder<MobEffect>, Float> effect, EnderBeeconBlockEntity tile, int clipLeft, int clipTop, int clipRight, int clipBottom) {
        super(x, y, 88, 22, BeeconTranslations.BEECON_EFFECT_BUTTON);
        this.tile = tile;
        this.effect = effect;
        this.effectSprite = Hud.getMobEffectSprite(effect.getFirst());
        this.effectTooltip = Tooltip.create(effect.getFirst().value().getDisplayName());
        this.clipLeft = clipLeft;
        this.clipTop = clipTop;
        this.clipRight = clipRight;
        this.clipBottom = clipBottom;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.enableScissor(clipLeft, clipTop, clipRight, clipBottom);
        try {
            Minecraft mc = Minecraft.getInstance();

            boolean buttonHover = inBounds(mouseX, mouseY);
            boolean spriteHover = inSpriteBounds(mouseX, mouseY);

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, selected ? 0 : 26, buttonHover ? 216 : 200, getX() + 59, getY() + 3, 26, 16);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, effectSprite, getX() + 2, getY() + 2, 18, 18);
            graphics.text(mc.font, Component.literal("+" + effect.getSecond()), this.getX() + 24, this.getY() + 6, 0xFFE0E0E0);

            if (spriteHover) {
                setTooltip(effectTooltip);
            } else if (buttonHover) {
                setTooltip(selected ? ACTIVE_TOOLTIP : INACTIVE_TOOLTIP);
            } else {
                setTooltip(null);
            }
        } finally {
            graphics.disableScissor();
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
        output.add(NarratedElementType.HINT, effect.getFirst().value().getDescriptionId());
        output.add(NarratedElementType.HINT, "Is active: %s".formatted(isActive()));
        // document why this method is empty
    }
}
