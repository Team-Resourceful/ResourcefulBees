package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.bee.base.RegisterBeeDataEvent;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.data.DataSetup;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BeeDataRegistry {

    public static final BeeDataRegistry INSTANCE = new BeeDataRegistry();
    private static final BeeDataSerializer<DummyBeeData> DUMMY_SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("noop"), 0, id -> Codec.unit(DummyBeeData::new), new DummyBeeData());

    private final Map<ResourceLocation, BeeDataSerializer<?>> serializers = new HashMap<>();
    private final Object2IntMap<ResourceLocation> types = new Object2IntArrayMap<>();
    private boolean locked = false;

    private BeeDataRegistry() {}

    public static void init() {
        ResourcefulBeesAPI.getEvents().registerBeeData(DataSetup::setupRegister);
        ResourcefulBeesAPI.getEvents().onRegisterBeeData(new RegisterBeeDataEvent(INSTANCE::register));
        INSTANCE.locked = true;
    }

    @SuppressWarnings("unchecked")
    public static Function<ResourceLocation, Codec<BeeData<?>>> codec(String id) {
        return type -> (Codec<BeeData<?>>) decode(type)
                .map(serializer -> serializer.codec(id))
                .result().orElse(null);
    }

    public <T extends BeeData<T>> void register(BeeDataSerializer<T> serializer) {
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
    }

    @Nullable
    public BeeDataSerializer<?> get(ResourceLocation id) {
        return this.serializers.get(id);
    }

    private static DataResult<BeeDataSerializer<?>> decode(ResourceLocation id) {
        BeeDataSerializer<?> serializer = INSTANCE.get(id);
        if (serializer == null) {
            if (!FMLLoader.isProduction() || SharedConstants.IS_RUNNING_IN_IDE || GeneralConfig.showDebugInfo) {
                ResourcefulBees.LOGGER.error("No serializer found for " + id);
            }
            return DataResult.success(DUMMY_SERIALIZER);
        }
        if (serializer.version() < INSTANCE.types.getInt(serializer.type())) {
            ResourcefulBees.LOGGER.warn("Serializer {} is outdated the current version is {}.", id, INSTANCE.types.getInt(serializer.type()));
        }
        return DataResult.success(serializer);
    }

    private static final class DummyBeeData implements BeeData<DummyBeeData> {
        @Override
        public BeeDataSerializer<DummyBeeData> serializer() {
            return DUMMY_SERIALIZER;
        }
    }
}
