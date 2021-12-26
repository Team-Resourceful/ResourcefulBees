package com.resourcefulbees.resourcefulbees;

import com.resourcefulbees.resourcefulbees.api.ResourcefulBeesAPI;
import com.resourcefulbees.resourcefulbees.capabilities.BeepediaData;
import com.resourcefulbees.resourcefulbees.client.gui.IncompatibleModWarning;
import com.resourcefulbees.resourcefulbees.client.render.patreon.PetLoader;
import com.resourcefulbees.resourcefulbees.command.BeepediaCommand;
import com.resourcefulbees.resourcefulbees.compat.top.TopCompat;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.config.ConfigLoader;
import com.resourcefulbees.resourcefulbees.data.DataGen;
import com.resourcefulbees.resourcefulbees.data.RecipeBuilder;
import com.resourcefulbees.resourcefulbees.init.*;
import com.resourcefulbees.resourcefulbees.item.BeeSpawnEggItem;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.recipe.HiveIngredient;
import com.resourcefulbees.resourcefulbees.registry.*;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod("resourcefulbees")
public class ResourcefulBees {

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

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> IncompatibleModWarning::init);


        BiomeDictionarySetup.buildDictionary();
        BeeSetup.setupBees();
        RegistryHandler.registerDynamicBees();
        RegistryHandler.registerDynamicHoney();
        if (FMLLoader.isProduction()) {
            Config.GENERATE_DEFAULTS.set(false);
            Config.GENERATE_DEFAULTS.save();
        }


        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistryHandler::addEntityAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
        MinecraftForge.EVENT_BUS.addListener(BeeSetup::onBiomeLoad);
        MinecraftForge.EVENT_BUS.addListener(this::serverLoaded);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommand);

        // events related to player specifically
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());


        MinecraftForge.EVENT_BUS.addListener(this::trade);
        //MinecraftForge.EVENT_BUS.addListener(EntityEventHandlers::entityDies);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::clientStuff);

        MinecraftForge.EVENT_BUS.register(this);
        ItemStack stack = new ItemStack(Items.ACACIA_DOOR);
        stack.setTag(stack.getOrCreateTag().merge(new CompoundNBT()));
    }

    private void registerCommand(RegisterCommandsEvent event) {
        BeepediaCommand.register(event.getDispatcher());
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        ModPotions.createMixes();
    }

    private void serverLoaded(FMLServerStartedEvent event) {
        if (event.getServer().isDedicatedServer()){
            MutationSetup.setupMutations();
            FlowerSetup.setupFlowers();
            BreedingSetup.setupFeedItems();
        }
    }

    public void trade(VillagerTradesEvent event) {
        List<VillagerTrades.ITrade> level1 = event.getTrades().get(1);
        List<VillagerTrades.ITrade> level2 = event.getTrades().get(2);
        List<VillagerTrades.ITrade> level3 = event.getTrades().get(3);
        List<VillagerTrades.ITrade> level4 = event.getTrades().get(4);
        List<VillagerTrades.ITrade> level5 = event.getTrades().get(5);

        if (event.getType() == ModVillagerProfessions.BEEKEEPER.get()) {
            ItemStack queenBeeBanner = new ItemStack(net.minecraft.item.Items.BLACK_BANNER);
            CompoundNBT compoundnbt = queenBeeBanner.getOrCreateTagElement("BlockEntityTag");
            ListNBT listnbt = (new BannerPattern.Builder()).addPattern(BannerPattern.RHOMBUS_MIDDLE, DyeColor.LIGHT_BLUE).addPattern(BannerPattern.STRIPE_DOWNRIGHT, DyeColor.YELLOW).addPattern(BannerPattern.STRIPE_DOWNLEFT, DyeColor.YELLOW).addPattern(BannerPattern.STRIPE_BOTTOM, DyeColor.YELLOW).addPattern(BannerPattern.TRIANGLE_TOP, DyeColor.YELLOW).addPattern(BannerPattern.CURLY_BORDER, DyeColor.YELLOW).toListTag();
            compoundnbt.put("Patterns", listnbt);
            queenBeeBanner.setHoverName(new TranslationTextComponent("block.resourcefulbees.queen_bee_banner").withStyle(TextFormatting.GOLD));
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

    private void setup(final FMLCommonSetupEvent event) {
        BeepediaData.register();
        BeeInfoUtils.makeValidApiaryTag();
        event.enqueueWork(ModSetup::registerDispenserBehaviors);
        NetPacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new RecipeBuilder());
        ModFeatures.ConfiguredFeatures.registerConfiguredFeatures();
        CraftingHelper.register(new ResourceLocation(ResourcefulBees.MOD_ID, "hive"), HiveIngredient.Serializer.INSTANCE);
    }

    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopCompat::new);
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        TraitRegistry.registerDefaultTraits();
        TraitSetup.buildCustomTraits();
        TraitRegistry.setTraitRegistryClosed();
        TraitRegistry.applyBeeTraits();
        BeeSetup.registerBeePlacements();
        BeeSpawnEggItem.initSpawnEggs();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DataGen::generateClientData);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::registerPatreonRender);
        DataGen.generateCommonData();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PetLoader::loadAPI);
    }
}
