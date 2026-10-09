package com.teamresourceful.resourcefulbees.client.component;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeconTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconEffectPacket;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

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
    private final int clipLeft;
    private final int clipTop;
    private final int clipRight;
    private final int clipBottom;

    public BeeconEffectWidget(int x, int y, Pair<Holder<MobEffect>, Float> effect, EnderBeeconBlockEntity tile, int clipLeft, int clipTop, int clipRight, int clipBottom) {
        super(x, y, 27, 23, BeeconTranslations.BEECON_EFFECT_BUTTON);
        this.tile = tile;
        this.effect = effect;
        this.effectSprite = Hud.getMobEffectSprite(effect.getFirst());

        MutableComponent name = effect.getFirst().value().getDisplayName().copy();
        MutableComponent drains = Component.literal("%nDrains: %smb/t".formatted(effect.getSecond()));
        setTooltip(Tooltip.create(name.append(drains)));
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
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 178, selected ? 112 : 136, getX(), getY() + 1, 27, 23);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, effectSprite, getX() + 2, getY() + 2, 18, 18);
        } finally {
            graphics.disableScissor();
        }
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        selected = !selected;
        NetworkHandler.NETWORK.sendToServer(new BeeconEffectPacket(
                selected ? BeeconPacketOption.EFFECT_ON : BeeconPacketOption.EFFECT_OFF,
                effect,
                tile.getBlockPos()
        ));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!visible || !active) {
            return false;
        }

        boolean insideViewport =
                mouseX >= clipLeft
                        && mouseX < clipRight
                        && mouseY >= clipTop
                        && mouseY < clipBottom;

        return insideViewport && super.isMouseOver(mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        output.add(NarratedElementType.HINT, effect.getFirst().value().getDescriptionId());
        output.add(NarratedElementType.HINT, "Is active: %s".formatted(isActive()));
        // document why this method is empty
    }
}
