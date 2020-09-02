package com.resourcefulbees.resourcefulbees.config;

import com.resourcefulbees.resourcefulbees.api.CustomBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeeRegistry {

    private static final LinkedHashMap<String, CustomBee> BEE_INFO = new LinkedHashMap<>();
    public static final HashMap<Biome, RandomCollection<String>> SPAWNABLE_BIOMES = new HashMap<>();
    public static final HashMap<Pair<String, String>, RandomCollection<String>> FAMILY_TREE = new HashMap<>();

    /**
     * Returns a random bee from the Bee Registry.
     * This is used for selecting a bee from all possible bees.
     *
     *  @return Returns random bee type as a string.
     */
    public static String getRandomBee(){
        int randInt = MathUtils.nextInt(BEE_INFO.size() - 1) + 1;
        Object[] dataArray = BEE_INFO.keySet().toArray();
        Object key = dataArray[randInt];
        return BEE_INFO.get(key.toString()).getName();
    }

    /**
     * Returns a random bee from the SPAWNABLE_BIOMES hashmap.
     * This is used for in-world spawning based on biome.
     *
     *  @param biome Biome supplied determines which bees the random bee is chosen from.
     *  @return Returns random bee type as a string.
     */
    public static String getRandomBee(Biome biome){
        if (SPAWNABLE_BIOMES.get(biome) != null) {
            return SPAWNABLE_BIOMES.get(biome).next();
        }
        return BeeConstants.DEFAULT_BEE_TYPE;
    }

    /**
     * Returns a BeeData object for the given bee type.
     * If the bee type does not exist the default bee will be returned as a fallback.
     *
     *  @param bee Bee type for which BeeData is requested.
     *  @return Returns a BeeData object for the given bee type.
     */
    public static CustomBee getInfo(String bee){
        CustomBee info = BEE_INFO.get(bee);
        return info != null ? info : BEE_INFO.get(BeeConstants.DEFAULT_BEE_TYPE);
    }

    /**
     * Returns true if supplied parents can make a child bee.
     *
     *  @param parent1 Bee type for Parent 1.
     *  @param parent2 Bee type for parent 2.
     *  @return Returns true/false if parents can breed.
     */
    public static boolean canParentsBreed(String parent1, String parent2){
        return FAMILY_TREE.containsKey(BeeInfoUtils.sortParents(parent1, parent2));
    }

    /**
     * Returns the a weighted random bee type based on the supplied parents.
     *
     *  @param parent1 Bee type for Parent 1.
     *  @param parent2 Bee type for parent 2.
     *  @return Returns a weighted random bee type as a string.
     */
    public static String getWeightedChild(String parent1, String parent2){
        return FAMILY_TREE.get(BeeInfoUtils.sortParents(parent1, parent2)).next();
    }

    /**
     * Returns the adjusted weight for the supplied child's data.
     * The returned value is an adjusted percentage in the range of 0 - 100 represented as a double.
     * This value is calculated based on the weighting of all possible children the supplied child's parents can have.
     *
     *  @param child BeeData object for the child.
     *  @return Returns random bee type as a string.
     */
    public static double getAdjustedWeightForChild(CustomBee child){
        return FAMILY_TREE.get(BeeInfoUtils.sortParents(child.BreedData.getParent1(), child.BreedData.getParent2())).getAdjustedWeight(child.BreedData.getBreedWeight());
    }

    /**
     * Registers the supplied Bee Type and associated data to the mod.
     * If the bee already exists in the registry the method will return false.
     *
     *  @param beeType Bee Type of the bee being registered.
     *  @param customBee BeeData of the bee being registered
     *  @return Returns false if bee already exists in the registry.
     */
    public static boolean registerBee(String beeType, CustomBee customBee) {
        if (!BEE_INFO.containsKey(beeType)) {
            BEE_INFO.put(beeType, customBee);
            return true;
        }
        return false;
    }

    /**
     * Returns an unmodifiable copy of the Bee Registry.
     * This is useful for iterating over all bees without worry of changing data
     *
     *  @return Returns unmodifiable copy of bee registry.
     */
    public static Map<String, CustomBee> getBees() {
        return Collections.unmodifiableMap(BEE_INFO);
    }

    public static float[] getColorFloats(String color){
        Color tempColor = Color.decode(color);
        return tempColor.getComponents(null);
    }


/*    *//**
     * Sets a bees traits.
     *  @param name Bee Type of the bee having its traits set.
     *//*
    public static void setBeesTraits(String name){
        CustomBee bee = BEE_INFO.get(name);
        List<CompoundNBT> traits = new ArrayList<>();
        if (bee.isWitherBee()) traits.add(TraitRegistration.getTrait("wither"));
        if (bee.isBlazeBee()) traits.add(TraitRegistration.getTrait("blaze"));
        if (bee.isCanSwim()) traits.add(TraitRegistration.getTrait("canswim"));
        if (bee.isCreeperBee()) traits.add(TraitRegistration.getTrait("creeper"));
        if (bee.isZomBee()) traits.add(TraitRegistration.getTrait("zombie"));
        if (bee.isPigmanBee()) traits.add(TraitRegistration.getTrait("pigman"));
        if (bee.isEnderBee()) traits.add(TraitRegistration.getTrait("ender"));
        if (bee.isNetherBee()) traits.add(TraitRegistration.getTrait("nether"));
        BEE_INFO.get(name).addBeeTraits(traits);
    }*/
}
