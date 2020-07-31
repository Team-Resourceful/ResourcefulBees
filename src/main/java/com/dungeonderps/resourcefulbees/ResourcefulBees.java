package com.dungeonderps.resourcefulbees;

import com.dungeonderps.resourcefulbees.client.gui.screen.ApiaryStorageScreen;
import com.dungeonderps.resourcefulbees.client.gui.screen.CentrifugeScreen;
import com.dungeonderps.resourcefulbees.client.gui.screen.UnvalidatedApiaryScreen;
import com.dungeonderps.resourcefulbees.client.gui.screen.ValidatedApiaryScreen;
import com.dungeonderps.resourcefulbees.client.render.entity.CustomBeeRenderer;
import com.dungeonderps.resourcefulbees.client.render.items.ItemModelPropertiesHandler;
import com.dungeonderps.resourcefulbees.compat.top.TopCompat;
import com.dungeonderps.resourcefulbees.config.BeeBuilder;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.data.RecipeBuilder;
import com.dungeonderps.resourcefulbees.init.ModSetup;
import com.dungeonderps.resourcefulbees.network.NetPacketHandler;
import com.dungeonderps.resourcefulbees.registry.ColorHandler;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.PreviewHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.dungeonderps.resourcefulbees.loot.function.BlockItemFunction;

//1.15.2 Branch
//TODO - Look into "Queen Bee"
//TODO - Additional Easter Eggs


@Mod("resourcefulbees")
public class ResourcefulBees
{
    public static final String MOD_ID = "resourcefulbees";

    public static final Logger LOGGER = LogManager.getLogger();

    public ResourcefulBees() {
        ModSetup.initialize();
        RegistryHandler.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::trade);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(PreviewHandler::onWorldRenderLast);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onBlockColors);
        });

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void trade(VillagerTradesEvent event) {
        List<VillagerTrades.ITrade> level1 = event.getTrades().get(1);
        List<VillagerTrades.ITrade> level2 = event.getTrades().get(2);
        List<VillagerTrades.ITrade> level3 = event.getTrades().get(3);
        List<VillagerTrades.ITrade> level4 = event.getTrades().get(4);
        List<VillagerTrades.ITrade> level5 = event.getTrades().get(5);

        if(event.getType() == RegistryHandler.BEEKEEPER.get()) {
            ItemStack queenBeeBanner = new ItemStack(Items.BLACK_BANNER);
            CompoundNBT compoundnbt = queenBeeBanner.getOrCreateChildTag("BlockEntityTag");
            ListNBT listnbt = (new BannerPattern.Builder()).setPatternWithColor(BannerPattern.RHOMBUS_MIDDLE, DyeColor.LIGHT_BLUE).setPatternWithColor(BannerPattern.STRIPE_DOWNRIGHT, DyeColor.YELLOW).setPatternWithColor(BannerPattern.STRIPE_DOWNLEFT, DyeColor.YELLOW).setPatternWithColor(BannerPattern.STRIPE_BOTTOM, DyeColor.YELLOW).setPatternWithColor(BannerPattern.TRIANGLE_TOP, DyeColor.YELLOW).setPatternWithColor(BannerPattern.CURLY_BORDER, DyeColor.YELLOW).buildNBT();
            compoundnbt.put("Patterns", listnbt);
            queenBeeBanner.setDisplayName(new TranslationTextComponent("block.resourcefulbees.queen_bee_banner").mergeStyle(TextFormatting.GOLD));
            queenBeeBanner.setCount(1);

            level1.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 3),
                    new ItemStack(RegistryHandler.WAX_BLOCK_ITEM.get(), 1),
                    32, 4, 1));
            level2.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(Items.HONEYCOMB, 3),
                    10, 4, 1));
            level3.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(RegistryHandler.BEESWAX.get(), 6),
                    15, 4, 1));
            level4.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.HONEY_BOTTLE, 4),
                    new ItemStack(Items.EMERALD, 2),
                    10, 4, 0));
            level5.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 12),
                    new ItemStack(RegistryHandler.SMOKER.get(), 1),
                    10, 4, 0));
            level5.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 64),
                    queenBeeBanner,
                    2, 4, 0));
        }
    }

    private void setup(final FMLCommonSetupEvent event){
        /*
        The lines below are necessary for getting mod bees into mod beehive.
        We're basically pushing the mod data into the minecraft POI list
        because forge POI doesn't seem to have any impact.
        Not entirely sure if forge registered POI is even necessary
         */
        Map<BlockState, PointOfInterestType> pointOfInterestTypeMap = new HashMap<>();
        RegistryHandler.T1_BEEHIVE.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.T2_BEEHIVE.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.T3_BEEHIVE.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.T4_BEEHIVE.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.BEE_NEST.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.ACACIA_BEE_NEST.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.GRASS_BEE_NEST.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.JUNGLE_BEE_NEST.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.NETHER_BEE_NEST.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.PRISMARINE_BEE_NEST.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.PURPUR_BEE_NEST.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.WITHER_BEE_NEST.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        RegistryHandler.T1_APIARY_BLOCK.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.TIERED_BEEHIVE_POI.get()));
        PointOfInterestType.POIT_BY_BLOCKSTATE.putAll(pointOfInterestTypeMap);

        //LootFunctionManager.func_237451_a_(new BlockItemFunction.Serializer());

        ModSetup.setupDispenserCollectionBehavior();

        NetPacketHandler.init();

        RegistryHandler.addEntityAttributes();
        MinecraftForge.EVENT_BUS.register(new RecipeBuilder());
    }
    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopCompat::new);
    }

    private void serverStarting(FMLServerStartingEvent event) {
        //ResourcefulBeeCommands.register(event.getCommandDispatcher()); //TODO find mapping
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        CentrifugeScreen.currentMonth = new SimpleDateFormat("MM").format(new Date());
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.CUSTOM_BEE.get(), CustomBeeRenderer::new);
        ScreenManager.registerFactory(RegistryHandler.CENTRIFUGE_CONTAINER.get(), CentrifugeScreen::new);
        ScreenManager.registerFactory(RegistryHandler.UNVALIDATED_APIARY_CONTAINER.get(), UnvalidatedApiaryScreen::new);
        ScreenManager.registerFactory(RegistryHandler.VALIDATED_APIARY_CONTAINER.get(), ValidatedApiaryScreen::new);
        ScreenManager.registerFactory(RegistryHandler.APIARY_STORAGE_CONTAINER.get(), ApiaryStorageScreen::new);
        RenderTypeLookup.setRenderLayer(RegistryHandler.GOLD_FLOWER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.PREVIEW_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.ERRORED_PREVIEW_BLOCK.get(), RenderType.getCutout());

        ItemModelPropertiesHandler.registerProperties();
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        BeeBuilder.setupBees();
    }
}
