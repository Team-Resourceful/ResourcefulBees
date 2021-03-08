package com.resourcefulbees.resourcefulbees;

import com.resourcefulbees.resourcefulbees.api.ResourcefulBeesAPI;
import com.resourcefulbees.resourcefulbees.compat.top.TopCompat;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.config.ConfigLoader;
import com.resourcefulbees.resourcefulbees.data.DataGen;
import com.resourcefulbees.resourcefulbees.data.DataPackLoader;
import com.resourcefulbees.resourcefulbees.data.RecipeBuilder;
import com.resourcefulbees.resourcefulbees.init.*;
import com.resourcefulbees.resourcefulbees.item.BeeSpawnEggItem;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.patreon.PatreonDataLoader;
import com.resourcefulbees.resourcefulbees.registry.*;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.registries.ForgeRegistries;
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

        checkForIncompatibleMods();

        BiomeDictionarySetup.buildDictionary();
        BeeSetup.setupBees();
        RegistryHandler.registerDynamicBees();
        RegistryHandler.registerDynamicHoney();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistryHandler::addEntityAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        MinecraftForge.EVENT_BUS.addListener(BeeSetup::onBiomeLoad);
        MinecraftForge.EVENT_BUS.addListener(DataPackLoader::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::serverLoaded);

        MinecraftForge.EVENT_BUS.addListener(this::trade);
        //MinecraftForge.EVENT_BUS.addListener(EntityEventHandlers::entityDies);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::clientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void serverLoaded(FMLServerStartedEvent event) {
        ModPotions.createMixes();
        MutationSetup.setupMutations();
        FlowerSetup.setupFlowers();
    }

    public void trade(VillagerTradesEvent event) {
        List<VillagerTrades.ITrade> level1 = event.getTrades().get(1);
        List<VillagerTrades.ITrade> level2 = event.getTrades().get(2);
        List<VillagerTrades.ITrade> level3 = event.getTrades().get(3);
        List<VillagerTrades.ITrade> level4 = event.getTrades().get(4);
        List<VillagerTrades.ITrade> level5 = event.getTrades().get(5);

        if (event.getType() == ModVillagerProfessions.BEEKEEPER.get()) {
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

    private void setup(final FMLCommonSetupEvent event) {
        BeeInfoUtils.makeValidApiaryTag();

        event.enqueueWork(ModSetup::registerDispenserBehaviors);

        NetPacketHandler.init();

        MinecraftForge.EVENT_BUS.register(new RecipeBuilder());

        ModFeatures.ConfiguredFeatures.registerConfiguredFeatures();
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
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PatreonDataLoader::loadAPI);
    }

    private void checkForIncompatibleMods() {
        if(Config.BYPASS_PERFORMANT_CHECK.get()) {
            LOGGER.warn("Performant check bypassed");
            LOGGER.warn("Performant {} present", (FMLLoader.getLoadingModList().getModFileById("performant") != null ? "is" : "is not"));
        } else {
            if (FMLLoader.getLoadingModList().getModFileById("performant") != null) {
                throw new IllegalStateException("" +
                        "Performant is incompatible with Resourceful Bees\n" +
                        "This is a known issue with performant and it breaking other mods, the author does not care\n" +
                        "GitHub issue on the matter: https://github.com/someaddons/performant_issues/issues/70\n" +
                        "To bypass this check set \"bypassPerformantCheck: true\" in your Resourceful Bees common.toml config\n" +
                        "If you bypass this check we will not provide any support for any issues related to Resourceful Bees or the mods that use it\n" +
                        "If you believe your issue is unrelated, disable performant and reproduce it\n" +
                        "By choosing to bypass this check you understand that here there be dragons");
            }
        }
    }
}
