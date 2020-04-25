package com.dungeonderps.resourcefulbees;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.commands.ResourcefulBeeCommands;
import com.dungeonderps.resourcefulbees.config.ResourcefulBeesConfig;
import com.dungeonderps.resourcefulbees.data.DataGen;
import com.dungeonderps.resourcefulbees.data.DataPackLoader;
import com.dungeonderps.resourcefulbees.entity.CustomBeeRenderer;
import com.dungeonderps.resourcefulbees.utils.ColorHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Mod("resourcefulbees")
public class ResourcefulBees
{
    public static final String MOD_ID = "resourcefulbees";

    public static final Logger LOGGER = LogManager.getLogger();

    public ResourcefulBees() {
        try {
            FileUtils.deleteDirectory(Paths.get(FMLPaths.CONFIGDIR.get().toString(), "resourcefulbees", "resources", "datapack", "data", "resourcefulbees","recipes").toFile());
        } catch (IOException e) {
            LOGGER.error("Failled to delete recipe directory.");
        }
        ResourcefulBeesConfig.setup();

        RegistryHandler.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGen::gatherData);
        MinecraftForge.EVENT_BUS.addListener(DataPackLoader::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
        });


        
        MinecraftForge.EVENT_BUS.register(this);
    }


    private void setup(final FMLCommonSetupEvent event){
        /*
        The 3 lines below are necessary for getting mod bees into mod beehive.
        We're basically pushing the mod data into the minecraft POI list
        because forge POI doesn't seem to have any impact.
        Not entirely sure if forge registered POI is even necessary
         */
        Map<BlockState, PointOfInterestType> pointOfInterestTypeMap = new HashMap<>();
        RegistryHandler.IRON_BEEHIVE.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.IRON_BEEHIVE_POI.get()));
        PointOfInterestType.POIT_BY_BLOCKSTATE.putAll(pointOfInterestTypeMap);

        addBeeToSpawnList();
    }

    private void serverStarting(FMLServerStartingEvent event) {
        ResourcefulBeeCommands.register(event.getCommandDispatcher());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        new ItemGroupResourcefulBees();
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.CUSTOM_BEE.get(), CustomBeeRenderer::new);
    }

    public static void addBeeToSpawnList() {
        ArrayList<String> biomeList = new ArrayList<>();
        Iterator<Map.Entry<String, BeeInfo>> beeInfoIterator = CustomBeeEntity.BEE_INFO.entrySet().iterator();
        while (beeInfoIterator.hasNext()) {
            Map.Entry<String, BeeInfo> element = beeInfoIterator.next();
            BeeInfo beeInfo = element.getValue();

            HashMap<String, BeeInfo> tempMap = new HashMap<>();
            tempMap.put(element.getKey(), element.getValue());


            biomeList.add(beeInfo.getBiomeList());
        }

        List<String> biomes = biomeList.stream().distinct().collect(Collectors.toList());

        for (String s : biomes) {
            if (!s.contains(" ")) {
                LOGGER.info("this biome is being added: " + s);
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(s));
                LOGGER.info("value returned after inputing biome: " + biome);
                if (biome != null) {
                    biome.getSpawns(EntityClassification.CREATURE).add(new Biome.SpawnListEntry(RegistryHandler.CUSTOM_BEE.get(), 20, 3, 30));
                }
            }
        }

        EntitySpawnPlacementRegistry.register(RegistryHandler.CUSTOM_BEE.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CustomBeeEntity::canBeeSpawn);
    }
}
