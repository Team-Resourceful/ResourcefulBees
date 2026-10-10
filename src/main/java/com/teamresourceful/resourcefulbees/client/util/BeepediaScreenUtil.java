package com.teamresourceful.resourcefulbees.client.util;

import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.string.TextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public final class BeepediaScreenUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private BeepediaScreenUtil() {
    }

    public static Component yesNo(boolean value) {
        return Component.translatable(value ? "gui.yes" : "gui.no");
    }

    public static AbstractWidget property(Component label, Component value) {
        return Widgets.labelled(
                Minecraft.getInstance().font,
                label,
                Widgets.text(value, TextWidget::withRightAlignment).withColor(Color.parse("green"))
        );
    }

    public static AbstractWidget property(Component label, Component value, Component tooltip) {
        return Widgets.labelled(
                Minecraft.getInstance().font,
                label,
                Widgets.text(value, TextWidget::withRightAlignment)
                        .withColor(Color.parse("green"))
                        .withTooltip(tooltip)
        );
    }

    public static AbstractWidget property(Component label, String value) {
        return Widgets.labelled(
                Minecraft.getInstance().font,
                label,
                Widgets.text(value, TextWidget::withRightAlignment).withColor(Color.parse("green"))
        );
    }

    public static Component formatDateTime(long epochSeconds) {
        if (epochSeconds <= 0) {
            return Component.literal("N/A");
        }
        return Component.literal(DATE_TIME_FORMATTER.withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(epochSeconds)));
    }

    public static Component formateRelativeDateTime(long epochSeconds) {
        if (epochSeconds <= 0) {
            return Component.literal("N/A");
        }
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        Instant now = Instant.now();
        long secondsAgo = now.getEpochSecond() - instant.getEpochSecond();

        if (secondsAgo < 0L) {
            return Component.translatable("mco.util.time.now");
        } else if (secondsAgo < 60L) {
            return Component.translatable("mco.time.secondsAgo", secondsAgo);
        } else if (secondsAgo < 3600L) {
            long minutes = secondsAgo / 60L;
            return Component.translatable("mco.time.minutesAgo", minutes);
        } else if (secondsAgo < 86400L) {
            long hours = secondsAgo / 3600L;
            return Component.translatable("mco.time.hoursAgo", hours);
        } else {
            long days = secondsAgo / 86400L;
            return Component.translatable("mco.time.daysAgo", days);
        }
    }

    public static String formatDouble(double value) {
        if (value == (long) value) {
            return Long.toString((long) value);
        }

        return Double.toString(value);
    }

    public static String formatFloat(float value) {
        if (value == (int) value) {
            return Integer.toString((int) value);
        }

        return Float.toString(value);
    }

    public static Identifier getMobEffectSprite(MobEffect effect) {
        Identifier id = BuiltInRegistries.MOB_EFFECT.getKey(effect);

        assert id != null;
        return id.withPrefix("mob_effect/");
    }

    public static Identifier getMobEffectSprite(LazyHolder<MobEffect> effect) {
        return effect.getId().withPrefix("mob_effect/");
    }

    public static TextWidget sectionTitle(String title) {
        return Widgets.text(Component.literal(title), text -> text
                .withColor(Color.parse("yellow"))
                .withShadow()
                .withLeftAlignment()
        );
    }

    public static boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
    }
}
