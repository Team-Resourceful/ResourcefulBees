package com.teamresourceful.resourcefulbees.registry;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.EntityMutation;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.ItemMutation;
import com.teamresourceful.resourcefulbees.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.RandomCollection;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BeeRegistry implements IBeeRegistry {

    private static final Map<ResourceLocation, RandomCollection<CustomBeeData>> spawnableBiomes = new LinkedHashMap<>();

    private static final BeeRegistry INSTANCE = new BeeRegistry();

    /**
     * Return the instance of this class. This is useful for calling methods to the mod from a static or threaded context.
     *
     * @return Instance of this class
     */
    public static BeeRegistry getRegistry() {
        return INSTANCE;
    }

    private final Map<String, JsonObject> rawBeeData = new LinkedHashMap<>();
    private final Map<String, CustomBeeData> beeData = new LinkedHashMap<>();
    public final Map<Pair<String, String>, RandomCollection<BeeFamily>> familyTree = new LinkedHashMap<>();


    public static Map<ResourceLocation, RandomCollection<CustomBeeData>> getSpawnableBiomes() {
        return spawnableBiomes;
    }

    /**
     * Returns a BeeData object for the given bee type.
     *
     * @param beeType Bee type for which BeeData is requested.
     * @return Returns a BeeData object for the given bee type.
     */
    public CustomBeeData getBeeData(String beeType) {
        return beeData.getOrDefault(beeType, CustomBeeData.DEFAULT);

        /*return CustomBeeData.codec(bee).parse(JsonOps.INSTANCE, rawBeeData.get(bee))
                .getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not create Custom Bee Data for {} bee", bee));*/
    }

    public void regenerateCustomBeeData() {
        rawBeeData.forEach((s, jsonObject) -> beeData.compute(s, (s1, customBeeDataCodec) ->
                CustomBeeData.codec(s).parse(JsonOps.INSTANCE, jsonObject)
                .getOrThrow(false, s2 -> ResourcefulBees.LOGGER.error("Could not create Custom Bee Data for {} bee", s))));
        beeData.values().forEach(customBeeData -> {
            //post init stuff gets called here
            customBeeData.getBreedData().getParents().forEach(BeeFamily::postInit);
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
     * Registers the supplied Bee Type and associated data to the mod.
     * If the bee already exists in the registry the method will return false.
     *
     * @param beeType       Bee Type of the bee being registered.
     * @param customBeeData BeeData of the bee being registered
     * @return Returns false if bee already exists in the registry.
     */
/*    public boolean registerBee(String beeType, @NotNull CustomBeeData customBeeData) {
        if (!beeData.containsKey(beeType)) {
            beeData.put(beeType, Objects.requireNonNull(customBeeData, "Cannot register 'null' bee data!"));
            if (customBeeData.getBreedData().hasParents()) {
                buildFamilyTree(customBeeData);
            }
            if (customBeeData.getSpawnData().canSpawnInWorld()) {
                customBeeData.getSpawnData().getSpawnableBiomes()
                        .forEach(resourceLocation -> spawnableBiomes.computeIfAbsent(resourceLocation, k -> new RandomCollection<>()).add(customBeeData.getSpawnData().getSpawnWeight(), customBeeData));
            }
            return true;
        }
        return false;
    }*/

    /**
     * Registers the supplied Bee Type and associated data to the mod.
     *
     * @param beeType       Bee Type of the bee being registered.
     * @param beeData       Raw BeeData of the bee being registered
     */
    public void cacheRawBeeData(String beeType, JsonObject beeData) {
        rawBeeData.computeIfAbsent(beeType.toLowerCase(Locale.ENGLISH).replace(" ", "_"), s -> beeData);
    }

    public Map<String, JsonObject> getRawBees() {
        return Collections.unmodifiableMap(rawBeeData);
    }

    /**
     * Returns an unmodifiable copy of the Bee Registry.
     * This is useful for iterating over all bees without worry of changing registry data
     *
     * @return Returns unmodifiable copy of bee registry.
     */
    public Map<String, CustomBeeData> getBees() {
        return Collections.unmodifiableMap(beeData);
    }

    /**
     * Returns a set containing all registered CustomBeeData.
     * This is useful for iterating over all bees without worry of changing registry data
     *
     * @return Returns a set containing all registered CustomBeeData.
     */
    public Set<CustomBeeData> getSetOfBees() {
        return Collections.unmodifiableSet(new HashSet<>(beeData.values()));
    }

    public List<BeeFamily> getChildren(CustomBeeData parent) {
        return familyTree.entrySet().stream()
                .filter(mapEntry -> parentMatchesBee(mapEntry.getKey(), parent))
                .flatMap(entry -> entry.getValue().getMap().values().stream())
                .collect(Collectors.toList());
    }

    private boolean parentMatchesBee(Pair<String, String> parents, CustomBeeData beeData) {
        return parents.getRight().equals(beeData.getCoreData().getName()) || parents.getLeft().equals(beeData.getCoreData().getName());
    }

    public List<EntityMutation> getMutationsContaining(CustomBeeData beeData) {
        List<EntityMutation> mutations = new LinkedList<>();
        getBees().forEach((s, bee) ->  bee.getMutationData().getEntityMutations().forEach((entityType, randomCollection) ->  {
            if (entityType.getRegistryName() != null && entityType.getRegistryName().equals(beeData.getRegistryID())) {
                mutations.add(new EntityMutation(BeeInfoUtils.getEntityType(beeData.getRegistryID()), entityType, randomCollection, bee.getMutationData().getMutationCount()));
            } else {
                randomCollection.forEach(entityOutput -> {
                    if (entityOutput.getEntityType().getRegistryName() != null && entityOutput.getEntityType().getRegistryName().equals(beeData.getRegistryID())) {
                        mutations.add(new EntityMutation(beeData.getEntityType(), entityType, randomCollection, bee.getMutationData().getMutationCount()));
                    }
                });
            }
        }));
        return mutations;
    }

    public List<ItemMutation> getItemMutationsContaining(CustomBeeData beeData) {
        List<ItemMutation> mutations = new LinkedList<>();
        INSTANCE.getBees().forEach((s, beeData1) ->   //THIS MAY BE BROKE AND NEED FIXING!
            beeData1.getMutationData().getItemMutations().forEach((block, randomCollection) ->  randomCollection.forEach(itemOutput -> {
                if (itemOutput.getItem() == BeeSpawnEggItem.byId(beeData.getEntityType())) {
                    mutations.add(new ItemMutation(BeeInfoUtils.getEntityType(beeData1.getRegistryID()), block, randomCollection, beeData1.getMutationData().getMutationCount()));
                }
            }))
        );
        return mutations;
    }

    public static void buildSpawnableBiomes() {
        spawnableBiomes.clear();
        getRegistry().getBees().values().stream()
                .filter(customBeeData -> customBeeData.getSpawnData().canSpawnInWorld())
                .forEach(customBeeData -> customBeeData.getSpawnData().getSpawnableBiomes()
                .forEach(resourceLocation -> spawnableBiomes.computeIfAbsent(resourceLocation, k -> new RandomCollection<>()).add(customBeeData.getSpawnData().getSpawnWeight(), customBeeData)));
    }

    public static void buildFamilyTree() {
        getRegistry().familyTree.clear();
        getRegistry().getBees().values().stream()
                .filter(customBeeData -> customBeeData.getBreedData().hasParents())
                .flatMap(customBeeData -> customBeeData.getBreedData().getParents().stream())
                .filter(BeeFamily::hasValidParents)
                .forEach(BeeRegistry::addBreedPairToFamilyTree);
    }

    private static void addBreedPairToFamilyTree(BeeFamily beeFamily) {
        getRegistry().familyTree.computeIfAbsent(beeFamily.getParents(), k -> new RandomCollection<>()).add(beeFamily.getWeight(), beeFamily);
    }

    public static boolean containsBeeType(String beeType) {
        return getRegistry().getBees().containsKey(beeType);
    }
}
