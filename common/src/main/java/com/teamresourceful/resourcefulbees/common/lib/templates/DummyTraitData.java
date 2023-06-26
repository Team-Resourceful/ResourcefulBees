package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.google.common.collect.Sets;
import com.teamresourceful.resourcefulbees.api.data.trait.*;
import com.teamresourceful.resourcefulbees.common.lib.enums.AuraType;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

public final class DummyTraitData {

    private DummyTraitData() {
        throw new UtilityClassError();
    }

    public static final Trait DUMMY_TRAIT_DATA = new Trait(
            "template",
            Items.WITHER_ROSE,
            Sets.newHashSet(new PotionEffect(MobEffects.BLINDNESS, 3)),
            Sets.newHashSet("inFire", "wither"),
            Sets.newHashSet(MobEffects.WEAKNESS, MobEffects.CONFUSION),
            Sets.newHashSet(new TraitDamageType("setOnFire", 4), new TraitDamageType("explosive", 3)),
            Sets.newHashSet("teleport"),
            Sets.newHashSet(ParticleTypes.SMOKE, ParticleTypes.SOUL_FIRE_FLAME),
            Sets.newHashSet(new Aura(
                    AuraType.EXPERIENCE,
                    new DamageEffect(DamageTypes.IN_FIRE, false, 0),
                    new PotionEffect(MobEffects.HEAL, 2),
                    3,
                    true
                )
            )
    );
}
