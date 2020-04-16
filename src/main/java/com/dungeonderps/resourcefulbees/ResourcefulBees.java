package com.dungeonderps.resourcefulbees;

import com.dungeonderps.resourcefulbees.config.ResourcefulBeesConfig;
import com.dungeonderps.resourcefulbees.data.DataGen;
import com.dungeonderps.resourcefulbees.utils.ColorHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.entity.CustomBeeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mod("resourcefulbees")
public class ResourcefulBees
{
    public static final String MOD_ID = "resourcefulbees";

    public static final Logger LOGGER = LogManager.getLogger();

    public ResourcefulBees() {
        ResourcefulBeesConfig.setup();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        RegistryHandler.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGen::gatherData);

        
        MinecraftForge.EVENT_BUS.register(this);

    }


    private void setup(final FMLCommonSetupEvent event){
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(
                (EntityType<CustomBeeEntity>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(MOD_ID, "bee")),
                (EntityRendererManager p_i226033_1_) -> new CustomBeeRenderer(p_i226033_1_));
    }

    @ObjectHolder(MOD_ID)
    public static class ObjectHolders {

        public static class Items{
            public static final Item RESOURCEFUL_HONEYCOMB = null;
        }

        public static class Blocks{
            public static final Block IRON_BEEHIVE = null;
        }

        public static class BlockEntities{
            public static final TileEntityType<IronBeehiveBlockEntity> IRON_BEEHIVE = null;
        }

    }
}
