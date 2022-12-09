package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.conditions.LoadCondition;
import com.teamresourceful.resourcefulbees.api.data.conditions.LoadConditionSerializer;
import com.teamresourceful.resourcefulbees.api.data.conditions.RegisterConditionEvent;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.tools.ModValidation;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class LoadConditionRegistry {

    public static final LoadConditionRegistry INSTANCE = new LoadConditionRegistry();
    private static final LoadConditionSerializer<FakeCondition> DUMMY_SERIALIZER = LoadConditionSerializer.of(new ModResourceLocation("noop"), Codec.unit(FakeCondition::new));

    private static final Codec<LoadConditionSerializer<?>> SERIALIZER_CODEC = ResourceLocation.CODEC.comapFlatMap(LoadConditionRegistry::decode, LoadConditionSerializer::id);
    public static final Codec<LoadCondition<?>> CODEC = SERIALIZER_CODEC.dispatch(LoadCondition::serializer, LoadConditionSerializer::codec);

    private final Map<ResourceLocation, LoadConditionSerializer<?>> serializers = new HashMap<>();
    private boolean locked = false;

    private LoadConditionRegistry() {}

    public static void init() {
        ResourcefulBeesAPI.getEvents().onRegisterLoadCondition(new RegisterConditionEvent(INSTANCE::register));
        INSTANCE.locked = true;
    }

    public static boolean canLoad(JsonObject object) {
        if (object != null && object.has("condition") && object.get("condition").isJsonObject()) {
            JsonObject condition = object.getAsJsonObject("condition");
            object.remove("condition");
            return LoadConditionRegistry.CODEC.parse(JsonOps.INSTANCE, condition)
                .result()
                .map(LoadCondition::canLoad)
                .orElse(true);
        }
        return true;
    }

    public <T extends LoadCondition<T>> void register(LoadConditionSerializer<T> serializer) {
        if (this.locked) {
            throw new IllegalStateException("Cannot register load conditions after the registry has been locked.");
        }
        if (this.serializers.containsKey(serializer.id())) {
            throw new IllegalStateException("Attempted to register load condition with duplicate id: " + serializer.id());
        }
        this.serializers.put(serializer.id(), serializer);
    }

    @Nullable
    public LoadConditionSerializer<?> get(ResourceLocation id) {
        return this.serializers.get(id);
    }

    private static DataResult<LoadConditionSerializer<?>> decode(ResourceLocation id) {
        LoadConditionSerializer<?> serializer = INSTANCE.get(id);
        if (serializer == null) {
            if (ModValidation.IS_RUNNING_IN_IDE || GeneralConfig.showDebugInfo) {
                Constants.LOGGER.error("No serializer found for " + id);
            }
            return DataResult.success(DUMMY_SERIALIZER);
        }
        return DataResult.success(serializer);
    }

    private static final class FakeCondition implements LoadCondition<FakeCondition> {
        @Override
        public boolean canLoad() {
            return true;
        }

        @Override
        public LoadConditionSerializer<FakeCondition> serializer() {
            return DUMMY_SERIALIZER;
        }
    }
}
