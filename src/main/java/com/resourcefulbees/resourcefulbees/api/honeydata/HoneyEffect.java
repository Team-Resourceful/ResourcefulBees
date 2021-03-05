package com.resourcefulbees.resourcefulbees.api.honeydata;

import com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
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
    private transient Effect effect = null;

    public HoneyEffect() {
    }

    public HoneyEffect(String effectID, int duration, int strength, float chance) {
        this.effectID = effectID;
        this.duration = duration;
        this.strength = strength;
        this.chance = chance;
    }

    public EffectInstance getInstance() {
        return new EffectInstance(getEffect(), duration, strength);
    }

    public boolean isEffectIDValid() {
        return effectID != null && effectID.matches(ValidatorUtils.SINGLE_RESOURCE_PATTERN.pattern());
    }

    public Effect getEffect() {
        if (effect != null) return effect;
        ResourceLocation location = ResourceLocation.tryCreate(effectID);
        return effectID == null || location == null ? null : ForgeRegistries.POTIONS.getValue(location);
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public String getEffectID() {
        return effectID;
    }
}
