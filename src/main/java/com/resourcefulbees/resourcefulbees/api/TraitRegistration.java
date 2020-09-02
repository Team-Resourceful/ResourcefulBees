package com.resourcefulbees.resourcefulbees.api;

import com.resourcefulbees.resourcefulbees.config.BeeRegistry;
import com.resourcefulbees.resourcefulbees.lib.TraitConstants;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

public class TraitRegistration {

    private static final HashMap<String, CompoundNBT> TRAIT_REGISTRY = new HashMap<>();

    public static void register(String name, CompoundNBT data){
        if (!TRAIT_REGISTRY.containsKey(name)){
            TRAIT_REGISTRY.put(name, data);
        }
    }

    public static CompoundNBT getTrait(String name) {
        return TRAIT_REGISTRY.get(name);
    }

    public static void registerDefaultTraits(){
        register("wither", TraitConstants.WITHER);
        register("blaze", TraitConstants.BLAZE);
        register("canswim", TraitConstants.CANSWIM);
        register("creeper", TraitConstants.CREEPER);
        register("zombie", TraitConstants.ZOMBIE);
        register("pigman", TraitConstants.PIGMAN);
        register("ender", TraitConstants.ENDER);
        register("nether", TraitConstants.NETHER);
    }

    public static void giveBeesTraits(){
        for (Map.Entry<String, CustomBee> bee : BeeRegistry.getBees().entrySet()){
            BeeRegistry.setBeesTraits(bee.getKey());
        }
    }
}
