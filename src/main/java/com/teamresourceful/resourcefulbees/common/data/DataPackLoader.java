package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.common.utils.GenericMemoryPack;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.tags.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DataPackLoader implements RepositorySource {

    private static final String DATAPACK_NAME = "resourcefulbees:internals";
    public static final DataPackLoader INSTANCE = new DataPackLoader();

    @Override
    public void loadPacks(@NotNull Consumer<Pack> packList, @NotNull Pack.PackConstructor factory) {
        try (MemoryDataPack dataPack = new MemoryDataPack()) {
            DataGen.getTags().forEach((location, resourceLocations) -> {
                Tag.Builder builder = Tag.Builder.tag();
                resourceLocations.forEach(t -> builder.addElement(t, DATAPACK_NAME));
                dataPack.putJson(PackType.SERVER_DATA, location, builder.serializeToJson());
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

        protected MemoryDataPack() {
            super(PackType.SERVER_DATA, DATAPACK_NAME, "{\"pack_format\": "+ PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()) +", \"description\": \"Data for resourcefulbees tags.\"}");
        }
    }

}
