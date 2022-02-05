package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DamageUtils {

    private static final Map<String, DamageSource> BASIC_SOURCE_MAP = Util.make(() -> {
        Map<String, DamageSource> map = new HashMap<>();
        map.put("infire", DamageSource.IN_FIRE);
        map.put("lightningbolt", DamageSource.LIGHTNING_BOLT);
        map.put("onfire", DamageSource.ON_FIRE);
        map.put("lava", DamageSource.LAVA);
        map.put("hotfloor", DamageSource.HOT_FLOOR);
        map.put("inwall", DamageSource.IN_WALL);
        map.put("cramming", DamageSource.CRAMMING);
        map.put("drown", DamageSource.DROWN);
        map.put("starve", DamageSource.STARVE);
        map.put("cactus", DamageSource.CACTUS);
        map.put("fall", DamageSource.FALL);
        map.put("flyintowall", DamageSource.FLY_INTO_WALL);
        map.put("outofworld", DamageSource.OUT_OF_WORLD);
        map.put("magic", DamageSource.MAGIC);
        map.put("wither", DamageSource.WITHER);
        map.put("anvil", DamageSource.ANVIL);
        map.put("fallingblock", DamageSource.FALLING_BLOCK);
        map.put("dragonbreath", DamageSource.DRAGON_BREATH);
        map.put("dryout", DamageSource.DRY_OUT);
        map.put("sweetberrybush", DamageSource.SWEET_BERRY_BUSH);
        return map;
    });

    private DamageUtils() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static DamageSource damageByName(String name) {
        return damageByName(name, null);
    }

    public static DamageSource damageByName(String name, @Nullable LivingEntity sourceEntity) {
        return switch (name.toLowerCase(Locale.ENGLISH)) {
            case "explosion" -> sourceEntity != null ? DamageSource.explosion(sourceEntity) : DamageSource.GENERIC;
            case "sting" -> sourceEntity != null ? DamageSource.sting(sourceEntity) : DamageSource.GENERIC;
            case "mobattack" -> sourceEntity != null ? DamageSource.mobAttack(sourceEntity) : DamageSource.GENERIC;
            case "thorns" -> sourceEntity != null ? DamageSource.thorns(sourceEntity) : DamageSource.GENERIC;
            default -> BASIC_SOURCE_MAP.getOrDefault(name.toLowerCase(Locale.ROOT), DamageSource.GENERIC);
        };
    }
}
