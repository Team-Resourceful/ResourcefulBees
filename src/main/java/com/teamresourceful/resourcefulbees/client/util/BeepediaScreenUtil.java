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


public final class BeepediaScreenUtil {

    private BeepediaScreenUtil() {
    }

    //todo change to actual yes/no translations vs on/off
    public static Component yesNo(boolean value) {
        return Component.translatable(value ? "options.on" : "options.off");
    }

    public static AbstractWidget property(Component label, Component value) {
        return Widgets.labelled(Minecraft.getInstance().font, label, Widgets.text(value, TextWidget::withRightAlignment).withColor(Color.parse("green")));
    }

    public static AbstractWidget property(Component label, String value) {
        return Widgets.labelled(Minecraft.getInstance().font, label, Widgets.text(value, TextWidget::withRightAlignment).withColor(Color.parse("green")));
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
