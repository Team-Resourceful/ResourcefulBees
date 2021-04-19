package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.ApiaryOutput;
import com.resourcefulbees.resourcefulbees.lib.BaseModelTypes;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public class CustomBeeData extends AbstractBeeData {

    /**
     * Which flowers the bee pollinates.
     */
    private String flower;

    /**
     * List of all block bees flowers
     */
    private transient HashSet<Block> blockFlowers;

    /**
     * List of all entity bees flowers
     */
    private transient ResourceLocation entityFlower;

    /**
     * The base texture the bee uses if it uses a custom texture.
     */
    private String baseLayerTexture;

    /**
     * The base model the bee uses
     */
    private BaseModelTypes baseModelType;

    /**
     * How long the bee can stay in a hive.
     */
    private int maxTimeInHive;

    /**
     * How big the bee is.
     */
    private float sizeModifier;

    /**
     * The special attributes can have.
     * <p>
     * With that it can as example port like an enderman.
     */
    private String[] traits;

    /**
     * The name of the bee for the registry.
     */
    private transient String name;

    /**
     * If the bee has a comb.
     */
    private boolean hasHoneycomb;

    /**
     * If a custom item should drop when normally combs drop.
     */
    private String customCombDrop;

    /**
     * If a custom item should drop when the comb block is broken.
     */
    private String customCombBlockDrop;

    /**
     * How much should come out of apiaries in combs.
     */
    private int[] apiaryOutputAmounts = new int[]{Config.T1_APIARY_QUANTITY.get(), Config.T2_APIARY_QUANTITY.get(), Config.T3_APIARY_QUANTITY.get(), Config.T4_APIARY_QUANTITY.get()};

    private ApiaryOutput[] apiaryOutputTypes = BeeInfoUtils.getDefaultApiaryTypes();

    private transient boolean shouldResourcefulBeesDoForgeRegistration;

    /**
     * Additional Data added by a Mod Author
     */
    private final transient HashMap<String, AbstractBeeData> ADDITIONAL_DATA = new HashMap<>();

    /**
     * Data for Breeding the Bee
     */
    private BreedData BreedData = null;

    /**
     * Data for Comb outputs
     */
    private CentrifugeData CentrifugeData = null;

    /**
     * Data for the coloring of the bee
     */
    private ColorData ColorData = null;

    /**
     * Data for the combat skills of the bee
     */
    private CombatData CombatData = null;

    /**
     * Data for Block Mutation
     */
    private MutationData MutationData = null;

    /**
     * Data for spawning of the Bee
     */
    private SpawnData SpawnData = null;

    /**
     * Data for Bee traits
     */
    private TraitData TraitData = null;

    private transient Supplier<ItemStack> combSupplier;
    private transient Supplier<ItemStack> combBlockItemSupplier;

    /**
     * The RegistryObject of the Bee Comb
     */
    private transient RegistryObject<Item> combRegistryObject;

    /**
     * The RegistryObject of the Bee Comb Block
     */
    private transient RegistryObject<Block> combBlockRegistryObject;

    /**
     * The RegistryObject of the Bee Comb Block Item
     */
    private transient RegistryObject<Item> combBlockItemRegistryObject;

    /**
     * The RegistryObject of the Bee Spawn Egg
     */
    private transient RegistryObject<Item> spawnEggItemRegistryObject;

    /**
     * The Registry ID of the Bee EntityType
     */
    private transient ResourceLocation entityTypeRegistryID;

    /**
     * is an easter egg bee
     */
    private final transient boolean isEasterEggBee;

    private CustomBeeData() {
        super("CustomBeeData");
        isEasterEggBee = false;
    }

    private CustomBeeData(String flower, String baseLayerTexture, BaseModelTypes baseModelType, int maxTimeInHive, float sizeModifier, String[] traits, int[] apiaryOutputAmounts, ApiaryOutput[] apiaryOutputTypes, String name, boolean hasHoneycomb, MutationData mutationData, ColorData colorData, CombatData combatData, CentrifugeData centrifugeData, BreedData breedData, SpawnData spawnData, TraitData traitData, boolean isEasterEggBee) {
        super("CustomBeeData");
        this.flower = flower;
        this.baseLayerTexture = baseLayerTexture;
        this.baseModelType = baseModelType;
        this.maxTimeInHive = maxTimeInHive;
        this.sizeModifier = sizeModifier;
        this.traits = traits;
        this.apiaryOutputAmounts = apiaryOutputAmounts;
        this.apiaryOutputTypes = apiaryOutputTypes;
        this.name = name;
        this.hasHoneycomb = hasHoneycomb;
        this.BreedData = breedData;
        this.CentrifugeData = centrifugeData;
        this.ColorData = colorData;
        this.MutationData = mutationData;
        this.SpawnData = spawnData;
        this.TraitData = traitData;
        this.CombatData = combatData;
        this.isEasterEggBee = isEasterEggBee;
    }

    public boolean isEasterEggBee() {
        return isEasterEggBee;
    }

    public String getFlower() {
        return flower == null ? BeeConstants.FLOWER_TAG_ALL : flower.toLowerCase(Locale.ENGLISH);
    }

    public ResourceLocation getEntityFlower() {
        return this.entityFlower;
    }

    public boolean hasEntityFlower() {
        return this.entityFlower != null && !this.entityFlower.getNamespace().isEmpty() && !this.entityFlower.getPath().isEmpty();
    }

    public void setEntityFlower(ResourceLocation entityID) {
        this.entityFlower = entityID;
    }

    public Set<Block> getBlockFlowers() {
        return Collections.unmodifiableSet(this.blockFlowers);
    }

    public boolean hasBlockFlowers() {
        return this.blockFlowers != null && !this.blockFlowers.isEmpty();
    }

    public void addBlockFlower(Block block) {
        if (this.blockFlowers == null) this.blockFlowers = new HashSet<>();
        this.blockFlowers.add(block);
    }

    public String getCustomCombDrop() {
        return customCombDrop;
    }

    public String getCustomCombBlockDrop() {
        return customCombBlockDrop;
    }

    public int[] getApiaryOutputAmounts() {
        return apiaryOutputAmounts;
    }

    public ApiaryOutput[] getApiaryOutputsTypes() {
        return apiaryOutputTypes != null ? apiaryOutputTypes : BeeInfoUtils.getDefaultApiaryTypes();
    }


    public boolean hasCustomDrop() {
        return (customCombDrop != null && !customCombDrop.isEmpty() && customCombBlockDrop != null && !customCombBlockDrop.isEmpty()) || isEasterEggBee();
    }

    public boolean hasHoneycomb() {
        return hasHoneycomb;
    }

    public String getBaseLayerTexture() {
        return baseLayerTexture == null ? "custom/bee" : baseLayerTexture.toLowerCase(Locale.ENGLISH);
    }

    public int getMaxTimeInHive() {
        if (maxTimeInHive < BeeConstants.MIN_HIVE_TIME) {
            return maxTimeInHive == 0 ? BeeConstants.MAX_TIME_IN_HIVE : BeeConstants.MIN_HIVE_TIME;
        }
        return maxTimeInHive;
    }

    public RegistryObject<Item> getCombRegistryObject() {
        return combRegistryObject;
    }

    public void setCombRegistryObject(RegistryObject<Item> combRegistryObject) {
        this.combRegistryObject = this.combRegistryObject == null ? combRegistryObject : this.combRegistryObject;
    }

    public RegistryObject<Block> getCombBlockRegistryObject() {
        return combBlockRegistryObject;
    }

    public void setCombBlockRegistryObject(RegistryObject<Block> combBlockRegistryObject) {
        this.combBlockRegistryObject = this.combBlockRegistryObject == null ? combBlockRegistryObject : this.combBlockRegistryObject;
    }

    public RegistryObject<Item> getCombBlockItemRegistryObject() {
        return combBlockItemRegistryObject;
    }

    public void setCombBlockItemRegistryObject(RegistryObject<Item> combBlockItemRegistryObject) {
        this.combBlockItemRegistryObject = this.combBlockItemRegistryObject == null ? combBlockItemRegistryObject : this.combBlockItemRegistryObject;
    }

    public ResourceLocation getEntityTypeRegistryID() {
        return entityTypeRegistryID;
    }

    public void setEntityTypeRegistryID(ResourceLocation entityTypeRegistryID) {
        this.entityTypeRegistryID = this.entityTypeRegistryID == null ? entityTypeRegistryID : this.entityTypeRegistryID;
    }

    public RegistryObject<Item> getSpawnEggItemRegistryObject() {
        return spawnEggItemRegistryObject;
    }

    public void setSpawnEggItemRegistryObject(RegistryObject<Item> spawnEggItemRegistryObject) {
        this.spawnEggItemRegistryObject = this.spawnEggItemRegistryObject == null ? spawnEggItemRegistryObject : this.spawnEggItemRegistryObject;
    }

    public void setName(String name) {
        this.name = this.name == null ? name : this.name;
    }

    public float getSizeModifier() {
        return sizeModifier != 0 ? sizeModifier : Config.BEE_SIZE_MODIFIER.get().floatValue();
    }

    public String[] getTraitNames() {
        return traits;
    }

    public boolean hasTraitNames() {
        return traits != null && traits.length > 0;
    }

    public String getName() {
        return this.name;
    }

    public void addData(String key, AbstractBeeData data) {
        if (data != null) this.ADDITIONAL_DATA.put(key, data);
    }

    public AbstractBeeData getData(String key) {
        return this.ADDITIONAL_DATA.get(key);
    }

    public boolean containsData(String key) {
        return this.ADDITIONAL_DATA.containsKey(key);
    }

    public BreedData getBreedData() {
        return this.BreedData != null ? this.BreedData : com.resourcefulbees.resourcefulbees.api.beedata.BreedData.createDefault();
    }

    public CentrifugeData getCentrifugeData() {
        return this.CentrifugeData != null ? this.CentrifugeData : com.resourcefulbees.resourcefulbees.api.beedata.CentrifugeData.createDefault();
    }

    public ColorData getColorData() {
        return this.ColorData != null ? this.ColorData : com.resourcefulbees.resourcefulbees.api.beedata.ColorData.createDefault();
    }

    public CombatData getCombatData() {
        return this.CombatData != null ? this.CombatData : com.resourcefulbees.resourcefulbees.api.beedata.CombatData.createDefault();
    }

    public MutationData getMutationData() {
        return this.MutationData != null ? this.MutationData : com.resourcefulbees.resourcefulbees.api.beedata.MutationData.createDefault();
    }

    public SpawnData getSpawnData() {
        return this.SpawnData != null ? this.SpawnData : com.resourcefulbees.resourcefulbees.api.beedata.SpawnData.createDefault();
    }

    public TraitData getTraitData() {
        return this.TraitData != null ? this.TraitData : com.resourcefulbees.resourcefulbees.api.beedata.TraitData.createDefault();
    }

    public void setTraitData(TraitData traitData) {
        this.TraitData = this.TraitData != null ? this.TraitData : traitData;
    }

    public ItemStack getCombStack() {
        return combSupplier != null ? combSupplier.get() : new ItemStack(getCombRegistryObject().get());
    }

    public ItemStack getCombBlockItemStack() {
        return combBlockItemSupplier != null ? combBlockItemSupplier.get() : new ItemStack(getCombBlockItemRegistryObject().get());
    }

    public void setCombSupplier(Supplier<ItemStack> combSupplier) {
        this.combSupplier = combSupplier;
    }

    public void setCombBlockItemSupplier(Supplier<ItemStack> combBlockItemSupplier) {
        this.combBlockItemSupplier = combBlockItemSupplier;
    }

    public BaseModelTypes getBaseModelType() {
        return baseModelType == null ? BaseModelTypes.DEFAULT : baseModelType;
    }

    public TranslatableComponent getTranslation() {
        return new TranslatableComponent("entity.resourcefulbees." + name + "_bee");
    }

    /**
     * If the ResourcefulBees mod should handle the registration
     */
    public boolean shouldResourcefulBeesDoForgeRegistration() {
        return shouldResourcefulBeesDoForgeRegistration;
    }

    public void setShouldResourcefulBeesDoForgeRegistration(boolean shouldResourcefulBeesDoForgeRegistration) {
        this.shouldResourcefulBeesDoForgeRegistration = shouldResourcefulBeesDoForgeRegistration;
    }



    public static class Builder {
        private final String flower;
        private String baseLayerTexture;
        private BaseModelTypes baseModelType = BaseModelTypes.DEFAULT;
        private int maxTimeInHive;
        private float sizeModifier;
        private String[] traits;
        private int[] apiaryOutputAmounts;
        private final String name;
        private final boolean hasHoneycomb;
        private final MutationData mutationData;
        private final ColorData colorData;
        private final CombatData combatData;
        private final CentrifugeData centrifugeData;
        private final BreedData breedData;
        private final SpawnData spawnData;
        private final TraitData traitData;
        private boolean isEasterEggBee = false;
        private ApiaryOutput[] apiaryOutputTypes = BeeInfoUtils.getDefaultApiaryTypes();

        public Builder(String name, String flower, boolean hasHoneycomb, MutationData mutationData, ColorData colorData, CombatData combatData, CentrifugeData centrifugeData, BreedData breedData, SpawnData spawnData, TraitData traitData) {
            this.name = name;
            this.flower = flower;
            this.hasHoneycomb = hasHoneycomb;
            this.mutationData = mutationData;
            this.colorData = colorData;
            this.combatData = combatData;
            this.centrifugeData = centrifugeData;
            this.breedData = breedData;
            this.spawnData = spawnData;
            this.traitData = traitData;
        }

        public Builder setApiaryOutputTypes(ApiaryOutput[] outputs) {
            this.apiaryOutputTypes = outputs;
            return this;
        }

        public Builder setEasterEggBee(boolean easterEggBee) {
            isEasterEggBee = easterEggBee;
            return this;
        }

        public Builder setBaseLayerTexture(String baseLayerTexture) {
            this.baseLayerTexture = baseLayerTexture;
            return this;
        }

        public Builder setBaseModelType(BaseModelTypes modelType) {
            this.baseModelType = modelType;
            return this;
        }

        public Builder setMaxTimeInHive(int maxTimeInHive) {
            this.maxTimeInHive = maxTimeInHive;
            return this;
        }

        public Builder setSizeModifier(float sizeModifier) {
            this.sizeModifier = sizeModifier;
            return this;
        }

        public Builder setTraits(String[] traits) {
            this.traits = traits;
            return this;
        }

        public Builder setApiaryOutputAmounts(int[] apiaryOutputAmounts) {
            this.apiaryOutputAmounts = apiaryOutputAmounts;
            return this;
        }

        public CustomBeeData createCustomBee() {
            return new CustomBeeData(flower, baseLayerTexture, baseModelType, maxTimeInHive, sizeModifier, traits, apiaryOutputAmounts, apiaryOutputTypes, name, hasHoneycomb, mutationData, colorData, combatData, centrifugeData, breedData, spawnData, traitData, isEasterEggBee);
        }
    }
}
