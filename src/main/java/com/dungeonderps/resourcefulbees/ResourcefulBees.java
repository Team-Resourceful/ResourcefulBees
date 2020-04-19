package com.dungeonderps.resourcefulbees;

import com.dungeonderps.resourcefulbees.config.ResourcefulBeesConfig;
import com.dungeonderps.resourcefulbees.data.DataGen;
import com.dungeonderps.resourcefulbees.utils.ColorHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.entity.CustomBeeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod("resourcefulbees")
public class ResourcefulBees
{
    public static final String MOD_ID = "resourcefulbees";

    public static final Logger LOGGER = LogManager.getLogger();

    public ResourcefulBees() {
        ResourcefulBeesConfig.setup();

        RegistryHandler.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGen::gatherData);

        
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
        PointOfInterestType.field_221073_u.putAll(pointOfInterestTypeMap);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(
                (EntityType<CustomBeeEntity>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(MOD_ID, "bee")),
                (EntityRendererManager p_i226033_1_) -> new CustomBeeRenderer(p_i226033_1_));
    }
}
