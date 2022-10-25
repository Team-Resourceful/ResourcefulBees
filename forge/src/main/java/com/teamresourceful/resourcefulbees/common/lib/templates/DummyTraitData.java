package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.google.common.collect.Sets;
import com.teamresourceful.resourcefulbees.api.beedata.traits.*;
import com.teamresourceful.resourcefulbees.common.lib.enums.AuraType;
import com.teamresourceful.resourcefulbees.common.lib.enums.DamageTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffects;

public class DummyTraitData {

    public static final TraitData DUMMY_TRAIT_DATA = new TraitData(
            "template",
            10,
            Sets.newHashSet("template", "seriously_this_is_a_template"),
            Sets.newHashSet(new PotionEffect(MobEffects.BLINDNESS, 3)),
            Sets.newHashSet("inFire", "wither"),
            Sets.newHashSet(MobEffects.WEAKNESS, MobEffects.CONFUSION),
            Sets.newHashSet(new DamageType("setOnFire", 4), new DamageType("explosive", 3)),
            Sets.newHashSet("teleport"),
            Sets.newHashSet(ParticleTypes.SMOKE, ParticleTypes.SOUL_FIRE_FLAME),
            Sets.newHashSet(new BeeAura(
                    AuraType.EXPERIENCE,
                    new DamageEffect(DamageTypes.IN_FIRE, 0),
                    new PotionEffect(MobEffects.HEAL, 2),
                    3,
                    true
                    )
            )
    );
}
