package com.teamresourceful.resourcefulbees.datagen;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
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
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ResourcefulBees.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ResourcefulBeesDataGenerator {

    private ResourcefulBeesDataGenerator() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        ResourcefulBees.LOGGER.info("Data Generator Loaded!");
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModLootTableProvider(generator));
        ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(generator, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagProvider);
        generator.addProvider(event.includeServer(), new ModPoiTagProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagProvider(generator, blockTagProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModFluidTagProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new ModAdvancementProvider(generator));
        generator.addProvider(event.includeClient(), new ModLanguageProvider(generator));
    }
}
