package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.custom.LoadConditionRegistry;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitRegistry;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.util.GsonHelper;

import java.io.Reader;
import java.util.Locale;

public final class TraitSetup {

    private TraitSetup() {
        throw new UtilityClassError();
    }

    public static void buildCustomTraits() {
        if (GeneralConfig.enableDevBees) {
            ModConstants.LOGGER.info("Loading Dev Traits...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_traits", TraitSetup::parseTrait, ModPaths.MOD_ROOT);
        }

        if (GeneralConfig.generateDefaults) {
            ModConstants.LOGGER.info("Copying Default Traits...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_traits", ModPaths.BEE_TRAITS, ModPaths.MOD_ROOT);
        }

        ModConstants.LOGGER.info("Loading Custom Traits...");
        FileUtils.streamFilesAndParse(ModPaths.BEE_TRAITS, TraitSetup::parseTrait);
    }

    private static void parseTrait(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(Constants.GSON, reader, JsonObject.class);
        if (LoadConditionRegistry.canLoad(jsonObject)) {
            name = Codec.STRING.fieldOf("name").orElse(name).codec().parse(JsonOps.INSTANCE, jsonObject).get().orThrow().toLowerCase(Locale.ENGLISH).replace(" ", "_");
            Trait beeTrait = Trait.getCodec(name).parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, s -> ModConstants.LOGGER.error("Could not Create Bee Trait"));
            TraitRegistry.getRegistry().register(name, beeTrait);
        }
    }
}
