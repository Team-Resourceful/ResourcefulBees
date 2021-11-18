package com.teamresourceful.resourcefulbees.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class GenericMemoryPack implements IResourcePack {

    private final HashMap<ResourceLocation, Supplier<? extends InputStream>> data = new HashMap<>();

    private final JsonObject metaData;
    private final ResourcePackType allowedType;
    private final String id;

    protected GenericMemoryPack(ResourcePackType type, String id, String meta) {
        this.metaData = ModConstants.GSON.fromJson(meta, JsonObject.class);
        this.allowedType = type;
        this.id = id;
    }

    private boolean isTypeAllowed(ResourcePackType type) {
        return allowedType.equals(type);
    }

    public void putData(ResourcePackType type, ResourceLocation location, Supplier<? extends InputStream> supplier){
        if (!isTypeAllowed(type)) return;
        data.put(location, supplier);
    }

    public void putJson(ResourcePackType type, ResourceLocation location, JsonElement json) {
        putData(type, location, () -> new ByteArrayInputStream(ModConstants.GSON.toJson(json).getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public @NotNull InputStream getRootResource(@NotNull String file) throws IOException {
        if(file.contains("/") || file.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        throw new FileNotFoundException(file);
    }

    @Override
    public @NotNull InputStream getResource(@NotNull ResourcePackType type, @NotNull ResourceLocation location) throws IOException {
        if(this.hasResource(type, location)) return data.get(location).get();
        throw new FileNotFoundException(location.toString());
    }

    @Override
    public @NotNull Collection<ResourceLocation> getResources(@NotNull ResourcePackType type, @NotNull String namespace, @NotNull String path, int maxFolderWalk, @NotNull Predicate<String> predicate) {
        if (!isTypeAllowed(type)) return Collections.emptyList();
        return data.keySet().stream()
                .filter(location->location.getNamespace().equals(namespace))
                .filter(location->location.getPath().split("/").length < maxFolderWalk)
                .filter(location->location.getPath().startsWith(path))
                .filter(location-> predicate.test(location.getPath().substring(Math.max(location.getPath().lastIndexOf('/'), 0)))
                ).collect(Collectors.toList());
    }

    @Override
    public boolean hasResource(@NotNull ResourcePackType type, @NotNull ResourceLocation location) {
        return isTypeAllowed(type) && data.containsKey(location);
    }

    @Override
    public @NotNull Set<String> getNamespaces(@NotNull ResourcePackType type) {
        if (!isTypeAllowed(type)) return Collections.emptySet();
        return data.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(@NotNull IMetadataSectionSerializer<T> serializer) {
        if (!serializer.getMetadataSectionName().equals("pack")) return null;
        return serializer.fromJson(metaData);
    }

    @Override
    public @NotNull String getName() {
        return id;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void close() {
        //Does nothing
    }
}
