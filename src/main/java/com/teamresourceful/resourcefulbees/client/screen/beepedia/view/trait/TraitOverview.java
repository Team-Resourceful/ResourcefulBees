package com.teamresourceful.resourcefulbees.client.screen.beepedia.view.trait;

import com.teamresourceful.resourcefulbees.api.data.trait.PotionEffect;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitDamageType;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.trait.TraitAuraRow;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.trait.TraitEffectRow;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.trait.TraitImmunityRow;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.string.TextWidget;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import java.util.Comparator;
import java.util.Objects;

public final class TraitOverview {

    private TraitOverview() {
    }

    public static void build(LinearViewLayout layout, Trait trait, int contentWidth) {
        layout.withGap(6);

        TraitHeader.build(layout, trait);

        buildAttackEffects(layout, trait, contentWidth);
        buildDamageTypes(layout, trait);
        buildDamageImmunities(layout, trait);
        buildPotionImmunities(layout, trait, contentWidth);
        buildSpecialAbilities(layout, trait, contentWidth);
        buildParticleEffects(layout, trait);
        buildAuras(layout, trait, contentWidth);
    }

    private static void buildAttackEffects(LinearViewLayout layout, Trait trait, int contentWidth) {
        if (!trait.hasPotionDamageEffects()) {
            return;
        }

        layout.withChild(BeepediaScreenUtil.sectionTitle("Attack Effects"));

        for (PotionEffect effect : trait.potionDamageEffects()) {
            layout.withChild(new TraitEffectRow(effect, contentWidth));
        }
    }

    private static void buildDamageTypes(LinearViewLayout layout, Trait trait) {
        if (!trait.hasDamageTypes()) {
            return;
        }

        layout.withChild(BeepediaScreenUtil.sectionTitle("Attack Abilities"));

        trait.damageTypes()
                .stream()
                .sorted(Comparator.comparing(TraitDamageType::type))
                .forEach(type -> layout.withChild(BeepediaScreenUtil.property(Component.literal(humanize(type.type())), Component.literal("Amplifier: " + type.amplifier()))));
    }

    private static void buildDamageImmunities(LinearViewLayout layout, Trait trait) {
        if (!trait.hasDamageImmunities()) {
            return;
        }

        layout.withChild(BeepediaScreenUtil.sectionTitle("Damage Immunities"));

        trait.damageImmunities()
                .stream()
                .sorted()
                .map(TraitOverview::humanize)
                .forEach(name -> layout.withChild(Widgets.text(Component.literal(name).withColor(TextColor.WHITE), TextWidget::withLeftAlignment)));
    }

    private static void buildPotionImmunities(LinearViewLayout layout, Trait trait, int contentWidth) {
        if (!trait.hasPotionImmunities()) {
            return;
        }

        layout.withChild(BeepediaScreenUtil.sectionTitle("Effect Immunities"));

        for (MobEffect effect : trait.potionImmunities()) {
            layout.withChild(new TraitImmunityRow(effect, contentWidth));
        }
    }

    private static void buildSpecialAbilities(LinearViewLayout layout, Trait trait, int contentWidth) {
        if (!trait.hasSpecialAbilities()) {
            return;
        }

        layout.withChild(BeepediaScreenUtil.sectionTitle("Abilities"));

        trait.specialAbilities()
                .stream()
                .sorted()
                .forEach(ability -> {
                    layout.withChild(Widgets.text(Component.literal(humanize(ability)).withColor(TextColor.WHITE), TextWidget::withLeftAlignment));
                    layout.withChild(Widgets.textarea(abilityDescription(ability), contentWidth));
                });
    }

    private static Component abilityDescription(String ability) {
        return Component.literal(
                switch (ability) {
                    case "teleport" -> "Can randomly teleport up to 4 blocks away.";
                    case "flammable" -> "Produces Blaze particle effects.";
                    case "slimy" -> "Produces slime particles and slime sounds when attacking players.";
                    case "angry" -> "Always angry at players unless affected by a calming effect.";
                    case "spider" -> "Cannot become stuck in cobwebs.";
                    default -> "No description set.";
                }
        );
    }

    private static void buildParticleEffects(LinearViewLayout layout, Trait trait) {
        if (!trait.hasParticleEffects()) {
            return;
        }

        layout.withChild(BeepediaScreenUtil.sectionTitle("Particles"));

        trait.particleEffects()
                .stream()
                .map(BuiltInRegistries.PARTICLE_TYPE::getKey)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Identifier::toString))
                .forEach(id -> layout.withChild(Widgets.text(Component.literal(humanize(id.getPath())).withColor(TextColor.WHITE), TextWidget::withLeftAlignment)));
    }

    private static void buildAuras(LinearViewLayout layout, Trait trait, int contentWidth) {
        if (!trait.hasAuras()) {
            return;
        }

        layout.withChild(BeepediaScreenUtil.sectionTitle("Auras"));

        trait.auras().stream()
                .sorted(Comparator.comparing(aura -> aura.type().name()))
                .forEach(aura -> layout.withChild(new TraitAuraRow(aura, contentWidth)));
    }

    private static String humanize(String value) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '_' || c == '-') {
                result.append(' ');
                continue;
            }

            if (i > 0 && Character.isUpperCase(c)) {
                result.append(' ');
            }

            result.append(c);
        }

        String text = result.toString().trim();

        if (text.isEmpty()) {
            return text;
        }

        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}