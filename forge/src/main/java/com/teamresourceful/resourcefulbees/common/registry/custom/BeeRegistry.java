package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.Parents;import com.teamresourceful.resourcefulbees.common.data.beedata.breeding.BeeParents;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class BeeRegistry implements com.teamresourceful.resourcefulbees.api.registry.BeeRegistry {

    private static final BeeRegistry INSTANCE = new BeeRegistry();
    private static final Map<String, JsonObject> RAW_DATA = new LinkedHashMap<>();
    private static final Map<String, CustomBeeData> CUSTOM_DATA = new LinkedHashMap<>();
    private static final Map<Parents, WeightedCollection<FamilyUnit>> FAMILY_TREE = new LinkedHashMap<>();
    private static final Supplier<CustomBeeData> DEFAULT_DATA = Suppliers.memoize(() -> ResourcefulBeesAPI.getInitializers().data("error", Map.of()));

    private BeeRegistry() {
        // Single instanced classes do not need to be able to be extended
    }

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

    public boolean containsBeeType(String beeType) {
        return CUSTOM_DATA.containsKey(beeType);
    }

    @Override
    public CustomBeeData getBeeData(String beeType) {
        return CUSTOM_DATA.getOrDefault(beeType, DEFAULT_DATA.get());
    }

    /**
     * This method first iterates over the internal map of raw data and populates
     * the registry with new {@link CustomBeeData} objects that have been parsed
     * using the codecs. Post Init methods are then ran on BeeFamilies to ensure
     * data has populated correctly. Finally, the family trees and spawnable biomes
     * maps are constructed.
     */
    public void regenerateCustomBeeData(RegistryAccess access) {
        DynamicOps<JsonElement> ops = access == null ? JsonOps.INSTANCE : RegistryOps.create(JsonOps.INSTANCE, access);
        RAW_DATA.forEach((s, jsonObject) -> CUSTOM_DATA.compute(s, (s1, customBeeDataCodec) -> parseData(s, ops, jsonObject)));
        com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry.buildFamilyTree();
    }

    private static CustomBeeData parseData(String id, DynamicOps<JsonElement> ops, JsonObject jsonObject) {
        var data = new DispatchMapCodec<>(ResourceLocation.CODEC, BeeDataRegistry.codec(id))
                .parse(ops, jsonObject)
                .getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not create Custom Bee Data for {} bee", id));
        return ResourcefulBeesAPI.getInitializers().data(id, data);
    }

    /**
     * Returns a BeeData object for the given bee type.
     *
     * @param bee Bee type for which BeeData is requested.
     * @return Returns a BeeData object for the given bee type.
     */
    @Override
    public JsonObject getRawBeeData(String bee) {
        return RAW_DATA.get(bee);
    }

    @Override
    public Map<Parents, WeightedCollection<FamilyUnit>> getFamilyTree() {
        return FAMILY_TREE;
    }

    @Override
    public boolean canParentsBreed(String parent1, String parent2) {
        return FAMILY_TREE.containsKey(BeeParents.nullOf(parent1, parent2));
    }

    @Override
    public FamilyUnit getWeightedChild(String parent1, String parent2) {
        return FAMILY_TREE.get(BeeParents.nullOf(parent1, parent2)).next();
    }

    @Override
    public double getAdjustedWeightForChild(FamilyUnit beeFamily) {
        return FAMILY_TREE.get(beeFamily.getParents()).getAdjustedWeight(beeFamily.weight());
    }

    /**
     * Registers the supplied Bee Type and associated data, in the form
     * of a {@link JsonObject}, to the mod.
     *
     * @param beeType The Bee Type of the bee being registered.
     * @param beeData The raw BeeData of the bee being registered
     */
    public void cacheRawBeeData(String beeType, JsonObject beeData) {
        RAW_DATA.computeIfAbsent(beeType.toLowerCase(Locale.ENGLISH).replace(" ", "_"), s -> Objects.requireNonNull(beeData));
    }

    @Override
    public Map<String, JsonObject> getRawBees() {
        return Collections.unmodifiableMap(RAW_DATA);
    }

    @Override
    public Map<String, CustomBeeData> getBees() {
        return Collections.unmodifiableMap(CUSTOM_DATA);
    }

    @Override
    public Set<CustomBeeData> getSetOfBees() {
        return Set.copyOf(CUSTOM_DATA.values());
    }

    @Override
    public Stream<CustomBeeData> getStreamOfBees() {
        return getSetOfBees().stream();
    }

    @Override
    public Set<String> getBeeTypes() {
        return Set.copyOf(CUSTOM_DATA.keySet());
    }

    //region Setup

    private static void buildFamilyTree() {
        FAMILY_TREE.clear();
        CUSTOM_DATA.values().stream()
                .filter(customBeeData -> customBeeData.getBreedData().hasParents())
                .flatMap(customBeeData -> customBeeData.getBreedData().families().stream())
                .filter(FamilyUnit::validUnit)
                .forEach(com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry::addBreedPairToFamilyTree);
    }

    private static void addBreedPairToFamilyTree(FamilyUnit beeFamily) {
        FAMILY_TREE.computeIfAbsent(beeFamily.getParents(), k -> new WeightedCollection<>()).add(beeFamily.weight(), beeFamily);
    }
    //endregion
}
