package com.resourcefulbees.resourcefulbees.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.*;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DataPackLoader implements IPackFinder {

    private static final String DATAPACK_NAME = "resourcefulbees:internals";
    public static final DataPackLoader INSTANCE = new DataPackLoader();
    private static final Gson GSON = new Gson();

    @Override
    public void loadPacks(@NotNull Consumer<ResourcePackInfo> packList, @NotNull ResourcePackInfo.IFactory factory) {
        try (MemoryDataPack dataPack = new MemoryDataPack()) {
            DataGen.getTags().forEach((location, resourceLocations) -> {
                ITag.Builder builder = ITag.Builder.tag();
                resourceLocations.forEach(t -> builder.addElement(t, DATAPACK_NAME));
                dataPack.putJson(ResourcePackType.SERVER_DATA, location, builder.serializeToJson());
            });

            ResourcePackInfo pack = ResourcePackInfo.create(
                    DATAPACK_NAME,
                    true,
                    () -> dataPack,
                    factory,
                    ResourcePackInfo.Priority.BOTTOM,
                    IPackNameDecorator.BUILT_IN
            );
            packList.accept(pack);
        }
    }


    private static class MemoryDataPack implements IResourcePack {

        private static final JsonObject meta = new JsonObject();
        static {
            meta.add("pack_format", new JsonPrimitive(4));
            meta.add("description", new JsonPrimitive("Data for resourcefulbees tags."));
        }

        private final HashMap<ResourceLocation, Supplier<? extends InputStream>> assets = new HashMap<>();
        private final HashMap<ResourceLocation, Supplier<? extends InputStream>> data = new HashMap<>();

        @Nullable
        private HashMap<ResourceLocation, Supplier<? extends InputStream>> getResourcePackTypeMap(ResourcePackType type){
            if (type.equals(ResourcePackType.CLIENT_RESOURCES)) return assets;
            else if (type.equals(ResourcePackType.SERVER_DATA)) return data;
            else return null;
        }

        public void putJson(ResourcePackType type, ResourceLocation location, JsonElement json) {
            HashMap<ResourceLocation, Supplier<? extends InputStream>> map = getResourcePackTypeMap(type);
            if (map != null){
                map.put(location, () -> new ByteArrayInputStream(GSON.toJson(json).getBytes(StandardCharsets.UTF_8)));
            }
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
            Map<ResourceLocation, Supplier<? extends InputStream>> map = getResourcePackTypeMap(type);
            if(map != null && map.containsKey(location)) {
                return map.get(location).get();
            }
            throw new FileNotFoundException(location.toString());
        }

        @Override
        public @NotNull Collection<ResourceLocation> getResources(@NotNull ResourcePackType type, @NotNull String namespace, @NotNull String path, int maxFolderWalk, @NotNull Predicate<String> predicate) {
            Map<ResourceLocation, Supplier<? extends InputStream>> map = getResourcePackTypeMap(type);
            if (map == null) return Collections.emptyList();

            return map.keySet().stream()
                    .filter(location->location.getNamespace().equals(namespace))
                    .filter(location->location.getPath().split("/").length < maxFolderWalk)
                    .filter(location->location.getPath().startsWith(path))
                    .filter(location-> predicate.test(location.getPath().substring(Math.max(location.getPath().lastIndexOf('/'), 0)))
            ).collect(Collectors.toList());
        }

        @Override
        public boolean hasResource(@NotNull ResourcePackType type, @NotNull ResourceLocation location) {
            Map<ResourceLocation, Supplier<? extends InputStream>> map = getResourcePackTypeMap(type);
            return map != null && map.containsKey(location);
        }

        @Override
        public @NotNull Set<String> getNamespaces(@NotNull ResourcePackType type) {
            Map<ResourceLocation, Supplier<? extends InputStream>> map = getResourcePackTypeMap(type);
            if (map == null) return Collections.emptySet();
            return map.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
        }

        @Nullable
        @Override
        public <T> T getMetadataSection(@NotNull IMetadataSectionSerializer<T> serializer) {
            return serializer.fromJson(meta);
        }

        @Override
        public @NotNull String getName() {
            return DATAPACK_NAME;
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
}
