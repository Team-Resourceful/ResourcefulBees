package com.teamresourceful.resourcefulbees.datagen;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.datagen.providers.advancements.ModAdvancementProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.blockstates.ModBlockStateProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.items.ModItemModelProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.lang.ModLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.loottables.ModLootTableProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.recipes.ModRecipeProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.ModBlockTagProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.ModFluidTagProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.ModItemTagProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.ModPoiTagProvider;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ResourcefulBeesDataGenerator {

    private ResourcefulBeesDataGenerator() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        ModConstants.LOGGER.info("Data Generator Loaded!");
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModLanguageProvider(generator));

        ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(generator, provider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagProvider);
        generator.addProvider(event.includeServer(), new ModPoiTagProvider(generator, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagProvider(generator, provider, blockTagProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new ModFluidTagProvider(generator, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new ModAdvancementProvider(generator, provider));
        generator.addProvider(event.includeServer(), new ModLootTableProvider(generator, provider));
        System.out.println("Data Generator Loaded!");
    }
}
