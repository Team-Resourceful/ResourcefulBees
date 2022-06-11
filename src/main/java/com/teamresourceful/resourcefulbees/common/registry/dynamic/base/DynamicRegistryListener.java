package com.teamresourceful.resourcefulbees.common.registry.dynamic.base;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DynamicRegistryListener<T> extends SimpleJsonResourceReloadListener {

    private final HashMap<String, T> map;
    private final Codec<T> codec;

    public DynamicRegistryListener(HashMap<String, T> map, Codec<T> codec, String path) {
        super(ModConstants.GSON, path);
        this.map = map;
        this.codec = codec;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> data, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        map.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : data.entrySet()) {
            ResourceLocation key = entry.getKey();
            if (key.getPath().contains("/")) continue;

            codec.parse(JsonOps.INSTANCE, entry.getValue())
                    .result()
                    .ifPresent(output -> map.put(key.getPath(), output));
        }
    }
}