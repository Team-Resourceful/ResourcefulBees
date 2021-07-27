package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.client.gui.IncompatibleModWarning;
import com.teamresourceful.resourcefulbees.compat.top.TopCompat;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.config.ConfigLoader;
import com.teamresourceful.resourcefulbees.data.DataGen;
import com.teamresourceful.resourcefulbees.data.RecipeBuilder;
import com.teamresourceful.resourcefulbees.init.BeeSetup;
import com.teamresourceful.resourcefulbees.init.ClientEventHandlers;
import com.teamresourceful.resourcefulbees.init.ModSetup;
import com.teamresourceful.resourcefulbees.init.TraitSetup;
import com.teamresourceful.resourcefulbees.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.patreon.PatreonDataLoader;
import com.teamresourceful.resourcefulbees.registry.*;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

import java.util.List;

@Mod("resourcefulbees")
public class ResourcefulBees {

    public static final String MOD_ID = "resourcefulbees";

    public static final Logger LOGGER = LogManager.getLogger();

    //need to determine how everything in this method can be moved to common module safely
    //oh and figure out how the hell to handle event priorities so the mod doesn't break functionality
    public ResourcefulBees() {
        GeckoLib.initialize();
        ModSetup.initialize();
        RegistryHandler.init();
        ResourcefulBeesAPI.setBeeRegistry(BeeRegistry.getRegistry());
        ResourcefulBeesAPI.setTraitRegistry(TraitRegistry.getRegistry());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.ClientConfig.CLIENT_CONFIG, "resourcefulbees/client.toml");

        ConfigLoader.load(Config.CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> IncompatibleModWarning::init);

        BiomeDictionary.build();
        BeeSetup.setupBees();
        RegistryHandler.registerDynamicBees();
        RegistryHandler.registerDynamicHoney();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistryHandler::addEntityAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        MinecraftForge.EVENT_BUS.addListener(BeeSetup::onBiomeLoad);
        MinecraftForge.EVENT_BUS.addListener(this::serverLoaded);
        MinecraftForge.EVENT_BUS.addListener(this::trade);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::clientStuff);

        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void serverLoaded(FMLServerStartedEvent event) { //arch has an event for this
        if (event.getServer().isDedicatedServer()){
            BeeRegistry.getRegistry().regenerateCustomBeeData();
        }
        ModPotions.createMixes();
    }

    @SubscribeEvent
    public void trade(VillagerTradesEvent event) {//arch should have an event for this
        List<VillagerTrades.ITrade> level1 = event.getTrades().get(1);
        List<VillagerTrades.ITrade> level2 = event.getTrades().get(2);
        List<VillagerTrades.ITrade> level3 = event.getTrades().get(3);
        List<VillagerTrades.ITrade> level4 = event.getTrades().get(4);
        List<VillagerTrades.ITrade> level5 = event.getTrades().get(5);

        if (event.getType() == ModVillagerProfessions.BEEKEEPER.get()) {
            ItemStack queenBeeBanner = new ItemStack(Items.BLACK_BANNER);
            CompoundNBT compoundnbt = queenBeeBanner.getOrCreateTagElement("BlockEntityTag");
            ListNBT listnbt = new BannerPattern.Builder().addPattern(BannerPattern.RHOMBUS_MIDDLE, DyeColor.LIGHT_BLUE).addPattern(BannerPattern.STRIPE_DOWNRIGHT, DyeColor.YELLOW).addPattern(BannerPattern.STRIPE_DOWNLEFT, DyeColor.YELLOW).addPattern(BannerPattern.STRIPE_BOTTOM, DyeColor.YELLOW).addPattern(BannerPattern.TRIANGLE_TOP, DyeColor.YELLOW).addPattern(BannerPattern.CURLY_BORDER, DyeColor.YELLOW).toListTag();
            compoundnbt.put("Patterns", listnbt);
            queenBeeBanner.setHoverName(new TranslationTextComponent("block.resourcefulbees.queen_bee_banner").withStyle(TextFormatting.GOLD));
            queenBeeBanner.setCount(1);

            level1.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 3),
                    new ItemStack(ModItems.WAX_BLOCK_ITEM.get(), 1),
                    32, 4, 1));
            level2.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(Items.HONEYCOMB, 3),
                    10, 4, 1));
            level3.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(ModItems.WAX.get(), 6),
                    15, 4, 1));
            level4.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.HONEY_BOTTLE, 4),
                    new ItemStack(Items.EMERALD, 2),
                    10, 4, 0));
            level5.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 12),
                    new ItemStack(ModItems.SMOKER.get(), 1),
                    10, 4, 0));
            level5.add((entity, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 64),
                    queenBeeBanner,
                    2, 4, 0));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void setup(FMLCommonSetupEvent event) { //find arch method for this to make tag
        BeeInfoUtils.makeValidApiaryTag();
        event.enqueueWork(RegistryHandler::registerDispenserBehaviors); //use arch method for this
        NetPacketHandler.init(); //find arch method for this
        //me.shedaniel.architectury.registry.ReloadListeners#registerReloadListener gets put in init() when moved to arch setup.
        MinecraftForge.EVENT_BUS.register(new RecipeBuilder());
        ModFeatures.ConfiguredFeatures.registerConfiguredFeatures(); //find arch method for this
    }

    @SubscribeEvent
    public void onInterModEnqueue(InterModEnqueueEvent event) { // determine if TOP is on fabric - if not, add forge impl and find fabric alternatives
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopCompat::new);
    }

    @SubscribeEvent
    public void loadComplete(FMLLoadCompleteEvent event) {
        //arch has client event for the client stuff - need to figure out where to
        // handle the traits and how to register spawn placements
        TraitRegistry.registerDefaultTraits();
        TraitSetup.buildCustomTraits();
        TraitRegistry.setTraitRegistryClosed();
        BeeSetup.registerBeePlacements();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DataGen::generateClientData);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::registerPatreonRender);
        DataGen.generateCommonData();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PatreonDataLoader::loadAPI);
    }
}
