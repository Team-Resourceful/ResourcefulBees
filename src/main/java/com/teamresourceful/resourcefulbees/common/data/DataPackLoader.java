package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.common.utils.GenericMemoryPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.ITag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DataPackLoader implements IPackFinder {

    private static final String DATAPACK_NAME = "resourcefulbees:internals";
    public static final DataPackLoader INSTANCE = new DataPackLoader();

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

    private static class MemoryDataPack extends GenericMemoryPack {

        protected MemoryDataPack() {
            super(ResourcePackType.SERVER_DATA, DATAPACK_NAME, "{\"pack_format\": 4, \"description\": \"Data for resourcefulbees tags.\"}");
        }
    }

}
