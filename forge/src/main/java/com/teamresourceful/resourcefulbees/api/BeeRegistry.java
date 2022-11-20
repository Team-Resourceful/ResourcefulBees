package com.teamresourceful.resourcefulbees.api;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;

import java.util.Map;
import java.util.Set;

/**
 * An interface for working with the Bee Registry. The bee Registry
 * is a central point for getting any bee data pertinent to
 * <i>Resourceful Bees</i>. The registry contains a cache of {@link JsonObject}'s
 * and {@link CustomBeeData} objects for all bees registered to the mod.
 * The registry also contains the spawn rules and breeding rules
 * for bees created by <i>Resourceful Bees</i>.
 */
@SuppressWarnings("unused")
public interface BeeRegistry {

    /**
     * Returns a BeeData object for the given bee type.
     *
     *  @param bee Bee type for which BeeData is requested.
     *  @return Returns a BeeData object for the given bee type.
     */
    CustomBeeData getBeeData(String bee);

    /**
     * Returns a JsonObject for the given bee type.
     *
     * @param bee Bee type for which BeeData is requested.
     * @return Returns a JsonObject for the given bee type.
     */
    JsonObject getRawBeeData(String bee);

    /**
     * Returns true if supplied parents can make a child bee.
     *
     *  @param parent1 Bee type for Parent 1.
     *  @param parent2 Bee type for parent 2.
     *  @return Returns true/false if parents can breed.
     */
    boolean canParentsBreed(String parent1, String parent2);

    /**
     * Returns the a weighted random bee type based on the supplied parents.
     *
     *  @param parent1 Bee type for Parent 1.
     *  @param parent2 Bee type for parent 2.
     *  @return Returns a weighted random bee type as a string.
     */
    BeeFamily getWeightedChild(String parent1, String parent2);

    /**
     * Returns the adjusted weight for the supplied child's data.
     * The returned value is an adjusted percentage in the range of 0 - 100 represented as a double.
     * This value is calculated based on the weighting of all possible children the supplied child's parents can have.
     *
     *  @param beeFamily BeeData object for the child.
     *  @return Returns random bee type as a string.
     */
    double getAdjustedWeightForChild(BeeFamily beeFamily);

    /**
     * Returns an unmodifiable copy of the Bee Registry.
     * This is useful for iterating over all bees without worry of changing data
     *
     *  @return Returns unmodifiable copy of bee registry.
     */
    Map<String, CustomBeeData> getBees();

    /**
     * Returns a set containing all registered CustomBeeData.
     * This is useful for iterating over all bees without worry of changing registry data
     *
     *  @return Returns a set containing all registered CustomBeeData.
     */
    Set<CustomBeeData> getSetOfBees();

    //void cacheRawBeeData(String name, JsonObject beeData);

    Map<String, JsonObject> getRawBees();
}
