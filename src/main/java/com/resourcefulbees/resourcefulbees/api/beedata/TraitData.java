package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;

public class TraitData {
    private final List<String> traits;
    private transient final List<CompoundNBT> beeTraits = new ArrayList<>();
    private transient boolean listBuilt = false;

    private TraitData(List<String> traits) {
        this.traits = new ArrayList<>();
        this.traits.addAll(traits);
    }

    private void buildCompoundList() {
        if (traits != null && beeTraits != null) {
            for (String trait : traits) {
                CompoundNBT traitNBT = TraitRegistry.getTrait(trait);
                if (traitNBT != null) beeTraits.add(traitNBT);
            }
        }
    }

    public List<CompoundNBT> getBeeTraits() {
        return beeTraits;
    }

    public boolean hasTraits() {
        if (!listBuilt && beeTraits != null && beeTraits.isEmpty()) {
            buildCompoundList();  //to cover both jsons and other mods using builder also keeps build method private
            listBuilt = true;
        }
        return beeTraits != null && !beeTraits.isEmpty();
    }

    //TODO Consider making these methods available directly from the TraitData Object vs Static class call
    // may need to add a string parameter to specify the trait name requested instead of the CompoundNBT
    // should marinate on this some more.

/*    public static boolean hasPotionEffects(CompoundNBT nbt){
        ListNBT potions = nbt.getList(POTION_EFFECTS, 10);
        return potions.size() > 0;
    }

    public static boolean hasPotionImmunities(CompoundNBT nbt){
        ListNBT potions = nbt.getList(POTION_IMMUNITIES, 8);
        return potions.size() > 0;
    }

    public static boolean hasDamageImmunities(CompoundNBT nbt){
        ListNBT damageImmunitiesNbtList = nbt.getList(DAMAGE_IMMUNITIES, 8);
        return damageImmunitiesNbtList.size() > 0;
    }

    public static boolean hasDamageTypes(CompoundNBT nbt){
        ListNBT damageTypeList = nbt.getList(DAMAGE_TYPES, 10);
        return damageTypeList.size() > 0;
    }

    public static boolean hasSpecialAbilities(CompoundNBT nbt){
        ListNBT abilityList = nbt.getList(ABILITY_TYPES, 8);
        return abilityList.size() > 0;
    }

    public static boolean hasParticleEffects(CompoundNBT nbt){
        return nbt.contains(PARTICLE_EFFECT);
    }*/

    public static class Builder{

        private final List<String> traits = new ArrayList<>();

        public Builder addTrait(String trait) {
            traits.add(trait);
            return this;
        }

        public TraitData createTraitData() {
            return new TraitData(traits);
        }
    }
}
