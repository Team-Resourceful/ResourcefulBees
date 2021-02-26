package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.ITraitRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.data.BeeTrait;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.TraitConstants;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

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
        return TRAIT_REGISTRY.get(name);
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
        getRegistry().register(TraitConstants.WITHER, new BeeTrait.Builder().addPotionImmunity(Effects.WITHER).addDamagePotionEffect(Pair.of(Effects.WITHER, 1)).build());
        getRegistry().register(TraitConstants.BLAZE, new BeeTrait.Builder()
                .addDamageImmunities(Arrays.asList(DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ON_FIRE, DamageSource.HOT_FLOOR))
                .addDamageType(Pair.of(TraitConstants.SET_ON_FIRE, 1)).addSpecialAbility(TraitConstants.FLAMMABLE).setParticleEffect(ParticleTypes.FLAME).build());
        getRegistry().register(TraitConstants.CAN_SWIM, new BeeTrait.Builder().addDamageImmunity(DamageSource.DROWN).build());
        getRegistry().register(TraitConstants.CREEPER, new BeeTrait.Builder().addDamageType(Pair.of(TraitConstants.EXPLOSIVE, 4)).build());
        getRegistry().register(TraitConstants.ZOMBIE, new BeeTrait.Builder().addDamagePotionEffect(Pair.of(Effects.HUNGER, 20)).build());
        getRegistry().register(TraitConstants.PIGMAN, new BeeTrait.Builder().addDamagePotionEffect(Pair.of(Effects.MINING_FATIGUE, 0)).build());
        getRegistry().register(TraitConstants.ENDER, new BeeTrait.Builder().addSpecialAbility(TraitConstants.TELEPORT).setParticleEffect(ParticleTypes.PORTAL).build());
        getRegistry().register(TraitConstants.NETHER, new BeeTrait.Builder().addDamageImmunities(Arrays.asList(DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ON_FIRE, DamageSource.HOT_FLOOR)).build());
        getRegistry().register(BeeConstants.OREO_BEE, new BeeTrait.Builder().addDamagePotionEffect(Pair.of(Effects.INSTANT_HEALTH, 2)).build());
        getRegistry().register(BeeConstants.KITTEN_BEE, new BeeTrait.Builder().addDamagePotionEffect(Pair.of(Effects.SPEED, 2)).build());
        getRegistry().register(TraitConstants.SLIMY, new BeeTrait.Builder().addSpecialAbility(TraitConstants.SLIMY).build());
        getRegistry().register(TraitConstants.DESERT, new BeeTrait.Builder().addDamageImmunity(DamageSource.CACTUS).build());
        getRegistry().register(TraitConstants.ANGRY, new BeeTrait.Builder().addSpecialAbility(TraitConstants.ANGRY).build());
    }

    public static void applyBeeTraits() {
        BeeRegistry.getRegistry().getBees().values().stream()
                .filter(CustomBeeData::hasTraitNames)
                .forEach((customBeeData -> Arrays.stream(customBeeData.getTraitNames())
                        .forEach(traitString -> {
                                BeeTrait trait = getRegistry().getTrait(traitString);
                                if (trait != null) {
                                    if (customBeeData.getTraitData().hasTraits()) {
                                        customBeeData.getTraitData().addTrait(trait);
                                    } else {
                                        ResourcefulBees.LOGGER.warn("Traits provided but TraitData object does not exist or does not contain \"hasTraits: true\" for '{}'", customBeeData.getName());
                                    }
                                } else {
                                    ResourcefulBees.LOGGER.warn("Trait '{}' given to '{}' does not exist in trait registry.", traitString, customBeeData.getName());
                                }
                })));
    }
}
