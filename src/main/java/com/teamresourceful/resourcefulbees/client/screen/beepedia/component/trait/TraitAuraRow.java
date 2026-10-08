package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.trait;

import com.teamresourceful.resourcefulbees.api.data.trait.Aura;
import com.teamresourceful.resourcefulbees.api.data.trait.PotionEffect;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import com.teamresourceful.resourcefulbees.common.lib.enums.AuraType;
import com.teamresourceful.resourcefulbees.common.lib.util.MathUtils;
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
import org.jspecify.annotations.NonNull;

public final class TraitAuraRow extends AbstractWidget {

    private static final int HEIGHT = 24;
    private static final int EFFECT_SIZE = 18;

    private final Aura aura;

    public TraitAuraRow(Aura aura, int width) {
        super(0, 0, width, HEIGHT, Component.empty());

        this.aura = aura;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (aura.type() == AuraType.POTION) {
            renderPotionAura(graphics);
            return;
        }

        renderStandardAura(graphics);
    }

    private void renderStandardAura(GuiGraphicsExtractor graphics) {
        Font font = Minecraft.getInstance().font;

        graphics.text(font, title(), getX(), getY() + 1, aura.isBeneficial() ? 0xFF55FF7F : 0xFFFF5555);
        graphics.text(font, details(), getX(), getY() + 12, 0xFFAAAAAA);
    }

    private void renderPotionAura(GuiGraphicsExtractor graphics) {
        Font font = Minecraft.getInstance().font;

        PotionEffect potion = aura.potionEffect();
        Identifier sprite = BeepediaScreenUtil.getMobEffectSprite(potion.effect());

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX() + 1, getY() + 2, EFFECT_SIZE, EFFECT_SIZE);
        graphics.text(font, title(), getX() + 24, getY() + 1, aura.isBeneficial() ? 0xFF55FF7F : 0xFFFF5555);
        graphics.text(font, potionDetails(potion), getX() + 24, getY() + 12, 0xFFAAAAAA);
    }

    private Component title() {
        return Component.literal(
                switch (aura.type()) {
                    case BURNING -> "Burning";
                    case POTION -> "Potion";
                    case HEALING -> "Healing";
                    case EXPERIENCE -> "Experience";
                    case DAMAGING -> "Damaging";
                    case EXPERIENCE_DRAIN -> "Experience Drain";
                }
        );
    }

    private Component details() {
        return switch (aura.type()) {
            case POTION -> potionDetails(aura.potionEffect());
            case DAMAGING -> aura.damageEffect().getDisplayName();
            case HEALING -> Component.literal("Amount: " + aura.modifier());
            case EXPERIENCE -> Component.literal("Amount: " + aura.modifier());
            case EXPERIENCE_DRAIN -> Component.literal("Amount: " + aura.modifier());
            case BURNING -> Component.literal("Sets nearby players on fire");
        };
    }

    private static Component potionDetails(PotionEffect effect) {
        MutableComponent name = effect.effect().getDisplayName().copy();

        if (effect.strength() > 0) {
            name.append(" " + MathUtils.createRomanNumeral(effect.strength() + 1));
        }

        return name;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, title());
        output.add(NarratedElementType.HINT, details());
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }
}