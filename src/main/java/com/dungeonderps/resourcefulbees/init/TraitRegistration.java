package com.dungeonderps.resourcefulbees.init;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.TraitConstants;
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
        for (Map.Entry<String, BeeData> bee : BeeInfo.getBees().entrySet()){
            BeeInfo.setBeesTraits(bee.getKey());
        }
    }
}
