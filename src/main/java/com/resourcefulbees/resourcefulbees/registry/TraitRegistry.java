package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.CustomBee;
import com.resourcefulbees.resourcefulbees.data.BeeTrait;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;

public class TraitRegistry {

    private static final HashMap<String, BeeTrait> TRAIT_REGISTRY = new HashMap<>();
    private static boolean closed = false;

    public static void register(String name, BeeTrait data){
        if (!closed) {
            if (!TRAIT_REGISTRY.containsKey(name)) {
                TRAIT_REGISTRY.put(name, data);
            } else ResourcefulBees.LOGGER.warn("Trait already Registered with that name: {}", name);
        }else ResourcefulBees.LOGGER.warn("Trait Registration closed register your traits before onLoadComplete, trait not registered: {}", name);
    }

    public static BeeTrait getTrait(String name) {
        return TRAIT_REGISTRY.get(name);
    }

    public static void setTraitRegistryClosed(){
        closed = true;
    }

    public static void registerDefaultTraits(){
        register("wither", new BeeTrait.Builder().addPotionImmunity(Effects.WITHER).addDamagePotionEffect(Pair.of(Effects.WITHER, 1)).build());
        register("blaze", new BeeTrait.Builder()
                .addDamageImmunities(Arrays.asList(DamageSource.LAVA,DamageSource.IN_FIRE,DamageSource.ON_FIRE,DamageSource.HOT_FLOOR))
                .addDamageType(Pair.of("setOnFire", 1)).addSpecialAbility("flammable").setParticleEffect(ParticleTypes.FLAME).build());
        register("canswim", new BeeTrait.Builder().addDamageImmunity(DamageSource.DROWN).build());
        register("creeper", new BeeTrait.Builder().addDamageType(Pair.of("explosive", 4)).build());
        register("zombie", new BeeTrait.Builder().addDamagePotionEffect(Pair.of(Effects.HUNGER, 20)).build());
        register("pigman", new BeeTrait.Builder().addDamagePotionEffect(Pair.of(Effects.MINING_FATIGUE, 0)).build());
        register("ender", new BeeTrait.Builder().addSpecialAbility("teleport").setParticleEffect(ParticleTypes.PORTAL).build());
        register("nether", new BeeTrait.Builder().addDamageImmunities(Arrays.asList(DamageSource.LAVA,DamageSource.IN_FIRE,DamageSource.ON_FIRE,DamageSource.HOT_FLOOR)).build());
    }

    public static void giveBeeTraits(){
        for (CustomBee bee : BeeRegistry.getBees().values()){
            if (bee.hasTraitNames()) {
                for (String traitString : bee.getTraitNames()) {
                    BeeTrait trait = TraitRegistry.getTrait(traitString);
                    if (trait != null) {
                        bee.TraitData.addTrait(trait);
                    }
                }
            }
        }
    }
}
