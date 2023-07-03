package com.teamresourceful.resourcefulbees.common.data;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import com.teamresourceful.resourcefullib.common.utils.GenericMemoryPack;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;

import java.util.function.Consumer;

public final class DataPackLoader implements RepositorySource {

    private static final String DATAPACK_NAME = "resourcefulbees:internals";
    public static final DataPackLoader INSTANCE = new DataPackLoader();
    private static final JsonObject META = Util.make(new JsonObject(), meta -> {
        meta.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
        meta.addProperty("description", "Data for resourcefulbees tags.");
    });

    private DataPackLoader() {}

    @Override
    public void loadPacks(Consumer<Pack> onLoad) {
        try (GenericMemoryPack dataPack = ModUtils.createHiddenDataPack(DATAPACK_NAME, META)) {
            DataGen.getTags().forEach((location, resourceLocations) -> {
                TagBuilder builder = TagBuilder.create();
                resourceLocations.forEach(builder::addElement);
                TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(builder.build(), false))
                    .result()
                    .ifPresent(json -> dataPack.putJson(PackType.SERVER_DATA, location, json));
            });

            onLoad.accept(Pack.readMetaAndCreate(
                DATAPACK_NAME,
                Component.empty(),
                true,
                (id) -> dataPack,
                PackType.SERVER_DATA,
                Pack.Position.BOTTOM,
                PackSource.BUILT_IN
            ));
        }
    }
}
