package com.teamresourceful.resourcefulbees.common.registry;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.ITraitRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;
import com.teamresourceful.resourcefulbees.api.beedata.traits.DamageType;
import com.teamresourceful.resourcefulbees.api.beedata.traits.PotionDamageEffect;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TraitRegistry implements ITraitRegistry {

    private static final HashMap<String, BeeTrait> TRAIT_REGISTRY = new HashMap<>();
    private static boolean closed = false;

    private static final TraitRegistry INSTANCE = new TraitRegistry();

    public static TraitRegistry getRegistry() {
        return INSTANCE;
    }

    /**
     * Registers the supplied Trait Name and associated data to the mod.
     * If the trait already exists in the registry the method will return false.
     *
     * @param name Trait Name of the trait being registered.
     * @param data BeeTrait of the trait being registered
     * @return Returns false if trait already exists in the registry.
     */
    public boolean register(String name, BeeTrait data) {
        if (closed || TRAIT_REGISTRY.containsKey(name)) {
            ResourcefulBees.LOGGER.error("Trait is already registered or registration is closed: {}", name);
            return false;
        }
        TRAIT_REGISTRY.put(name, data);
        return true;
    }

    /**
     * Returns a BeeTrait object for the given trait name.
     *
     * @param name Trait name for which BeeTrait is requested.
     * @return Returns a BeeTrait object for the given bee type.
     */
    public BeeTrait getTrait(String name) {
        return TRAIT_REGISTRY.getOrDefault(name, BeeTrait.DEFAULT);
    }

    /**
     * Returns an unmodifiable copy of the Trait Registry.
     * This is useful for iterating over all traits without worry of changing data
     *
     * @return Returns unmodifiable copy of trait registry.
     */
    public Map<String, BeeTrait> getTraits() {
        return Collections.unmodifiableMap(TRAIT_REGISTRY);
    }

    public static void setTraitRegistryClosed() {
        closed = true;
    }

    public static void registerDefaultTraits() {
        ResourcefulBees.LOGGER.info("Registering Default Bee Traits...");
        getRegistry().register(TraitConstants.WITHER, new BeeTrait.Mutable().setName(TraitConstants.WITHER).addPotionImmunity(Effects.WITHER).addDamagePotionEffect(PotionDamageEffect.create(Effects.WITHER, 1)).setDisplayItem(Items.WITHER_ROSE).toImmutable());
        getRegistry().register(TraitConstants.BLAZE, new BeeTrait.Mutable().setName(TraitConstants.BLAZE)
                .addDamageImmunities(Arrays.asList(DamageSource.LAVA.msgId, DamageSource.IN_FIRE.msgId, DamageSource.ON_FIRE.msgId, DamageSource.HOT_FLOOR.msgId))
                .addDamageType(DamageType.create(TraitConstants.SET_ON_FIRE, 1)).addSpecialAbility(TraitConstants.FLAMMABLE).addParticleEffect(ParticleTypes.FLAME).setDisplayItem(Items.BLAZE_ROD).toImmutable());
        getRegistry().register(TraitConstants.CAN_SWIM, new BeeTrait.Mutable().setName(TraitConstants.CAN_SWIM).addDamageImmunity(DamageSource.DROWN.msgId).setDisplayItem(Items.WATER_BUCKET).toImmutable());
        getRegistry().register(TraitConstants.CREEPER, new BeeTrait.Mutable().setName(TraitConstants.CREEPER).addDamageType(DamageType.create(TraitConstants.EXPLOSIVE, 4)).setDisplayItem(Items.TNT).toImmutable());
        getRegistry().register(TraitConstants.ZOMBIE, new BeeTrait.Mutable().setName(TraitConstants.ZOMBIE).addDamagePotionEffect(PotionDamageEffect.create(Effects.HUNGER, 20)).setDisplayItem(Items.ROTTEN_FLESH).toImmutable());
        getRegistry().register(TraitConstants.PIGMAN, new BeeTrait.Mutable().setName(TraitConstants.PIGMAN).addDamagePotionEffect(PotionDamageEffect.create(Effects.DIG_SLOWDOWN, 0)).setDisplayItem(Items.GOLD_NUGGET).toImmutable());
        getRegistry().register(TraitConstants.ENDER, new BeeTrait.Mutable().setName(TraitConstants.ENDER).addSpecialAbility(TraitConstants.TELEPORT).addParticleEffect(ParticleTypes.PORTAL).setDisplayItem(Items.ENDER_PEARL).toImmutable());
        getRegistry().register(TraitConstants.NETHER, new BeeTrait.Mutable().setName(TraitConstants.NETHER).addDamageImmunities(Arrays.asList(DamageSource.LAVA.msgId, DamageSource.IN_FIRE.msgId, DamageSource.ON_FIRE.msgId, DamageSource.HOT_FLOOR.msgId)).setDisplayItem(Items.NETHERRACK).toImmutable());
        getRegistry().register(BeeConstants.OREO_BEE, new BeeTrait.Mutable().setName(BeeConstants.OREO_BEE).addDamagePotionEffect(PotionDamageEffect.create(Effects.HEAL, 2)).setDisplayItem(ModItems.OREO_COOKIE.get()).toImmutable());
        getRegistry().register(BeeConstants.KITTEN_BEE, new BeeTrait.Mutable().setName(BeeConstants.KITTEN_BEE).addDamagePotionEffect(PotionDamageEffect.create(Effects.MOVEMENT_SPEED, 2)).setDisplayItem(Items.RED_BED).toImmutable());
        getRegistry().register(TraitConstants.SLIMY, new BeeTrait.Mutable().setName(TraitConstants.SLIMY).addSpecialAbility(TraitConstants.SLIMY).setDisplayItem(Items.SLIME_BALL).toImmutable());
        getRegistry().register(TraitConstants.DESERT, new BeeTrait.Mutable().setName(TraitConstants.DESERT).addDamageImmunity(DamageSource.CACTUS.msgId).setDisplayItem(Items.CACTUS).toImmutable());
        getRegistry().register(TraitConstants.ANGRY, new BeeTrait.Mutable().setName(TraitConstants.ANGRY).addSpecialAbility(TraitConstants.ANGRY).toImmutable());
        getRegistry().register(TraitConstants.SPIDER, new BeeTrait.Mutable().setName(TraitConstants.SPIDER).addSpecialAbility(TraitConstants.SPIDER).setDisplayItem(Items.COBWEB).toImmutable());
    }
}
