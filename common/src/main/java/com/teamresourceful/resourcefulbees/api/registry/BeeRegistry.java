package com.teamresourceful.resourcefulbees.api.registry;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.Parents;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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

    static BeeRegistry get() {
        return ResourcefulBeesAPI.getRegistry().getBeeRegistry();
    }

    /**
     * Returns a BeeData object for the given bee type.
     *
     *  @param bee Bee type for which BeeData is requested.
     *  @return Returns a BeeData object for the given bee type.
     */
    CustomBeeData getBeeData(String bee);

    /**
     * Returns a BeeData object for the given bee type.
     *
     *  @param bee Bee type for which BeeData is requested.
     *  @return Returns an optional BeeData object for the given bee type.
     */
    Optional<CustomBeeData> getOptionalBeeData(String bee);

    /**
     * Returns weather a bee type is registered in the registry.
     *
     *  @param bee Bee type for which bee is checked.
     *  @return Returns a boolean if the bee type is registered.
     */
    boolean containsBeeType(String bee);

    /**
     * Returns a JsonObject for the given bee type.
     *
     * @param bee Bee type for which BeeData is requested.
     * @return Returns a JsonObject for the given bee type.
     */
    JsonObject getRawBeeData(String bee);

    /**
     * Returns a map of parents to families.
     * @return Returns a map of parents to families.
     */
    Map<Parents, WeightedCollection<FamilyUnit>> getFamilyTree();

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
    FamilyUnit getWeightedChild(String parent1, String parent2);

    /**
     * Returns the adjusted weight for the supplied child's data.
     * The returned value is an adjusted percentage in the range of 0 - 100 represented as a double.
     * This value is calculated based on the weighting of all possible children the supplied child's parents can have.
     *
     *  @param beeFamily BeeData object for the child.
     *  @return Returns random bee type as a string.
     */
    double getAdjustedWeightForChild(FamilyUnit beeFamily);

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

    /**
     * A helper method that returns a stream using the {@link BeeRegistry#getSetOfBees()} method.
     */
    Stream<CustomBeeData> getStreamOfBees();

    /**
     *  @return Returns a set containing all bee types.
     */
    Set<String> getBeeTypes();

    Map<String, JsonObject> getRawBees();
}
