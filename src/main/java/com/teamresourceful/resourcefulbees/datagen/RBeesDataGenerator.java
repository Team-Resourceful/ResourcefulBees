package com.teamresourceful.resourcefulbees.datagen;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.datagen.providers.RBeesModelProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.RBeesFluidTagProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.RBeesBlockTagProvider;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public final class RBeesDataGenerator {

    private RBeesDataGenerator() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        ModConstants.LOGGER.info("Data Generator Loaded!");
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        PackOutput output = generator.getPackOutput();

        event.addProvider(new RBeesModelProvider(output));
        event.addProvider(new RBeesFluidTagProvider(output, provider));
        event.addProvider(new RBeesBlockTagProvider(output, provider));






//        generator.addProvider(event.includeClient(), new ModBlockStateProvider(generator, existingFileHelper));
//        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator, existingFileHelper));
//        generator.addProvider(event.includeClient(), new ModLanguageProvider(generator));
//
//        ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(generator, provider, existingFileHelper);
//        generator.addProvider(event.includeServer(), blockTagProvider);
//        generator.addProvider(event.includeServer(), new ModPoiTagProvider(generator, provider, existingFileHelper));
//        generator.addProvider(event.includeServer(), new ModItemTagProvider(generator, provider, blockTagProvider.contentsGetter(), existingFileHelper));
//        generator.addProvider(event.includeServer(), new ModFluidTagProvider(generator, provider, existingFileHelper));
//        generator.addProvider(event.includeServer(), new ModRecipeProvider(generator));
//        generator.addProvider(event.includeServer(), new ModAdvancementProvider(generator, provider));
//        generator.addProvider(event.includeServer(), new ModLootTableProvider(generator, provider));
    }
}
