package com.resourcefulbees.resourcefulbees.registry;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.BreedData;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.EntityMutation;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.ItemMutation;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.item.BeeSpawnEggItem;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class BeeRegistry implements IBeeRegistry {

    private static final Map<ResourceLocation, RandomCollection<CustomBeeData>> spawnableBiomes = new HashMap<>();

    private static final BeeRegistry INSTANCE = new BeeRegistry();

    /**
     * Return the instance of this class. This is useful for calling methods to the mod from a static or threaded context.
     *
     * @return Instance of this class
     */
    public static BeeRegistry getRegistry() {
        return INSTANCE;
    }

    private BeeRegistry() {
        this.allowRegistration = true;
        ResourcefulBees.LOGGER.info("Bee Registration Enabled...");
    }

    private final Map<String, JsonObject> rawBeeData = new LinkedHashMap<>();
    private final Map<String, JsonObject> rawHoneyData = new LinkedHashMap<>();
    private final Map<String, CustomBeeData> beeData = new LinkedHashMap<>();
    private final Map<String, HoneyBottleData> honeyInfo = new LinkedHashMap<>();
    public final Map<Pair<String, String>, RandomCollection<CustomBeeData>> familyTree = new HashMap<>();

    private boolean allowRegistration;

    public static Map<ResourceLocation, RandomCollection<CustomBeeData>> getSpawnableBiomes() {
        return spawnableBiomes;
    }

    public void denyRegistration() {
        this.allowRegistration = false;
        ResourcefulBees.LOGGER.info("Bee Registration Disabled...");
    }

    /**
     * Returns a BeeData object for the given bee type.
     *
     * @param bee Bee type for which BeeData is requested.
     * @return Returns a BeeData object for the given bee type.
     */
    public CustomBeeData getBeeData(String bee) {
        return CustomBeeData.codec(bee).parse(JsonOps.INSTANCE, rawBeeData.get(bee))
                .getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not create Custom Bee Data for {} bee", bee));
    }

    //TODO Look at calling this method early on to reduce method calls and number of created objects in addition to where it is called now
    public void regenerateCustomBeeData() {
        rawBeeData.forEach((s, jsonObject) -> {
            System.out.println(s);
            beeData.compute(s, (s1, customBeeDataCodec) ->  CustomBeeData.codec(s).parse(JsonOps.INSTANCE, jsonObject)
                    .getOrThrow(false, s2 -> ResourcefulBees.LOGGER.error("Could not create Custom Bee Data for {} bee", s)));
        });
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
     * Returns a HoneyBottleData object for the given honey type.
     *
     * @param honey Honey type for which HoneyData is requested.
     * @return Returns a HoneyBottleData object for the given bee type.
     */
    public HoneyBottleData getHoneyData(String honey) {
        return honeyInfo.get(honey);
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
    public CustomBeeData getWeightedChild(String parent1, String parent2) {
        return familyTree.get(BeeInfoUtils.sortParents(parent1, parent2)).next();
    }

    /**
     * Returns the adjusted weight for the supplied child's data.
     * The returned value is an adjusted percentage in the range of 0 - 100 represented as a double.
     * This value is calculated based on the weighting of all possible children the supplied child's parents can have.
     *
     * @param childBreedData BeeData object for the child.
     * @return Returns random bee type as a string.
     */
    public double getAdjustedWeightForChild(CustomBeeData childBreedData, CustomBeeData parent1, CustomBeeData parent2) {
        return familyTree.get(BeeInfoUtils.sortParents(parent1.getCoreData().getName(), parent2.getCoreData().getName())).getAdjustedWeight(childBreedData.getBreedData().getBreedWeight());
    }

    /**
     * Registers the supplied Bee Type and associated data to the mod.
     * If the bee already exists in the registry the method will return false.
     *
     * @param beeType       Bee Type of the bee being registered.
     * @param customBeeData BeeData of the bee being registered
     * @return Returns false if bee already exists in the registry.
     */
    public boolean registerBee(String beeType, CustomBeeData customBeeData) {

        beeData.put(beeType, customBeeData);
        return true;
/*

        if (allowRegistration && !beeInfo.containsKey(beeType) && FirstPhaseValidator.validate(customBeeData)) {
            beeInfo.put(beeType, customBeeData);
            if (customBeeData.getBreedData().isBreedable()) BeeInfoUtils.buildFamilyTree(customBeeData);
            if (customBeeData.getSpawnData().canSpawnInWorld()) BeeInfoUtils.parseBiomes(customBeeData);
            return true;
        }
        return false;
*/
    }

    /**
     * Registers the supplied Bee Type and associated data to the mod.
     *
     * @param beeType       Bee Type of the bee being registered.
     * @param beeData       Raw BeeData of the bee being registered
     */
    public void cacheRawBeeData(String beeType, JsonObject beeData) {
        rawBeeData.computeIfAbsent(beeType, s -> beeData);
    }

    public void cacheRawHoneyData(String name, JsonObject jsonObject) {
        rawHoneyData.computeIfAbsent(name, s -> jsonObject);
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

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     * @return Returns unmodifiable copy of honey registry.
     */
    public Map<String, HoneyBottleData> getHoneyBottles() {
        return Collections.unmodifiableMap(honeyInfo);
    }

    /**
     * Registers the supplied Honey Type and associated data to the mod.
     * If the bee already exists in the registry the method will return false.
     *
     * @param honeyType Honey Type of the honey being registered.
     * @param honeyData HoneyData of the honey being registered
     * @return Returns false if bee already exists in the registry.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean registerHoney(String honeyType, HoneyBottleData honeyData) {
        honeyInfo.putIfAbsent(honeyType, honeyData);
        return true;
    }

    public float getBreedChance(String parent1, String parent2, BreedData childData) {
        return parent1.equals(parent2) ? 1F : childData.getBreedChance();
    }

    public Map<Pair<String, String>, RandomCollection<CustomBeeData>> getChildren(CustomBeeData parent) {
        return familyTree.entrySet().stream()
                .filter(this::registryContainsParents)
                .filter(mapEntry -> parentMatchesBee(mapEntry.getKey(), parent))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean registryContainsParents(Map.Entry<Pair<String, String>, RandomCollection<CustomBeeData>> mapEntry) {
        return getBees().containsKey(mapEntry.getKey().getLeft()) && getBees().containsKey(mapEntry.getKey().getRight());
    }

    private boolean parentMatchesBee(Pair<String, String> parents, CustomBeeData beeData) {
        return parents.getRight().equals(beeData.getCoreData().getName()) || parents.getLeft().equals(beeData.getCoreData().getName());
    }

    public Map<Pair<String, String>, CustomBeeData> getParents(CustomBeeData child) {
        return familyTree.entrySet().stream()
                .filter(this::registryContainsParents)
                .filter(entry -> entry.getValue().getMap().containsValue(child))
                .collect(Collectors.toMap(Map.Entry::getKey, o -> child));
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

    public Map<String, JsonObject> getRawHoney() {
        return rawHoneyData;
    }
}
