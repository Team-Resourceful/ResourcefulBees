package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class DamageUtils {

    private DamageUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static DamageSource getDamageSource(String source, @Nullable LivingEntity sourceEntity) {
        switch (source.toLowerCase(Locale.ENGLISH)) {
            case "infire" : return DamageSource.IN_FIRE;
            case "lightningbolt" : return DamageSource.LIGHTNING_BOLT;
            case "onfire" : return DamageSource.ON_FIRE;
            case "lava" : return DamageSource.LAVA;
            case "hotfloor" : return DamageSource.HOT_FLOOR;
            case "inwall" : return DamageSource.IN_WALL;
            case "cramming" : return DamageSource.CRAMMING;
            case "drown" : return DamageSource.DROWN;
            case "starve" : return DamageSource.STARVE;
            case "cactus" : return DamageSource.CACTUS;
            case "fall" : return DamageSource.FALL;
            case "flyintowall" : return DamageSource.FLY_INTO_WALL;
            case "outofworld" : return DamageSource.OUT_OF_WORLD;
            case "magic" : return DamageSource.MAGIC;
            case "wither" : return DamageSource.WITHER;
            case "anvil" : return DamageSource.ANVIL;
            case "fallingblock" : return DamageSource.FALLING_BLOCK;
            case "dragonbreath" : return DamageSource.DRAGON_BREATH;
            case "dryout" : return DamageSource.DRY_OUT;
            case "sweetberrybush" : return DamageSource.SWEET_BERRY_BUSH;
            case "explosion" : return sourceEntity != null ? DamageSource.explosion(sourceEntity) : DamageSource.GENERIC;
            case "sting" : return sourceEntity != null ? DamageSource.sting(sourceEntity) : DamageSource.GENERIC;
            case "mobattack" : return sourceEntity != null ? DamageSource.mobAttack(sourceEntity) : DamageSource.GENERIC;
            case "thorns" : return sourceEntity != null ? DamageSource.thorns(sourceEntity) : DamageSource.GENERIC;
            case "generic":
            default: return DamageSource.GENERIC;
        }
    }

    public static boolean dealDamage(float damage, String source, LivingEntity targetEntity, @Nullable LivingEntity sourceEntity) {
        return targetEntity.hurt(getDamageSource(source, sourceEntity), damage);
    }

}
