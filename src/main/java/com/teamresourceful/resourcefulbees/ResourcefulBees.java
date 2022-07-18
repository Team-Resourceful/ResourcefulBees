package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.client.config.ClientConfig;
import com.teamresourceful.resourcefulbees.client.data.LangGeneration;
import com.teamresourceful.resourcefulbees.client.event.ClientEventHandlers;
import com.teamresourceful.resourcefulbees.client.pets.PetLoader;
import com.teamresourceful.resourcefulbees.common.capabilities.Capabilities;
import com.teamresourceful.resourcefulbees.common.compat.top.TopCompat;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.config.ConfigLoader;
import com.teamresourceful.resourcefulbees.common.data.DataGen;
import com.teamresourceful.resourcefulbees.common.data.DataPackLoader;
import com.teamresourceful.resourcefulbees.common.data.RecipeBuilder;
import com.teamresourceful.resourcefulbees.common.entity.villager.Beekeeper;
import com.teamresourceful.resourcefulbees.common.init.*;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.recipe.conditions.LoadDevRecipes;
import com.teamresourceful.resourcefulbees.common.recipe.ingredients.AmountSensitiveIngredient;
import com.teamresourceful.resourcefulbees.common.recipe.ingredients.BeeJarIngredient;
import com.teamresourceful.resourcefulbees.common.recipe.ingredients.NBTAmountSensitiveIngredient;
import com.teamresourceful.resourcefulbees.common.registry.RegistryHandler;
import com.teamresourceful.resourcefulbees.common.registry.custom.*;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFeatures;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

@Mod(ResourcefulBees.MOD_ID)
public class ResourcefulBees {

    public static final String MOD_ID = "resourcefulbees";
    public static final Logger LOGGER = LogManager.getLogger();

    public ResourcefulBees() {
        GeckoLib.initialize();
        ModSetup.initialize();
        RegistryHandler.init();
        ResourcefulBeesAPI.setBeeRegistry(BeeRegistry.getRegistry());
        ResourcefulBeesAPI.setTraitRegistry(TraitRegistry.getRegistry());
        DefaultTraitAbilities.registerDefaultAbilities(TraitAbilityRegistry.getRegistry());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG, "resourcefulbees/client.toml");

        ConfigLoader.load(CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");

        HoneycombSetup.setupHoneycombs();

        BeeSetup.setupBees();
        RegistryHandler.registerDynamicBees();
        HoneySetup.setupHoney();
        RegistryHandler.registerDynamicHoney();
        if (FMLLoader.isProduction()) {
            CommonConfig.GENERATE_DEFAULTS.set(false);
            CommonConfig.GENERATE_DEFAULTS.save();
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistryHandler::addEntityAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onPackFinders);

        MinecraftForge.EVENT_BUS.addListener(this::serverLoaded);
        MinecraftForge.EVENT_BUS.addListener(Beekeeper::setupBeekeeper);
        MinecraftForge.EVENT_BUS.addListener(this::cloneEvent);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::clientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverLoaded(ServerStartedEvent event) {
        if (event.getServer().isDedicatedServer()){
            BeeRegistry.getRegistry().regenerateCustomBeeData(event.getServer().registryAccess());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(RegistryHandler::registerDispenserBehaviors);
        NetPacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new RecipeBuilder());
        ModFeatures.ConfiguredFeatures.registerConfiguredFeatures();
        ModPotions.createMixes();
        CraftingHelper.register(new ResourceLocation(ResourcefulBees.MOD_ID, "amount_sensitive"), AmountSensitiveIngredient.Serializer.INSTANCE);
        CraftingHelper.register(new ResourceLocation(ResourcefulBees.MOD_ID, "nbt_amount_sensitive"), NBTAmountSensitiveIngredient.Serializer.INSTANCE);
        CraftingHelper.register(new ResourceLocation(ResourcefulBees.MOD_ID, "beejar"), BeeJarIngredient.Serializer.INSTANCE);
        CraftingHelper.register(LoadDevRecipes.Serializer.INSTANCE);
    }

    @SubscribeEvent
    public void cloneEvent(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(Capabilities.BEEPEDIA_DATA).ifPresent(cap ->
                event.getEntity().getCapability(Capabilities.BEEPEDIA_DATA).ifPresent(c -> c.deserializeNBT(cap.serializeNBT())));
    }

    @SubscribeEvent
    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopCompat::new);
    }

    @SubscribeEvent
    public void loadComplete(FMLLoadCompleteEvent event) {
        TraitAbilityRegistry.closeAbilityRegistry();
        TraitSetup.buildCustomTraits();
        TraitRegistry.setTraitRegistryClosed();
        BeeSetup.registerBeePlacements();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> LangGeneration::generateEnglishLang);
        DataGen.generateCommonData();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PetLoader::loadAPI);
        HoneycombRegistry.getRegistry().regenerateVariationData();
    }

    @SubscribeEvent
    public void onPackFinders(AddPackFindersEvent event) {
        if (event.getPackType().equals(PackType.SERVER_DATA)) event.addRepositorySource(DataPackLoader.INSTANCE);
    }
}
