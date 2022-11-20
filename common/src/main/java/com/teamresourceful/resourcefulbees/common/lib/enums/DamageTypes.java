package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum DamageTypes {
    IN_FIRE(DamageSource.IN_FIRE),
    LIGHTNING_BOLT(DamageSource.LIGHTNING_BOLT),
    ON_FIRE(DamageSource.ON_FIRE),
    LAVA(DamageSource.LAVA),
    HOT_FLOOR(DamageSource.HOT_FLOOR),
    IN_WALL(DamageSource.IN_WALL),
    CRAMMING(DamageSource.CRAMMING),
    DROWN(DamageSource.DROWN),
    STARVE(DamageSource.STARVE),
    CACTUS(DamageSource.CACTUS),
    FALL(DamageSource.FALL),
    FLY_INTO_WALL(DamageSource.FLY_INTO_WALL),
    OUT_OF_WORLD(DamageSource.OUT_OF_WORLD),
    GENERIC(DamageSource.GENERIC),
    MAGIC(DamageSource.MAGIC),
    WITHER(DamageSource.WITHER),
    ANVIL(DamageSource.ANVIL),
    FALLING_BLOCK(DamageSource.FALLING_BLOCK),
    DRAGON_BREATH(DamageSource.DRAGON_BREATH),
    DRY_OUT(DamageSource.DRY_OUT),
    SWEET_BERRY_BUSH(DamageSource.SWEET_BERRY_BUSH),
    FREEZE(DamageSource.FREEZE),
    FALLING_STALACTITE(DamageSource.FALLING_STALACTITE),
    STALAGMITE(DamageSource.STALAGMITE),
    EXPLOSION(DamageSource::explosion, false),
    STING(DamageSource::sting),
    MOB_ATTACK(DamageSource::mobAttack),
    THORN(DamageSource::thorns);

    public static final EnumCodec<DamageTypes> CODEC = EnumCodec.of(DamageTypes.class);

    private final Function<LivingEntity, DamageSource> source;
    private final boolean genericOnNull;

    DamageTypes(DamageSource source) {
        this(livingEntity -> source);
    }

    DamageTypes(Function<LivingEntity, DamageSource> source) {
        this(source, true);
    }

    DamageTypes(Function<LivingEntity, DamageSource> source, boolean genericOnNull) {
        this.source = source;
        this.genericOnNull = genericOnNull;
    }

    public DamageSource getSource(@Nullable LivingEntity livingEntity) {
        return genericOnNull && livingEntity == null ? DamageSource.GENERIC : source.apply(livingEntity);
    }

}
