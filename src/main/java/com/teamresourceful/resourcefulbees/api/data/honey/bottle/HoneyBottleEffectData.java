package com.teamresourceful.resourcefulbees.api.data.honey.bottle;

import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public interface HoneyBottleEffectData {

    LazyHolder<MobEffect> effect();

    int duration();

    int strength();

    float chance();

    default MobEffectInstance getInstance() {
        return new MobEffectInstance(Holder.direct(effect().get()), duration(), strength());
    }
}
