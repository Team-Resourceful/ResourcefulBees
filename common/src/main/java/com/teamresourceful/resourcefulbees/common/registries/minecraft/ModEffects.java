package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import org.jetbrains.annotations.NotNull;

public final class ModEffects {

    private ModEffects() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<MobEffect> EFFECTS = RegistryHelper.create(BuiltInRegistries.MOB_EFFECT, ModConstants.MOD_ID);

    public static final RegistryEntry<MobEffect> CALMING = EFFECTS.register("calming", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 16763783) {
        @Override
        public void applyEffectTick(@NotNull LivingEntity entity, int level) {
            if (entity instanceof NeutralMob neutralMob) neutralMob.stopBeingAngry();
            super.applyEffectTick(entity, level);
        }

        @Override
        public boolean isDurationEffectTick(int duration, int level) {
            return duration % 5 == 0;
        }
    });
}
