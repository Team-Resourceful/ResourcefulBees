package com.dungeonderps.resourcefulbees;

import com.dungeonderps.resourcefulbees.commands.ResourcefulBeeCommands;
import com.dungeonderps.resourcefulbees.compat.top.TopCompat;
import com.dungeonderps.resourcefulbees.config.BeeBuilder;
import com.dungeonderps.resourcefulbees.config.ResourcefulBeesConfig;
import com.dungeonderps.resourcefulbees.data.DataGen;
import com.dungeonderps.resourcefulbees.data.RecipeBuilder;
import com.dungeonderps.resourcefulbees.entity.CustomBeeRenderer;
import com.dungeonderps.resourcefulbees.init.ModSetup;
import com.dungeonderps.resourcefulbees.loot.function.BlockItemFunction;
import com.dungeonderps.resourcefulbees.screen.CentrifugeScreen;
import com.dungeonderps.resourcefulbees.utils.ColorHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
        ModSetup.initialize();
        RegistryHandler.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ResourcefulBeesConfig.CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGen::gatherData);

        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::OnServerSetup);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onBlockColors);
        });

        MinecraftForge.EVENT_BUS.register(this);
    }


    public void OnServerSetup(FMLServerAboutToStartEvent event){
        LOGGER.info("recipe should be loaded");
        IResourceManager manager = event.getServer().getResourceManager();
        if (manager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloader = (IReloadableResourceManager)manager;
            reloader.addReloadListener(new RecipeBuilder());
        }
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

        LootFunctionManager.registerFunction(new BlockItemFunction.Serializer());

        ModSetup.setupDispenserCollectionBehavior();

        DeferredWorkQueue.runLater(BeeBuilder::setupBees);
    }
    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopCompat::new);
    }

    private void serverStarting(FMLServerStartingEvent event) {
        ResourcefulBeeCommands.register(event.getCommandDispatcher());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ModSetup.loadResources();
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.CUSTOM_BEE.get(), CustomBeeRenderer::new);
        ScreenManager.registerFactory(RegistryHandler.CENTRIFUGE_CONTAINER.get(), CentrifugeScreen::new);
    }
}
