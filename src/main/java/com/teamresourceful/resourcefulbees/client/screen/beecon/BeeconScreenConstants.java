package com.teamresourceful.resourcefulbees.client.screen.beecon;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;

public class BeeconScreenConstants {

    public static final WidgetSprites EFFECT_BUTTON = new WidgetSprites(
        ModIdentifier.of("beecon/effects/selected/base"),
        ModIdentifier.of("beecon/effects/disabled/base"),
        ModIdentifier.of("beecon/effects/selected/hover"),
        ModIdentifier.of("beecon/effects/disabled/hover")
    );

    public static final WidgetSprites TOGGLE_SOUND = new WidgetSprites(
        ModIdentifier.of("beecon/toggles/sound/selected/base"),
        ModIdentifier.of("beecon/toggles/sound/disabled/base"),
        ModIdentifier.of("beecon/toggles/sound/selected/hover"),
        ModIdentifier.of("beecon/toggles/sound/disabled/hover")
    );

    public static final WidgetSprites TOGGLE_BEAM = new WidgetSprites(
        ModIdentifier.of("beecon/toggles/beam/selected/base"),
        ModIdentifier.of("beecon/toggles/beam/disabled/base"),
        ModIdentifier.of("beecon/toggles/beam/selected/hover"),
        ModIdentifier.of("beecon/toggles/beam/disabled/hover")
    );

    public static final WidgetSprites TOGGLE_TARGET = new WidgetSprites(
        ModIdentifier.of("beecon/toggles/target/selected/base"),
        ModIdentifier.of("beecon/toggles/target/disabled/base"),
        ModIdentifier.of("beecon/toggles/target/selected/hover"),
        ModIdentifier.of("beecon/toggles/target/disabled/hover")
    );

    public static final Identifier SCROLLBAR = ModIdentifier.of("beecon/scrollbar");

    public static final Identifier SLIDER = ModIdentifier.of("beecon/slider");

    public static final Identifier PREVIEW_BEE = ModIdentifier.of("beecon/preview/bee");
    public static final Identifier PREVIEW_PLAYER = ModIdentifier.of("beecon/preview/player");
    public static final Identifier PREVIEW_BEAM = ModIdentifier.of("beecon/preview/beam");
    public static final Identifier PREVIEW_SOUND = ModIdentifier.of("beecon/preview/sound");
}
