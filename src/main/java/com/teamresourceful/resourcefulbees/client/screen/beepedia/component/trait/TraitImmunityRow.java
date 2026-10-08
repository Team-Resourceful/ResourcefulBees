package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.trait;

import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import org.jspecify.annotations.NonNull;

public final class TraitImmunityRow extends AbstractWidget {

    private static final int HEIGHT = 20;
    private static final int EFFECT_SIZE = 18;

    private final MobEffect effect;

    public TraitImmunityRow(MobEffect effect, int width) {
        super(0, 0, width, HEIGHT, Component.empty());

        this.effect = effect;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        Identifier sprite = BeepediaScreenUtil.getMobEffectSprite(effect);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX() + 1, getY() + 1, EFFECT_SIZE, EFFECT_SIZE);
        graphics.text(font, effect.getDisplayName(), getX() + 24, getY() + 5, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, effect.getDisplayName());
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }
}