package com.resourcefulbees.resourcefulbees.data;

import com.google.common.collect.Lists;
import com.resourcefulbees.resourcefulbees.config.BeeSetup;
import net.minecraft.resources.*;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import java.io.File;
import java.util.Collection;

public class DataPackLoader {
    public static void serverStarting(FMLServerAboutToStartEvent event) {
        event.getServer().getResourcePacks().addPackFinder((packInfoConsumer, factory) -> {
            File configDatapackFile = BeeSetup.RESOURCE_PATH.toFile();
            if(configDatapackFile.exists() && configDatapackFile.isDirectory()) {
                ResourcePackInfo pack = ResourcePackInfo.createResourcePack(
                        "resourcefulbees:internals",
                        true,
                        () -> new FolderPack(BeeSetup.RESOURCE_PATH.toFile()),
                        factory,
                        ResourcePackInfo.Priority.BOTTOM,
                        IPackNameDecorator.method_29485()
                );
                if (pack != null) packInfoConsumer.accept(pack);
            }
        });



        ResourcePackList packs = event.getServer().getResourcePacks();
        packs.reloadPacksFromFinders();

        event.getServer().reloadResources(getDataPacks(packs, event.getServer().getSaveProperties(), packs.getEnabledNames())).exceptionally(exception -> null);
    }

    private static Collection<String> getDataPacks(ResourcePackList packs, IServerConfiguration serverConf, Collection<String> existingPacks) {
        Collection<String> collection = Lists.newArrayList(existingPacks);
        Collection<String> collection1 = serverConf.getDataPackSettings().getDisabled();

        for (String s : packs.getNames()) {
            if (!collection1.contains(s) && !collection.contains(s)) {
                collection.add(s);
            }
        }

        return collection;
    }
}
