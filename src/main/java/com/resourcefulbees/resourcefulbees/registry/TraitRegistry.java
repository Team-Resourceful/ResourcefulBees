package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.ITraitRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.traitdata.BeeTrait;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.TraitConstants;
import net.minecraft.item.Items;
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
        ResourcefulBees.LOGGER.info("Registering Default Bee Traits...");
        getRegistry().register(TraitConstants.WITHER, new BeeTrait.Builder(TraitConstants.WITHER).addPotionImmunity(Effects.WITHER).addDamagePotionEffect(Pair.of(Effects.WITHER, 1)).setBeepediaItem(Items.WITHER_ROSE).build());
        getRegistry().register(TraitConstants.BLAZE, new BeeTrait.Builder(TraitConstants.BLAZE)
                .addDamageImmunities(Arrays.asList(DamageSource.LAVA.msgId, DamageSource.IN_FIRE.msgId, DamageSource.ON_FIRE.msgId, DamageSource.HOT_FLOOR.msgId))
                .addDamageType(Pair.of(TraitConstants.SET_ON_FIRE, 1)).addSpecialAbility(TraitConstants.FLAMMABLE).setParticleEffect(ParticleTypes.FLAME).setBeepediaItem(Items.BLAZE_ROD).build());
        getRegistry().register(TraitConstants.CAN_SWIM, new BeeTrait.Builder(TraitConstants.CAN_SWIM).addDamageImmunity(DamageSource.DROWN.msgId).setBeepediaItem(Items.WATER_BUCKET).build());
        getRegistry().register(TraitConstants.CREEPER, new BeeTrait.Builder(TraitConstants.CREEPER).addDamageType(Pair.of(TraitConstants.EXPLOSIVE, 4)).setBeepediaItem(Items.TNT).build());
        getRegistry().register(TraitConstants.ZOMBIE, new BeeTrait.Builder(TraitConstants.ZOMBIE).addDamagePotionEffect(Pair.of(Effects.HUNGER, 20)).setBeepediaItem(Items.ROTTEN_FLESH).build());
        getRegistry().register(TraitConstants.PIGMAN, new BeeTrait.Builder(TraitConstants.PIGMAN).addDamagePotionEffect(Pair.of(Effects.DIG_SLOWDOWN, 0)).setBeepediaItem(Items.GOLD_NUGGET).build());
        getRegistry().register(TraitConstants.ENDER, new BeeTrait.Builder(TraitConstants.ENDER).addSpecialAbility(TraitConstants.TELEPORT).setParticleEffect(ParticleTypes.PORTAL).setBeepediaItem(Items.ENDER_PEARL).build());
        getRegistry().register(TraitConstants.NETHER, new BeeTrait.Builder(TraitConstants.NETHER).addDamageImmunities(Arrays.asList(DamageSource.LAVA.msgId, DamageSource.IN_FIRE.msgId, DamageSource.ON_FIRE.msgId, DamageSource.HOT_FLOOR.msgId)).setBeepediaItem(Items.NETHERRACK).build());
        getRegistry().register(BeeConstants.OREO_BEE, new BeeTrait.Builder(BeeConstants.OREO_BEE).addDamagePotionEffect(Pair.of(Effects.HEAL, 2)).setBeepediaItem(ModItems.OREO_COOKIE.get()).build());
        getRegistry().register(BeeConstants.KITTEN_BEE, new BeeTrait.Builder(BeeConstants.KITTEN_BEE).addDamagePotionEffect(Pair.of(Effects.MOVEMENT_SPEED, 2)).setBeepediaItem(Items.RED_BED).build());
        getRegistry().register(TraitConstants.SLIMY, new BeeTrait.Builder(TraitConstants.SLIMY).addSpecialAbility(TraitConstants.SLIMY).setBeepediaItem(Items.SLIME_BALL).build());
        getRegistry().register(TraitConstants.DESERT, new BeeTrait.Builder(TraitConstants.DESERT).addDamageImmunity(DamageSource.CACTUS.msgId).setBeepediaItem(Items.CACTUS).build());
        getRegistry().register(TraitConstants.ANGRY, new BeeTrait.Builder(TraitConstants.ANGRY).addSpecialAbility(TraitConstants.ANGRY).build());
        getRegistry().register(TraitConstants.SPIDER, new BeeTrait.Builder(TraitConstants.SPIDER).addSpecialAbility(TraitConstants.SPIDER).setBeepediaItem(Items.COBWEB).build());
    }

    public static void applyBeeTraits() {
        //oreo reminder make method for BeeRegistry.getRegistry().getBees().values()
        BeeRegistry.getRegistry().getBees().values().forEach(beeData -> {
            beeData.getTraitData().initializeTraitSets();
            if (beeData.hasTraitNames()) {
                Arrays.stream(beeData.getTraitNames())
                        .forEach(traitString -> addTrait(beeData, traitString));
            }
        });
    }

    private static void addTrait(CustomBeeData customBeeData, String traitString) {
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
    }
}
