package com.teamresourceful.resourcefulbees.registry;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.RegisterBeeEvent;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.RandomCollection;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Unmodifiable
public class BeeRegistry implements IBeeRegistry {

    private static final Map<ResourceLocation, RandomCollection<CustomBeeData>> spawnableBiomes = new LinkedHashMap<>();
    private static final Map<String, JsonObject> rawBeeData = new LinkedHashMap<>();
    private static final Map<String, CustomBeeData> beeData = new LinkedHashMap<>();
    private static final Map<Pair<String, String>, RandomCollection<BeeFamily>> familyTree = new LinkedHashMap<>();
    private static final BeeRegistry INSTANCE = new BeeRegistry();

    /**
     * Returns an instance of the {@link BeeRegistry} for accessing data from the registry.
     * The bee Registry is a central point for getting any bee data pertinent to
     * <i>Resourceful Bees</i>. The registry contains a cache of {@link JsonObject}'s and
     * {@link CustomBeeData} objects for all bees registered to the mod. The registry also
     * contains the spawn rules and breeding rules for bees created by <i>Resourceful Bees</i>.
     *
     * @return Returns an instance of the {@link BeeRegistry} for accessing data from the registry.
     */
    public static BeeRegistry getRegistry() {
        return INSTANCE;
    }

    public static boolean isSpawnableBiome(ResourceLocation biome) {
        return spawnableBiomes.containsKey(biome);
    }

    public static RandomCollection<CustomBeeData> getSpawnableBiome(ResourceLocation biome) {
        return spawnableBiomes.get(biome);
    }

    public static boolean containsBeeType(String beeType) {
        return beeData.containsKey(beeType);
    }

    public Map<Pair<String, String>, RandomCollection<BeeFamily>> getFamilyTree() {
        return familyTree;
    }

    /**
     * Returns a {@link CustomBeeData} object for the given bee type.
     *
     * @param beeType Bee type for which BeeData is requested.
     * @return Returns a {@link CustomBeeData} object for the given bee type.
     */
    public CustomBeeData getBeeData(String beeType) {
        return beeData.getOrDefault(beeType, CustomBeeData.DEFAULT);
    }

    /**
     * This method first iterates over the internal map of raw data and populates
     * the registry with new {@link CustomBeeData} objects that have been parsed
     * using the codecs. A {@link RegisterBeeEvent} is then posted for other mods
     * to register {@link CustomBeeData} objects they want Resourceful Bees to have
     * compatibility with. Post Init methods are then ran on BeeFamilies to ensure
     * data has populated correctly. Finally, the family trees and spawnable biomes
     * maps are constructed.
     */
    public void regenerateCustomBeeData() {
        rawBeeData.forEach((s, jsonObject) -> beeData.compute(s, (s1, customBeeDataCodec) ->
                CustomBeeData.codec(s).parse(JsonOps.INSTANCE, jsonObject)
                .getOrThrow(false, s2 -> ResourcefulBees.LOGGER.error("Could not create Custom Bee Data for {} bee", s))));
        //TODO verify this arch event works correctly - and figure out how forge
        // accesses it - does not load in testing apparently. fails due to not being
        // a forge event. - something must be missing or bc it's in forge module.
        //MinecraftForge.EVENT_BUS.post(new RegisterBeeEvent(beeData)); <- forge event
        //RegisterBeeEvent.EVENT.invoker().act(new RegisterBeeEvent(beeData)); <- arch event
        beeData.values().forEach(customBeeData -> {
            //post init stuff gets called here
            customBeeData.getBreedData().getFamilies().forEach(BeeFamily::postInit);
        });
        BeeRegistry.buildFamilyTree();
        BeeRegistry.buildSpawnableBiomes();
    }

    /**
     * Returns a BeeData object for the given bee type.
     *
     * @param bee Bee type for which BeeData is requested.
     * @return Returns a BeeData object for the given bee type.
     */
    @Override
    public JsonObject getRawBeeData(String bee) {
        return rawBeeData.get(bee);
    }

    /**
     * Returns true if supplied parents can make a child bee.
     *
     * @param parent1 Bee type for Parent 1.
     * @param parent2 Bee type for parent 2.
     * @return Returns true/false if parents can breed.
     */
    public boolean canParentsBreed(String parent1, String parent2) {
        return familyTree.containsKey(BeeInfoUtils.sortParents(parent1, parent2));
    }

    /**
     * Returns the a weighted random bee type based on the supplied parents.
     *
     * @param parent1 Bee type for Parent 1.
     * @param parent2 Bee type for parent 2.
     * @return Returns a weighted random bee type as a string.
     */
    public BeeFamily getWeightedChild(String parent1, String parent2) {
        return familyTree.get(BeeInfoUtils.sortParents(parent1, parent2)).next();
    }

    /**
     * Returns the adjusted weight for the supplied child's data.
     * The returned value is an adjusted percentage in the range of 0 - 100 represented as a double.
     * This value is calculated based on the weighting of all possible children the supplied child's parents can have.
     *
     * @param beeFamily BeeData object for the child.
     * @return Returns random bee type as a string.
     */
    public double getAdjustedWeightForChild(BeeFamily beeFamily) {
        return familyTree.get(beeFamily.getParents()).getAdjustedWeight(beeFamily.getWeight());
    }

    /**
     * Registers the supplied Bee Type and associated data, in the form
     * of a {@link JsonObject}, to the mod.
     *
     * @param beeType The Bee Type of the bee being registered.
     * @param beeData The raw BeeData of the bee being registered
     */
    public void cacheRawBeeData(String beeType, JsonObject beeData) {
        rawBeeData.computeIfAbsent(beeType.toLowerCase(Locale.ENGLISH).replace(" ", "_"), s -> Objects.requireNonNull(beeData));
    }

    /**
     * Returns an unmodifiable copy of the internal {@link JsonObject} map representing
     * the raw json data. This is useful for iterating over all bees without worry of
     * changing registry data as the objects contained in the map are immutable.
     *
     * @return Returns unmodifiable copy of the internal {@link JsonObject} map representing
     * the raw json data.
     */
    public Map<String, JsonObject> getRawBees() {
        return Collections.unmodifiableMap(rawBeeData);
    }

    /**
     * Returns an unmodifiable copy of the internal {@link CustomBeeData} map.
     * This is useful for iterating over all bees without worry of changing registry data
     * as the objects contained in the map are immutable.
     *
     * @return Returns an unmodifiable copy of the internal {@link CustomBeeData} map.
     */
    public Map<String, CustomBeeData> getBees() {
        return Collections.unmodifiableMap(beeData);
    }

    /**
     * A helper method that returns an unmodifiable set of the values contained in the internal
     * {@link CustomBeeData} map. This is useful for iterating over all bees without
     * worry of changing registry data as the objects contained in the map are immutable.
     *
     * @return Returns an unmodifiable set of the values contained in the internal
     * {@link CustomBeeData} map
     */
    public Set<CustomBeeData> getSetOfBees() {
        return Collections.unmodifiableSet(new HashSet<>(beeData.values()));
    }

    //region Setup
    private static void buildSpawnableBiomes() {
        spawnableBiomes.clear();
        beeData.values().stream()
                .filter(customBeeData -> customBeeData.getSpawnData().canSpawnInWorld())
                .forEach(customBeeData -> customBeeData.getSpawnData().getSpawnableBiomes()
                .forEach(resourceLocation -> spawnableBiomes.computeIfAbsent(resourceLocation, k -> new RandomCollection<>()).add(customBeeData.getSpawnData().getSpawnWeight(), customBeeData)));
    }

    private static void buildFamilyTree() {
        familyTree.clear();
        beeData.values().stream()
                .filter(customBeeData -> customBeeData.getBreedData().hasParents())
                .flatMap(customBeeData -> customBeeData.getBreedData().getFamilies().stream())
                .filter(BeeFamily::hasValidParents)
                .forEach(BeeRegistry::addBreedPairToFamilyTree);
    }

    private static void addBreedPairToFamilyTree(BeeFamily beeFamily) {
        familyTree.computeIfAbsent(beeFamily.getParents(), k -> new RandomCollection<>()).add(beeFamily.getWeight(), beeFamily);
    }
    //endregion
}
