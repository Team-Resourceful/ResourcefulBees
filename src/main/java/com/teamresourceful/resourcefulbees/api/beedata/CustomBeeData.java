package com.teamresourceful.resourcefulbees.api.beedata;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.api.beedata.spawning.SpawnData;
import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.function.Consumer;

@Unmodifiable
@SuppressWarnings("unused")
public class CustomBeeData {
    /**
     * A default implementation of {@link CustomBeeData} that can be
     * used to prevent {@link NullPointerException}'s
     */
    public static final CustomBeeData DEFAULT = new CustomBeeData(CoreData.DEFAULT, "", RenderData.DEFAULT, BreedData.DEFAULT, CentrifugeData.DEFAULT, CombatData.DEFAULT, MutationData.DEFAULT, SpawnData.DEFAULT, TraitData.DEFAULT);

    /**
     * Returns a {@link Codec<CustomBeeData>} that can be parsed to create a
     * {@link CustomBeeData} object. The name value passed in is a fallback value
     * usually obtained from the bee json file name.
     * <i>Note: Name is synonymous with "bee type"</i>
     *
     * @param name The name (or "bee type") for the bee.
     * @return Returns a {@link Codec<CustomBeeData>} that can be parsed to
     * create a {@link CustomBeeData} object.
     */
    public static Codec<CustomBeeData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                CoreData.codec(name).fieldOf("CoreData").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error("CoreData is REQUIRED!"), null).forGetter(CustomBeeData::getCoreData),
                Codec.STRING.fieldOf("honeycombVariation").orElse("").forGetter(s -> s.honeycombIdentifier),
                RenderData.CODEC.fieldOf("RenderData").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error("RenderData is REQUIRED!"), null).forGetter(CustomBeeData::getRenderData),
                BreedData.codec(name).fieldOf("BreedData").orElse(BreedData.DEFAULT).forGetter(CustomBeeData::getBreedData),
                CentrifugeData.CODEC.fieldOf("CentrifugeData").orElse(CentrifugeData.DEFAULT).forGetter(CustomBeeData::getCentrifugeData),
                CombatData.CODEC.fieldOf("CombatData").orElse(CombatData.DEFAULT).forGetter(CustomBeeData::getCombatData),
                MutationData.CODEC.fieldOf("MutationData").orElse(MutationData.DEFAULT).forGetter(CustomBeeData::getMutationData),
                SpawnData.CODEC.fieldOf("SpawnData").orElse(SpawnData.DEFAULT).forGetter(CustomBeeData::getSpawnData),
                TraitData.codec(name).fieldOf("TraitData").orElse(TraitData.DEFAULT).forGetter(CustomBeeData::getTraitData)
        ).apply(instance, CustomBeeData::new));
    }

    protected CoreData coreData;
    protected String honeycombIdentifier;
    protected RenderData renderData;
    protected BreedData breedData;
    protected CentrifugeData centrifugeData;
    protected CombatData combatData;
    protected MutationData mutationData;
    protected SpawnData spawnData;
    protected TraitData traitData;
    protected JsonObject rawData;
    protected ResourceLocation registryID;
    protected EntityType<?> entityType;
    protected TranslationTextComponent displayName;

    private CustomBeeData(CoreData coreData, String honeycombIdentifier, RenderData renderData, BreedData breedData, CentrifugeData centrifugeData, CombatData combatData, MutationData mutationData, SpawnData spawnData, TraitData traitData) {
        this.coreData = coreData;
        this.honeycombIdentifier = honeycombIdentifier;
        this.renderData = renderData;
        this.breedData = breedData;
        this.centrifugeData = centrifugeData;
        this.combatData = combatData;
        this.mutationData = mutationData;
        this.spawnData = spawnData;
        this.traitData = traitData;
        this.rawData = BeeRegistry.getRegistry().getRawBeeData(coreData.getName());
        this.registryID = new ResourceLocation(ResourcefulBees.MOD_ID + ":" + coreData.getName() + "_bee");
        this.displayName = new TranslationTextComponent("entity.resourcefulbees." + coreData.getName() + "_bee");
    }

    private CustomBeeData(Mutable mutable) {
        this.coreData = mutable.coreData.toImmutable();
        this.honeycombIdentifier = mutable.honeycombIdentifier;
        this.renderData = mutable.renderData;
        this.breedData = mutable.breedData.toImmutable();
        this.centrifugeData = mutable.centrifugeData.toImmutable();
        this.combatData = mutable.combatData.toImmutable();
        this.mutationData = mutable.mutationData;
        this.spawnData = mutable.spawnData;
        this.traitData = mutable.traitData;
        this.rawData = mutable.rawData;
        this.registryID = mutable.registryID;
        this.entityType = mutable.entityType;
        this.displayName = mutable.displayName;
    }

    /**
     * Returns a {@link CoreData} object containing basic information regarding the bee
     * such as its bee type, flowers used for pollination, and its max time in hive.
     *
     * This object is <b>required</b> in the bee json.
     *
     * @return Returns the {@link CoreData} Object for the bee
     */
    public CoreData getCoreData() {
        return coreData;
    }

    /**
     * Returns an {@link Optional}&lt;{@link OutputVariation}&gt; object containing information regarding the
     * honeycomb a bee produces if it is specified to produce one.
     *
     * Omitting this object from the bee json results in a default object where the bee
     * <b>does not</b> produce a honeycomb.
     *
     * @return Returns an {@link Optional}&lt;{@link OutputVariation}&gt; with the contained data being immutable.
     */
    public Optional<OutputVariation> getHoneycombData() {
        return Optional.ofNullable(HoneycombRegistry.getOutputVariation(honeycombIdentifier));
    }

    /**
     * Returns a {@link RenderData} object containing all necessary information
     * for rendering the bee in the world.
     *
     * This object is <b>required</b> in the bee json.
     *
     * @return Returns an immutable {@link RenderData} object.
     */
    public RenderData getRenderData() {
        return renderData;
    }

    /**
     * Returns a {@link BreedData} object containing the breeding rules for the bee.
     *
     * Omitting this object from the bee json results in a default object where the bee
     * <b>cannot</b> be created through breeding.
     *
     * @return Returns an immutable {@link BreedData} object.
     */
    public BreedData getBreedData() {
        return breedData;
    }

    /**
     * Returns a {@link CentrifugeData} object containing information regarding the
     * centrifuge recipe for the bees honeycomb.
     *
     * Omitting this object from the bee json results in a default object where the bee
     * <b>does not</b> have a centrifuge recipe for its honeycomb.
     *
     * @return Returns an immutable {@link CentrifugeData} object.
     */
    public CentrifugeData getCentrifugeData() {
        return centrifugeData;
    }

    /**
     * Returns a {@link CombatData} object containing information regarding the
     * honeycomb a bee produces if it is specified to produce one.
     *
     * Omitting this object from the bee json results in a default object where the bee
     * <b>does not</b> produce a honeycomb.
     *
     * @return Returns an immutable {@link CombatData} object.
     */
    public CombatData getCombatData() {
        return combatData;
    }

    /**
     * Returns a {@link MutationData} object containing information regarding the
     * various block and entity mutations a bee may perform.
     *
     * Omitting this object from the bee json results in a default object where the bee
     * <b>does not</b> mutate and blocks or entities.
     *
     * @return Returns an immutable {@link MutationData} object.
     */
    public MutationData getMutationData() {
        return mutationData;
    }

    /**
     * Returns a {@link SpawnData} object containing the spawning rules for a bee.
     *
     * Omitting this object from the bee json results in a default object where the bee
     * <b>does not</b> spawn in the world.
     *
     * @return Returns an immutable {@link SpawnData} object.
     */
    public SpawnData getSpawnData() {
        return spawnData;
    }

    /**
     * Returns a {@link TraitData} object containing information regarding the
     * various traits a bee may have.
     *
     * Omitting this object from the bee json results in a default object where the bee
     * <b>does not</b> have any traits.
     *
     * @return Returns an immutable {@link TraitData} object.
     */
    public TraitData getTraitData() {
        return traitData;
    }

    public @NotNull EntityType<?> getEntityType() {
        if (entityType == null) {
            this.entityType = BeeInfoUtils.getEntityType(registryID);
        }
        return entityType == null ? EntityType.BEE : entityType;
    }

    public ResourceLocation getRegistryID() {
        return registryID;
    }

    /**
     * Returns a {@link JsonObject} containing the raw unadulterated data from the bees json file.
     * This object may not be reflective of any file changes made during runtime.
     *
     * @return Returns a {@link JsonObject} containing the raw unadulterated data from the bees json file.
     */
    @Nullable
    public JsonObject getRawData() {
        return rawData;
    }

    public TranslationTextComponent getDisplayName() {
        return displayName;
    }

    public CustomBeeData toImmutable() {
        return this;
    }

    //TODO: javadoc this sub class
    public static class Mutable extends CustomBeeData {
        public Mutable(CoreData coreData, String honeycombIdentifier, RenderData renderData, BreedData breedData, CentrifugeData centrifugeData, CombatData combatData, MutationData mutationData, SpawnData spawnData, TraitData traitData) {
            super(coreData, honeycombIdentifier, renderData, breedData, centrifugeData, combatData, mutationData, spawnData, traitData);
        }

        public Mutable() {
            super(CoreData.DEFAULT, "", RenderData.DEFAULT, BreedData.DEFAULT, CentrifugeData.DEFAULT, CombatData.DEFAULT, MutationData.DEFAULT, SpawnData.DEFAULT, TraitData.DEFAULT);
        }

        public Mutable setCoreData(CoreData coreData) {
            this.coreData = coreData;
            return this;
        }

        public Mutable setHoneycombData(String honeycombIdentifier) {
            this.honeycombIdentifier = honeycombIdentifier;
            return this;
        }

        public Mutable setRenderData(RenderData renderData) {
            this.renderData = renderData;
            return this;
        }

        public Mutable setBreedData(BreedData breedData) {
            this.breedData = breedData;
            return this;
        }

        public Mutable setCentrifugeData(CentrifugeData centrifugeData) {
            this.centrifugeData = centrifugeData;
            return this;
        }

        public Mutable setCombatData(CombatData combatData) {
            this.combatData = combatData;
            return this;
        }

        public Mutable setMutationData(MutationData mutationData) {
            this.mutationData = mutationData;
            return this;
        }

        public Mutable setSpawnData(SpawnData spawnData) {
            this.spawnData = spawnData;
            return this;
        }

        public Mutable setTraitData(TraitData traitData) {
            this.traitData = traitData;
            return this;
        }

        public Mutable setRegistryID(ResourceLocation registryID) {
            this.registryID = registryID;
            return this;
        }

        public Mutable setDisplayName(TranslationTextComponent displayName) {
            this.displayName = displayName;
            return this;
        }

        public Mutable setRawData(JsonObject rawData) {
            this.rawData = rawData;
            return this;
        }

        public Mutable setEntityType(EntityType<?> entityType) {
            this.entityType = entityType;
            return this;
        }

        @Override
        public CustomBeeData toImmutable() {
            return new CustomBeeData(this);
        }
    }
}
