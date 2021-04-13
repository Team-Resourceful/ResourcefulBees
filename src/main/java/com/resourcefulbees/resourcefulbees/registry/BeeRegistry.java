package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.EntityMutation;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import com.resourcefulbees.resourcefulbees.utils.validation.FirstPhaseValidator;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
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

    private final Map<String, CustomBeeData> beeInfo = new LinkedHashMap<>();
    private final Map<String, HoneyBottleData> honeyInfo = new LinkedHashMap<>();
    public final Map<Pair<String, String>, RandomCollection<CustomBeeData>> familyTree = new HashMap<>();

    private boolean allowRegistration;

    public static Map<ResourceLocation, RandomCollection<CustomBeeData>> getSpawnableBiomes() {
        return spawnableBiomes;
    }

    public void allowRegistration() {
        this.allowRegistration = true;
    }

    public void denyRegistration() {
        this.allowRegistration = false;
    }

    /**
     * Returns a BeeData object for the given bee type.
     *
     * @param bee Bee type for which BeeData is requested.
     * @return Returns a BeeData object for the given bee type.
     */
    public CustomBeeData getBeeData(String bee) {
        return beeInfo.get(bee);
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
     * @param child BeeData object for the child.
     * @return Returns random bee type as a string.
     */
    public double getAdjustedWeightForChild(CustomBeeData child, String parent1, String parent2) {
        return familyTree.get(BeeInfoUtils.sortParents(parent1, parent2)).getAdjustedWeight(child.getBreedData().getBreedWeight());
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
        if (allowRegistration && !beeInfo.containsKey(beeType) && FirstPhaseValidator.validate(customBeeData)) {
            beeInfo.put(beeType, customBeeData);
            if (customBeeData.getBreedData().isBreedable()) BeeInfoUtils.buildFamilyTree(customBeeData);
            if (customBeeData.getSpawnData().canSpawnInWorld()) BeeInfoUtils.parseBiomes(customBeeData);
            return true;
        }
        return false;
    }

    /**
     * Returns an unmodifiable copy of the Bee Registry.
     * This is useful for iterating over all bees without worry of changing registry data
     *
     * @return Returns unmodifiable copy of bee registry.
     */
    public Map<String, CustomBeeData> getBees() {
        return Collections.unmodifiableMap(beeInfo);
    }

    /**
     * Returns a set containing all registered CustomBeeData.
     * This is useful for iterating over all bees without worry of changing registry data
     *
     *  @return Returns a set containing all registered CustomBeeData.
     */
    public Set<CustomBeeData> getSetOfBees() {
        return Collections.unmodifiableSet(new HashSet<>(beeInfo.values()));
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
        if (allowRegistration && !honeyInfo.containsKey(honeyType) && FirstPhaseValidator.validate(honeyData)) {
            honeyInfo.put(honeyType, honeyData);
            return true;
        }
        return false;
    }

    public float getBreedChance(String parent1, String parent2, CustomBeeData childData) {
        return parent1.equals(parent2) ? 1F : childData.getBreedData().getBreedChance();
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
        return parents.getRight().equals(beeData.getName()) || parents.getLeft().equals(beeData.getName());
    }

    public Map<Pair<String, String>, CustomBeeData> getParents(CustomBeeData child) {
        return familyTree.entrySet().stream()
                .filter(this::registryContainsParents)
                .filter(entry -> entry.getValue().getMap().containsValue(child))
                .collect(Collectors.toMap(Map.Entry::getKey, o -> child));
    }

    public List<EntityMutation> getMutationsContaining(CustomBeeData beeData) {
        List<EntityMutation> mutations = new LinkedList<>();
        INSTANCE.getBees().forEach((s, beeData1) -> beeData1.getMutationData().getEntityMutations().forEach((entityType, pair) -> {
            if (entityType.getRegistryName() != null && entityType.getRegistryName().equals(beeData.getEntityTypeRegistryID())) {
                mutations.add(new EntityMutation(BeeInfoUtils.getEntityType(beeData1.getEntityTypeRegistryID()), entityType, pair));
            }else {
                pair.getRight().forEach(entityOutput -> {
                    if (entityOutput.getEntityType().getRegistryName() != null && entityOutput.getEntityType().getRegistryName().equals(beeData.getEntityTypeRegistryID())) {
                        mutations.add(new EntityMutation(BeeInfoUtils.getEntityType(beeData1.getEntityTypeRegistryID()), entityType, pair));
                    }
                });
            }
        }));
        return mutations;
    }
}
