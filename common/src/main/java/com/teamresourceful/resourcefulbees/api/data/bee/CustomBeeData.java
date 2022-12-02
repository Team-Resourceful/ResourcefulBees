package com.teamresourceful.resourcefulbees.api.data.bee;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface CustomBeeData {

    String name();

    ResourceLocation id();

    Map<ResourceLocation, BeeData<?>> data();

    MutableComponent displayName();

    EntityType<?> entityType();

    BeeBreedData getBreedData();
    BeeCombatData getCombatData();
    BeeRenderData getRenderData();
    BeeMutationData getMutationData();
    BeeCoreData getCoreData();
    BeeTraitData getTraitData();

    BeekeeperTradeData getTradeData();

    default BeeData<?> getBeeData(ResourceLocation id) {
        return data().get(id);
    }

    /**
     * @return returns Optional.empty() if the data is not present
     * @throws IllegalArgumentException if serializer on data does not match data passed in.
     */
    default <T extends BeeData<T>> Optional<T> getOptionalData(BeeDataSerializer<T> serializer) {
        BeeData<?> data = getBeeData(serializer.id());
        if (data != null) {
            if (data.serializer().equals(serializer)) {
                return Optional.of(serializer.cast(data));
            }
            throw new IllegalArgumentException("BeeDataSerializer type does not match ModData");
        }
        return Optional.empty();
    }

    /**
     * @return returns null or default if data not found.
     * @throws IllegalArgumentException if serializer on data does not match data passed in.
     */
    @Nullable
    default <T extends BeeData<T>> T getData(BeeDataSerializer<T> serializer) {
        return getOptionalData(serializer).orElse(serializer.defaultValue());
    }


}
