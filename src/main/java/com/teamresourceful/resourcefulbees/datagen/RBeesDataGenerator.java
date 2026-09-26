package com.teamresourceful.resourcefulbees.datagen;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.datagen.providers.BeekeeperTradeProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.RBeesModelProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.lang.RBeesLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.loottables.RBeesLootTableProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.recipes.RBeesRecipeProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.RBeesBlockTagProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.RBeesFluidTagProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.RBeesItemTagProvider;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
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
        CompletableFuture<HolderLookup.Provider> provider = event.getReloadableLookupProvider();
        PackOutput output = generator.getPackOutput();

        event.addProvider(new RBeesModelProvider(output));
        event.addProvider(new RBeesFluidTagProvider(output, provider));
        event.addProvider(new RBeesBlockTagProvider(output, provider));
        event.addProvider(new BeekeeperTradeProvider(output, provider));
        event.addProvider(new RBeesLanguageProvider(output));
        event.addProvider(new RBeesItemTagProvider(output, provider));

        RegistrySetBuilder registryBuilder = new RegistrySetBuilder()
                .add(RecipeProvider.asBootstrap(RBeesRecipeProvider::new))
                .add(Registries.LOOT_TABLE, new RBeesLootTableProvider());

        event.createReloadableRegistryObjects(registryBuilder);




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
