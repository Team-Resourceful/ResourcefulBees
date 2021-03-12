package com.resourcefulbees.resourcefulbees.data;

import com.google.common.collect.Lists;
import com.resourcefulbees.resourcefulbees.init.BeeSetup;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import java.io.File;
import java.util.Collection;

public class DataPackLoader {

    private DataPackLoader() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void serverStarting(FMLServerAboutToStartEvent event) {
        event.getServer().getPackRepository().addPackFinder((packInfoConsumer, factory) -> {
            File configDataPackFile = BeeSetup.getResourcePath().toFile();
            if(configDataPackFile.exists() && configDataPackFile.isDirectory()) {
                ResourcePackInfo pack = ResourcePackInfo.create(
                        "resourcefulbees:internals",
                        true,
                        () -> new FolderPack(BeeSetup.getResourcePath().toFile()),
                        factory,
                        ResourcePackInfo.Priority.BOTTOM,
                        IPackNameDecorator.passThrough()
                );
                if (pack != null) packInfoConsumer.accept(pack);
            }
        });



        ResourcePackList packs = event.getServer().getPackRepository();
        packs.reload();

        event.getServer().reloadResources(getDataPacks(packs, event.getServer().getWorldData(), packs.getSelectedIds())).exceptionally(exception -> null);
    }

    private static Collection<String> getDataPacks(ResourcePackList packs, IServerConfiguration serverConf, Collection<String> existingPacks) {
        Collection<String> collection = Lists.newArrayList(existingPacks);
        Collection<String> collection1 = serverConf.getDataPackConfig().getDisabled();

        for (String s : packs.getAvailableIds()) {
            if (!collection1.contains(s) && !collection.contains(s)) {
                collection.add(s);
            }
        }

        return collection;
    }
}
