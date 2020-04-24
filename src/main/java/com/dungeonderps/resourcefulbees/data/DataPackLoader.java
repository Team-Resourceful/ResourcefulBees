package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.config.ResourcefulBeesConfig;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import java.io.File;
import java.util.Map;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public class DataPackLoader {
    public static void serverAboutToStart(FMLServerAboutToStartEvent event) {
        event.getServer().getResourcePacks().addPackFinder(new IPackFinder() {
            @Override
            public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
                File configDatapackFile = ResourcefulBeesConfig.DATAPACK_PATH.toFile();
                if(configDatapackFile.exists() && configDatapackFile.isDirectory()) {
                    LOGGER.info("CONFIG RESOURCE FILE EXISTS");
                    T t = ResourcePackInfo.createResourcePack("resourcefuldatapack:" + configDatapackFile.getName(), false, () -> configDatapackFile.isDirectory() ? new FolderPack(configDatapackFile) : new FilePack(configDatapackFile), packInfoFactory, ResourcePackInfo.Priority.TOP);
                    if (t != null) nameToPackMap.put("resourcefuldatapack:" + configDatapackFile.getName(), t);
                }
            }
        });
    }
}
