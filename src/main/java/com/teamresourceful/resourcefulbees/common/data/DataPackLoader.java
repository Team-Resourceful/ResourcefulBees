package com.teamresourceful.resourcefulbees.common.data;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefullib.common.utils.GenericMemoryPack;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DataPackLoader implements RepositorySource {

    private static final String DATAPACK_NAME = "resourcefulbees:internals";
    public static final DataPackLoader INSTANCE = new DataPackLoader();

    @Override
    public void loadPacks(@NotNull Consumer<Pack> packList, @NotNull Pack.PackConstructor factory) {
        try (MemoryDataPack dataPack = new MemoryDataPack()) {
            DataGen.getTags().forEach((location, resourceLocations) -> {
                TagBuilder builder = TagBuilder.create();
                resourceLocations.forEach(builder::addElement);
                TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(builder.build(), false))
                        .result()
                        .ifPresent(json -> dataPack.putJson(PackType.SERVER_DATA, location, json));
            });

            Pack pack = Pack.create(
                DATAPACK_NAME,
                true,
                () -> dataPack,
                factory,
                Pack.Position.BOTTOM,
                PackSource.BUILT_IN
            );
            packList.accept(pack);
        }
    }

    private static class MemoryDataPack extends GenericMemoryPack {

        private static final JsonObject META = Util.make(new JsonObject(), meta -> {
           meta.addProperty("pack_format", PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
           meta.addProperty("description", "Data for resourcefulbees tags.");
        });

        protected MemoryDataPack() {
            super(PackType.SERVER_DATA, DATAPACK_NAME, META);
        }
    }

}
