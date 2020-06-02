package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
import net.minecraft.world.biome.Biome;

import java.util.*;

public class BeeInfo {

    public static final LinkedHashMap<String, BeeData> BEE_INFO = new LinkedHashMap<>();
    public static final HashMap<Biome, Set<String>> SPAWNABLE_BIOMES = new HashMap<>();
    public static final HashMap<Integer, String> FAMILY_TREE = new HashMap<>();

    public static float[] getBeeColorAsFloat(String color){
        java.awt.Color tempColor = java.awt.Color.decode(color);
        return tempColor.getComponents(null);
    }

    /**
     * Returns a random bee from the BEE_INFO hashmap.
     * This is used for selecting a bee from all possible bees.
     *
     *  @return Returns random bee type as a string.
     */
    public static String getRandomBee(){
        int randInt = MathUtils.nextInt(BEE_INFO.size() - 1) +1;
        Object[] dataArray = BEE_INFO.keySet().toArray();
        Object key = dataArray[randInt];
        return BEE_INFO.get(key.toString()).getName();
    }

    /**
     * Returns a random bee from the SPAWNABLE_BIOMES hashmap.
     * This is used for in-world spawning based on biome.
     *
     *  @return Returns random bee type as a string.
     */
    public static String getRandomBee(Biome biome){
        if (SPAWNABLE_BIOMES.get(biome) != null) {
            ArrayList<String> spawnList = new ArrayList<>(SPAWNABLE_BIOMES.get(biome));
            return spawnList.get(MathUtils.nextInt(spawnList.size()));
        }
        return BeeConst.DEFAULT_BEE_TYPE;
    }

    public static BeeData getInfo(String bee){
        BeeData info = BEE_INFO.get(bee);
        return info != null ? info : BEE_INFO.get(BeeConst.DEFAULT_BEE_TYPE);
    }

}
