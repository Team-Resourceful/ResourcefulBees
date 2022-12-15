package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.honey.base.RegisterHoneyDataEvent;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.ModValidation;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class HoneyDataRegistry {

    public static final HoneyDataRegistry INSTANCE = new HoneyDataRegistry();
    private static final HoneyDataSerializer<DummyHoneyData> DUMMY_SERIALIZER = HoneyDataSerializer.of(new ModResourceLocation("noop"), 0, id -> Codec.unit(DummyHoneyData::new), new DummyHoneyData());

    private final Map<ResourceLocation, HoneyDataSerializer<?>> serializers = new HashMap<>();
    private final Set<ResourceLocation> required = new HashSet<>();
    private final Object2IntMap<ResourceLocation> types = new Object2IntArrayMap<>();
    private boolean locked = false;

    private HoneyDataRegistry() {}

    public static void init() {
        ResourcefulBeesAPI.getEvents().onRegisterHoneyData(new RegisterHoneyDataEvent(INSTANCE::register));
        INSTANCE.locked = true;
    }

    @SuppressWarnings("unchecked")
    public static Function<ResourceLocation, Codec<HoneyData<?>>> codec(String id) {
        return type -> (Codec<HoneyData<?>>) decode(type)
                .map(serializer -> serializer.codec(id))
                .result().orElse(null);
    }

    public <T extends HoneyData<T>> void register(HoneyDataSerializer<T> serializer) {
        if (this.locked) {
            throw new IllegalStateException("Cannot register extra bee data after the registry has been locked.");
        }
        if (this.serializers.containsKey(serializer.id())) {
            throw new IllegalStateException("Attempted to register extra bee data with duplicate id: " + serializer.id());
        }
        this.serializers.put(serializer.id(), serializer);
        int typeVersion = this.types.getInt(serializer.type());
        if (typeVersion < serializer.version()) {
            this.types.put(serializer.type(), serializer.version());
        }
        if (serializer.required()) {
            this.required.add(serializer.type());
        }
    }

    public void check(Collection<HoneyData<?>> check) {
        Set<ResourceLocation> types = check.stream().map(HoneyData::serializer).map(HoneyDataSerializer::type).collect(Collectors.toSet());
        for (ResourceLocation resourceLocation : required) {
            if (!types.contains(resourceLocation)) {
                throw new IllegalStateException("Missing required bee data type: " + resourceLocation);
            }
        }
    }

    @Nullable
    public HoneyDataSerializer<?> get(ResourceLocation id) {
        return this.serializers.get(id);
    }

    private static DataResult<HoneyDataSerializer<?>> decode(ResourceLocation id) {
        HoneyDataSerializer<?> serializer = INSTANCE.get(id);
        if (serializer == null) {
            if (ModValidation.IS_RUNNING_IN_IDE || GeneralConfig.showDebugInfo) {
                ModConstants.LOGGER.error("No serializer found for {}", id);
            }
            return DataResult.success(DUMMY_SERIALIZER);
        }
        if (serializer.version() < INSTANCE.types.getInt(serializer.type())) {
            ModConstants.LOGGER.warn("Serializer {} is outdated the current version is {}.", id, INSTANCE.types.getInt(serializer.type()));
        }
        return DataResult.success(serializer);
    }

    private static final class DummyHoneyData implements HoneyData<DummyHoneyData> {
        @Override
        public HoneyDataSerializer<DummyHoneyData> serializer() {
            return DUMMY_SERIALIZER;
        }
    }
}
