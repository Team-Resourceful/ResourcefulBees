package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import com.resourcefulbees.resourcefulbees.utils.validation.FirstPhaseValidator;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeeRegistry implements IBeeRegistry {

    public static final HashMap<String, RegistryObject<EntityType<? extends CustomBeeEntity>>> MOD_BEES = new HashMap<>();
    public static final HashMap<ResourceLocation, RandomCollection<CustomBeeData>> SPAWNABLE_BIOMES = new HashMap<>();

    private static final BeeRegistry INSTANCE = new BeeRegistry();

    /**
     * Return the instance of this class. This is useful for calling methods to the mod from a static or threaded context.
     *
     * @return Instance of this class
     */
    public static BeeRegistry getRegistry() {
        return INSTANCE;
    }

    private final LinkedHashMap<String, CustomBeeData> BEE_INFO = new LinkedHashMap<>();
    private final LinkedHashMap<String, HoneyBottleData> HONEY_INFO = new LinkedHashMap<>();
    public final HashMap<Pair<String, String>, RandomCollection<CustomBeeData>> FAMILY_TREE = new HashMap<>();

    private boolean ALLOW_REGISTRATION;

    public void allowRegistration() {
        this.ALLOW_REGISTRATION = true;
    }

    public void denyRegistration() {
        this.ALLOW_REGISTRATION = false;
    }

    /**
     * Returns a BeeData object for the given bee type.
     *
     * @param bee Bee type for which BeeData is requested.
     * @return Returns a BeeData object for the given bee type.
     */
    public CustomBeeData getBeeData(String bee) {
        return BEE_INFO.get(bee);
    }

    /**
     * Returns a HoneyBottleData object for the given honey type.
     *
     * @param honey Honey type for which HoneyData is requested.
     * @return Returns a HoneyBottleData object for the given bee type.
     */
    public HoneyBottleData getHoneyData(String honey) {
        return HONEY_INFO.get(honey);
    }

    /**
     * Returns true if supplied parents can make a child bee.
     *
     * @param parent1 Bee type for Parent 1.
     * @param parent2 Bee type for parent 2.
     * @return Returns true/false if parents can breed.
     */
    public boolean canParentsBreed(String parent1, String parent2) {
        return FAMILY_TREE.containsKey(BeeInfoUtils.sortParents(parent1, parent2));
    }

    /**
     * Returns the a weighted random bee type based on the supplied parents.
     *
     * @param parent1 Bee type for Parent 1.
     * @param parent2 Bee type for parent 2.
     * @return Returns a weighted random bee type as a string.
     */
    public CustomBeeData getWeightedChild(String parent1, String parent2) {
        return FAMILY_TREE.get(BeeInfoUtils.sortParents(parent1, parent2)).next();
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
        return FAMILY_TREE.get(BeeInfoUtils.sortParents(parent1, parent2)).getAdjustedWeight(child.getBreedData().getBreedWeight());
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
        if (ALLOW_REGISTRATION) {
            if (!BEE_INFO.containsKey(beeType) && FirstPhaseValidator.validate(customBeeData)) {
                BEE_INFO.put(beeType, customBeeData);
                if (customBeeData.getBreedData().isBreedable()) BeeInfoUtils.buildFamilyTree(customBeeData);
                if (customBeeData.getSpawnData().canSpawnInWorld()) BeeInfoUtils.parseBiomes(customBeeData);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an unmodifiable copy of the Bee Registry.
     * This is useful for iterating over all bees without worry of changing data
     *
     * @return Returns unmodifiable copy of bee registry.
     */
    public Map<String, CustomBeeData> getBees() {
        return Collections.unmodifiableMap(BEE_INFO);
    }

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     * @return Returns unmodifiable copy of honey registry.
     */
    public Map<String, HoneyBottleData> getHoneyBottles() {
        return Collections.unmodifiableMap(HONEY_INFO);
    }

    /**
     * Registers the supplied Honey Type and associated data to the mod.
     * If the bee already exists in the registry the method will return false.
     *
     * @param honeyType Honey Type of the honey being registered.
     * @param honeyData HoneyData of the honey being registered
     * @return Returns false if bee already exists in the registry.
     */
    public boolean registerHoney(String honeyType, HoneyBottleData honeyData) {
        if (ALLOW_REGISTRATION) {
            if (!HONEY_INFO.containsKey(honeyType) && FirstPhaseValidator.validate(honeyData)) {
                HONEY_INFO.put(honeyType, honeyData);
                return true;
            }
        }
        return false;
    }

    public float getBreedChance(String parent1, String parent2, CustomBeeData childData) {
        if (parent1.equals(parent2)) return 1f;
        else return childData.getBreedData().getBreedChance();
    }
}
