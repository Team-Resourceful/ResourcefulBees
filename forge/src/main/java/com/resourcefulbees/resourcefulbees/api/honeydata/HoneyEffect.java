package com.resourcefulbees.resourcefulbees.api.honeydata;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoneyEffect {

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * effect : generated from the effect id
     * instance : generated from the effect, duration and strength
     * effectID : used to define the effect used
     * duration : duration in ticks of the effect
     * strength : strength of the potion effect
     * chance : chance for effect to proc on drinking honey
     */
    public String effectID;
    public int duration = 60;
    public int strength = 0;
    public float chance = 1;
    private transient MobEffect effect = null;

    public HoneyEffect() {
    }

    public HoneyEffect(String effectID, int duration, int strength, float chance) {
        this.effectID = effectID;
        this.duration = duration;
        this.strength = strength;
        this.chance = chance;
    }

    public MobEffectInstance getInstance() {
        return new MobEffectInstance(getEffect(), duration, strength);
    }

    public boolean isEffectIDValid() {
        return ResourceLocation.tryParse(effectID) != null;
    }

    public MobEffect getEffect() {
        if (effect != null) return effect;
        ResourceLocation location = ResourceLocation.tryParse(effectID);
        return effectID == null || location == null ? null : ForgeRegistries.POTIONS.getValue(location);
    }

    public void setEffect(MobEffect effect) {
        this.effect = effect;
    }

    public String getEffectID() {
        return effectID;
    }
}
