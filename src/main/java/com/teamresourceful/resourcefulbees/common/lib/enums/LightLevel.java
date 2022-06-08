package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.LevelAccessor;

public enum LightLevel {
    DAY(TranslationConstants.LightLevel.DAY),
    NIGHT(TranslationConstants.LightLevel.NIGHT),
    ANY(TranslationConstants.LightLevel.ANY);

    private final MutableComponent component;

    LightLevel(MutableComponent component) {
        this.component = component;
    }

    public static final Codec<LightLevel> CODEC = Codec.STRING.xmap(LightLevel::valueOf, LightLevel::toString);

    public MutableComponent getDisplay() {
        return component;
    }

    public boolean canSpawn(LevelAccessor level, BlockPos pos) {
        return switch (this) {
            case DAY -> level.getMaxLocalRawBrightness(pos) >= 8;
            case NIGHT -> level.getMaxLocalRawBrightness(pos) <= 7;
            case ANY -> true;
        };
    }
}
