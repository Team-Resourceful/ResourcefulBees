package com.teamresourceful.resourcefulbees.api.data.honey;

import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface CustomHoneyData {

    String name();

    Map<ResourceLocation, HoneyData<?>> data();

    MutableComponent displayName();

    HoneyBlockData getBlockData();

    HoneyFluidData getFluidData();

    HoneyBottleData getBottleData();

    default HoneyData<?> getHoneyData(ResourceLocation id) {
        return data().get(id);
    }

    /**
     * @return returns Optional.empty() if the data is not present
     * @throws IllegalArgumentException if serializer on data does not match data passed in.
     */
    default <T extends HoneyData<T>> Optional<T> getOptionalData(HoneyDataSerializer<T> serializer) {
        HoneyData<?> data = getHoneyData(serializer.id());
        if (data != null) {
            if (data.serializer().equals(serializer)) {
                return Optional.of(serializer.cast(data));
            }
            throw new IllegalArgumentException("HoneyDataSerializer type does not match ModData");
        }
        return Optional.empty();
    }

    /**
     * @return returns null or default if data not found.
     * @throws IllegalArgumentException if serializer on data does not match data passed in.
     */
    @Nullable
    default <T extends HoneyData<T>> T getData(HoneyDataSerializer<T> serializer) {
        return getOptionalData(serializer).orElse(serializer.defaultValue());
    }
}
