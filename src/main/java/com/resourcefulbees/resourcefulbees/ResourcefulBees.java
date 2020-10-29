package com.resourcefulbees.resourcefulbees;

import com.resourcefulbees.resourcefulbees.api.ResourcefulBeesAPI;
import com.resourcefulbees.resourcefulbees.block.TieredBeehiveBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryBlock;
import com.resourcefulbees.resourcefulbees.client.gui.screen.*;
import com.resourcefulbees.resourcefulbees.client.models.ModelHandler;
import com.resourcefulbees.resourcefulbees.client.render.entity.CustomBeeRenderer;
import com.resourcefulbees.resourcefulbees.client.render.fluid.FluidRender;
import com.resourcefulbees.resourcefulbees.client.render.items.ItemModelPropertiesHandler;
import com.resourcefulbees.resourcefulbees.compat.top.TopCompat;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import com.resourcefulbees.resourcefulbees.init.BeeSetup;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.config.ConfigLoader;
import com.resourcefulbees.resourcefulbees.data.DataGen;
import com.resourcefulbees.resourcefulbees.data.DataPackLoader;
import com.resourcefulbees.resourcefulbees.data.RecipeBuilder;
import com.resourcefulbees.resourcefulbees.init.BiomeDictonarySetup;
import com.resourcefulbees.resourcefulbees.init.ModSetup;
import com.resourcefulbees.resourcefulbees.init.TraitSetup;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.registry.*;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.PreviewHandler;
import com.resourcefulbees.resourcefulbees.utils.color.ColorHandler;
import com.resourcefulbees.resourcefulbees.utils.validation.SecondPhaseValidator;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

@Mod("resourcefulbees")
public class ResourcefulBees
{
    //TODO Test other mods can register their own bees (and pollen) with minimal issue
    //TODO Weed out all possible NPE's

    public static final String MOD_ID = "resourcefulbees";

    public static final Logger LOGGER = LogManager.getLogger();

    public ResourcefulBees() {
        ModSetup.initialize();
        RegistryHandler.init();
        ResourcefulBeesAPI.setBeeRegistry(BeeRegistry.getRegistry());
        ResourcefulBeesAPI.setTraitRegistry(TraitRegistry.getRegistry());
        BeeRegistry.getRegistry().allowRegistration();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.ClientConfig.CLIENT_CONFIG, "resourcefulbees/client.toml");

        ConfigLoader.load(Config.CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");

        BiomeDictonarySetup.buildDictionary();
        BeeSetup.setupBees();
        RegistryHandler.registerDynamicBees();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        MinecraftForge.EVENT_BUS.addListener(BeeSetup::onBiomeLoad);
        MinecraftForge.EVENT_BUS.addListener(DataPackLoader::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::ServerLoaded);

        MinecraftForge.EVENT_BUS.addListener(this::trade);
        MinecraftForge.EVENT_BUS.addListener(this::entityDies);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(PreviewHandler::onWorldRenderLast);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::registerModels);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::onModelBake);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onBlockColors);
            MinecraftForge.EVENT_BUS.addListener(FluidRender::honeyOverlay);
        });

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void ServerLoaded(FMLServerStartedEvent event) {
        BeeRegistry.getRegistry().getBees().forEach(((s, beeData) -> SecondPhaseValidator.validateMutation(beeData)));
    }

    private void entityDies(LivingDeathEvent event) {
        //TODO: move this to it's own class in the entity package - epic
        //TODO: enable WIP Pollen Feature.
        /*
        if (event.getEntity() instanceof ResourcefulBee) {
            ResourcefulBee bee = (ResourcefulBee) event.getEntity();

            if (bee.hasNectar()) {

                World world = event.getEntity().world;
                ItemStack item = new ItemStack(ModItems.POLLEN.get());

                int count = Math.round((world.rand.nextFloat() + 1) * 2.5f);
                CompoundNBT beeNBT = new CompoundNBT();
                bee.writeUnlessPassenger(beeNBT);
                BlockPos flower = bee.getLastFlower();

                System.out.println(flower.getX() + " " + flower.getY() + " " + flower.getZ());

                if(flower != null) {
                    CompoundNBT compoundNBT = new CompoundNBT();
                    BlockState flowerState = world.getBlockState(flower);

                    compoundNBT.putString("specific", ForgeRegistries.BLOCKS.getKey(flowerState.getBlock()).toString());

                    item.setTag(compoundNBT);
                }

                item.setCount(count);

                ItemEntity entityItem = new ItemEntity(world,
                        bee.getX(), bee.getY(), bee.getZ(),
                        item.copy());

                //To give the item the little and neat drop motion ;)
                entityItem.setMotion(
                        world.rand.nextGaussian() * 0.08F,
                        world.rand.nextGaussian() * 0.08F + 0.2F,
                        world.rand.nextGaussian() * 0.08F);


                world.addEntity(entityItem);
            }
        }
        */
    }

    public void trade(VillagerTradesEvent event) {
        List<VillagerTrades.ITrade> level1 = event.getTrades().get(1);
        List<VillagerTrades.ITrade> level2 = event.getTrades().get(2);
        List<VillagerTrades.ITrade> level3 = event.getTrades().get(3);
        List<VillagerTrades.ITrade> level4 = event.getTrades().get(4);
        List<VillagerTrades.ITrade> level5 = event.getTrades().get(5);

        if(event.getType() == ModVillagerProfessions.BEEKEEPER.get()) {
            ItemStack queenBeeBanner = new ItemStack(net.minecraft.item.Items.BLACK_BANNER);
            CompoundNBT compoundnbt = queenBeeBanner.getOrCreateChildTag("BlockEntityTag");
            ListNBT listnbt = (new BannerPattern.Builder()).func_222477_a(BannerPattern.RHOMBUS_MIDDLE, DyeColor.LIGHT_BLUE).func_222477_a(BannerPattern.STRIPE_DOWNRIGHT, DyeColor.YELLOW).func_222477_a(BannerPattern.STRIPE_DOWNLEFT, DyeColor.YELLOW).func_222477_a(BannerPattern.STRIPE_BOTTOM, DyeColor.YELLOW).func_222477_a(BannerPattern.TRIANGLE_TOP, DyeColor.YELLOW).func_222477_a(BannerPattern.CURLY_BORDER, DyeColor.YELLOW).func_222476_a();
            compoundnbt.put("Patterns", listnbt);
            queenBeeBanner.setDisplayName(new TranslationTextComponent("block.resourcefulbees.queen_bee_banner").formatted(TextFormatting.GOLD));
            queenBeeBanner.setCount(1);

            level1.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(net.minecraft.item.Items.EMERALD, 3),
                    new ItemStack(ModItems.WAX_BLOCK_ITEM.get(), 1),
                    32, 4, 1));
            level2.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(net.minecraft.item.Items.EMERALD, 2),
                    new ItemStack(net.minecraft.item.Items.HONEYCOMB, 3),
                    10, 4, 1));
            level3.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(net.minecraft.item.Items.EMERALD, 2),
                    new ItemStack(ModItems.WAX.get(), 6),
                    15, 4, 1));
            level4.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(net.minecraft.item.Items.HONEY_BOTTLE, 4),
                    new ItemStack(net.minecraft.item.Items.EMERALD, 2),
                    10, 4, 0));
            level5.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(net.minecraft.item.Items.EMERALD, 12),
                    new ItemStack(ModItems.SMOKER.get(), 1),
                    10, 4, 0));
            level5.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(net.minecraft.item.Items.EMERALD, 64),
                    queenBeeBanner,
                    2, 4, 0));
        }
    }

    private void setup(final FMLCommonSetupEvent event){
        BeeInfoUtils.makeValidApiaryTag();
        Map<BlockState, PointOfInterestType> pointOfInterestTypeMap = new HashMap<>();
        ModBlocks.BLOCKS.getEntries().stream()
                .filter(blockRegistryObject -> blockRegistryObject.get() instanceof TieredBeehiveBlock)
                .forEach((blockRegistryObject -> blockRegistryObject.get()
                        .getStateContainer()
                        .getValidStates()
                        .forEach(blockState -> putPOIInMap(blockState, pointOfInterestTypeMap))));
        ModBlocks.BLOCKS.getEntries().stream()
                .filter(blockRegistryObject -> blockRegistryObject.get() instanceof ApiaryBlock)
                .forEach((blockRegistryObject -> blockRegistryObject.get()
                        .getStateContainer()
                        .getValidStates()
                        .forEach(blockState -> putPOIInMap(blockState, pointOfInterestTypeMap))));
        PointOfInterestType.field_221073_u.putAll(pointOfInterestTypeMap);


        event.enqueueWork(ModSetup::registerDispenserBehaviors);

        NetPacketHandler.init();

        RegistryHandler.addEntityAttributes();
        MinecraftForge.EVENT_BUS.register(new RecipeBuilder());

        ModFeatures.ConfiguredFeatures.registerConfiguredFeatures();
    }

    private void putPOIInMap(BlockState blockState, Map<BlockState, PointOfInterestType> pointOfInterestTypeMap) {
        pointOfInterestTypeMap.put(blockState, ModPOIs.TIERED_BEEHIVE_POI.get());
    }

    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopCompat::new);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        CentrifugeScreen.currentMonth = new SimpleDateFormat("MM").format(new Date());
        BeeRegistry.MOD_BEES.forEach((s, customBee) -> RenderingRegistry.registerEntityRenderingHandler(customBee.get(), CustomBeeRenderer::new));
        ScreenManager.registerFactory(ModContainers.CENTRIFUGE_CONTAINER.get(), CentrifugeScreen::new);
        ScreenManager.registerFactory(ModContainers.MECHANICAL_CENTRIFUGE_CONTAINER.get(), MechanicalCentrifugeScreen::new);
        ScreenManager.registerFactory(ModContainers.CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), CentrifugeMultiblockScreen::new);
        ScreenManager.registerFactory(ModContainers.UNVALIDATED_APIARY_CONTAINER.get(), UnvalidatedApiaryScreen::new);
        ScreenManager.registerFactory(ModContainers.VALIDATED_APIARY_CONTAINER.get(), ValidatedApiaryScreen::new);
        ScreenManager.registerFactory(ModContainers.APIARY_STORAGE_CONTAINER.get(), ApiaryStorageScreen::new);
        ScreenManager.registerFactory(ModContainers.APIARY_BREEDER_CONTAINER.get(), ApiaryBreederScreen::new);
        ScreenManager.registerFactory(ModContainers.HONEY_GENERATOR_CONTAINER.get(), HoneyGeneratorScreen::new);
        RenderTypeLookup.setRenderLayer(ModBlocks.GOLD_FLOWER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.PREVIEW_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ERRORED_PREVIEW_BLOCK.get(), RenderType.getCutout());

        ItemModelPropertiesHandler.registerProperties();

        event.enqueueWork(FluidRender::setHoneyRenderType);
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        TraitRegistry.registerDefaultTraits();
        TraitSetup.buildCustomTraits();
        TraitRegistry.setTraitRegistryClosed();
        TraitRegistry.applyBeeTraits();
        BeeSetup.registerBeePlacements();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DataGen::generateClientData);
        DataGen.generateCommonData();
    }
}
