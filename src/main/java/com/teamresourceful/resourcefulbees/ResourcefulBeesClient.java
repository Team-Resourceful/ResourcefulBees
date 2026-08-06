package com.teamresourceful.resourcefulbees;

import com.geckolib.renderer.GeoBlockRenderer;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.model.property.FilledBeeJarProperty;
import com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge.CentrifugeCrankRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge.CentrifugeRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.entities.CustomBeeRenderer;
import com.teamresourceful.resourcefulbees.client.screen.ApiaryScreen;
import com.teamresourceful.resourcefulbees.client.screen.BreederScreen;
import com.teamresourceful.resourcefulbees.client.screen.CentrifugeScreen;
import com.teamresourceful.resourcefulbees.client.tints.*;
import com.teamresourceful.resourcefulbees.common.blocks.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.blocks.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.HoneyBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.ArrayList;
import java.util.List;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = ResourcefulBees.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = ResourcefulBees.MODID, value = Dist.CLIENT)
public class ResourcefulBeesClient {
    public ResourcefulBeesClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ModEntities.getModBees().forEach((s, entityType) ->
                event.registerEntityRenderer(entityType.get(), context -> new CustomBeeRenderer<>(context, BeeRegistry.get().getBeeData(s).getRenderData()))
        );

        event.registerBlockEntityRenderer(ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get(), context -> new GeoBlockRenderer<>(context, ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get()));
        event.registerBlockEntityRenderer(ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get(), context -> new CentrifugeCrankRenderer<>(context, ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get()));
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.APIARY.get(), ApiaryScreen::new);
        event.register(ModMenuTypes.CENTRIFUGE.get(), CentrifugeScreen::new);
        event.register(ModMenuTypes.BREEDER.get(), BreederScreen::new);
    }

    @SubscribeEvent
    public static void itemColor(RegisterColorHandlersEvent.ItemTintSources event){
        event.register(ModIdentifier.of("honeycomb"), HoneycombTintSource.CODEC);
        event.register(ModIdentifier.of("honeycomb_block"), HoneycombBlockItemTintSource.CODEC);
        event.register(ModIdentifier.of("honey_block"), HoneyBlockBlockItemTintSource.CODEC);
        event.register(ModIdentifier.of("honey_bottle"), HoneyBottleTintSource.CODEC);
        event.register(ModIdentifier.of("filled_bee_jar"), FilledBeeJarTintSource.CODEC);
        event.register(ModIdentifier.of("bee_spawn_egg"), BeeSpawnEggTintSource.CODEC);
    }

    @SubscribeEvent
    public static void blockColor(RegisterColorHandlersEvent.BlockTintSources event) {
        List<HoneycombBlock> blocks = new ArrayList<>();
        ModBlocks.HONEYCOMB_BLOCKS.stream()
                .map(RegistryEntry::get)
                .filter(HoneycombBlock.class::isInstance)
                .map(HoneycombBlock.class::cast)
                .forEach(blocks::add);
        event.register(List.of(new HoneycombBlockTintSource()), blocks.toArray(new HoneycombBlock[0]));

        List<CustomHoneyBlock> honeyBlocks = new ArrayList<>();
        ModBlocks.HONEY_BLOCKS.stream()
                .map(RegistryEntry::get)
                .filter(CustomHoneyBlock.class::isInstance)
                .map(CustomHoneyBlock.class::cast)
                .forEach(honeyBlocks::add);
        event.register(List.of(new HoneyBlockTintSource()), honeyBlocks.toArray(new CustomHoneyBlock[0]));
    }

    @SubscribeEvent
    public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
        event.register(ModIdentifier.of("filled_bee_jar"), FilledBeeJarProperty.MAP_CODEC);
    }
}
