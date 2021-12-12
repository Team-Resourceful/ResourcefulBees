package com.teamresourceful.resourcefulbees.datagen;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.datagen.providers.advancements.ModAdvancementProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.blockstates.ModBlockStateProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.lang.CentrifugeLangProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.lang.ModLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.loottables.ModLootTableProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.recipes.ModRecipeProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.ModBlockTagProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.ModFluidTagProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.tags.ModItemTagProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ResourcefulBees.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ResourcefulBeesDataGenerator {

    private ResourcefulBeesDataGenerator() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        ResourcefulBees.LOGGER.info("Data Generator Loaded!");
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        if (event.includeClient()) {
            generator.addProvider(new ModBlockStateProvider(generator, existingFileHelper));
        }
        if (event.includeServer()) {
            generator.addProvider(new ModLootTableProvider(generator));
            ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(generator, existingFileHelper);
            generator.addProvider(blockTagProvider);
            generator.addProvider(new ModItemTagProvider(generator, blockTagProvider, existingFileHelper));
            generator.addProvider(new ModFluidTagProvider(generator, existingFileHelper));
            generator.addProvider(new ModRecipeProvider(generator));
            generator.addProvider(new ModAdvancementProvider(generator));
        }

        if (event.includeClient()) {
            generator.addProvider(new ModLanguageProvider(generator));
            generator.addProvider(new CentrifugeLangProvider(generator));
        }
    }
}
