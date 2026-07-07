package com.teamresourceful.resourcefulbees.common.data;

import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefulbees.common.util.ModUtils;
import com.teamresourceful.resourcefullib.common.utils.GenericMemoryPack;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public final class DataPackLoader implements RepositorySource {

    private static final String DATAPACK_NAME = "resourcefulbees:internals";
    public static final DataPackLoader INSTANCE = new DataPackLoader();
    public static final Component TITLE = Component.literal("Data for Resourceful Bees");
    private static final PackMetadataSection METADATA = new PackMetadataSection(TITLE, SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).minorRange());

    public DataPackLoader() {}

    @Override
    public void loadPacks(Consumer<Pack> onLoad) {
        try (GenericMemoryPack dataPack = ModUtils.createHiddenDataPack(DATAPACK_NAME, METADATA)) {
            generateTags(dataPack);
            generateHiveRecipes(dataPack);

            PackLocationInfo info = new PackLocationInfo(DATAPACK_NAME, TITLE, PackSource.BUILT_IN, Optional.empty());
            PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.BOTTOM, true);
            onLoad.accept(Pack.readMetaAndCreate(info, BuiltInPackSource.fixedResources(dataPack), PackType.SERVER_DATA, selectionConfig));
        }
    }

    private static void generateTags(GenericMemoryPack dataPack) {
        DataGen.getTags().forEach((tagID, tagEntries) -> {
            TagBuilder builder = TagBuilder.create();
            tagEntries.forEach(builder::addElement);
            TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(builder.build(), false))
                .result()
                .ifPresent(json -> dataPack.putJson(PackType.SERVER_DATA, tagID, json));
        });
    }

    private static void generateHiveRecipes(GenericMemoryPack dataPack) {
        ResourcefulBeesAPI.getRegistry().getBeeRegistry().getStreamOfBees().forEach(customBeeData -> {
            Recipe<HiveRecipe.Input> recipe = RecipeBuilder.makeHiveRecipe(customBeeData);
            if (recipe != null) {
                HiveRecipe.MAP_CODEC.codec().encodeStart(JsonOps.INSTANCE, (HiveRecipe) recipe)
                        .result()
                        .ifPresent(jsonElement -> {
                            var jsonobj = jsonElement.getAsJsonObject();
                            jsonobj.addProperty("type", ModRecipes.HIVE_RECIPE_TYPE.getId().toString());
                            String path = "recipe/hive/" + customBeeData.name().toLowerCase(Locale.ROOT) + "_hive_output.json";
                            dataPack.putJson(PackType.SERVER_DATA, ModIdentifier.of(path), jsonobj);
                        });

            }
        });
    }
}
