package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.honey;

import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleEffectData;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import com.teamresourceful.resourcefulbees.common.lib.util.MathUtils;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.jspecify.annotations.NonNull;

import java.text.NumberFormat;
import java.util.Locale;

public final class HoneyEffectRow extends AbstractWidget {

    private static final int HEIGHT = 22;
    private static final int EFFECT_SIZE = 18;

    private final HoneyBottleEffectData data;
    private final LazyHolder<MobEffect> effect;

    public HoneyEffectRow(HoneyBottleEffectData data, int width) {
        super(0, 0, width, HEIGHT, Component.empty());

        this.data = data;
        this.effect = data.effect();
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        Identifier sprite = BeepediaScreenUtil.getMobEffectSprite(effect);

        int iconX = getX() + 1;
        int iconY = getY() + 2;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, iconX, iconY, EFFECT_SIZE, EFFECT_SIZE);

        Component name = effectName();
        graphics.text(font, name, getX() + 24, getY() + 1, effect.get().getCategory() == MobEffectCategory.HARMFUL ? 0xFFFF5555 : 0xFF55FF7F);

        Component details = details();
        graphics.text(font, details, getX() + 24, getY() + 12, 0xFFAAAAAA);
    }

    private Component effectName() {
        MutableComponent name = effect.get().getDisplayName().copy();

        if (data.strength() > 0) {
            name.append(" " + MathUtils.createRomanNumeral(data.strength() + 1));
        }

        return name;
    }

    private Component details() {
        MutableComponent result = Component.literal(formatDuration(data.duration()));

        if (data.chance() > 0.0f && data.chance() < 1.0f) {
            result.append("  " + NumberFormat.getPercentInstance().format(data.chance()));
        }

        return result;
    }

    private static String formatDuration(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds %= 60;

        return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, effectName());
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }
}