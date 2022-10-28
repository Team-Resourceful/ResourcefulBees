package com.teamresourceful.resourcefulbees.api.beedata;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @param coreData Returns a {@link CoreData} object containing basic information regarding the bee such as its bee type, flowers used for pollination, and its max time in hive.
 * @param renderData Returns a {@link RenderData} object containing all necessary information for rendering the bee in the world.
 * @param breedData Returns a {@link BreedData} object containing the breeding rules for the bee.
 * @param combatData Returns a {@link CombatData} object containing information about the bees combat info such as health and attack damage.
 * @param mutationData Returns a {@link MutationData} object containing information regarding the various block and entity mutations a bee may perform.
 * @param traitData Returns a {@link TraitData} object containing information regarding the various traits a bee may have.
 * @param registryID
 * @param displayName
 * @param entityType
 */
public record CustomBeeData(CoreData coreData, RenderData renderData, BreedData breedData, CombatData combatData, MutationData mutationData, TraitData traitData, ResourceLocation registryID, MutableComponent displayName, Supplier<EntityType<?>> entityType) {
    /**
     * A default implementation of {@link CustomBeeData} that can be
     * used to prevent {@link NullPointerException}'s
     */
    public static final CustomBeeData DEFAULT = CustomBeeData.of(CoreData.DEFAULT, RenderData.DEFAULT, BreedData.DEFAULT, CombatData.DEFAULT, MutationData.DEFAULT, TraitData.DEFAULT);

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
                CoreData.codec(name).fieldOf("CoreData").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error("CoreData is REQUIRED!"), null).forGetter(CustomBeeData::coreData),
                RenderData.CODEC.fieldOf("RenderData").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error("RenderData is REQUIRED!"), null).forGetter(CustomBeeData::renderData),
                BreedData.codec(name).fieldOf("BreedData").orElse(BreedData.DEFAULT).forGetter(CustomBeeData::breedData),
                CombatData.CODEC.fieldOf("CombatData").orElse(CombatData.DEFAULT).forGetter(CustomBeeData::combatData),
                MutationData.CODEC.fieldOf("MutationData").orElse(MutationData.DEFAULT).forGetter(CustomBeeData::mutationData),
                TraitData.codec(name).fieldOf("TraitData").orElse(TraitData.DEFAULT).forGetter(CustomBeeData::traitData)
        ).apply(instance, CustomBeeData::of));
    }

    private static CustomBeeData of(CoreData coreData, RenderData renderData, BreedData breedData, CombatData combatData, MutationData mutationData, TraitData traitData) {
        ResourceLocation registryId = new ResourceLocation(ResourcefulBees.MOD_ID, coreData.name() + "_bee");
        MutableComponent displayName = Component.translatable("bee_type.resourcefulbees." + coreData.name());
        Supplier<EntityType<?>> beeEntity = Suppliers.memoize(() -> getEntity(registryId));
        return new CustomBeeData(coreData, renderData, breedData, combatData, mutationData, traitData, registryId, displayName, beeEntity);
    }

    public @NotNull EntityType<?> getEntityType() {
        return entityType().get();
    }

    private static EntityType<?> getEntity(ResourceLocation id) {
        EntityType<?> entity = BeeInfoUtils.getEntityType(id);
        return entity == null ? EntityType.BEE : entity;
    }
}
