package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record DipperEntity(UUID uuid) {

    public static final Codec<DipperEntity> CODEC = RecordCodecBuilder.create(i -> i.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(DipperEntity::uuid)
    ).apply(i, DipperEntity::new));

    public static final StreamCodec<FriendlyByteBuf, DipperEntity> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            DipperEntity::uuid,
            DipperEntity::new
    );

    public static DipperEntity of(UUID uuid) {
        return new DipperEntity(uuid);
    }

    public static final DipperEntity EMPTY = new DipperEntity(null);
}
