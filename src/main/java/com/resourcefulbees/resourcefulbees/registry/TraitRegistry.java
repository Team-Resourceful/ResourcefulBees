package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.TraitConstants;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;

public class TraitRegistry {

    private static final HashMap<String, CompoundNBT> TRAIT_REGISTRY = new HashMap<>();
    private static boolean closed = false;

    public static void register(String name, CompoundNBT data){
        if (!closed) {
            if (!TRAIT_REGISTRY.containsKey(name)) {
                TRAIT_REGISTRY.put(name, data);
            } else ResourcefulBees.LOGGER.warn("Trait already Registered with that name: {}", name);
        }else ResourcefulBees.LOGGER.warn("Trait Registration closed register your traits before onLoadComplete, trait not registered: {}", name);
    }

    public static CompoundNBT getTrait(String name) {
        return TRAIT_REGISTRY.get(name);
    } //As long as other mods are using deferred register or @ObjectHolder for their custom damage sources/potion effects etc. they should be able to supply the static field for use later

    public static void setTraitRegistrationClosed(){
        closed = true;
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


    //TODO this may not bee needed anymore with the TraitData Builder
/*    public static void giveBeesTraits(){
        for (Map.Entry<String, CustomBee> bee : BeeRegistry.getBees().entrySet()){
            BeeRegistry.setBeesTraits(bee.getKey());
        }
    }*/
}
