package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.trait;

import com.teamresourceful.resourcefulbees.api.data.trait.PotionEffect;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import com.teamresourceful.resourcefulbees.common.lib.util.MathUtils;
import net.minecraft.client.Minecraft;
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

public final class TraitEffectRow extends AbstractWidget {

    private static final int HEIGHT = 20;
    private static final int EFFECT_SIZE = 18;

    private final PotionEffect effect;

    public TraitEffectRow(PotionEffect effect, int width) {
        super(0, 0, width, HEIGHT, Component.empty());

        this.effect = effect;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        MobEffect mobEffect = effect.effect();
        Identifier sprite = BeepediaScreenUtil.getMobEffectSprite(mobEffect);

        int iconX = getX() + 1;
        int iconY = getY() + 1;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, iconX, iconY, EFFECT_SIZE, EFFECT_SIZE);

        graphics.text(Minecraft.getInstance().font, effectName(), getX() + 24, getY() + 5, mobEffect.getCategory() == MobEffectCategory.HARMFUL ? 0xFFFF5555 : 0xFF55FF7F);
    }

    private Component effectName() {
        MutableComponent name = effect.effect().getDisplayName().copy();

        if (effect.strength() > 0) {
            name.append(" " + MathUtils.createRomanNumeral(effect.strength() + 1));
        }

        return name;
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