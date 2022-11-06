package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.api.moddata.ModData;
import com.teamresourceful.resourcefulbees.api.moddata.ModDataSerializer;
import com.teamresourceful.resourcefulbees.api.moddata.RegisterModDataEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class CustomDataRegistry {

    public static final CustomDataRegistry INSTANCE = new CustomDataRegistry();
    private static final ModDataSerializer<DummyData> DUMMY_SERIALIZER = ModDataSerializer.of(new ResourceLocation("resourcefulbees", "noop"), Codec.unit(DummyData::new));
    private static final Codec<ModDataSerializer<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(CustomDataRegistry::decode, ModDataSerializer::id);
    public static final Codec<ModData<?>> CODEC = TYPE_CODEC.dispatch(ModData::serializer, ModDataSerializer::codec);

    private final Map<ResourceLocation, ModDataSerializer<?>> serializers = new HashMap<>();
    private boolean locked = false;

    private CustomDataRegistry() {}

    public void init() {
        MinecraftForge.EVENT_BUS.post(new RegisterModDataEvent(this));
        this.locked = true;
    }

    public void register(ModDataSerializer<?> serializer) {
        if (this.locked) {
            throw new IllegalStateException("Cannot register mod data after the registry has been locked.");
        }
        if (this.serializers.containsKey(serializer.id())) {
            throw new IllegalStateException("Attempted to register mod data with duplicate id: " + serializer.id());
        }
        this.serializers.put(serializer.id(), serializer);
    }

    @Nullable
    public ModDataSerializer<?> get(ResourceLocation id) {
        return this.serializers.get(id);
    }

    private static DataResult<ModDataSerializer<?>> decode(ResourceLocation id) {
        ModDataSerializer<?> serializer = INSTANCE.get(id);
        if (serializer == null) {
            CodecUtils.debugLog().accept("No serializer found for " + id);
            return DataResult.success(DUMMY_SERIALIZER);
        }
        return DataResult.success(serializer);
    }

    private static final class DummyData implements ModData<DummyData> {
        @Override
        public ModDataSerializer<DummyData> serializer() {
            return DUMMY_SERIALIZER;
        }
    }
}
